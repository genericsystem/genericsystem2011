package org.genericsystem.impl;

import java.util.Arrays;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.SingularConstraintViolationException;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Property;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.Statics;
import org.testng.annotations.Test;

@Test
public class RelationTest extends AbstractTest {

	public void testReverseTernaryAccess() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type passenger = cache.newType("Passenger");
		Type time = cache.newType("time");

		final Relation carPassengerTime = car.addRelation(cache, "CarPassengerTime", passenger, time);
		carPassengerTime.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPassengerTime.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Generic michael = passenger.newInstance(cache, "michael");
		passenger.newInstance(cache, "nicolas");
		Generic today = time.newInstance(cache, "today");
		Generic yesterday = time.newInstance(cache, "yesterday");
		Generic yourAudi = car.newInstance(cache, "yourAudi");

		michael.bind(cache, carPassengerTime, Statics.TARGET_POSITION, myBmw, today);
		assert michael.getLinks(cache, carPassengerTime, Statics.TARGET_POSITION).size() == 1;
		michael.bind(cache, carPassengerTime, Statics.TARGET_POSITION, yourAudi, today);
		assert michael.getLinks(cache, carPassengerTime, Statics.TARGET_POSITION).size() == 1;

		yesterday.bind(cache, carPassengerTime, Statics.SECOND_TARGET_POSITION, myBmw, passenger);
		assert yesterday.getLinks(cache, carPassengerTime, Statics.SECOND_TARGET_POSITION).size() == 1;

	}

	public void testSimple() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type person = cache.newType("Person");
		Relation carDriver = car.addRelation(cache, "driver", person);
		carDriver.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = person.newInstance(cache, "me");
		Generic you = person.newInstance(cache, "you");
		myBmw.bind(cache, carDriver, me);
		myBmw.bind(cache, carDriver, you);
		assert myBmw.getLinks(cache, carDriver).size() == 2 : myBmw.getLinks(cache, (Property) carDriver);
	}

	public void testSimpleReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type person = cache.newType("Person");
		Relation carDriver = car.addRelation(cache, "driver", person);
		carDriver.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = person.newInstance(cache, "me");
		me.bind(cache, carDriver, Statics.TARGET_POSITION, myBmw);
		assert me.getLinks(cache, carDriver, Statics.TARGET_POSITION).size() == 1;
	}

	public void testSimpleTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type person = cache.newType("Person");
		Type time = cache.newType("Time");
		Relation carDriverTime = car.addRelation(cache, "driver", person, time);
		carDriverTime.enableSingularConstraint(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = person.newInstance(cache, "me");
		Generic you = person.newInstance(cache, "you");
		Generic myTime = time.newInstance(cache, "myTime");
		Generic yourTime = time.newInstance(cache, "yourTime");

		myBmw.bind(cache, carDriverTime, me, myTime);
		myBmw.bind(cache, carDriverTime, you, yourTime);

		assert myBmw.getLinks(cache, carDriverTime).size() == 2 : myBmw.getLinks(cache, (Property) carDriverTime);
		assert carDriverTime.getComponents().get(Statics.BASE_POSITION).equals(car);
		assert carDriverTime.getComponents().get(Statics.TARGET_POSITION).equals(person);
		assert carDriverTime.getComponents().get(Statics.SECOND_TARGET_POSITION).equals(time);

		assert carDriverTime.getBaseComponent().equals(car);
	}

	public void testOneToManyManyToManyImpl() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type passenger = cache.newType("Passenger");
		final Relation carPassenger = car.addRelation(cache, "CarPassenger", passenger);
		carPassenger.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPassenger.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Generic michael = passenger.newInstance(cache, "michael");
		Generic nicolas = passenger.newInstance(cache, "nicolas");

		myBmw.setLink(cache, carPassenger, "30%", michael);
		myBmw.setLink(cache, carPassenger, "40%", nicolas);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBmw.setLink(cache, carPassenger, "60%", michael);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testOneToManyManyToManyImplReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");

		Type passenger = cache.newType("Passenger");

		final Relation carPassenger = car.addRelation(cache, "CarPassenger", passenger);
		carPassenger.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPassenger.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");

		final Generic michael = passenger.newInstance(cache, "michael");
		Generic nicolas = passenger.newInstance(cache, "nicolas");

		Link link30 = michael.setLink(cache, carPassenger, "30%", Statics.TARGET_POSITION, myBmw);
		Link link40 = nicolas.setLink(cache, carPassenger, "40%", Statics.TARGET_POSITION, myBmw);
		assert link30.isAlive(cache);
		michael.setLink(cache, carPassenger, "60%", Statics.TARGET_POSITION, myBmw);
		assert !link30.isAlive(cache);
		assert link40.isAlive(cache);
	}

	public void testOneToManyManyToManyImplTernary() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type passenger = cache.newType("Passenger");
		Type time = cache.newType("time");

		final Relation carPassengerTime = car.addRelation(cache, "CarPassenger", passenger, time);
		carPassengerTime.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPassengerTime.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Generic yourAudi = car.newInstance(cache, "yourAudi");
		final Generic michael = passenger.newInstance(cache, "michael");
		final Generic today = time.newInstance(cache, "today");
		Generic nicolas = passenger.newInstance(cache, "nicolas");

		myBmw.setLink(cache, carPassengerTime, "30%", michael, today);
		myBmw.setLink(cache, carPassengerTime, "40%", nicolas, today);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				yourAudi.setLink(cache, carPassengerTime, "60%", michael, today);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testToOne() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");

		Type owner = cache.newType("Owner");

		Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");

		Generic sven = owner.newInstance(cache, "sven");

		myBmw.bind(cache, carOwner, sven);
		yourAudi.bind(cache, carOwner, sven);

		assert myBmw.getLinks(cache, carOwner).size() == 1 : myBmw.getLinks(cache, (Property) carOwner);
		assert yourAudi.getLinks(cache, carOwner).size() == 1 : yourAudi.getLinks(cache, (Property) carOwner);
	}

	public void testToOneReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic sven = owner.newInstance(cache, "sven");

		sven.bind(cache, carOwner, Statics.TARGET_POSITION, myBmw);
		sven.bind(cache, carOwner, Statics.TARGET_POSITION, yourAudi);

		assert sven.getLinks(cache, carOwner, Statics.TARGET_POSITION).size() == 2 : sven.getLinks(cache, (Property) carOwner, Statics.TARGET_POSITION);
	}

	public void testToOneTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");

		final Relation carOwnerTime = car.addRelation(cache, "CarOwnerTime", owner, time);
		carOwnerTime.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic nicolas = owner.newInstance(cache, "nicolas");
		final Generic michael = owner.newInstance(cache, "michael");
		Generic today = time.newInstance(cache, "today");
		final Generic yesterday = time.newInstance(cache, "yesterday");

		Link myBmwNicolasToday = myBmw.bind(cache, carOwnerTime, nicolas, today);
		assert myBmw.getLinks(cache, carOwnerTime).size() == 1 : myBmw.getLinks(cache, (Property) carOwnerTime);
		Link myBmwMichaelYesterday = myBmw.bind(cache, carOwnerTime, michael, yesterday);

		assert myBmw.getLinks(cache, carOwnerTime).contains(myBmwMichaelYesterday);
		assert !myBmw.getLinks(cache, carOwnerTime).contains(myBmwNicolasToday);
	}

	public void testToOneInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");

		Type owner = cache.newType("Owner");

		Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");

		Generic me = owner.newInstance(cache, "me");
		Generic you = owner.newInstance(cache, "you");

		Link carMe = car.setLink(cache, carOwner, "defaultOwner", me);
		assert myBmw.getLink(cache, (Property) carOwner).getBaseComponent().equals(car);

		Link carYou = myBmw.bind(cache, carOwner, you);

		assert myBmw.getLink(cache, (Property) carOwner).equals(carYou) : myBmw.getLinks(cache, (Property) carOwner);
		assert !myBmw.getLinks(cache, carOwner).contains(carMe) : myBmw.getLinks(cache, (Property) carOwner);
	}

	public void testToOneInheritanceReverse() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");

		final Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");
		final Generic you = owner.newInstance(cache, "you");

		me.setLink(cache, carOwner, "defaultOwner", Statics.TARGET_POSITION, car);
		assert myBmw.getLink(cache, (Property) carOwner).getBaseComponent().equals(car);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				you.bind(cache, carOwner, Statics.TARGET_POSITION, myBmw);
			}

		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testToOneInheritanceTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");

		Relation carOwnerTime = car.addRelation(cache, "CarOwnerTime", owner, time);
		carOwnerTime.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");
		Generic you = owner.newInstance(cache, "you");
		Generic today = time.newInstance(cache, "today");
		Generic yesterday = time.newInstance(cache, "yesterday");

		Link carMeToday = car.setLink(cache, carOwnerTime, "defaultOwner", me, today);
		assert myBmw.getLink(cache, (Property) carOwnerTime).getBaseComponent().equals(car);

		Link carYouYesterday = myBmw.bind(cache, carOwnerTime, you, yesterday);

		assert myBmw.getLink(cache, (Property) carOwnerTime).equals(carYouYesterday) : myBmw.getLinks(cache, (Property) carOwnerTime);
		assert !myBmw.getLinks(cache, carOwnerTime).contains(carMeToday) : myBmw.getLinks(cache, (Property) carOwnerTime);
	}

	public void testToOneNewTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");
		Generic you = owner.newInstance(cache, "you");

		Link myBmwMe = myBmw.bind(cache, carOwner, me);
		assert myBmw.getLinks(cache, carOwner).contains(myBmwMe);
		Link myBmwYou = myBmw.bind(cache, carOwner, you);
		assert !myBmw.getLinks(cache, carOwner).contains(myBmwMe);
		assert myBmw.getLinks(cache, carOwner).contains(myBmwYou);
		assert myBmw.getLinks(cache, carOwner).size() == 1 : myBmw.getLinks(cache, (Property) carOwner);
	}

	public void testToOneNewTargetReverse() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		final Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");
		final Generic you = owner.newInstance(cache, "you");

		Link myBmwMe = me.bind(cache, carOwner, Statics.TARGET_POSITION, myBmw);
		assert me.getLinks(cache, carOwner, Statics.TARGET_POSITION).contains(myBmwMe);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				you.bind(cache, carOwner, Statics.TARGET_POSITION, myBmw);
			}

		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testToOneNewTargetTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");
		Relation carOwnerTime = car.addRelation(cache, "CarOwner", owner, time);
		carOwnerTime.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");
		Generic you = owner.newInstance(cache, "you");
		Generic today = time.newInstance(cache, "today");

		Link myBmwMeToday = myBmw.bind(cache, carOwnerTime, me, today);
		assert myBmw.getLinks(cache, carOwnerTime).contains(myBmwMeToday);
		Link myBmwYou = myBmw.bind(cache, carOwnerTime, you, today);
		assert !myBmw.getLinks(cache, carOwnerTime).contains(myBmwMeToday);
		assert myBmw.getLinks(cache, carOwnerTime).contains(myBmwYou);
		assert myBmw.getLinks(cache, carOwnerTime).size() == 1 : myBmw.getLinks(cache, (Property) carOwnerTime);
	}

	public void testToOneSameValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");

		Link myBmwMe1 = myBmw.setLink(cache, carOwner, "value1", me);
		Link myBmwMe2 = myBmw.setLink(cache, carOwner, "value1", me);
		assert myBmwMe1 == myBmwMe2;

		assert myBmw.getLinks(cache, carOwner).size() == 1 : myBmw.getLinks(cache, (Property) carOwner);
	}

	public void testToOneSameValueReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");

		Link myBmwMe1 = me.setLink(cache, carOwner, "value1", Statics.TARGET_POSITION, myBmw);
		Link myBmwMe2 = me.setLink(cache, carOwner, "value1", Statics.TARGET_POSITION, myBmw);
		assert myBmwMe1 == myBmwMe2;

		assert me.getLinks(cache, carOwner, Statics.TARGET_POSITION).size() == 1 : me.getLinks(cache, (Property) carOwner);
	}

	public void testToOneSameValueTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");

		final Relation carOwnerTime = car.addRelation(cache, "CarOwnerTime", owner, time);
		carOwnerTime.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Generic me = owner.newInstance(cache, "me");
		Generic today = time.newInstance(cache, "today");
		time.newInstance(cache, "yesterday");

		Link myBmwMe1 = myBmw.setLink(cache, carOwnerTime, "value1", me, today);
		Link myBmwMe2 = myBmw.setLink(cache, carOwnerTime, "value1", me, today);
		assert myBmwMe1 == myBmwMe2;
		assert myBmw.getLinks(cache, carOwnerTime).size() == 1 : myBmw.getLinks(cache, (Property) carOwnerTime);

		myBmw.setLink(cache, carOwnerTime, "value2", me, today);
		assert myBmw.getLinks(cache, carOwnerTime).size() == 1 : myBmw.getLinks(cache, (Property) carOwnerTime);
	}

	public void testToOneDifferentValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");

		Link myBmwMe1 = myBmw.setLink(cache, carOwner, "value1", me);
		Link myBmwMe2 = myBmw.setLink(cache, carOwner, "value2", me);
		assert myBmwMe1 != myBmwMe2;

		assert myBmw.getLinks(cache, carOwner).size() == 1 : myBmw.getLinks(cache, (Property) carOwner);
		assert myBmw.getLinks(cache, carOwner).findFirst("value1") == null;
		assert myBmw.getLinks(cache, carOwner).findFirst("value2") != null;
	}

	public void testToOneDifferentValueReverse() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		final Relation carOwner = car.addRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Generic me = owner.newInstance(cache, "me");
		me.setLink(cache, carOwner, "value1", Statics.TARGET_POSITION, myBmw);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				me.setLink(cache.newSuperCache(), carOwner, "value2", Statics.TARGET_POSITION, myBmw);
			}

		}.assertIsCausedBy(SingularConstraintViolationException.class);

		assert me.getLinks(cache, carOwner, Statics.TARGET_POSITION).size() == 1 : me.getLinks(cache, (Property) carOwner, Statics.TARGET_POSITION);
		assert me.getLinks(cache, carOwner, Statics.TARGET_POSITION).findFirst("value1") != null;
		assert me.getLinks(cache, carOwner, Statics.TARGET_POSITION).findFirst("value2") == null;
		cache.flush();
	}

	public void testToOneDifferentValueTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("time");
		Relation carOwnerTime = car.addRelation(cache, "CarOwnerTime", owner);
		carOwnerTime.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");
		Generic today = time.newInstance(cache, "today");

		Link myBmwMeToday1 = myBmw.setLink(cache, carOwnerTime, "value1", me, today);
		Link myBmwMeToday2 = myBmw.setLink(cache, carOwnerTime, "value2", me, today);
		assert myBmwMeToday1 != myBmwMeToday2;

		assert myBmw.getLinks(cache, carOwnerTime).size() == 1 : myBmw.getLinks(cache, (Property) carOwnerTime);
		assert myBmw.getLinks(cache, carOwnerTime).findFirst("value1") == null;
		assert myBmw.getLinks(cache, carOwnerTime).findFirst("value2") != null;
	}

	public void testOneToMany() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.addRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		Generic rearRight = tyre.newInstance(cache, "rearRight");

		myBmw.bind(cache, carTyres, frontLeft);
		myBmw.bind(cache, carTyres, frontRight);
		myBmw.bind(cache, carTyres, rearLeft);
		myBmw.bind(cache, carTyres, rearRight);

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, (Property) carTyres);
	}

	public void testOneToManyReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.addRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		Generic rearRight = tyre.newInstance(cache, "rearRight");

		frontLeft.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);
		frontRight.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);
		rearLeft.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);
		rearRight.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);

		assert frontLeft.getLinks(cache, carTyres, Statics.TARGET_POSITION).size() == 1 : frontLeft.getLinks(cache, (Property) carTyres, Statics.TARGET_POSITION);
		assert frontRight.getLinks(cache, carTyres, Statics.TARGET_POSITION).size() == 1 : frontLeft.getLinks(cache, (Property) carTyres, Statics.TARGET_POSITION);
		assert rearLeft.getLinks(cache, carTyres, Statics.TARGET_POSITION).size() == 1 : frontLeft.getLinks(cache, (Property) carTyres, Statics.TARGET_POSITION);
		assert rearRight.getLinks(cache, carTyres, Statics.TARGET_POSITION).size() == 1 : frontLeft.getLinks(cache, (Property) carTyres, Statics.TARGET_POSITION);
	}

	public void testOneToManyInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.addRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		Generic rearRight = tyre.newInstance(cache, "rearRight");
		Generic center = tyre.newInstance(cache, "center");

		Link carCenter = car.setLink(cache, carTyres, "defaultTyre", center);
		assert myBmw.getLink(cache, (Property) carTyres).getBaseComponent().equals(car);

		myBmw.bind(cache, carTyres, frontLeft);
		myBmw.bind(cache, carTyres, frontRight);
		myBmw.bind(cache, carTyres, rearLeft);
		myBmw.bind(cache, carTyres, rearRight);

		assert myBmw.getLinks(cache, carTyres).size() == 5 : myBmw.getLinks(cache, (Property) carTyres);
		assert myBmw.getLinks(cache, (Property) carTyres).contains(carCenter) : myBmw.getLinks(cache, (Property) carTyres);
	}

	public void testOneToManyInheritanceReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type person = cache.newType("person");
		Relation carPerson = car.addRelation(cache, "CarPersons", person);

		carPerson.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPerson.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic michael = person.newInstance(cache, "michael");
		Generic nicolas = person.newInstance(cache, "nicolas");
		Generic sven = person.newInstance(cache, "sven");
		Generic sofiane = person.newInstance(cache, "sofiane");
		Generic pierre = person.newInstance(cache, "pierre");

		Link carPierre = pierre.setLink(cache, carPerson, "defaultPerson", Statics.TARGET_POSITION, car);
		assert myBmw.getLink(cache, (Property) carPerson).getBaseComponent().equals(car);

		michael.bind(cache, carPerson, Statics.TARGET_POSITION, myBmw);
		nicolas.bind(cache, carPerson, Statics.TARGET_POSITION, myBmw);
		sven.bind(cache, carPerson, Statics.TARGET_POSITION, myBmw);
		sofiane.bind(cache, carPerson, Statics.TARGET_POSITION, myBmw);

		assert michael.getLinks(cache, carPerson, Statics.TARGET_POSITION).size() == 1 : michael.getLinks(cache, (Property) carPerson, Statics.TARGET_POSITION);
		assert nicolas.getLinks(cache, carPerson, Statics.TARGET_POSITION).size() == 1 : nicolas.getLinks(cache, (Property) carPerson, Statics.TARGET_POSITION);
		assert sven.getLinks(cache, carPerson, Statics.TARGET_POSITION).size() == 1 : sven.getLinks(cache, (Property) carPerson, Statics.TARGET_POSITION);
		assert sofiane.getLinks(cache, carPerson, Statics.TARGET_POSITION).size() == 1 : sofiane.getLinks(cache, (Property) carPerson, Statics.TARGET_POSITION);

		assert myBmw.getLinks(cache, (Property) carPerson).contains(carPierre) : myBmw.getLinks(cache, (Property) carPerson);

	}

	public void testOneToManyDifferentValue() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		final Relation carTyres = car.addRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);
		carTyres.enablePropertyConstraint(cache);
		assert carTyres.isPropertyConstraintEnabled(cache);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		final Generic rearRight = tyre.newInstance(cache, "rearRight");

		myBmw.bind(cache, carTyres, frontLeft);
		myBmw.bind(cache, carTyres, frontRight);
		myBmw.bind(cache, carTyres, rearLeft);

		Link myBmwRearRight1 = myBmw.setLink(cache, carTyres, "value1", rearRight);
		Link myBmwRearRight2 = myBmw.setLink(cache, carTyres, "value2", rearRight);

		assert !myBmwRearRight1.isAlive(cache);
		assert myBmwRearRight2.isAlive(cache);

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, (Property) carTyres);
		assert myBmw.getLinks(cache, carTyres).findFirst("value1") == null;
		assert myBmw.getLinks(cache, carTyres).findFirst("value2") != null;

		carTyres.disablePropertyConstraint(cache);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBmw.setLink(cache, carTyres, "value3", rearRight);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testOneToManyDifferentValueReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		final Relation carTyres = car.addRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);
		carTyres.enablePropertyConstraint(cache);
		assert carTyres.isPropertyConstraintEnabled(cache);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		final Generic rearRight = tyre.newInstance(cache, "rearRight");

		frontLeft.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);
		frontRight.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);
		rearLeft.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);

		final Link myBmwRearRight1 = rearRight.setLink(cache, carTyres, "value1", Statics.TARGET_POSITION, myBmw);
		final Link myBmwRearRight2 = rearRight.setLink(cache, carTyres, "value2", Statics.TARGET_POSITION, myBmw);

		assert !myBmwRearRight1.isAlive(cache);
		assert myBmwRearRight2.isAlive(cache);

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, (Property) carTyres);
		assert myBmw.getLinks(cache, carTyres).findFirst("value1") == null;
		assert myBmw.getLinks(cache, carTyres).findFirst("value2") != null;

		carTyres.disablePropertyConstraint(cache);

		rearRight.setLink(cache, carTyres, "value3", Statics.TARGET_POSITION, myBmw);
		assert !myBmwRearRight1.isAlive(cache);
		assert !myBmwRearRight2.isAlive(cache);
	}

	public void testOneToManySameValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.addRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		Generic rearRight = tyre.newInstance(cache, "rearRight");

		myBmw.bind(cache, carTyres, frontLeft);
		myBmw.bind(cache, carTyres, frontRight);
		myBmw.bind(cache, carTyres, rearLeft);
		Link myBmwRearRight1 = myBmw.setLink(cache, carTyres, "value1", rearRight);
		assert myBmw.getLinks(cache, carTyres).contains(myBmwRearRight1);
		Link myBmwRearRight2 = myBmw.setLink(cache, carTyres, "value1", rearRight);
		assert myBmwRearRight1 == myBmwRearRight2;

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, (Property) carTyres);
		assert myBmw.getLinks(cache, carTyres).contains(myBmwRearRight1);
	}

	public void testOneToManySameValueReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.addRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		Generic rearRight = tyre.newInstance(cache, "rearRight");

		frontLeft.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);
		frontRight.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);
		rearLeft.bind(cache, carTyres, Statics.TARGET_POSITION, myBmw);
		Link myBmwRearRight1 = rearRight.setLink(cache, carTyres, "value1", Statics.TARGET_POSITION, myBmw);
		assert myBmw.getLinks(cache, carTyres).contains(myBmwRearRight1);
		Link myBmwRearRight2 = rearRight.setLink(cache, carTyres, "value1", Statics.TARGET_POSITION, myBmw);
		assert myBmwRearRight1 == myBmwRearRight2;

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, (Property) carTyres);
		assert myBmw.getLinks(cache, carTyres).contains(myBmwRearRight1);
	}

	public void testManyToMany() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.addRelation(cache, "CarColor", color);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");

		myBmw.bind(cache, carColor, red);
		myBmw.bind(cache, carColor, blue);
		yourAudi.bind(cache, carColor, blue);
		yourAudi.bind(cache, carColor, red);

		assert myBmw.getLinks(cache, carColor).size() == 2 : myBmw.getLinks(cache, (Property) carColor);
		assert yourAudi.getLinks(cache, carColor).size() == 2 : yourAudi.getLinks(cache, (Property) carColor);
	}

	public void testManyToManyReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.addRelation(cache, "CarColor", color);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");

		red.bind(cache, carColor, Statics.TARGET_POSITION, myBmw);
		blue.bind(cache, carColor, Statics.TARGET_POSITION, myBmw);
		blue.bind(cache, carColor, Statics.TARGET_POSITION, yourAudi);
		red.bind(cache, carColor, Statics.TARGET_POSITION, yourAudi);

		assert red.getLinks(cache, carColor, Statics.TARGET_POSITION).size() == 2 : red.getLinks(cache, (Property) carColor, Statics.TARGET_POSITION);
		assert blue.getLinks(cache, carColor, Statics.TARGET_POSITION).size() == 2 : yourAudi.getLinks(cache, (Property) carColor, Statics.TARGET_POSITION);
	}

	public void testManyToManyInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Generic yourAudi = car.newInstance(cache, "yourAudi");

		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");

		Relation carColor = car.addRelation(cache, "CarColor", color);
		Link carRed = car.setLink(cache, carColor, "defaultColor", red);
		assert yourAudi.getLink(cache, (Property) carColor).getBaseComponent().equals(car);

		Link yourAudiRed = yourAudi.bind(cache, carColor, red);

		assert yourAudi.getLinks(cache, carColor).size() == 2;
		assert yourAudi.getLinks(cache, carColor).contains(carRed);
		assert yourAudi.getLinks(cache, carColor).contains(yourAudiRed);
	}

	public void testManyToManyInheritanceReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Generic yourAudi = car.newInstance(cache, "yourAudi");

		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");

		Relation carColor = car.addRelation(cache, "CarColor", color);
		Link carRed = red.setLink(cache, carColor, "defaultColor", Statics.TARGET_POSITION, car);
		assert red.getLink(cache, (Property) carColor, Statics.TARGET_POSITION).getBaseComponent().equals(car);

		Link yourAudiRed = red.bind(cache, carColor, Statics.TARGET_POSITION, yourAudi);

		assert yourAudi.getLinks(cache, carColor).size() == 2;
		assert yourAudi.getLinks(cache, carColor).contains(carRed);
		assert yourAudi.getLinks(cache, carColor).contains(yourAudiRed);
	}

	public void testManyToManyPropertyConstraint() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.addRelation(cache, "CarColor", color);

		carColor.enablePropertyConstraint(cache);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");

		myBmw.setLink(cache, carColor, "value1", red);
		Link bmwRed2 = myBmw.setLink(cache, carColor, "value2", red);

		yourAudi.bind(cache, carColor, blue);
		yourAudi.bind(cache, carColor, red);

		assert myBmw.getLinks(cache, carColor).size() == 1 : myBmw.getLinks(cache, (Property) carColor);
		assert myBmw.getLinks(cache, carColor).findFirst("value1") == null;
		assert myBmw.getLinks(cache, carColor).findFirst("value2") == bmwRed2;
		assert yourAudi.getLinks(cache, carColor).size() == 2 : yourAudi.getLinks(cache, (Property) carColor);
	}

	public void testManyToManyPropertyConstraintReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.addRelation(cache, "CarColor", color);

		carColor.enablePropertyConstraint(cache);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");

		red.setLink(cache, carColor, "value1", Statics.TARGET_POSITION, myBmw);
		Link bmwRed2 = red.setLink(cache, carColor, "value2", Statics.TARGET_POSITION, myBmw);

		blue.bind(cache, carColor, Statics.TARGET_POSITION, yourAudi);
		red.bind(cache, carColor, Statics.TARGET_POSITION, yourAudi);

		assert myBmw.getLinks(cache, carColor).size() == 1 : myBmw.getLinks(cache, (Property) carColor);
		assert myBmw.getLinks(cache, carColor).findFirst("value1") == null;
		assert myBmw.getLinks(cache, carColor).findFirst("value2") == bmwRed2;
		assert yourAudi.getLinks(cache, carColor).size() == 2 : yourAudi.getLinks(cache, (Property) carColor);

		assert red.getLinks(cache, carColor, Statics.TARGET_POSITION).findFirst("value2") == bmwRed2;
		assert blue.getLinks(cache, carColor, Statics.TARGET_POSITION).size() == 1 : blue.getLinks(cache, (Property) carColor, Statics.TARGET_POSITION);
	}

	public void testSimpleRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type sportsCar = car.newSubType(cache, "SportsCar");
		Type person = cache.newType("Person");
		Type pilot = person.newSubType(cache, "Pilot");
		Relation carDriver = car.addRelation(cache, "driver", person);
		Relation sportsCarPilot = sportsCar.addRelation(cache, "driver", pilot);
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic ourFerrari = sportsCar.newInstance(cache, "ourFerrari");

		Generic me = person.newInstance(cache, "me");
		Generic you = person.newInstance(cache, "you");
		Generic ayrton = pilot.newInstance(cache, "Ayrton");

		myBmw.bind(cache, carDriver, me);
		myBmw.bind(cache, carDriver, ayrton);

		yourAudi.bind(cache, carDriver, you);
		yourAudi.bind(cache, carDriver, ayrton);
		ourFerrari.bind(cache, carDriver, me);
		ourFerrari.bind(cache, carDriver, you);
		ourFerrari.bind(cache, sportsCarPilot, ayrton);

		assert myBmw.getTargets(cache, carDriver).contains(me);
		assert myBmw.getTargets(cache, carDriver).contains(ayrton);
		assert !myBmw.getTargets(cache, sportsCarPilot).contains(ayrton) : myBmw.getTargets(cache, carDriver);
		assert myBmw.getTargets(cache, carDriver).size() == 2;
		assert yourAudi.getTargets(cache, carDriver).contains(you);
		assert yourAudi.getTargets(cache, carDriver).contains(ayrton);
		assert yourAudi.getTargets(cache, carDriver).size() == 2;
		assert ourFerrari.getTargets(cache, carDriver).contains(me);
		assert ourFerrari.getTargets(cache, carDriver).contains(you);
		assert ourFerrari.getTargets(cache, carDriver).contains(ayrton);

	}

	public void testSimpleRelationReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type sportsCar = car.newSubType(cache, "SportsCar");

		Type person = cache.newType("Person");
		Type pilot = person.newSubType(cache, "Pilot");

		Relation carDriver = car.addRelation(cache, "driver", person);
		Relation sportsCarPilot = sportsCar.addRelation(cache, "driver", pilot);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic ourFerrari = sportsCar.newInstance(cache, "ourFerrari");

		Generic me = person.newInstance(cache, "me");
		Generic you = person.newInstance(cache, "you");
		Generic ayrton = pilot.newInstance(cache, "Ayrton");

		me.bind(cache, carDriver, Statics.TARGET_POSITION, myBmw);
		ayrton.bind(cache, carDriver, Statics.TARGET_POSITION, myBmw);

		you.bind(cache, carDriver, Statics.TARGET_POSITION, yourAudi);
		ayrton.bind(cache, carDriver, Statics.TARGET_POSITION, yourAudi);
		me.bind(cache, carDriver, Statics.TARGET_POSITION, ourFerrari);
		you.bind(cache, carDriver, Statics.TARGET_POSITION, ourFerrari);
		ayrton.bind(cache, sportsCarPilot, Statics.TARGET_POSITION, ourFerrari);

		assert myBmw.getTargets(cache, carDriver).contains(me);
		assert myBmw.getTargets(cache, carDriver).contains(ayrton);
		assert !myBmw.getTargets(cache, sportsCarPilot).contains(ayrton) : myBmw.getTargets(cache, carDriver);
		assert myBmw.getTargets(cache, carDriver).size() == 2;
		assert yourAudi.getTargets(cache, carDriver).contains(you);
		assert yourAudi.getTargets(cache, carDriver).contains(ayrton);
		assert yourAudi.getTargets(cache, carDriver).size() == 2;
		assert ourFerrari.getTargets(cache, carDriver).contains(me);
		assert ourFerrari.getTargets(cache, carDriver).contains(you);
		assert ourFerrari.getTargets(cache, carDriver).contains(ayrton);

		assert me.getTargets(cache, carDriver, Statics.TARGET_POSITION, Statics.BASE_POSITION).contains(myBmw);
		assert ayrton.getTargets(cache, carDriver, Statics.TARGET_POSITION, Statics.BASE_POSITION).contains(myBmw);
		assert !ayrton.getTargets(cache, sportsCarPilot, Statics.TARGET_POSITION, Statics.BASE_POSITION).contains(myBmw) : ayrton.getTargets(cache, carDriver);
		assert me.getTargets(cache, carDriver, Statics.TARGET_POSITION, Statics.BASE_POSITION).size() == 2;
		assert you.getTargets(cache, carDriver, Statics.TARGET_POSITION, Statics.BASE_POSITION).contains(yourAudi);
		assert ayrton.getTargets(cache, carDriver, Statics.TARGET_POSITION, Statics.BASE_POSITION).contains(yourAudi);
		assert you.getTargets(cache, carDriver, Statics.TARGET_POSITION, Statics.BASE_POSITION).size() == 2;
		assert me.getTargets(cache, carDriver, Statics.TARGET_POSITION, Statics.BASE_POSITION).contains(ourFerrari);
		assert you.getTargets(cache, carDriver, Statics.TARGET_POSITION, Statics.BASE_POSITION).contains(ourFerrari);
		assert ayrton.getTargets(cache, carDriver, Statics.TARGET_POSITION, Statics.BASE_POSITION).contains(ourFerrari);

	}

	public void testCountAncestor() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation relation = vehicle.addRelation(cache, "pilot", human);
		assert relation.getComponents().size() == 2 : relation.getComponents().size();
	}

	public void testDependency() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation relationPilot = vehicle.addRelation(cache, "pilot", human);
		Type pilot = relationPilot.getImplicit();
		assert cache.getEngine().getInheritings(cache).containsAll(Arrays.asList(vehicle, human, pilot));
	}

	public void testIsRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation relation = vehicle.addRelation(cache, "pilot", human);
		assert !relation.inheritsFrom(vehicle);
		assert !relation.inheritsFrom(human);
		assert relation.inheritsFrom(relation.getImplicit());
		assert relation.isAttributeOf(vehicle);
	}

	public void testIsTernaryRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type level = cache.newType("Level");
		Type human = cache.newType("Human");
		Relation humanGames = human.addRelation(cache, "Games", car, level);
		assert humanGames.isAttributeOf(human);
		assert humanGames.inheritsFrom(humanGames.getImplicit());
		assert !humanGames.inheritsFrom(human);
		assert !humanGames.inheritsFrom(car);
		assert !humanGames.inheritsFrom(level);
	}

	public void testOnegetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		Relation carHuman = car.addRelation(cache, "pilot", human);
		assert car.getRelations(cache).size() == 1 : car.getRelations(cache);
		assert car.getRelations(cache).contains(carHuman) : car.getRelations(cache);
	}

	public void testTwogetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		Relation pilot = car.addRelation(cache, "pilot", human);
		Relation passenger = car.addRelation(cache, "passenger", human);
		assert car.getRelations(cache).size() == 2 : car.getRelations(cache);
		assert car.getRelations(cache).contains(pilot) : car.getRelations(cache);
		assert car.getRelations(cache).contains(passenger) : car.getRelations(cache);
	}

	public void testSuperOnegetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.addRelation(cache, "pilot", human);
		assert car.getRelations(cache).size() == 1 : car.getRelations(cache);
		assert car.getRelations(cache).contains(vehicleHuman) : car.getRelations(cache);
	}

	public void testSuperTwogetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type human = cache.newType("Human");
		Relation pilot = vehicle.addRelation(cache, "pilot", human);
		Relation passenger = vehicle.addRelation(cache, "passenger", human);
		assert car.getRelations(cache).size() == 2 : car.getRelations(cache);
		assert car.getRelations(cache).contains(pilot) : car.getRelations(cache);
		assert car.getRelations(cache).contains(passenger) : car.getRelations(cache);
	}

	public void testDuplicateRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation humanPilotVehicle = vehicle.addRelation(cache, "pilot", human);
		Relation humanPilotVehicle2 = vehicle.addRelation(cache, "pilot", human);
		assert vehicle.getStructurals(cache).contains(humanPilotVehicle);
		assert humanPilotVehicle == humanPilotVehicle2;
	}

	public void testGetRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation humanPilotVehicle = human.addRelation(cache, "pilot", vehicle);
		Relation getRelation = human.getRelation(cache, "pilot");
		assert getRelation != null;
		assert getRelation.equals(humanPilotVehicle);
		assert human.getRelation(cache, "passenger") == null;
	}

	public void testGetRelationWithTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type bike = cache.newType("Bike");
		Type human = cache.newType("Human");
		Relation humanPilot = human.addRelation(cache, "pilot", vehicle, bike);
		Relation getRelation = human.getRelation(cache, "pilot");
		assert human.getAttribute(cache, "pilot") == null;
		assert getRelation != null;
		assert getRelation.equals(humanPilot);
		assert human.getRelation(cache, "passenger") == null;
	}

	// public void testSubRelation() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache, "Car");
	// Type human = cache.newType("Human");
	// Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);
	// Relation possessCar = human.addSubRelation(cache, possessVehicle, "HumanPossessCar", car);
	// assert human.getRelations(cache).size() == 1;
	// assert human.getRelations(cache).contains(possessCar);
	// assert possessCar.inheritsFrom(possessVehicle);
	// }

	// public void testSubRelationSymetric() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType(cache, "Car");
	// Type human = cache.newType("Human");
	// Type man = human.newSubType(cache, "Man");
	// Relation humanPossessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);
	// Relation manPossessCar = man.addSubRelation(cache, humanPossessVehicle, "ManPossessCar", car);
	// assert human.getRelations(cache).size() == 1;
	// assert human.getRelations(cache).contains(humanPossessVehicle);
	// assert man.getRelations(cache).size() == 1;
	// assert man.getRelations(cache).contains(manPossessCar);
	// assert manPossessCar.inheritsFrom(humanPossessVehicle);
	// }

	public void testTargetsAncestor() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);
		assert possessVehicle.getTargetComponent().equals(vehicle);
	}

	public void testTargetsAncestorWithMultipleTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle, human);
		assert possessVehicle.getTargetComponent().equals(vehicle);
		assert possessVehicle.getComponent(Statics.SECOND_TARGET_POSITION).equals(human);
	}

	public void testUnidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);
		assert human.getRelations(cache).size() == 1;
		assert human.getRelations(cache).contains(possessVehicle);
		assert human.getRelation(cache, "HumanPossessVehicle").equals(possessVehicle);
		assert vehicle.getRelation(cache, "HumanPossessVehicle") == null;
	}

	public void testBidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);

		Snapshot<Relation> vehicleRelations = vehicle.getRelations(cache);
		possessVehicle.enableMultiDirectional(cache);
		assert human.getRelations(cache).contains(possessVehicle);
		assert vehicleRelations.contains(possessVehicle) : vehicleRelations + " " + possessVehicle;
	}

	public void testRelationToHimself() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Relation brother = human.addRelation(cache, "brother", human);
		assert human.getRelations(cache).size() == 1;
		assert human.getRelations(cache).contains(brother);
		assert human.getRelation(cache, "brother").equals(brother);
	}

	public void testGetLinkFromTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.addRelation(cache, "HumanPossessVehicle", vehicle);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic myHuman = human.newInstance(cache, "myHuman");
		Link possession = myHuman.addLink(cache, possessVehicle, "possession", myVehicle);
		assert possessVehicle.getTargetComponent().equals(vehicle);

		assert myVehicle.getLinks(cache, possessVehicle, 1).size() == 1;
		assert myHuman.getLinks(cache, possessVehicle, Statics.BASE_POSITION).size() == 1;
		assert myVehicle.getLinks(cache, possessVehicle, Statics.TARGET_POSITION).contains(possession);
		assert myHuman.getLinks(cache, possessVehicle, Statics.BASE_POSITION).contains(possession);
	}

	public void testGetLinkFromTarget2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type road = cache.newType("Road");
		Relation drivesOn = human.addRelation(cache, "DrivingOn", vehicle, road);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic myHuman = human.newInstance(cache, "myHuman");
		Generic myRoad = road.newInstance(cache, "myRoad");
		Link driving = myHuman.addLink(cache, drivesOn, "myDrivingOn", myVehicle, myRoad);
		assert drivesOn.getTargetComponent().equals(vehicle);

		assert myRoad.getLinks(cache, drivesOn, Statics.SECOND_TARGET_POSITION).size() == 1;
		assert myVehicle.getLinks(cache, drivesOn, Statics.TARGET_POSITION).size() == 1;
		assert myHuman.getLinks(cache, drivesOn, Statics.BASE_POSITION).size() == 1;
		assert myRoad.getLinks(cache, drivesOn, Statics.SECOND_TARGET_POSITION).contains(driving);
		assert myVehicle.getLinks(cache, drivesOn, Statics.TARGET_POSITION).contains(driving);
		assert myHuman.getLinks(cache, drivesOn, Statics.BASE_POSITION).contains(driving);
	}

	public void testDefaultReverseLinks() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance(cache, "myBmw");

		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");
		color.newInstance(cache, "blue");

		Relation carColor = car.addRelation(cache, "carColor", color);
		car.bind(cache, carColor, red);
		myBmw.bind(cache, carColor, red);

		assert red.getLinks(cache, carColor, Statics.TARGET_POSITION).size() == 3 : red.getLinks(cache, carColor, Statics.TARGET_POSITION);
		assert false : red.getTargets(cache, carColor, Statics.TARGET_POSITION, Statics.BASE_POSITION);
	}

	public void testDiamantKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type person = cache.newType("Person");
		final Property age = person.addProperty(cache, "Age");
		person.setValue(cache, age, "25");

		final Type student = person.newSubType(cache, "Student");
		student.setValue(cache, age, "30");

		final Type teacher = person.newSubType(cache, "Teacher");
		teacher.setValue(cache, age, "20");

		new RollbackCatcher() {
			@Override
			public void intercept() {
				cache.newSubType("doctoral", student, teacher).getValue(cache, age);
			}
		}.assertIsCausedBy(IllegalStateException.class);

	}

	public void testDiamantOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type person = cache.newType("Person");
		Property age = person.addProperty(cache, "Age");
		person.setValue(cache, age, "25");

		Type student = person.newSubType(cache, "Student");
		student.setValue(cache, age, "30");

		Type teacher = person.newSubType(cache, "Teacher");

		assert "30" == cache.newSubType("doctoral", student, teacher).getValue(cache, age);
	}

}
