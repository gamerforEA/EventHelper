package com.gamerforea.eventhelper.integration.sponge.cause;

import com.gamerforea.eventhelper.cause.ICauseStackFrame;
import com.gamerforea.eventhelper.cause.ICauseStackManager;

public class SpongeCauseStackManager implements ICauseStackManager
{
	public static final SpongeCauseStackManager INSTANCE = new SpongeCauseStackManager();

	@Override
	public ICauseStackFrame pushCauseFrame()
	{
		return new SpongeCauseStackFrame();
	}
}
