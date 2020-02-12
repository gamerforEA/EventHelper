package com.gamerforea.eventhelper.coremod.sponge;

import org.spongepowered.api.event.Event;

public final class SpongeMethodHooks
{
	public static final String NAME = "com/gamerforea/eventhelper/coremod/sponge/SpongeMethodHooks";
	public static final String IIE_NAME = "isIgnoredEvent";
	public static final String IIE_DESC = "(Lorg/spongepowered/api/event/Event;)Z";

	public static final ThreadLocal<Event> CURRENT_EVENT = new ThreadLocal<>();

	public static boolean isIgnoredEvent(Event event)
	{
		return event == CURRENT_EVENT.get();
	}
}
