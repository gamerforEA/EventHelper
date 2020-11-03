package com.gamerforea.loadbalancer.dummy.meter;

import com.gamerforea.loadbalancer.api.meter.ITickMeter;
import com.gamerforea.loadbalancer.api.meter.ITickMeterFrame;
import com.gamerforea.loadbalancer.api.meter.MeterPeriod;

public final class DummyTickMeter implements ITickMeter
{
	public static final DummyTickMeter INSTANCE = new DummyTickMeter();

	private DummyTickMeter()
	{
	}

	@Override
	public ITickMeterFrame startMeasurement()
	{
		return DummyTickMeterFrame.INSTANCE;
	}

	@Override
	public long averageTickTime(MeterPeriod period)
	{
		return 0;
	}
}
