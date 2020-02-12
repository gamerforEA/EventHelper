package com.gamerforea.eventhelper.coremod;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Map;

import static com.gamerforea.eventhelper.ModConstants.COREMOD_NAME;
import static com.gamerforea.eventhelper.ModConstants.MC_VERSION;

@IFMLLoadingPlugin.Name(COREMOD_NAME)
@IFMLLoadingPlugin.MCVersion(MC_VERSION)
@IFMLLoadingPlugin.SortingIndex(1001)
public final class CoreMod implements IFMLLoadingPlugin
{
	public static final Logger LOGGER = LogManager.getLogger(COREMOD_NAME);

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { "com.gamerforea.eventhelper.coremod.sponge.EventHelperSpongeClassTransformer" };
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Nullable
	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}
