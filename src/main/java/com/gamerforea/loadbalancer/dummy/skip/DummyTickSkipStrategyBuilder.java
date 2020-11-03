package com.gamerforea.loadbalancer.dummy.skip;

import com.gamerforea.loadbalancer.api.meter.ITickMeterView;
import com.gamerforea.loadbalancer.api.meter.MeterPeriod;
import com.gamerforea.loadbalancer.api.skip.ITickSkipStrategy;
import com.gamerforea.loadbalancer.api.skip.ITickSkipStrategyBuilder;

public final class DummyTickSkipStrategyBuilder implements ITickSkipStrategyBuilder
{
	public static final DummyTickSkipStrategyBuilder INSTANCE = new DummyTickSkipStrategyBuilder();

	private DummyTickSkipStrategyBuilder()
	{
	}

	@Override
	public ITickSkipStrategyBuilder setMinSkippedTicks(int minSkippedTicks)
	{
		return this;
	}

	@Override
	public ITickSkipStrategyBuilder setMaxSkippedTicks(int maxSkippedTicks)
	{
		return this;
	}

	@Override
	public ITickSkipStrategyBuilder setCriticalGradualProgressiveSkippedTicks()
	{
		return this;
	}

	@Override
	public ITickSkipStrategyBuilder setNonCriticalGradualRegressiveSkippedTicks()
	{
		return this;
	}

	@Override
	public ITickSkipStrategyBuilder setCriticalAverageTickTime(ITickMeterView tickMeter, long tickTime, MeterPeriod period)
	{
		return this;
	}

	@Override
	public ITickSkipStrategyBuilder setCriticalTickTimePercentage(ITickMeterView tickMeter, ITickMeterView parentTickMeter, float percentage, MeterPeriod period)
	{
		return this;
	}

	@Override
	public ITickSkipStrategy build()
	{
		return DummyTickSkipStrategy.INSTANCE;
	}
}
