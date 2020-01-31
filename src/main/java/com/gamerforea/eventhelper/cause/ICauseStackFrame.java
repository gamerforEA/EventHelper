package com.gamerforea.eventhelper.cause;

import com.gamerforea.eventhelper.fake.FakePlayerContainer;

public interface ICauseStackFrame extends AutoCloseable
{
	ICauseStackFrame pushCause(Object obj);

	default ICauseStackFrame pushCause(FakePlayerContainer fakePlayerContainer)
	{
		return this.pushCause(fakePlayerContainer.getPlayer());
	}

	@Override
	void close();
}
