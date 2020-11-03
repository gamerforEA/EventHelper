package com.gamerforea.loadbalancer.api.skip;

import com.gamerforea.loadbalancer.api.meter.ITickMeterView;
import com.gamerforea.loadbalancer.api.meter.MeterPeriod;

public interface ITickSkipStrategyBuilder
{
	ITickSkipStrategyBuilder setMinSkippedTicks(int minSkippedTicks);

	ITickSkipStrategyBuilder setMaxSkippedTicks(int maxSkippedTicks);

	default ITickSkipStrategyBuilder setSkippedTicks(int skippedTicks)
	{
		return this.setMinSkippedTicks(skippedTicks).setMaxSkippedTicks(skippedTicks);
	}

	ITickSkipStrategyBuilder setCriticalGradualProgressiveSkippedTicks();

	ITickSkipStrategyBuilder setNonCriticalGradualRegressiveSkippedTicks();

	ITickSkipStrategyBuilder setCriticalAverageTickTime(ITickMeterView tickMeter, long tickTime, MeterPeriod period);

	ITickSkipStrategyBuilder setCriticalTickTimePercentage(ITickMeterView tickMeter, ITickMeterView parentTickMeter, float percentage, MeterPeriod period);

	ITickSkipStrategy build();
}
