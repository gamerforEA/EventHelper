package com.gamerforea.eventhelper.config;

import com.gamerforea.eventhelper.EventHelperMod;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ConfigUtils
{
	private static final String PACKAGE_DEFAULT = "default";
	private static final Set<Class<?>> LOADED_CONFIG_CLASSES = new HashSet<>();

	@Nonnull
	public static <T extends Collection<String>> T readStringCollection(
			@Nonnull Configuration cfg,
			@Nonnull String name, @Nonnull String category, @Nonnull String comment, @Nonnull T def)
	{
		String[] temp = cfg.getStringList(name, category, def.toArray(new String[0]), comment);
		def.clear();
		Collections.addAll(def, temp);
		return def;
	}

	@Nonnull
	public static Configuration getConfig(@Nonnull Class<?> configClass)
	{
		return getConfig(getConfigName(configClass));
	}

	@Nonnull
	public static Configuration getConfig(@Nonnull String cfgName)
	{
		Configuration cfg = new Configuration(new File(EventHelperMod.CFG_DIR, cfgName + ".cfg"));
		cfg.load();
		return cfg;
	}

	public static void readConfig(@Nonnull Class<?> configClass)
	{
		readConfig(configClass, false);
	}

	public static void readConfig(@Nonnull Class<?> configClass, @Nonnull String configName)
	{
		readConfig(configClass, configName, false);
	}

	public static void readConfig(@Nonnull Class<?> configClass, boolean reload)
	{
		readConfig(configClass, getConfigName(configClass), reload);
	}

	public static void readConfig(@Nonnull Class<?> configClass, @Nonnull String configName, boolean reload)
	{
		if (!LOADED_CONFIG_CLASSES.add(configClass) && !reload)
			return;

		Configuration cfg = getConfig(configName);
		try
		{
			for (Field field : configClass.getDeclaredFields())
			{
				int modifiers = field.getModifiers();
				if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers))
				{
					Class<?> type = field.getType();
					if (type == boolean.class)
					{
						ConfigBoolean annotation = field.getAnnotation(ConfigBoolean.class);
						if (annotation != null)
						{
							String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
							tryMoveProperty(cfg, name, annotation.category(), annotation.oldName(), annotation.oldCategory());

							boolean defaultValue = field.getBoolean(null);
							boolean value = cfg.getBoolean(name, annotation.category(), defaultValue, annotation.comment());
							field.setBoolean(null, value);
						}
					}
					else if (type == float.class)
					{
						ConfigFloat annotation = field.getAnnotation(ConfigFloat.class);
						if (annotation != null)
						{
							String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
							tryMoveProperty(cfg, name, annotation.category(), annotation.oldName(), annotation.oldCategory());

							float defaultValue = field.getFloat(null);
							float value = cfg.getFloat(name, annotation.category(), defaultValue, annotation.min(), annotation.max(), annotation.comment());
							field.setFloat(null, value);
						}
					}
					else if (type == int.class)
					{
						ConfigInt annotation = field.getAnnotation(ConfigInt.class);
						if (annotation != null)
						{
							String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
							tryMoveProperty(cfg, name, annotation.category(), annotation.oldName(), annotation.oldCategory());

							int defaultValue = field.getInt(null);
							int value = cfg.getInt(name, annotation.category(), defaultValue, annotation.min(), annotation.max(), annotation.comment());
							field.setInt(null, value);
						}
					}
					else if (type == String.class)
					{
						ConfigString annotation = field.getAnnotation(ConfigString.class);
						if (annotation != null)
						{
							String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
							tryMoveProperty(cfg, name, annotation.category(), annotation.oldName(), annotation.oldCategory());

							String defaultValue = (String) field.get(null);
							String value = cfg.getString(name, annotation.category(), defaultValue, annotation.comment());
							field.set(null, value);
						}
					}
					else if (type == ItemBlockList.class)
					{
						ConfigItemBlockList annotation = field.getAnnotation(ConfigItemBlockList.class);
						if (annotation != null)
						{
							String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
							tryMoveProperty(cfg, name, annotation.category(), annotation.oldName(), annotation.oldCategory());

							ItemBlockList list = (ItemBlockList) field.get(null);
							Objects.requireNonNull(list, "ItemBlockList " + configClass.getName() + '.' + field.getName() + " must not be null");
							Set<String> values = readStringCollection(cfg, name, annotation.category(), annotation.comment(), new HashSet<>(list.getRaw()));
							list.clear();
							list.addRaw(values);
						}
					}
					else if (type == ClassSet.class)
					{
						ConfigClassSet annotation = field.getAnnotation(ConfigClassSet.class);
						if (annotation != null)
						{
							String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
							tryMoveProperty(cfg, name, annotation.category(), annotation.oldName(), annotation.oldCategory());

							ClassSet<?> classSet = (ClassSet<?>) field.get(null);
							Objects.requireNonNull(classSet, "ClassSet " + configClass.getName() + '.' + field.getName() + " must not be null");
							Set<String> values = readStringCollection(cfg, name, annotation.category(), annotation.comment(), new HashSet<>(classSet.getRaw()));
							classSet.clear();
							classSet.addRaw(values);
						}
					}
					else if (Enum.class.isAssignableFrom(type))
					{
						ConfigEnum annotation = field.getAnnotation(ConfigEnum.class);
						if (annotation != null)
						{
							String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
							tryMoveProperty(cfg, name, annotation.category(), annotation.oldName(), annotation.oldCategory());

							Enum<?> defaultValue = (Enum<?>) field.get(null);
							Objects.requireNonNull(defaultValue, "Enum " + configClass.getName() + '.' + field.getName() + " must not be null");
							String valueName = cfg.getString(name, annotation.category(), defaultValue.name(), annotation.comment());
							try
							{
								Enum<?> value = Enum.valueOf(defaultValue.getDeclaringClass(), valueName);
								field.set(null, value);
							}
							catch (IllegalArgumentException e)
							{
								e.printStackTrace();
							}
						}
					}
					else if (Collection.class.isAssignableFrom(type))
					{
						// TODO Check generic type
						ConfigStringCollection annotation = field.getAnnotation(ConfigStringCollection.class);
						if (annotation != null)
						{
							String name = annotation.name().isEmpty() ? field.getName() : annotation.name();
							tryMoveProperty(cfg, name, annotation.category(), annotation.oldName(), annotation.oldCategory());

							Collection<String> collection = (Collection<String>) field.get(null);
							Objects.requireNonNull(collection, "Collection " + configClass.getName() + '.' + field.getName() + " must not be null");
							readStringCollection(cfg, name, annotation.category(), annotation.comment(), collection);
						}
					}
				}
			}
		}
		catch (Throwable throwable)
		{
			EventHelperMod.LOGGER.error("Failed reading config " + cfg.getConfigFile().getName(), throwable);
		}
		cfg.save();
	}

	private static boolean tryMoveProperty(
			@Nonnull Configuration cfg,
			@Nonnull String newName,
			@Nonnull String newCategory, @Nullable String oldName, @Nullable String oldCategory)
	{
		if (newName.isEmpty() || newCategory.isEmpty())
			return false;

		if (Strings.isNullOrEmpty(oldCategory))
			oldCategory = newCategory;
		if (Strings.isNullOrEmpty(oldName))
			oldName = newName;

		if (newName.equalsIgnoreCase(oldName) && newCategory.equalsIgnoreCase(oldCategory))
			return false;

		if (cfg.hasKey(newCategory, newName) || !cfg.hasKey(oldCategory, oldName))
			return false;

		ConfigCategory prevCategory = cfg.getCategory(oldCategory);
		if (prevCategory.containsKey(oldName))
		{
			Property property = prevCategory.remove(oldName);
			property.setName(newName);

			ConfigCategory category;
			if (newCategory.equalsIgnoreCase(oldCategory))
				category = prevCategory;
			else
			{
				category = cfg.getCategory(newCategory);
				if (prevCategory.isEmpty())
					cfg.removeCategory(prevCategory);
			}

			category.put(newName, property);

			return true;
		}

		return false;
	}

	@Nonnull
	private static String getConfigName(@Nonnull Class<?> configClass)
	{
		Config annotation = configClass.getAnnotation(Config.class);
		Objects.requireNonNull(annotation, "Annotaion " + Config.class.getName() + " not found for class " + configClass.getName());
		String cfgName = annotation.name();
		if (Strings.isNullOrEmpty(cfgName))
			cfgName = getPackageName(configClass.getName());
		Preconditions.checkArgument(!Strings.isNullOrEmpty(cfgName), "Config name for class " + configClass.getName() + " is not determined");
		return cfgName;
	}

	@Nonnull
	private static String getPackageName(@Nullable String className)
	{
		if (Strings.isNullOrEmpty(className))
			return PACKAGE_DEFAULT;
		int classDelimeterIndex = className.lastIndexOf('.');
		if (classDelimeterIndex == -1)
			return PACKAGE_DEFAULT;
		String packageName = className.substring(0, classDelimeterIndex);
		if (Strings.isNullOrEmpty(packageName))
			return PACKAGE_DEFAULT;
		int packageDelimeterIndex = packageName.lastIndexOf('.');
		if (packageDelimeterIndex == -1)
			return packageName;
		String simplePackageName = packageName.substring(packageDelimeterIndex + 1);
		return Strings.isNullOrEmpty(simplePackageName) ? PACKAGE_DEFAULT : simplePackageName;
	}
}
