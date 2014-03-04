package org.genericSystem.cdi.event;

import javax.enterprise.event.Event;
import javax.inject.Inject;

public class EventLauncher {

	public class AfterGenericSystemStarts {

	}

	public class BeforeGenericSystemStops {

	}

	@Inject
	private Event<BeforeGenericSystemStops> stopEventLauncher;

	@Inject
	private Event<AfterGenericSystemStarts> startEventLauncher;

	public void launchStopEvent() {
		stopEventLauncher.fire(new BeforeGenericSystemStops());
	}

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
