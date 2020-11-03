package com.gamerforea.loadbalancer.api.meter;

public interface ITickMeterFrame extends AutoCloseable
{
	@Override
	void close();
}
