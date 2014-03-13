package org.genericsystem.jsf.example.initialisation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.genericsystem.cdi.event.EventLauncher.AfterGenericSystemStarts;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Attributes.Power;
import org.genericsystem.jsf.example.structure.Relations.CarColorRelation;
import org.genericsystem.jsf.example.structure.Types.Cars;
import org.genericsystem.jsf.example.structure.Types.Colors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InitializationScript {

	protected static Logger log = LoggerFactory.getLogger(InitializationScript.class);

	@Inject
	private Engine engine;

	public void init(@Observes AfterGenericSystemStarts event) {

		Cache cache = engine.newCache().start();

		Type colors = cache.find(Colors.class);
		Generic red = colors.setInstance("red");
		Generic blue = colors.setInstance("blue");

		Type cars = cache.find(Cars.class);
		Generic myBmw = cars.setInstance("myBmw");
		Generic myAudi = cars.setInstance("myAudi");

		Attribute power = cache.find(Power.class);
		myBmw.setValue(power, 123);
		myAudi.setValue(power, 321);

		Relation carColorRelation = cache.find(CarColorRelation.class);
		myBmw.bind(carColorRelation, red);
		myAudi.bind(carColorRelation, blue);

		cache.flush();
	}
}
