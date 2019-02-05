package com.gamerforea.eventhelper;

import com.gamerforea.eventhelper.command.CommandReloadAllConfigs;
import com.gamerforea.eventhelper.config.ConfigBoolean;
import com.gamerforea.eventhelper.config.ConfigEnum;
import com.gamerforea.eventhelper.config.ConfigUtils;
import com.gamerforea.eventhelper.integration.IntegrationType;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;

import static com.gamerforea.eventhelper.ModConstants.*;

@Mod(modid = MODID, name = NAME, version = VERSION, acceptableRemoteVersions = "*")
public final class EventHelperMod
{
	public static final File CFG_DIR = new File(Loader.instance().getConfigDir(), "Events");
	public static final Logger LOGGER = LogManager.getLogger(NAME);

	@ConfigBoolean(comment = "Enable debugging messages")
	public static boolean debug = true;

	@Nonnull
	@ConfigEnum(comment = "Default API for integration (AUTO, SPONGE, BUKKIT)")
	public static IntegrationType integrationType = IntegrationType.AUTO;

	@ConfigBoolean(comment = "Enable additional checks to grief prevention (may be needed for Bukkit)")
	public static boolean paranoidProtection = false;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		ConfigUtils.readConfig(this.getClass(), NAME);
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandReloadAllConfigs());
	}
}