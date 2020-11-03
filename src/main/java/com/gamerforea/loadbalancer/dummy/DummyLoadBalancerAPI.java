package com.gamerforea.loadbalancer.dummy;

import com.gamerforea.loadbalancer.api.ILoadBalancerAPI;
import com.gamerforea.loadbalancer.api.meter.ITickMeter;
import com.gamerforea.loadbalancer.api.meter.ITickMeterView;
import com.gamerforea.loadbalancer.api.skip.ITickSkipStrategyBuilder;
import com.gamerforea.loadbalancer.dummy.meter.DummyTickMeter;
import com.gamerforea.loadbalancer.dummy.skip.DummyTickSkipStrategyBuilder;

public final class DummyLoadBalancerAPI implements ILoadBalancerAPI
{
	public static final DummyLoadBalancerAPI INSTANCE = new DummyLoadBalancerAPI();

	private DummyLoadBalancerAPI()
	{
	}

	@Override
	public ITickMeterView getServerTickMeter()
	{
		return DummyTickMeter.INSTANCE;
	}

	@Override
	public ITickMeter createTickMeter()
	{
		return DummyTickMeter.INSTANCE;
	}

	@Override
	public ITickSkipStrategyBuilder createTickSkipStrategyBuilder()
	{
		return DummyTickSkipStrategyBuilder.INSTANCE;
	}

	@Override
	public int getPriority()
	{
		return Integer.MIN_VALUE;
	}
}
