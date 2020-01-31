package com.gamerforea.eventhelper.cause;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;

public final class DummyCauseStackFrame implements ICauseStackFrame
{
	public static final DummyCauseStackFrame INSTANCE = new DummyCauseStackFrame();

	private DummyCauseStackFrame()
	{
	}

	@Override
	public ICauseStackFrame pushCause(Object obj)
	{
		return this;
	}

	@Override
	public ICauseStackFrame pushCause(FakePlayerContainer fakePlayerContainer)
	{
		return this;
	}

	@Override
	public void close()
	{
	}
}
