package com.gamerforea.eventhelper.util;

import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.InputStream;
import java.lang.reflect.Method;

public final class InjectionUtils
{
	private static final Method defineClass;

	// Need Inj subclass
	public static Class<?> injectClass(String pluginName, Class<?> clazz)
	{
		Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
		if (plugin == null)
			return null;

		try (InputStream in = clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + "$Inj.class"))
		{
			byte[] bytes = ByteStreams.toByteArray(in);
			return (Class<?>) defineClass.invoke(plugin.getClass().getClassLoader(), null, bytes, 0, bytes.length);
		}
		catch (Throwable throwable)
		{
			throwable.printStackTrace();
			return null;
		}
	}

	static
	{
		try
		{
			defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
			defineClass.setAccessible(true);
		}
		catch (Throwable throwable)
		{
			throw new RuntimeException("Failed hooking ClassLoader.defineClass(String, byte[], int, int) method!", throwable);
		}
	}
}
