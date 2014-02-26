package org.genericsystem.jsf.example.initialisation;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

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
import org.jboss.solder.beanManager.BeanManagerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitializationScript implements Extension {

	protected Logger log = LoggerFactory.getLogger(InitializationScript.class);

	public void init(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
		Engine engine = BeanManagerUtils.getContextualInstance(beanManager, Engine.class);
		Cache cache = engine.newCache().start();

		Type colors = cache.find(Colors.class);
		Generic red = colors.setInstance("red");
		Generic blue = colors.setInstance("blue");
		Generic green = colors.setInstance("green");

		Type cars = cache.find(Cars.class);
		Attribute power = cache.find(Power.class);
		// Attribute price = cache.find(Price.class);
		// Attribute fuelTank = cache.find(FuelTank.class);
		Generic myBmw = cars.setInstance("myBmw");
		Generic myAudi = cars.setInstance("myAudi");
		Generic myBemo = cars.setInstance("myBemo");

		Relation carColorRelation = cache.find(CarColorRelation.class);

		myBmw.setValue(power, 123);
		// myBmw.setValue(price, 12345);
		// myBmw.setValue(fuelTank, 500);
		myBmw.bind(carColorRelation, red);

		myAudi.setValue(power, 321);
		// myAudi.setValue(price, 43212);
		// myAudi.setValue(fuelTank, 700);
		myAudi.bind(carColorRelation, blue);

		myBemo.setValue(power, 288);
		// myBemo.setValue(price, 30654);
		// myBemo.setValue(fuelTank, 400);
		myBemo.bind(carColorRelation, green);

		cache.flush();
	}
}
