package com.gamerforea.eventhelper;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import com.gamerforea.eventhelper.util.FastUtils;
import com.gamerforea.eventhelper.wg.WGReflection;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.common.config.Configuration;

@SideOnly(Side.SERVER)
@Mod(modid = "EventHelper", name = "EventHelper", version = "1.4", acceptableRemoteVersions = "*")
public final class EventHelper
{
	public static final File cfgDir = new File(Loader.instance().getConfigDir(), "Events");
	public static final List<RegisteredListener> listeners = Lists.newArrayList();
	public static boolean debug = false;
	public static boolean fastEvents = false;

	@EventHandler
	public final void serverStarted(FMLServerStartedEvent event)
	{
		Configuration cfg = FastUtils.getConfig("EventHelper");
		String[] plugins = cfg.getStringList("plugins", CATEGORY_GENERAL, new String[] { "WorldGuard" }, "Plugins for sending events");
		boolean wgHooking = cfg.getBoolean("wgHooking", CATEGORY_GENERAL, true, "Hooking WorldGuard plugin (allow checking regions)");
		debug = cfg.getBoolean("debug", CATEGORY_GENERAL, debug, "Debugging enabled");
		fastEvents = cfg.getBoolean("fastEvents", CATEGORY_GENERAL, fastEvents, "Fast events enabled (not recommended, work only on WorldGuard)");
		cfg.save();

		PluginManager plManager = Bukkit.getPluginManager();
		for (String plName : plugins)
			listeners.addAll(HandlerList.getRegisteredListeners(plManager.getPlugin(plName)));
		if (wgHooking)
			WGReflection.setWG(plManager.getPlugin("WorldGuard"));
	}

	public static final void callEvent(Event event)
	{
		for (RegisteredListener listener : listeners)
			try
			{
				listener.callEvent(event);
			}
			catch (Throwable throwable)
			{
				if (debug)
					throwable.printStackTrace();
			}
	}
}