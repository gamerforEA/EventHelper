package com.gamerforea.eventhelper.cause;

public final class DummyCauseStackManager implements ICauseStackManager
{
	public static final DummyCauseStackManager INSTANCE = new DummyCauseStackManager();

	private DummyCauseStackManager()
	{
	}

	@Override
	public ICauseStackFrame pushCauseFrame()
	{
		return DummyCauseStackFrame.INSTANCE;
	}
}
