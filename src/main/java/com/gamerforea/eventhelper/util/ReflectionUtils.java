package com.gamerforea.eventhelper.util;

public final class ReflectionUtils extends SecurityManager
{
	private static final ReflectionUtils INSTANCE = new ReflectionUtils();

	public static Class[] getCallingClasses()
	{
		return INSTANCE.getClassContext();
	}
}
