package com.gamerforea.eventhelper;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.gamerforea.eventhelper.util.FastUtils;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "EventHelper", name = "EventHelper", version = "@VERSION@", acceptableRemoteVersions = "*")
public final class EventHelper
{
	public static final File cfgDir = new File(Loader.instance().getConfigDir(), "Events");
	public static final Logger logger = LogManager.getLogger("EventHelper");
	public static boolean debug = false;

	@EventHandler
	public final void preInit(FMLPreInitializationEvent event)
	{
		Configuration cfg = FastUtils.getConfig("EventHelper");
		debug = cfg.getBoolean("debug", CATEGORY_GENERAL, debug, "Debugging enabled");
		cfg.save();
	}
}