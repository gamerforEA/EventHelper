package com.gamerforea.loadbalancer.api;

import com.gamerforea.loadbalancer.dummy.DummyLoadBalancerAPI;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

final class LoadBalancerAPIProvider
{
	private static ILoadBalancerAPI loadBalancerAPI;

	public static ILoadBalancerAPI getLoadBalancerAPI()
	{
		if (loadBalancerAPI != null)
			return loadBalancerAPI;

		Logger logger = LogManager.getLogger("LoadBalancerAPIProvider");
		ServiceLoader<ILoadBalancerAPI> serviceLoader = ServiceLoader.load(ILoadBalancerAPI.class);
		List<ILoadBalancerAPI> implementations = Lists.newArrayList(serviceLoader);

		if (implementations.isEmpty())
		{
			logger.warn("LoadBalancerAPI implementation not found. Fallback to dummy implementation");
			return loadBalancerAPI = DummyLoadBalancerAPI.INSTANCE;
		}

		ILoadBalancerAPI implementation = Collections.max(implementations, Comparator.comparingInt(ILoadBalancerAPI::getPriority));
		logger.warn("LoadBalancerAPI implementation found: " + implementation.getClass().getName());
		return loadBalancerAPI = implementation;
	}
}
