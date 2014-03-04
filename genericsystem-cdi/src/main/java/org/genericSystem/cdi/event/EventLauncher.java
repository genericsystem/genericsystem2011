package org.genericSystem.cdi.event;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class EventLauncher {

	@Inject
	private Event<BeforeGenericSystemStops> stopEventLauncher;

	@Inject
	private Event<AfterGenericSystemStarts> startEventLauncher;

	public void launchStartEvent() {
		startEventLauncher.fire(new AfterGenericSystemStarts());
	}

	public void setStartEventLauncher(Event<AfterGenericSystemStarts> startEventLauncher) {
		this.startEventLauncher = startEventLauncher;
	}

	public Event<BeforeGenericSystemStops> getStopEventLauncher() {
		return stopEventLauncher;
	}

	public void setStopEventLauncher(Event<BeforeGenericSystemStops> stopEventLauncher) {
		this.stopEventLauncher = stopEventLauncher;
	}
}
