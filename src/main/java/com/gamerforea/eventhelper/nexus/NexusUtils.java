package com.gamerforea.eventhelper.nexus;

import com.gamerforea.eventhelper.util.ReflectionUtils;
import com.google.common.base.Preconditions;
import com.google.common.hash.Hashing;
import com.mojang.authlib.GameProfile;

import javax.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class NexusUtils
{
	@Nonnull
	public static ModNexusFactory getFactory()
	{
		return getFactory(getGameProfile());
	}

	@Nonnull
	public static ModNexusFactory getFactory(@Nonnull Class<?> modNexusClass)
	{
		return getFactory(getGameProfile(modNexusClass));
	}

	@Nonnull
	public static ModNexusFactory getFactory(@Nonnull GameProfile profile)
	{
		return new ModNexusFactory(profile);
	}

	@Nonnull
	public static GameProfile getGameProfile()
	{
		Class[] callingClasses = ReflectionUtils.getCallingClasses();
		Set<Class<?>> visitedClasses = new HashSet<>(callingClasses.length);
		for (Class<?> callerClass : callingClasses)
		{
			if (visitedClasses.add(callerClass))
			{
				ModNexus annotation = callerClass.getAnnotation(ModNexus.class);
				if (annotation != null)
					return getGameProfile(callerClass);
			}
		}
		throw new IllegalStateException("Class with ModNexus annotation not found in StackTrace");
	}

	@Nonnull
	public static GameProfile getGameProfile(@Nonnull Class<?> modNexusClass)
	{
		ModNexus annotation = getModNexusAnnotation(modNexusClass);
		return annotation.uuid().isEmpty() ? getGameProfile(annotation.name()) : getGameProfile(annotation.name(), UUID.fromString(annotation.uuid()));
	}

	@Nonnull
	public static GameProfile getGameProfile(@Nonnull String fakeName)
	{
		Preconditions.checkArgument(!fakeName.isEmpty(), "fakeName must not be empty");
		if (fakeName.charAt(0) != '[')
			fakeName = '[' + fakeName + ']';
		return getGameProfile(fakeName, stringToId(fakeName));
	}

	@Nonnull
	public static GameProfile getGameProfile(@Nonnull String fakeName, @Nonnull UUID fakeId)
	{
		Preconditions.checkArgument(!fakeName.isEmpty(), "fakeName must not be empty");
		if (fakeName.charAt(0) != '[')
			fakeName = '[' + fakeName + ']';
		return new GameProfile(fakeId, fakeName);
	}

	@Nonnull
	private static UUID stringToId(@Nonnull String fakeName)
	{
		return new UUID(Hashing.md5().hashBytes(fakeName.getBytes(StandardCharsets.UTF_8)).asLong(), Hashing.sha1().hashBytes(fakeName.getBytes(StandardCharsets.UTF_8)).asLong());
	}

	@Nonnull
	private static ModNexus getModNexusAnnotation(@Nonnull Class<?> modNexusClass)
	{
		ModNexus annotation = modNexusClass.getAnnotation(ModNexus.class);
		Preconditions.checkNotNull(annotation, "ModNexus annotation not found in class " + modNexusClass.getName());
		Preconditions.checkArgument(!annotation.name().isEmpty(), "ModNexus.name() must not be empty");
		return annotation;
	}
}
