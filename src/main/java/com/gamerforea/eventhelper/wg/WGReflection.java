package com.gamerforea.eventhelper.wg;

import java.lang.reflect.Method;

import org.bukkit.plugin.Plugin;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

public final class WGReflection
{
	private static Plugin wgPlugin;
	private static ClassLoader wgClassLoader;

	public static final void setWG(Plugin plugin)
	{
		Preconditions.checkNotNull(plugin, "WorldGuard not installed!");
		wgPlugin = plugin;
		wgClassLoader = plugin.getClass().getClassLoader();
	}

	public static final ClassLoader getWGClassLoader()
	{
		if (wgClassLoader == null)
			throw new IllegalStateException("WorldGuard ClassLoader not found!");
		return wgClassLoader;
	}

	public static final Plugin getWGPlugin()
	{
		if (wgPlugin == null)
			throw new IllegalStateException("WorldGuard not found!");
		return wgPlugin;
	}

	// Need Inj subclass
	public static final Class<?> injectIntoWG(Class<?> clazz) throws Throwable
	{
		byte[] bytes = ByteStreams.toByteArray(clazz.getClassLoader().getResourceAsStream(clazz.getName().replace('.', '/') + "$Inj.class"));
		Method m = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
		m.setAccessible(true);
		return (Class<?>) m.invoke(getWGClassLoader(), null, bytes, 0, bytes.length);
	}
}
