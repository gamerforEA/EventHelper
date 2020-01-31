package com.gamerforea.eventhelper.integration.sponge.cause;

import com.gamerforea.eventhelper.cause.ICauseStackFrame;
import net.minecraftforge.common.util.FakePlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;

public class SpongeCauseStackFrame implements ICauseStackFrame
{
	protected final CauseStackManager.StackFrame stackFrame;

	public SpongeCauseStackFrame()
	{
		this.stackFrame = Sponge.getCauseStackManager().pushCauseFrame();
	}

	@Override
	public ICauseStackFrame pushCause(Object obj)
	{
		this.stackFrame.pushCause(obj);
		if (obj instanceof FakePlayer && obj instanceof Player)
			this.stackFrame.addContext(EventContextKeys.FAKE_PLAYER, (Player) obj);
		return this;
	}

	@Override
	public void close()
	{
		this.stackFrame.close();
	}
}
