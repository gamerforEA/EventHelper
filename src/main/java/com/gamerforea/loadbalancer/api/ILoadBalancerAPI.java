package com.gamerforea.loadbalancer.api;

import com.gamerforea.loadbalancer.api.meter.ITickMeter;
import com.gamerforea.loadbalancer.api.meter.ITickMeterView;
import com.gamerforea.loadbalancer.api.skip.ITickSkipStrategyBuilder;

public interface ILoadBalancerAPI
{
	ITickMeterView getServerTickMeter();

	ITickMeter createTickMeter();

	ITickSkipStrategyBuilder createTickSkipStrategyBuilder();

	int getPriority();

	static ILoadBalancerAPI getInstance()
	{
		return LoadBalancerAPIProvider.getLoadBalancerAPI();
	}
}
