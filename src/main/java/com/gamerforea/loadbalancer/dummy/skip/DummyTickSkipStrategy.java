package com.gamerforea.loadbalancer.dummy.skip;

import com.gamerforea.loadbalancer.api.skip.ITickSkipStrategy;

final class DummyTickSkipStrategy implements ITickSkipStrategy
{
	public static final DummyTickSkipStrategy INSTANCE = new DummyTickSkipStrategy();

	private DummyTickSkipStrategy()
	{
	}

	@Override
	public boolean skipTick()
	{
		return false;
	}
}
