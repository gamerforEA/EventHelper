package com.gamerforea.loadbalancer.api.meter;

import java.util.concurrent.TimeUnit;

public interface ITickMeterView
{
	long averageTickTime(MeterPeriod period);

	default long averageTickTime(MeterPeriod period, TimeUnit timeUnit)
	{
		return timeUnit.convert(this.averageTickTime(period), TimeUnit.NANOSECONDS);
	}

	default float averageTPS(MeterPeriod period)
	{
		long tickTime = this.averageTickTime(period);
		return tickTime == 0 ? 20.0F : Math.min(20.0F, 1_000_000_000.0F / (float) tickTime);
	}

	default float averageTickTimePercentage(ITickMeterView parentTickMeter, MeterPeriod period)
	{
		long tickTime = this.averageTickTime(period);
		if (tickTime == 0)
			return 0.0F;

		long parentTickTime = parentTickMeter.averageTickTime(period);
		return tickTime < parentTickTime ? (float) tickTime / (float) parentTickTime : 1.0F;
	}
}
