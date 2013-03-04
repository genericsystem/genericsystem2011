package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Statics;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
@SuppressWarnings({ "unchecked", "rawtypes" })
public class RelationTest extends AbstractTest {

	public void test() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");
		Generic yellow = color.newInstance(cache, "yellow");
		Relation carColor = car.setRelation(cache, "carColor", color);
		Link carRed = car.bind(cache, carColor, red);
		assert carRed.inheritsFrom(carColor);
		Link myBmwYellow = myBmw.bind(cache, carColor, yellow);
		assert !myBmwYellow.inheritsFrom(carRed);
		assert myBmw.getLinks(cache, carColor).size() == 2;
		assert myBmw.getLinks(cache, carColor).contains(myBmwYellow);
		assert myBmw.getLinks(cache, carColor).contains(carRed);
	}

	public void test2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");
		Generic yellow = color.newInstance(cache, "yellow");
		Relation carColor = car.setRelation(cache, "carColor", color);
		carColor.enableSingularConstraint(cache);
		Link carRed = car.bind(cache, carColor, red);
		assert carRed.inheritsFrom(carColor);
		Link myBmwYellow = myBmw.bind(cache, carColor, yellow);
		assert !myBmwYellow.inheritsFrom(carRed);
		assert myBmw.getLinks(cache, carColor).size() == 1;
		assert myBmw.getLinks(cache, carColor).contains(myBmwYellow);
		assert !myBmw.getLinks(cache, carColor).contains(carRed);
	}

	public void testToOneOverride() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "driver", color);
		carColor.enableSingularConstraint(cache);
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic myAudi = car.newInstance(cache, "myAudi");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");
		Link carRed = car.bind(cache, carColor, red);
		((GenericImpl) carRed).deduct(cache);
		assert myBmw.getTargets(cache, carColor).contains(red) : myBmw.getTargets(cache, carColor);
		assert myAudi.getTargets(cache, carColor).contains(red);
		myBmw.bind(cache, carColor, blue);
		assert car.getLinks(cache, carColor).size() == 1;
		assert car.getLinks(cache, carColor).first().getTargetComponent().equals(red);

		assert myBmw.getLinks(cache, carColor).size() == 1;
		assert myBmw.getLinks(cache, carColor).first().getTargetComponent().equals(blue);
		assert blue.getLinks(cache, carColor).size() == 1;
		assert myBmw.equals(blue.getLinks(cache, carColor).first().getBaseComponent());
		assert red.getLinks(cache, carColor).size() == 1 : red.getLinks(cache, carColor);
		assert myAudi.equals(red.getLinks(cache, carColor).first().getBaseComponent()) : red.getLinks(cache, carColor);
	}

	public void testReverseTernaryAccess() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type passenger = cache.newType("Passenger");
		Type time = cache.newType("time");

		final Relation carPassengerTime = car.setRelation(cache, "CarPassengerTime", passenger, time);
		carPassengerTime.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPassengerTime.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Generic michael = passenger.newInstance(cache, "michael");
		passenger.newInstance(cache, "nicolas");
		Generic today = time.newInstance(cache, "today");
		Generic yesterday = time.newInstance(cache, "yesterday");
		Generic yourAudi = car.newInstance(cache, "yourAudi");

		michael.bind(cache, carPassengerTime, myBmw, today);
		assert michael.getLinks(cache, carPassengerTime).size() == 1;
		michael.bind(cache, carPassengerTime, yourAudi, today);
		assert michael.getLinks(cache, carPassengerTime).size() == 1;

		yesterday.bind(cache, carPassengerTime, myBmw, passenger);
		assert yesterday.getLinks(cache, carPassengerTime).size() == 1;

	}

	public void testSimple() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type person = cache.newType("Person");
		Relation carDriver = car.setRelation(cache, "driver", person);
		carDriver.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = person.newInstance(cache, "me");
		Generic you = person.newInstance(cache, "you");
		myBmw.bind(cache, carDriver, me);
		myBmw.bind(cache, carDriver, you);
		assert myBmw.getLinks(cache, carDriver).size() == 2 : myBmw.getLinks(cache, carDriver);
	}

	public void testSimpleReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type person = cache.newType("Person");
		Relation carDriver = car.setRelation(cache, "driver", person);
		carDriver.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = person.newInstance(cache, "me");
		me.bind(cache, carDriver, myBmw);
		assert me.getLinks(cache, carDriver).size() == 1;
	}

	public void testSimpleTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type person = cache.newType("Person");
		Type time = cache.newType("Time");
		Relation carDriverTime = car.setRelation(cache, "driver", person, time);
		carDriverTime.enableSingularConstraint(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = person.newInstance(cache, "me");
		Generic you = person.newInstance(cache, "you");
		Generic myTime = time.newInstance(cache, "myTime");
		Generic yourTime = time.newInstance(cache, "yourTime");

		myBmw.bind(cache, carDriverTime, me, myTime);
		myBmw.bind(cache, carDriverTime, you, yourTime);

		assert myBmw.getLinks(cache, carDriverTime).size() == 2 : myBmw.getLinks(cache, carDriverTime);
		assert carDriverTime.getComponents().get(Statics.BASE_POSITION).equals(car);
		assert carDriverTime.getComponents().get(Statics.TARGET_POSITION).equals(person);
		assert carDriverTime.getComponents().get(Statics.SECOND_TARGET_POSITION).equals(time);

		assert carDriverTime.getBaseComponent().equals(car);
	}

	public void testOneToManyManyToManyImpl() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type passenger = cache.newType("Passenger");
		final Relation carPassenger = car.setRelation(cache, "CarPassenger", passenger);
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

		final Relation carPassenger = car.setRelation(cache, "CarPassenger", passenger);
		carPassenger.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPassenger.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");

		final Generic michael = passenger.newInstance(cache, "michael");
		Generic nicolas = passenger.newInstance(cache, "nicolas");

		Link link30 = michael.setLink(cache, carPassenger, "30%", myBmw);
		Link link40 = nicolas.setLink(cache, carPassenger, "40%", myBmw);
		assert link30.isAlive(cache);
		michael.setLink(cache, carPassenger, "60%", myBmw);
		assert !link30.isAlive(cache);
		assert link40.isAlive(cache);
	}

	public void testOneToManyManyToManyImplTernary() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type passenger = cache.newType("Passenger");
		Type time = cache.newType("time");

		final Relation carPassengerTime = car.setRelation(cache, "CarPassenger", passenger, time);
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

		Relation carOwner = car.setRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");

		Generic sven = owner.newInstance(cache, "sven");

		myBmw.bind(cache, carOwner, sven);
		yourAudi.bind(cache, carOwner, sven);

		assert myBmw.getLinks(cache, carOwner).size() == 1 : myBmw.getLinks(cache, carOwner);
		assert yourAudi.getLinks(cache, carOwner).size() == 1 : yourAudi.getLinks(cache, carOwner);
	}

	public void testToOneReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic sven = owner.newInstance(cache, "sven");

		sven.bind(cache, carOwner, myBmw);
		sven.bind(cache, carOwner, yourAudi);

		assert sven.getLinks(cache, carOwner).size() == 2 : sven.getLinks(cache, carOwner);
	}

	public void testToOneTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");

		final Relation carOwnerTime = car.setRelation(cache, "CarOwnerTime", owner, time);
		carOwnerTime.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic nicolas = owner.newInstance(cache, "nicolas");
		final Generic michael = owner.newInstance(cache, "michael");
		Generic today = time.newInstance(cache, "today");
		final Generic yesterday = time.newInstance(cache, "yesterday");

		Link myBmwNicolasToday = myBmw.bind(cache, carOwnerTime, nicolas, today);
		assert myBmw.getLinks(cache, carOwnerTime).size() == 1 : myBmw.getLinks(cache, carOwnerTime);
		Link myBmwMichaelYesterday = myBmw.bind(cache, carOwnerTime, michael, yesterday);

		assert myBmw.getLinks(cache, carOwnerTime).contains(myBmwMichaelYesterday);
		assert !myBmw.getLinks(cache, carOwnerTime).contains(myBmwNicolasToday);
	}

	public void testToOneInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");

		Type owner = cache.newType("Owner");

		Relation carOwner = car.setRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");

		Generic me = owner.newInstance(cache, "me");
		Generic you = owner.newInstance(cache, "you");

		Link carMe = car.setLink(cache, carOwner, "defaultOwner", me);
		assert myBmw.getLink(cache, carOwner).getBaseComponent().equals(car);

		Link carYou = myBmw.bind(cache, carOwner, you);

		assert myBmw.getLink(cache, carOwner).equals(carYou) : myBmw.getLinks(cache, carOwner);
		assert !myBmw.getLinks(cache, carOwner).contains(carMe) : myBmw.getLinks(cache, carOwner);
	}

	public void testToOneInheritanceReverse() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");

		final Relation carOwner = car.setRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");
		final Generic you = owner.newInstance(cache, "you");

		me.setLink(cache, carOwner, "defaultOwner", car);
		assert myBmw.getLink(cache, carOwner).getBaseComponent().equals(car);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				you.bind(cache, carOwner, myBmw);
			}

		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testToOneInheritanceTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");

		Relation carOwnerTime = car.setRelation(cache, "CarOwnerTime", owner, time);
		carOwnerTime.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");
		Generic you = owner.newInstance(cache, "you");
		Generic today = time.newInstance(cache, "today");
		Generic yesterday = time.newInstance(cache, "yesterday");

		Link carMeToday = car.setLink(cache, carOwnerTime, "defaultOwner", me, today);
		assert myBmw.getLink(cache, carOwnerTime).getBaseComponent().equals(car);

		Link carYouYesterday = myBmw.bind(cache, carOwnerTime, you, yesterday);

		assert myBmw.getLink(cache, carOwnerTime).equals(carYouYesterday) : myBmw.getLinks(cache, carOwnerTime);
		assert !myBmw.getLinks(cache, carOwnerTime).contains(carMeToday) : myBmw.getLinks(cache, carOwnerTime);
	}

	public void testToOneNewTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation(cache, "CarOwner", owner);
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
		assert myBmw.getLinks(cache, carOwner).size() == 1 : myBmw.getLinks(cache, carOwner);
	}

	public void testToOneNewTargetReverse() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		final Relation carOwner = car.setRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");
		final Generic you = owner.newInstance(cache, "you");

		Link myBmwMe = me.bind(cache, carOwner, myBmw);
		assert me.getLinks(cache, carOwner).contains(myBmwMe);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				you.bind(cache, carOwner, myBmw);
			}

		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testToOneNewTargetTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");
		Relation carOwnerTime = car.setRelation(cache, "CarOwner", owner, time);
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
		assert myBmw.getLinks(cache, carOwnerTime).size() == 1 : myBmw.getLinks(cache, carOwnerTime);
	}

	public void testToOneSameValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");

		Link myBmwMe1 = myBmw.setLink(cache, carOwner, "value1", me);
		Link myBmwMe2 = myBmw.setLink(cache, carOwner, "value1", me);
		assert myBmwMe1 == myBmwMe2;

		assert myBmw.getLinks(cache, carOwner).size() == 1 : myBmw.getLinks(cache, carOwner);
	}

	public void testToOneSameValueReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");

		Link myBmwMe1 = me.setLink(cache, carOwner, "value1", myBmw);
		Link myBmwMe2 = me.setLink(cache, carOwner, "value1", myBmw);
		assert myBmwMe1 == myBmwMe2;

		assert me.getLinks(cache, carOwner).size() == 1 : me.getLinks(cache, carOwner);
	}

	public void testToOneSameValueTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");

		final Relation carOwnerTime = car.setRelation(cache, "CarOwnerTime", owner, time);
		carOwnerTime.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Generic me = owner.newInstance(cache, "me");
		Generic today = time.newInstance(cache, "today");
		time.newInstance(cache, "yesterday");

		Link myBmwMe1 = myBmw.setLink(cache, carOwnerTime, "value1", me, today);
		Link myBmwMe2 = myBmw.setLink(cache, carOwnerTime, "value1", me, today);
		assert myBmwMe1 == myBmwMe2;
		assert myBmw.getLinks(cache, carOwnerTime).size() == 1 : myBmw.getLinks(cache, carOwnerTime);

		myBmw.setLink(cache, carOwnerTime, "value2", me, today);
		assert myBmw.getLinks(cache, carOwnerTime).size() == 1 : myBmw.getLinks(cache, carOwnerTime);
	}

	public void testToOneDifferentValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic me = owner.newInstance(cache, "me");

		Link myBmwMe1 = myBmw.setLink(cache, carOwner, "value1", me);
		Link myBmwMe2 = myBmw.setLink(cache, carOwner, "value2", me);
		assert myBmwMe1 != myBmwMe2;

		assert myBmw.getLinks(cache, carOwner).size() == 1 : myBmw.getLinks(cache, carOwner);
		assert equals((Snapshot) myBmw.getLinks(cache, carOwner), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(cache, carOwner), "value2") != null;

	}

	private Generic equals(Snapshot<Generic> snapshot, final Serializable value) {
		return snapshot.filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return Objects.equals(element.getValue(), value);
			}
		}).get(0);
	}

	public void testToOneDifferentValueReverse() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		final Relation carOwner = car.setRelation(cache, "CarOwner", owner);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Generic me = owner.newInstance(cache, "me");
		me.setLink(cache, carOwner, "value1", myBmw);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				me.setLink(cache.newSuperCache(), carOwner, "value2", myBmw);
			}

		}.assertIsCausedBy(SingularConstraintViolationException.class);

		assert me.getLinks(cache, carOwner).size() == 1 : me.getLinks(cache, carOwner);
		assert equals((Snapshot) myBmw.getLinks(cache, carOwner), "value1") != null;
		assert equals((Snapshot) myBmw.getLinks(cache, carOwner), "value2") == null;
		cache.flush();
	}

	public void testOneToMany() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation(cache, "CarTyres", tyre);

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

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, carTyres);
	}

	public void testOneToManyReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		Generic rearRight = tyre.newInstance(cache, "rearRight");

		frontLeft.bind(cache, carTyres, myBmw);
		frontRight.bind(cache, carTyres, myBmw);
		rearLeft.bind(cache, carTyres, myBmw);
		rearRight.bind(cache, carTyres, myBmw);

		assert frontLeft.getLinks(cache, carTyres).size() == 1 : frontLeft.getLinks(cache, carTyres);
		assert frontRight.getLinks(cache, carTyres).size() == 1 : frontLeft.getLinks(cache, carTyres);
		assert rearLeft.getLinks(cache, carTyres).size() == 1 : frontLeft.getLinks(cache, carTyres);
		assert rearRight.getLinks(cache, carTyres).size() == 1 : frontLeft.getLinks(cache, carTyres);
	}

	public void testOneToManyInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		Generic rearRight = tyre.newInstance(cache, "rearRight");
		Generic center = tyre.newInstance(cache, "center");

		car.setLink(cache, carTyres, "defaultTyre", center);
		assert myBmw.getLink(cache, carTyres).getBaseComponent().equals(car);

		myBmw.bind(cache, carTyres, frontLeft);
		myBmw.bind(cache, carTyres, frontRight);
		myBmw.bind(cache, carTyres, rearLeft);
		myBmw.bind(cache, carTyres, rearRight);

		assert myBmw.getLinks(cache, carTyres).size() == 5 : myBmw.getLinks(cache, carTyres);
	}

	public void testSingularTargetDefaultColor() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		Type color = cache.newType("Color");

		final Generic myBmw = car.newInstance(cache, "myBmw");
		final Generic myAudi = car.newInstance(cache, "myAudi");
		final Generic red = color.newInstance(cache, "red");

		final Relation carColor = car.setRelation(cache, "CarColor", color);
		carColor.enableSingularConstraint(cache, Statics.TARGET_POSITION);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				Generic carRed = car.bind(cache, carColor, red);
				((GenericImpl) carRed).deduct(cache);
				// assert red.getLinks(cache, carColor, Statics.TARGET_POSITION).size() == 2 : red.getLinks(cache, carColor, Statics.TARGET_POSITION);
				// assert red.getTargets(cache, carColor, Statics.BASE_POSITION).contains(myBmw);
				// assert red.getTargets(cache, carColor, Statics.BASE_POSITION).contains(myAudi);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testOneToManyInheritanceReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type person = cache.newType("person");
		Relation carPerson = car.setRelation(cache, "CarPersons", person);

		carPerson.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPerson.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");

		// Generic michael = person.newInstance(cache, "michael");
		// Generic nicolas = person.newInstance(cache, "nicolas");
		// Generic sven = person.newInstance(cache, "sven");
		// Generic sofiane = person.newInstance(cache, "sofiane");
		Generic pierre = person.newInstance(cache, "pierre");

		Link carPierre = pierre.setLink(cache, carPerson, "defaultPerson", car);
		Generic myAudi = car.newInstance(cache, "myAudi");
		log.info("" + pierre.getLinks(cache, carPerson));
		// should throw a singular constraint exception

		// assert myBmw.getLink(cache, carPerson).getBaseComponent().equals(car);
		// assert false : "" + michael.getLinks(cache, carPerson, Statics.TARGET_POSITION);
		// Link myBmwMichael = michael.bind(cache, carPerson, myBmw);
		// assert false : carPerson.getAllInstances(cache);
		// Link myBmwNicolas = nicolas.bind(cache, carPerson, myBmw);
		// Link myBmwSven = sven.bind(cache, carPerson, myBmw);
		// Link myBmwSofiane = sofiane.bind(cache, carPerson, myBmw);
		//
		// assert michael.getLinks(cache, carPerson, Statics.TARGET_POSITION).size() == 1 : michael.getLinks(cache, carPerson, Statics.TARGET_POSITION);
		// assert nicolas.getLinks(cache, carPerson, Statics.TARGET_POSITION).size() == 1 : nicolas.getLinks(cache, carPerson, Statics.TARGET_POSITION);
		// assert sven.getLinks(cache, carPerson, Statics.TARGET_POSITION).size() == 1 : sven.getLinks(cache, carPerson, Statics.TARGET_POSITION);
		// assert sofiane.getLinks(cache, carPerson, Statics.TARGET_POSITION).size() == 1 : sofiane.getLinks(cache, carPerson, Statics.TARGET_POSITION);
		//
		// assert myBmw.getLinks(cache, carPerson).contains(carPierre) : myBmw.getLinks(cache, carPerson);
		// assert myBmw.getLinks(cache, carPerson).contains(myBmwMichael) : myBmw.getLinks(cache, carPerson);
		// assert myBmw.getLinks(cache, carPerson).contains(myBmwNicolas) : myBmw.getLinks(cache, carPerson);
		// assert myBmw.getLinks(cache, carPerson).contains(myBmwSven) : myBmw.getLinks(cache, carPerson);
		// assert myBmw.getLinks(cache, carPerson).contains(myBmwSofiane) : myBmw.getLinks(cache, carPerson);
	}

	public void testOneToManyInheritanceReverse2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type person = cache.newType("person");
		Relation carPerson = car.setRelation(cache, "CarPersons", person);

		carPerson.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPerson.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic michael = person.newInstance(cache, "michael");
		Generic nicolas = person.newInstance(cache, "nicolas");
		Generic sven = person.newInstance(cache, "sven");
		Generic sofiane = person.newInstance(cache, "sofiane");
		Generic pierre = person.newInstance(cache, "pierre");

		Link carPierre = pierre.setLink(cache, carPerson, "defaultPerson", car);
		assert myBmw.getLink(cache, carPerson).getBaseComponent().equals(car);

		Link myBmwMichael = michael.bind(cache, carPerson, myBmw);
		Link myBmwNicolas = nicolas.bind(cache, carPerson, myBmw);
		Link myBmwSven = sven.bind(cache, carPerson, myBmw);
		Link myBmwSofiane = sofiane.bind(cache, carPerson, myBmw);

		assert michael.getLinks(cache, carPerson).size() == 1 : michael.getLinks(cache, carPerson);
		assert nicolas.getLinks(cache, carPerson).size() == 1 : nicolas.getLinks(cache, carPerson);
		assert sven.getLinks(cache, carPerson).size() == 1 : sven.getLinks(cache, carPerson);
		assert sofiane.getLinks(cache, carPerson).size() == 1 : sofiane.getLinks(cache, carPerson);

		assert myBmw.getLinks(cache, carPerson).contains(carPierre) : myBmw.getLinks(cache, carPerson);
		assert myBmw.getLinks(cache, carPerson).contains(myBmwMichael) : myBmw.getLinks(cache, carPerson);
		assert myBmw.getLinks(cache, carPerson).contains(myBmwNicolas) : myBmw.getLinks(cache, carPerson);
		assert myBmw.getLinks(cache, carPerson).contains(myBmwSven) : myBmw.getLinks(cache, carPerson);
		assert myBmw.getLinks(cache, carPerson).contains(myBmwSofiane) : myBmw.getLinks(cache, carPerson);
	}

	public void testOneToManyDifferentValue() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		final Relation carTyres = car.setRelation(cache, "CarTyres", tyre);

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

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, carTyres);
		assert equals((Snapshot) myBmw.getLinks(cache, carTyres), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(cache, carTyres), "value2") != null;

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
		final Relation carTyres = car.setRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);
		carTyres.enablePropertyConstraint(cache);
		assert carTyres.isPropertyConstraintEnabled(cache);

		final Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		final Generic rearRight = tyre.newInstance(cache, "rearRight");

		frontLeft.bind(cache, carTyres, myBmw);
		frontRight.bind(cache, carTyres, myBmw);
		rearLeft.bind(cache, carTyres, myBmw);

		final Link myBmwRearRight1 = rearRight.setLink(cache, carTyres, "value1", myBmw);
		final Link myBmwRearRight2 = rearRight.setLink(cache, carTyres, "value2", myBmw);

		assert !myBmwRearRight1.isAlive(cache);
		assert myBmwRearRight2.isAlive(cache);

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, carTyres);
		assert equals((Snapshot) myBmw.getLinks(cache, carTyres), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(cache, carTyres), "value2") != null;

		carTyres.disablePropertyConstraint(cache);

		rearRight.setLink(cache, carTyres, "value3", myBmw);
		assert !myBmwRearRight1.isAlive(cache);
		assert !myBmwRearRight2.isAlive(cache);
	}

	public void testOneToManySameValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation(cache, "CarTyres", tyre);

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

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, carTyres);
		assert myBmw.getLinks(cache, carTyres).contains(myBmwRearRight1);
	}

	public void testOneToManySameValueReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation(cache, "CarTyres", tyre);

		carTyres.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic frontLeft = tyre.newInstance(cache, "frontLeft");
		Generic frontRight = tyre.newInstance(cache, "frontRight");
		Generic rearLeft = tyre.newInstance(cache, "rearLeft");
		Generic rearRight = tyre.newInstance(cache, "rearRight");

		frontLeft.bind(cache, carTyres, myBmw);
		frontRight.bind(cache, carTyres, myBmw);
		rearLeft.bind(cache, carTyres, myBmw);
		Link myBmwRearRight1 = rearRight.setLink(cache, carTyres, "value1", myBmw);
		assert myBmw.getLinks(cache, carTyres).contains(myBmwRearRight1);
		log.info("zzzzzzzzzzzzzzzzzzzzzzzzz");
		Link myBmwRearRight2 = rearRight.setLink(cache, carTyres, "value1", myBmw);
		assert myBmwRearRight1 == myBmwRearRight2;

		assert myBmw.getLinks(cache, carTyres).size() == 4 : myBmw.getLinks(cache, carTyres);
		assert myBmw.getLinks(cache, carTyres).contains(myBmwRearRight1);
	}

	public void testManyToMany() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");

		myBmw.bind(cache, carColor, red);
		myBmw.bind(cache, carColor, blue);
		yourAudi.bind(cache, carColor, blue);
		yourAudi.bind(cache, carColor, red);

		assert myBmw.getLinks(cache, carColor).size() == 2 : myBmw.getLinks(cache, carColor);
		assert yourAudi.getLinks(cache, carColor).size() == 2 : yourAudi.getLinks(cache, carColor);
	}

	public void testManyToManyReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");

		red.bind(cache, carColor, myBmw);
		blue.bind(cache, carColor, myBmw);
		blue.bind(cache, carColor, yourAudi);
		red.bind(cache, carColor, yourAudi);

		assert red.getLinks(cache, carColor).size() == 2 : red.getLinks(cache, carColor);
		assert blue.getLinks(cache, carColor).size() == 2 : yourAudi.getLinks(cache, carColor);
	}

	public void testManyToManyInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic yourAudi = car.newInstance(cache, "yourAudi");

		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");

		Relation carColor = car.setRelation(cache, "CarColor", color);
		Link carRed = car.setLink(cache, carColor, "defaultColor", red);
		assert yourAudi.getLink(cache, carColor).getBaseComponent().equals(car);

		Link yourAudiRed = yourAudi.bind(cache, carColor, red);

		assert yourAudi.getLinks(cache, carColor).size() == 2 : yourAudi.getLinks(cache, carColor);
		assert yourAudi.getLinks(cache, carColor).contains(carRed);
		assert yourAudi.getLinks(cache, carColor).contains(yourAudiRed);
	}

	public void testManyToManyInheritanceReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");

		Relation carColor = car.setRelation(cache, "CarColor", color);
		Link carRed = red.setLink(cache, carColor, "defaultColor", car);
		log.info("" + red.getLink(cache, carColor) + " " + carColor.getAllInstances(cache));
		assert red.getLink(cache, carColor).getBaseComponent().equals(car);

		Link yourAudiRed = red.bind(cache, carColor, yourAudi);

		assert yourAudi.getLinks(cache, carColor).size() == 2;
		assert yourAudi.getLinks(cache, carColor).contains(carRed);
		assert yourAudi.getLinks(cache, carColor).contains(yourAudiRed);
	}

	public void testManyToManyPropertyConstraint() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);

		carColor.enablePropertyConstraint(cache);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");

		myBmw.setLink(cache, carColor, "value1", red);
		Link bmwRed2 = myBmw.setLink(cache, carColor, "value2", red);

		yourAudi.bind(cache, carColor, blue);
		yourAudi.bind(cache, carColor, red);

		assert myBmw.getLinks(cache, carColor).size() == 1 : myBmw.getLinks(cache, carColor);
		assert equals((Snapshot) myBmw.getLinks(cache, carColor), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(cache, carColor), "value2") == bmwRed2;
		assert yourAudi.getLinks(cache, carColor).size() == 2 : yourAudi.getLinks(cache, carColor);
	}

	public void testManyToManyPropertyConstraintReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation(cache, "CarColor", color);

		carColor.enablePropertyConstraint(cache);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");

		red.setLink(cache, carColor, "value1", myBmw);
		Link bmwRed2 = red.setLink(cache, carColor, "value2", myBmw);

		blue.bind(cache, carColor, yourAudi);
		red.bind(cache, carColor, yourAudi);

		assert myBmw.getLinks(cache, carColor).size() == 1 : myBmw.getLinks(cache, carColor);
		assert equals((Snapshot) myBmw.getLinks(cache, carColor), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(cache, carColor), "value2") == bmwRed2;
		assert yourAudi.getLinks(cache, carColor).size() == 2 : yourAudi.getLinks(cache, carColor);

		assert equals((Snapshot) red.getLinks(cache, carColor), "value2") == bmwRed2;
		assert blue.getLinks(cache, carColor).size() == 1 : blue.getLinks(cache, carColor);
	}

	public void testSimpleRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type car = cache.newType("Car");
		Type sportsCar = car.newSubType(cache, "SportsCar");
		Type person = cache.newType("Person");
		Type pilot = person.newSubType(cache, "Pilot");
		Relation carDriver = car.setRelation(cache, "driver", person);
		Relation sportsCarPilot = sportsCar.setRelation(cache, "driver", pilot);
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

		Relation carDriver = car.setRelation(cache, "driver", person);
		Relation sportsCarPilot = sportsCar.setRelation(cache, "driver", pilot);

		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic yourAudi = car.newInstance(cache, "yourAudi");
		Generic ourFerrari = sportsCar.newInstance(cache, "ourFerrari");

		Generic me = person.newInstance(cache, "me");
		Generic you = person.newInstance(cache, "you");
		Generic ayrton = pilot.newInstance(cache, "Ayrton");

		me.bind(cache, carDriver, myBmw);
		ayrton.bind(cache, carDriver, myBmw);

		you.bind(cache, carDriver, yourAudi);
		ayrton.bind(cache, carDriver, yourAudi);
		me.bind(cache, carDriver, ourFerrari);
		you.bind(cache, carDriver, ourFerrari);
		ayrton.bind(cache, sportsCarPilot, ourFerrari);

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

		assert me.getTargets(cache, carDriver, Statics.BASE_POSITION).contains(myBmw);
		assert ayrton.getTargets(cache, carDriver, Statics.BASE_POSITION).contains(myBmw);
		assert !ayrton.getTargets(cache, sportsCarPilot, Statics.BASE_POSITION).contains(myBmw) : ayrton.getTargets(cache, carDriver);
		assert me.getTargets(cache, carDriver, Statics.BASE_POSITION).size() == 2;
		assert you.getTargets(cache, carDriver, Statics.BASE_POSITION).contains(yourAudi);
		assert ayrton.getTargets(cache, carDriver, Statics.BASE_POSITION).contains(yourAudi);
		assert you.getTargets(cache, carDriver, Statics.BASE_POSITION).size() == 2;
		assert me.getTargets(cache, carDriver, Statics.BASE_POSITION).contains(ourFerrari);
		assert you.getTargets(cache, carDriver, Statics.BASE_POSITION).contains(ourFerrari);
		assert ayrton.getTargets(cache, carDriver, Statics.BASE_POSITION).contains(ourFerrari);

	}

	public void testCountAncestor() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation relation = vehicle.setRelation(cache, "pilot", human);
		assert relation.getComponents().size() == 2 : relation.getComponents().size();
	}

	public void testDependency() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation relationPilot = vehicle.setRelation(cache, "pilot", human);
		Type pilot = relationPilot.getImplicit();
		assert cache.getEngine().getInheritings(cache).containsAll(Arrays.asList(vehicle, human, pilot));
	}

	public void testIsRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation relation = vehicle.setRelation(cache, "pilot", human);
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
		Relation humanGames = human.setRelation(cache, "Games", car, level);
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
		Relation carHuman = car.setRelation(cache, "pilot", human);
		assert car.getRelations(cache).size() == 1 : car.getRelations(cache);
		assert car.getRelations(cache).contains(carHuman) : car.getRelations(cache);
	}

	public void testTwogetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		Relation pilot = car.setRelation(cache, "pilot", human);
		Relation passenger = car.setRelation(cache, "passenger", human);
		assert car.getRelations(cache).size() == 2 : car.getRelations(cache);
		assert car.getRelations(cache).contains(pilot) : car.getRelations(cache);
		assert car.getRelations(cache).contains(passenger) : car.getRelations(cache);
	}

	public void testSuperOnegetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.setRelation(cache, "pilot", human);
		assert car.getRelations(cache).size() == 1 : car.getRelations(cache);
		assert car.getRelations(cache).contains(vehicleHuman) : car.getRelations(cache);
	}

	public void testSuperTwogetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType(cache, "Car");
		Type human = cache.newType("Human");
		Relation pilot = vehicle.setRelation(cache, "pilot", human);
		Relation passenger = vehicle.setRelation(cache, "passenger", human);
		assert car.getRelations(cache).size() == 2 : car.getRelations(cache);
		assert car.getRelations(cache).contains(pilot) : car.getRelations(cache);
		assert car.getRelations(cache).contains(passenger) : car.getRelations(cache);
	}

	public void testDuplicateRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation humanPilotVehicle = vehicle.setRelation(cache, "pilot", human);
		Relation humanPilotVehicle2 = vehicle.setRelation(cache, "pilot", human);
		assert vehicle.getAttributes(cache).contains(humanPilotVehicle);
		assert humanPilotVehicle == humanPilotVehicle2;
	}

	public void testGetRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation humanPilotVehicle = human.setRelation(cache, "pilot", vehicle);
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
		Relation humanPilot = human.setRelation(cache, "pilot", vehicle, bike);
		Relation getRelation = human.getRelation(cache, "pilot");
		assert human.getAttribute(cache, "pilot") != null;
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
		Relation possessVehicle = human.setRelation(cache, "HumanPossessVehicle", vehicle);
		assert possessVehicle.getTargetComponent().equals(vehicle);
	}

	public void testTargetsAncestorWithMultipleTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.setRelation(cache, "HumanPossessVehicle", vehicle, human);
		assert possessVehicle.getTargetComponent().equals(vehicle);
		assert possessVehicle.getComponent(Statics.SECOND_TARGET_POSITION).equals(human);
	}

	public void testUnidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.setRelation(cache, "HumanPossessVehicle", vehicle);
		assert human.getRelations(cache).size() == 1;
		assert human.getRelations(cache).contains(possessVehicle);
		assert human.getRelation(cache, "HumanPossessVehicle").equals(possessVehicle);
		assert vehicle.getRelation(cache, "HumanPossessVehicle").equals(possessVehicle);
	}

	public void testBidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.setRelation(cache, "HumanPossessVehicle", vehicle);

		Snapshot<Relation> vehicleRelations = vehicle.getRelations(cache);
		possessVehicle.enableMultiDirectional(cache);
		assert human.getRelations(cache).contains(possessVehicle);
		assert vehicleRelations.contains(possessVehicle) : vehicleRelations + " " + possessVehicle;
	}

	public void testRelationToHimself() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type human = cache.newType("Human");
		Relation brother = human.setRelation(cache, "brother", human);
		assert human.getRelations(cache).size() == 1;
		assert human.getRelations(cache).contains(brother);
		assert human.getRelation(cache, "brother").equals(brother);
	}

	public void testGetLinkFromTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.setRelation(cache, "HumanPossessVehicle", vehicle);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic myHuman = human.newInstance(cache, "myHuman");
		Link possession = myHuman.setLink(cache, possessVehicle, "possession", myVehicle);
		assert possessVehicle.getTargetComponent().equals(vehicle);

		assert myVehicle.getLinks(cache, possessVehicle).size() == 1;
		assert myHuman.getLinks(cache, possessVehicle).size() == 1;
		assert myVehicle.getLinks(cache, possessVehicle).contains(possession);
		assert myHuman.getLinks(cache, possessVehicle).contains(possession);
	}

	public void testGetLinkFromTarget2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type road = cache.newType("Road");
		Relation drivesOn = human.setRelation(cache, "DrivingOn", vehicle, road);
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Generic myHuman = human.newInstance(cache, "myHuman");
		Generic myRoad = road.newInstance(cache, "myRoad");
		Link driving = myHuman.setLink(cache, drivesOn, "myDrivingOn", myVehicle, myRoad);
		assert drivesOn.getTargetComponent().equals(vehicle);

		assert myRoad.getLinks(cache, drivesOn).size() == 1;
		assert myVehicle.getLinks(cache, drivesOn).size() == 1;
		assert myHuman.getLinks(cache, drivesOn).size() == 1;
		assert myRoad.getLinks(cache, drivesOn).contains(driving);
		assert myVehicle.getLinks(cache, drivesOn).contains(driving);
		assert myHuman.getLinks(cache, drivesOn).contains(driving);
	}

	public void testDefaultReverseLinks2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic myAudi = car.newInstance(cache, "myAudi");
		Generic myMercedes = car.newInstance(cache, "myMercedes");
		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");

		Relation carColor = car.setRelation(cache, "carColor", color);
		Link carRed = car.bind(cache, carColor, red);

		((Attribute) carRed).deduct(cache);

		assert red.getLinks(cache, carColor).size() == 3 : red.getLinks(cache, carColor);
		assert red.getTargets(cache, carColor, Statics.BASE_POSITION).containsAll(Arrays.asList(new Generic[] { myBmw, myAudi, myMercedes }));
	}

	public void testDefaultReverseLinks() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance(cache, "myBmw");
		Generic myAudi = car.newInstance(cache, "myAudi");
		Generic myMercedes = car.newInstance(cache, "myMercedes");
		Type color = cache.newType("Color");
		Generic red = color.newInstance(cache, "red");
		Generic blue = color.newInstance(cache, "blue");
		Relation carColor = car.setRelation(cache, "carColor", color).enableSingularConstraint(cache);
		Link carRed = car.bind(cache, carColor, red);
		myBmw.bind(cache, carColor, red);
		((GenericImpl) myAudi).cancel(cache, carRed);
		myAudi.bind(cache, carColor, blue);

		((Attribute) carRed).deduct(cache);
		assert red.getLinks(cache, carColor).size() == 2 : red.getLinks(cache, carColor);
		assert red.getTargets(cache, carColor, Statics.BASE_POSITION).containsAll(Arrays.asList(new Generic[] { myMercedes, myBmw }));
	}

	public void testDiamantKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type person = cache.newType("Person");
		final Attribute age = person.setProperty(cache, "Age");
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
		Attribute age = person.setProperty(cache, "Age");
		person.setValue(cache, age, "25");

		Type student = person.newSubType(cache, "Student");
		student.setValue(cache, age, "30");

		Type teacher = person.newSubType(cache, "Teacher");

		assert "30" == cache.newSubType("doctoral", student, teacher).getValue(cache, age);
	}

}
