package org.genericsystem.tracker.initialisation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.genericsystem.cdi.event.EventLauncher.AfterGenericSystemStarts;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.structure.Types.Issues;
import org.genericsystem.tracker.structure.Types.Priorities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InitializationScript {

	protected static Logger log = LoggerFactory.getLogger(InitializationScript.class);

	@Inject
	private Engine engine;

	public void init(@Observes AfterGenericSystemStarts event) {

		Cache cache = engine.newCache().start();

		Type issues = cache.find(Issues.class);
		Generic bug1 = issues.setInstance("bug1");
		Generic bug2 = issues.setInstance("bug2");

		Type priority = cache.find(Priorities.class);

		// Type colors = cache.find(Colors.class);
		// Generic red = colors.setInstance("red");
		// Generic blue = colors.setInstance("blue");
		// Generic green = colors.setInstance("green");

		// Type cars = cache.find(Cars.class);
		// Attribute power = cache.find(Power.class);

		// Generic myBmw = cars.setInstance("myBmw");
		// Generic myAudi = cars.setInstance("myAudi");
		// Generic myBemo = cars.setInstance("myBemo");

		// Relation carColorRelation = cache.find(CarColorRelation.class);
		//
		// myBmw.setValue(power, 123);
		// myBmw.bind(carColorRelation, red);
		//
		// myAudi.setValue(power, 321);
		// myAudi.bind(carColorRelation, blue);
		//
		// myBemo.setValue(power, 288);
		// myBemo.bind(carColorRelation, green);

		cache.flush();
	}
}
