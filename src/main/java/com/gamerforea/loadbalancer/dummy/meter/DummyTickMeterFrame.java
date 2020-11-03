package com.gamerforea.loadbalancer.dummy.meter;

import com.gamerforea.loadbalancer.api.meter.ITickMeterFrame;

final class DummyTickMeterFrame implements ITickMeterFrame
{
	public static final DummyTickMeterFrame INSTANCE = new DummyTickMeterFrame();

	private DummyTickMeterFrame()
	{
	}

	@Override
	public void close()
	{
	}
}
