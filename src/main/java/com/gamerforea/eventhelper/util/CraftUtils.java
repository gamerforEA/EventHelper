package com.gamerforea.eventhelper.util;

import com.gamerforea.eventhelper.EventHelper;

public final class CraftUtils
{
	public static final Class<?> getCraftClass(String name) throws ClassNotFoundException
	{
		return Class.forName((EventHelper.craftPackage + '/' + name).replace("//", ".").replace('/', '.'));
	}
}
