package org.genericsystem.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Statics;
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
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance("myBmw");
		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");
		Generic yellow = color.newInstance("yellow");
		Relation carColor = car.setRelation("carColor", color);
		Link carRed = car.bind(carColor, red);
		assert carRed.inheritsFrom(carColor);
		Link myBmwYellow = myBmw.bind(carColor, yellow);
		assert !myBmwYellow.inheritsFrom(carRed);
		assert myBmw.getLinks(carColor).size() == 2;
		assert myBmw.getLinks(carColor).contains(myBmwYellow);
		assert myBmw.getLinks(carColor).contains(carRed);
	}

	public void test2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance("myBmw");
		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");
		Generic yellow = color.newInstance("yellow");
		Relation carColor = car.setRelation("carColor", color);
		carColor.enableSingularConstraint();

		car.setLink(carColor, "defaultColor", red);
		myBmw.setLink(carColor, "myBmwYellow", yellow);
	}

	public void testToOneOverride() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		final Relation carColor = car.setRelation("driver", color);
		carColor.enableSingularConstraint();
		final Generic myBmw = car.newInstance("myBmw");
		car.newInstance("myAudi");
		Generic red = color.newInstance("red");
		final Generic blue = color.newInstance("blue");
		car.bind(carColor, red);
		assert myBmw.getTargets(carColor).contains(red) : myBmw.getTargets(carColor);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				myBmw.bind(carColor, blue);
			}
		};
	}

	public void testReverseTernaryAccess() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		final Type passenger = cache.newType("Passenger");
		Type time = cache.newType("time");

		final Relation carPassengerTime = car.setRelation("CarPassengerTime", passenger, time);
		carPassengerTime.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carPassengerTime.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance("myBmw");
		final Generic michael = passenger.newInstance("michael");
		passenger.newInstance("nicolas");
		Generic today = time.newInstance("today");
		final Generic yesterday = time.newInstance("yesterday");
		Generic yourAudi = car.newInstance("yourAudi");

		michael.bind(carPassengerTime, myBmw, today);

		assert michael.getLinks(carPassengerTime).size() == 1;
		michael.bind(carPassengerTime, yourAudi, today);
		assert michael.getLinks(carPassengerTime).size() == 1;
		new RollbackCatcher() {

			@Override
			public void intercept() {
				yesterday.bind(carPassengerTime, myBmw, passenger);
				assert yesterday.getLinks(carPassengerTime).size() == 1;

			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);

	}

	public void testSimple() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type person = cache.newType("Person");
		Relation carDriver = car.setRelation("driver", person);
		carDriver.enableSingularConstraint(Statics.TARGET_POSITION);
		Generic myBmw = car.newInstance("myBmw");
		Generic me = person.newInstance("me");
		Generic you = person.newInstance("you");
		myBmw.bind(carDriver, me);
		myBmw.bind(carDriver, you);
		assert myBmw.getLinks(carDriver).size() == 2 : myBmw.getLinks(carDriver);
	}

	public void testSimpleReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type person = cache.newType("Person");
		Relation carDriver = car.setRelation("driver", person);
		carDriver.enableSingularConstraint(Statics.TARGET_POSITION);
		Generic myBmw = car.newInstance("myBmw");
		Generic me = person.newInstance("me");
		me.bind(carDriver, myBmw);
		assert me.getLinks(carDriver).size() == 1;
	}

	public void testSimpleTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type person = cache.newType("Person");
		Type time = cache.newType("Time");
		Relation carDriverTime = car.setRelation("driver", person, time);
		carDriverTime.enableSingularConstraint(Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic me = person.newInstance("me");
		Generic you = person.newInstance("you");
		Generic myTime = time.newInstance("myTime");
		Generic yourTime = time.newInstance("yourTime");

		myBmw.bind(carDriverTime, me, myTime);
		myBmw.bind(carDriverTime, you, yourTime);

		assert myBmw.getLinks(carDriverTime).size() == 2 : myBmw.getLinks(carDriverTime);
		assert carDriverTime.getComponents().get(Statics.BASE_POSITION).equals(car);
		assert carDriverTime.getComponents().get(Statics.TARGET_POSITION).equals(person);
		assert carDriverTime.getComponents().get(Statics.SECOND_TARGET_POSITION).equals(time);

		assert carDriverTime.getBaseComponent().equals(car);
	}

	public void testOneToManyManyToManyImpl() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type passenger = cache.newType("Passenger");
		final Relation carPassenger = car.setRelation("CarPassenger", passenger);
		carPassenger.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carPassenger.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance("myBmw");
		final Generic michael = passenger.newInstance("michael");
		Generic nicolas = passenger.newInstance("nicolas");

		myBmw.setLink(carPassenger, "30%", michael);
		myBmw.setLink(carPassenger, "40%", nicolas);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBmw.setLink(carPassenger, "60%", michael);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testOneToManyManyToManyImplReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");

		Type passenger = cache.newType("Passenger");

		final Relation carPassenger = car.setRelation("CarPassenger", passenger);
		carPassenger.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carPassenger.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance("myBmw");

		final Generic michael = passenger.newInstance("michael");
		Generic nicolas = passenger.newInstance("nicolas");

		Link link30 = michael.setLink(carPassenger, "30%", myBmw);
		Link link40 = nicolas.setLink(carPassenger, "40%", myBmw);
		assert link30.isAlive();
		michael.setLink(carPassenger, "60%", myBmw);
		assert !link30.isAlive();
		assert link40.isAlive();
	}

	public void testOneToManyManyToManyImplTernary() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type passenger = cache.newType("Passenger");
		Type time = cache.newType("time");

		final Relation carPassengerTime = car.setRelation("CarPassenger", passenger, time);
		carPassengerTime.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carPassengerTime.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		final Generic myBmw = car.newInstance("myBmw");
		final Generic yourAudi = car.newInstance("yourAudi");
		final Generic michael = passenger.newInstance("michael");
		final Generic today = time.newInstance("today");
		Generic nicolas = passenger.newInstance("nicolas");

		myBmw.setLink(carPassengerTime, "30%", michael, today);
		myBmw.setLink(carPassengerTime, "40%", nicolas, today);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				yourAudi.setLink(carPassengerTime, "60%", michael, today);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testToOne() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");

		Type owner = cache.newType("Owner");

		Relation carOwner = car.setRelation("CarOwner", owner);
		carOwner.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic yourAudi = car.newInstance("yourAudi");

		Generic sven = owner.newInstance("sven");

		myBmw.bind(carOwner, sven);
		yourAudi.bind(carOwner, sven);

		assert myBmw.getLinks(carOwner).size() == 1 : myBmw.getLinks(carOwner);
		assert yourAudi.getLinks(carOwner).size() == 1 : yourAudi.getLinks(carOwner);
	}

	public void testToOneReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation("CarOwner", owner);
		carOwner.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic yourAudi = car.newInstance("yourAudi");
		Generic sven = owner.newInstance("sven");

		sven.bind(carOwner, myBmw);
		sven.bind(carOwner, yourAudi);

		assert sven.getLinks(carOwner).size() == 2 : sven.getLinks(carOwner);
	}

	public void testToOneTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");

		final Relation carOwnerTime = car.setRelation("CarOwnerTime", owner, time);
		carOwnerTime.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance("myBmw");
		Generic nicolas = owner.newInstance("nicolas");
		final Generic michael = owner.newInstance("michael");
		Generic today = time.newInstance("today");
		final Generic yesterday = time.newInstance("yesterday");

		Link myBmwNicolasToday = myBmw.bind(carOwnerTime, nicolas, today);
		assert myBmw.getLinks(carOwnerTime).size() == 1 : myBmw.getLinks(carOwnerTime);
		Link myBmwMichaelYesterday = myBmw.bind(carOwnerTime, michael, yesterday);

		assert myBmw.getLinks(carOwnerTime).contains(myBmwMichaelYesterday);
		assert !myBmw.getLinks(carOwnerTime).contains(myBmwNicolasToday);
	}

	public void testToOneInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");

		Type owner = cache.newType("Owner");

		Relation carOwner = car.setRelation("CarOwner", owner);
		carOwner.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Generic myBmw = car.newInstance("myBmw");

		Generic me = owner.newInstance("me");
		Generic you = owner.newInstance("you");

		Link carMe = car.setLink(carOwner, "defaultOwner", me);
		assert myBmw.getLink(carOwner).getBaseComponent().equals(car);

		Link carYou = myBmw.bind(carOwner, you);

		assert myBmw.getLink(carOwner).equals(carYou) : myBmw.getLinks(carOwner);
		assert !myBmw.getLinks(carOwner).contains(carMe) : myBmw.getLinks(carOwner);
	}

	public void testToOneInheritanceReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		final Relation carColor = car.setRelation("CarColor", color);
		carColor.enableSingularConstraint(Statics.BASE_POSITION);
		assert carColor.isSingularConstraintEnabled(Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance("myBmw");
		Generic red = color.newInstance("red");
		final Generic yellow = color.newInstance("yellow");

		red.setLink(carColor, "defaultColor", car);
		assert myBmw.getLink(carColor).getBaseComponent().equals(car);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				yellow.bind(carColor, myBmw);
			}

		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testToOneInheritanceTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");

		Relation carOwnerTime = car.setRelation("CarOwnerTime", owner, time);
		carOwnerTime.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic me = owner.newInstance("me");
		Generic you = owner.newInstance("you");
		Generic today = time.newInstance("today");
		Generic yesterday = time.newInstance("yesterday");

		Link carMeToday = car.setLink(carOwnerTime, "defaultOwner", me, today);
		assert myBmw.getLink(carOwnerTime).getBaseComponent().equals(car);

		Link carYouYesterday = myBmw.bind(carOwnerTime, you, yesterday);

		assert myBmw.getLink(carOwnerTime).equals(carYouYesterday) : myBmw.getLinks(carOwnerTime);
		assert !myBmw.getLinks(carOwnerTime).contains(carMeToday) : myBmw.getLinks(carOwnerTime);
	}

	public void testToOneNewTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation("CarOwner", owner);
		carOwner.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic me = owner.newInstance("me");
		Generic you = owner.newInstance("you");

		Link myBmwMe = myBmw.bind(carOwner, me);
		assert myBmw.getLinks(carOwner).contains(myBmwMe);
		Link myBmwYou = myBmw.bind(carOwner, you);
		assert !myBmw.getLinks(carOwner).contains(myBmwMe);
		assert myBmw.getLinks(carOwner).contains(myBmwYou);
		assert myBmw.getLinks(carOwner).size() == 1 : myBmw.getLinks(carOwner);
	}

	public void testToOneNewTargetReverse() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		final Relation carOwner = car.setRelation("CarOwner", owner);
		carOwner.enableSingularConstraint(Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance("myBmw");
		Generic me = owner.newInstance("me");
		final Generic you = owner.newInstance("you");

		Link myBmwMe = me.bind(carOwner, myBmw);
		assert me.getLinks(carOwner).contains(myBmwMe);
		new RollbackCatcher() {

			@Override
			public void intercept() {
				you.bind(carOwner, myBmw);
			}

		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testToOneNewTargetTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");
		Relation carOwnerTime = car.setRelation("CarOwner", owner, time);
		carOwnerTime.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic me = owner.newInstance("me");
		Generic you = owner.newInstance("you");
		Generic today = time.newInstance("today");

		Link myBmwMeToday = myBmw.bind(carOwnerTime, me, today);
		assert myBmw.getLinks(carOwnerTime).contains(myBmwMeToday);
		Link myBmwYou = myBmw.bind(carOwnerTime, you, today);
		assert !myBmw.getLinks(carOwnerTime).contains(myBmwMeToday);
		assert myBmw.getLinks(carOwnerTime).contains(myBmwYou);
		assert myBmw.getLinks(carOwnerTime).size() == 1 : myBmw.getLinks(carOwnerTime);
	}

	public void testToOneSameValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation("CarOwner", owner);
		carOwner.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic me = owner.newInstance("me");

		Link myBmwMe1 = myBmw.setLink(carOwner, "value1", me);
		Link myBmwMe2 = myBmw.setLink(carOwner, "value1", me);
		assert myBmwMe1 == myBmwMe2;

		assert myBmw.getLinks(carOwner).size() == 1 : myBmw.getLinks(carOwner);
	}

	public void testToOneSameValueReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation("CarOwner", owner);
		carOwner.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic me = owner.newInstance("me");

		Link myBmwMe1 = me.setLink(carOwner, "value1", myBmw);
		Link myBmwMe2 = me.setLink(carOwner, "value1", myBmw);
		assert myBmwMe1 == myBmwMe2;

		assert me.getLinks(carOwner).size() == 1 : me.getLinks(carOwner);
	}

	public void testToOneSameValueTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Type time = cache.newType("Time");

		final Relation carOwnerTime = car.setRelation("CarOwnerTime", owner, time);
		carOwnerTime.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwnerTime.isSingularConstraintEnabled(Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance("myBmw");
		final Generic me = owner.newInstance("me");
		Generic today = time.newInstance("today");
		time.newInstance("yesterday");

		Link myBmwMe1 = myBmw.setLink(carOwnerTime, "value1", me, today);
		Link myBmwMe2 = myBmw.setLink(carOwnerTime, "value1", me, today);
		assert myBmwMe1 == myBmwMe2;
		assert myBmw.getLinks(carOwnerTime).size() == 1 : myBmw.getLinks(carOwnerTime);

		myBmw.setLink(carOwnerTime, "value2", me, today);
		assert myBmw.getLinks(carOwnerTime).size() == 1 : myBmw.getLinks(carOwnerTime);
	}

	public void testToOneDifferentValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		Relation carOwner = car.setRelation("CarOwner", owner);
		carOwner.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(Statics.BASE_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic me = owner.newInstance("me");

		Link myBmwMe1 = myBmw.setLink(carOwner, "value1", me);
		Link myBmwMe2 = myBmw.setLink(carOwner, "value2", me);
		assert myBmwMe1 != myBmwMe2;

		assert myBmw.getLinks(carOwner).size() == 1 : myBmw.getLinks(carOwner);
		assert equals((Snapshot) myBmw.getLinks(carOwner), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(carOwner), "value2") != null;

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
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type owner = cache.newType("Owner");
		final Relation carOwner = car.setRelation("CarOwner", owner);
		carOwner.enableSingularConstraint(Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(Statics.BASE_POSITION);

		final Generic myBmw = car.newInstance("myBmw");
		final Generic me = owner.newInstance("me");
		me.setLink(carOwner, "value1", myBmw);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				cache.newSuperCache().start();
				me.setLink(carOwner, "value2", myBmw);
			}

		}.assertIsCausedBy(SingularConstraintViolationException.class);

		cache.start();
		assert me.getLinks(carOwner).size() == 1 : me.getLinks(carOwner);
		assert equals((Snapshot) myBmw.getLinks(carOwner), "value1") != null;
		assert equals((Snapshot) myBmw.getLinks(carOwner), "value2") == null;
		cache.flush();
	}

	public void testOneToMany() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation("CarTyres", tyre);

		carTyres.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic frontLeft = tyre.newInstance("frontLeft");
		Generic frontRight = tyre.newInstance("frontRight");
		Generic rearLeft = tyre.newInstance("rearLeft");
		Generic rearRight = tyre.newInstance("rearRight");

		myBmw.bind(carTyres, frontLeft);
		myBmw.bind(carTyres, frontRight);
		myBmw.bind(carTyres, rearLeft);
		myBmw.bind(carTyres, rearRight);

		assert myBmw.getLinks(carTyres).size() == 4 : myBmw.getLinks(carTyres);
	}

	public void testOneToManyReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation("CarTyres", tyre);

		carTyres.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic frontLeft = tyre.newInstance("frontLeft");
		Generic frontRight = tyre.newInstance("frontRight");
		Generic rearLeft = tyre.newInstance("rearLeft");
		Generic rearRight = tyre.newInstance("rearRight");

		frontLeft.bind(carTyres, myBmw);
		frontRight.bind(carTyres, myBmw);
		rearLeft.bind(carTyres, myBmw);
		rearRight.bind(carTyres, myBmw);

		assert frontLeft.getLinks(carTyres).size() == 1 : frontLeft.getLinks(carTyres);
		assert frontRight.getLinks(carTyres).size() == 1 : frontLeft.getLinks(carTyres);
		assert rearLeft.getLinks(carTyres).size() == 1 : frontLeft.getLinks(carTyres);
		assert rearRight.getLinks(carTyres).size() == 1 : frontLeft.getLinks(carTyres);
	}

	public void testOneToManyInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation("CarTyres", tyre);
		carTyres.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		Generic myBmw = car.newInstance("myBmw");
		Generic frontLeft = tyre.newInstance("frontLeft");
		Generic frontRight = tyre.newInstance("frontRight");
		Generic rearLeft = tyre.newInstance("rearLeft");
		Generic rearRight = tyre.newInstance("rearRight");
		Generic center = tyre.newInstance("center");
		car.setLink(carTyres, "defaultTyre", center);
		assert center.getHolders(carTyres, 1).size() == 1;
		assert myBmw.getLink(carTyres).getBaseComponent().equals(myBmw);
		myBmw.bind(carTyres, frontLeft);
		myBmw.bind(carTyres, frontRight);
		myBmw.bind(carTyres, rearLeft);
		myBmw.bind(carTyres, rearRight);
		assert myBmw.getLinks(carTyres).size() == 5 : myBmw.getLinks(carTyres);
	}

	public void testSingularTargetDefaultColor() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.newType("Car");
		Type color = cache.newType("Color");

		car.newInstance("myBmw");
		car.newInstance("myAudi");
		final Generic red = color.newInstance("red");

		final Relation carColor = car.setRelation("CarColor", color);
		carColor.enableSingularConstraint(Statics.TARGET_POSITION);

		new RollbackCatcher() {

			@Override
			public void intercept() {
				car.bind(carColor, red);
				assert red.getLinks(carColor).size() == 2 : red.getLinks(carColor);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testOneToManyInheritanceReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type person = cache.newType("person");
		Relation carPerson = car.setRelation("CarPersons", person);

		carPerson.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carPerson.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance("myBmw");

		// Generic michael = person.newInstance( "michael");
		// Generic nicolas = person.newInstance( "nicolas");
		// Generic sven = person.newInstance( "sven");
		// Generic sofiane = person.newInstance( "sofiane");
		Generic pierre = person.newInstance("pierre");

		Link carPierre = pierre.setLink(carPerson, "defaultPerson", car);
		Generic myAudi = car.newInstance("myAudi");
		// should throw a singular constraint exception

		// assert myBmw.getLink( carPerson).getBaseComponent().equals(car);
		// assert false : "" + michael.getLinks( carPerson, Statics.TARGET_POSITION);
		// Link myBmwMichael = michael.bind( carPerson, myBmw);
		// assert false : carPerson.getAllInstances();
		// Link myBmwNicolas = nicolas.bind( carPerson, myBmw);
		// Link myBmwSven = sven.bind( carPerson, myBmw);
		// Link myBmwSofiane = sofiane.bind( carPerson, myBmw);
		//
		// assert michael.getLinks( carPerson, Statics.TARGET_POSITION).size() == 1 : michael.getLinks( carPerson, Statics.TARGET_POSITION);
		// assert nicolas.getLinks( carPerson, Statics.TARGET_POSITION).size() == 1 : nicolas.getLinks( carPerson, Statics.TARGET_POSITION);
		// assert sven.getLinks( carPerson, Statics.TARGET_POSITION).size() == 1 : sven.getLinks( carPerson, Statics.TARGET_POSITION);
		// assert sofiane.getLinks( carPerson, Statics.TARGET_POSITION).size() == 1 : sofiane.getLinks( carPerson, Statics.TARGET_POSITION);
		//
		// assert myBmw.getLinks( carPerson).contains(carPierre) : myBmw.getLinks( carPerson);
		// assert myBmw.getLinks( carPerson).contains(myBmwMichael) : myBmw.getLinks( carPerson);
		// assert myBmw.getLinks( carPerson).contains(myBmwNicolas) : myBmw.getLinks( carPerson);
		// assert myBmw.getLinks( carPerson).contains(myBmwSven) : myBmw.getLinks( carPerson);
		// assert myBmw.getLinks( carPerson).contains(myBmwSofiane) : myBmw.getLinks( carPerson);
	}

	public void testOneToManyInheritanceReverse2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type person = cache.newType("person");
		Relation carPerson = car.setRelation("CarPersons", person);

		carPerson.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carPerson.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic michael = person.newInstance("michael");
		Generic nicolas = person.newInstance("nicolas");
		Generic sven = person.newInstance("sven");
		Generic sofiane = person.newInstance("sofiane");
		Generic pierre = person.newInstance("pierre");

		Link carPierre = pierre.setLink(carPerson, "defaultPerson", car);
		assert myBmw.getLink(carPerson).getBaseComponent().equals(myBmw) : myBmw.getLink(carPerson);

		Link myBmwMichael = michael.bind(carPerson, myBmw);
		Link myBmwNicolas = nicolas.bind(carPerson, myBmw);
		Link myBmwSven = sven.bind(carPerson, myBmw);
		Link myBmwSofiane = sofiane.bind(carPerson, myBmw);

		assert michael.getLinks(carPerson).size() == 1 : michael.getLinks(carPerson);
		assert nicolas.getLinks(carPerson).size() == 1 : nicolas.getLinks(carPerson);
		assert sven.getLinks(carPerson).size() == 1 : sven.getLinks(carPerson);
		assert sofiane.getLinks(carPerson).size() == 1 : sofiane.getLinks(carPerson);

		assert !myBmw.getLinks(carPerson).contains(carPierre) : myBmw.getLinks(carPerson);
		assert myBmw.getLinks(carPerson).contains(myBmwMichael) : myBmw.getLinks(carPerson);
		assert myBmw.getLinks(carPerson).contains(myBmwNicolas) : myBmw.getLinks(carPerson);
		assert myBmw.getLinks(carPerson).contains(myBmwSven) : myBmw.getLinks(carPerson);
		assert myBmw.getLinks(carPerson).contains(myBmwSofiane) : myBmw.getLinks(carPerson);
	}

	public void testOneToManyDifferentValue() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		final Relation carTyres = car.setRelation("CarTyres", tyre);

		carTyres.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		carTyres.enablePropertyConstraint();
		assert carTyres.isPropertyConstraintEnabled();

		final Generic myBmw = car.newInstance("myBmw");
		Generic frontLeft = tyre.newInstance("frontLeft");
		Generic frontRight = tyre.newInstance("frontRight");
		Generic rearLeft = tyre.newInstance("rearLeft");
		final Generic rearRight = tyre.newInstance("rearRight");

		myBmw.bind(carTyres, frontLeft);
		myBmw.bind(carTyres, frontRight);
		myBmw.bind(carTyres, rearLeft);

		Link myBmwRearRight1 = myBmw.setLink(carTyres, "value1", rearRight);
		Link myBmwRearRight2 = myBmw.setLink(carTyres, "value2", rearRight);

		assert !myBmwRearRight1.isAlive();
		assert myBmwRearRight2.isAlive();

		assert myBmw.getLinks(carTyres).size() == 4 : myBmw.getLinks(carTyres);
		assert equals((Snapshot) myBmw.getLinks(carTyres), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(carTyres), "value2") != null;

		carTyres.disablePropertyConstraint();
		new RollbackCatcher() {
			@Override
			public void intercept() {
				myBmw.setLink(carTyres, "value3", rearRight);
			}
		}.assertIsCausedBy(SingularConstraintViolationException.class);
	}

	public void testOneToManyDifferentValueReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		final Relation carTyres = car.setRelation("CarTyres", tyre);

		carTyres.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(Statics.TARGET_POSITION);
		carTyres.enablePropertyConstraint();
		assert carTyres.isPropertyConstraintEnabled();

		final Generic myBmw = car.newInstance("myBmw");
		Generic frontLeft = tyre.newInstance("frontLeft");
		Generic frontRight = tyre.newInstance("frontRight");
		Generic rearLeft = tyre.newInstance("rearLeft");
		final Generic rearRight = tyre.newInstance("rearRight");

		frontLeft.bind(carTyres, myBmw);
		frontRight.bind(carTyres, myBmw);
		rearLeft.bind(carTyres, myBmw);

		final Link myBmwRearRight1 = rearRight.setLink(carTyres, "value1", myBmw);
		final Link myBmwRearRight2 = rearRight.setLink(carTyres, "value2", myBmw);

		assert !myBmwRearRight1.isAlive();
		assert myBmwRearRight2.isAlive();

		assert myBmw.getLinks(carTyres).size() == 4 : myBmw.getLinks(carTyres);
		assert equals((Snapshot) myBmw.getLinks(carTyres), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(carTyres), "value2") != null;

		carTyres.disablePropertyConstraint();

		rearRight.setLink(carTyres, "value3", myBmw);
		assert !myBmwRearRight1.isAlive();
		assert !myBmwRearRight2.isAlive();
	}

	public void testOneToManySameValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation("CarTyres", tyre);

		carTyres.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic frontLeft = tyre.newInstance("frontLeft");
		Generic frontRight = tyre.newInstance("frontRight");
		Generic rearLeft = tyre.newInstance("rearLeft");
		Generic rearRight = tyre.newInstance("rearRight");

		myBmw.bind(carTyres, frontLeft);
		myBmw.bind(carTyres, frontRight);
		myBmw.bind(carTyres, rearLeft);
		Link myBmwRearRight1 = myBmw.setLink(carTyres, "value1", rearRight);
		assert myBmw.getLinks(carTyres).contains(myBmwRearRight1);
		Link myBmwRearRight2 = myBmw.setLink(carTyres, "value1", rearRight);
		assert myBmwRearRight1 == myBmwRearRight2;

		assert myBmw.getLinks(carTyres).size() == 4 : myBmw.getLinks(carTyres);
		assert myBmw.getLinks(carTyres).contains(myBmwRearRight1);
	}

	public void testOneToManySameValueReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type tyre = cache.newType("Tyre");
		Relation carTyres = car.setRelation("CarTyres", tyre);

		carTyres.enableSingularConstraint(Statics.TARGET_POSITION);
		assert carTyres.isSingularConstraintEnabled(Statics.TARGET_POSITION);

		Generic myBmw = car.newInstance("myBmw");
		Generic frontLeft = tyre.newInstance("frontLeft");
		Generic frontRight = tyre.newInstance("frontRight");
		Generic rearLeft = tyre.newInstance("rearLeft");
		Generic rearRight = tyre.newInstance("rearRight");

		frontLeft.bind(carTyres, myBmw);
		frontRight.bind(carTyres, myBmw);
		rearLeft.bind(carTyres, myBmw);
		Link myBmwRearRight1 = rearRight.setLink(carTyres, "value1", myBmw);
		assert myBmw.getLinks(carTyres).contains(myBmwRearRight1);
		Link myBmwRearRight2 = rearRight.setLink(carTyres, "value1", myBmw);
		assert myBmwRearRight1 == myBmwRearRight2;

		assert myBmw.getLinks(carTyres).size() == 4 : myBmw.getLinks(carTyres);
		assert myBmw.getLinks(carTyres).contains(myBmwRearRight1);
	}

	public void testManyToMany() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);

		Generic myBmw = car.newInstance("myBmw");
		Generic yourAudi = car.newInstance("yourAudi");
		Generic red = color.newInstance("red");
		Generic blue = color.newInstance("blue");

		myBmw.bind(carColor, red);
		myBmw.bind(carColor, blue);
		yourAudi.bind(carColor, blue);
		yourAudi.bind(carColor, red);

		assert myBmw.getLinks(carColor).size() == 2 : myBmw.getLinks(carColor);
		assert yourAudi.getLinks(carColor).size() == 2 : yourAudi.getLinks(carColor);
	}

	public void testManyToManyReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);

		Generic myBmw = car.newInstance("myBmw");
		Generic yourAudi = car.newInstance("yourAudi");
		Generic red = color.newInstance("red");
		Generic blue = color.newInstance("blue");

		red.bind(carColor, myBmw);
		blue.bind(carColor, myBmw);
		blue.bind(carColor, yourAudi);
		red.bind(carColor, yourAudi);

		assert red.getLinks(carColor).size() == 2 : red.getLinks(carColor);
		assert blue.getLinks(carColor).size() == 2 : yourAudi.getLinks(carColor);
	}

	public void testManyToManyInheritance() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic yourAudi = car.newInstance("yourAudi");

		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");

		Relation carColor = car.setRelation("CarColor", color);
		Link carRed = car.setLink(carColor, "defaultColor", red);
		assert yourAudi.getLink(carColor).getBaseComponent().equals(car);

		Link yourAudiRed = yourAudi.bind(carColor, red);

		assert yourAudi.getLinks(carColor).size() == 2 : yourAudi.getLinks(carColor);
		assert yourAudi.getLinks(carColor).contains(carRed);
		assert yourAudi.getLinks(carColor).contains(yourAudiRed);
	}

	public void testManyToManyInheritanceReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic yourAudi = car.newInstance("yourAudi");
		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");

		Relation carColor = car.setRelation("CarColor", color);
		red.setLink(carColor, "defaultColor", car);
		assert red.getLink(carColor).getBaseComponent().equals(yourAudi) : red.getLink(carColor).info();

		Link yourAudiRed = red.bind(carColor, yourAudi);

		assert yourAudi.getLinks(carColor).size() == 2;
		assert yourAudi.getLinks(carColor).contains(yourAudiRed);
	}

	public void testManyToManyPropertyConstraint() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);

		carColor.enablePropertyConstraint();

		Generic myBmw = car.newInstance("myBmw");
		Generic yourAudi = car.newInstance("yourAudi");
		Generic red = color.newInstance("red");
		Generic blue = color.newInstance("blue");

		myBmw.setLink(carColor, "value1", red);
		Link bmwRed2 = myBmw.setLink(carColor, "value2", red);

		yourAudi.bind(carColor, blue);
		yourAudi.bind(carColor, red);

		assert myBmw.getLinks(carColor).size() == 1 : myBmw.getLinks(carColor);
		assert equals((Snapshot) myBmw.getLinks(carColor), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(carColor), "value2") == bmwRed2;
		assert yourAudi.getLinks(carColor).size() == 2 : yourAudi.getLinks(carColor);
	}

	public void testManyToManyPropertyConstraintReverse() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carColor = car.setRelation("CarColor", color);

		carColor.enablePropertyConstraint();

		Generic myBmw = car.newInstance("myBmw");
		Generic yourAudi = car.newInstance("yourAudi");
		Generic red = color.newInstance("red");
		Generic blue = color.newInstance("blue");

		red.setLink(carColor, "value1", myBmw);
		Link bmwRed2 = red.setLink(carColor, "value2", myBmw);

		blue.bind(carColor, yourAudi);
		red.bind(carColor, yourAudi);

		assert myBmw.getLinks(carColor).size() == 1 : myBmw.getLinks(carColor);
		assert equals((Snapshot) myBmw.getLinks(carColor), "value1") == null;
		assert equals((Snapshot) myBmw.getLinks(carColor), "value2") == bmwRed2;
		assert yourAudi.getLinks(carColor).size() == 2 : yourAudi.getLinks(carColor);

		assert equals((Snapshot) red.getLinks(carColor), "value2") == bmwRed2;
		assert blue.getLinks(carColor).size() == 1 : blue.getLinks(carColor);
	}

	public void testSimpleRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type car = cache.newType("Car");
		Type sportsCar = car.newSubType("SportsCar");
		Type person = cache.newType("Person");
		Type pilot = person.newSubType("Pilot");
		Relation carDriver = car.setRelation("driver", person);
		Relation sportsCarPilot = sportsCar.setRelation("driver", pilot);
		Generic myBmw = car.newInstance("myBmw");
		Generic yourAudi = car.newInstance("yourAudi");
		Generic ourFerrari = sportsCar.newInstance("ourFerrari");

		Generic me = person.newInstance("me");
		Generic you = person.newInstance("you");
		Generic ayrton = pilot.newInstance("Ayrton");

		myBmw.bind(carDriver, me);
		myBmw.bind(carDriver, ayrton);

		yourAudi.bind(carDriver, you);
		yourAudi.bind(carDriver, ayrton);
		ourFerrari.bind(carDriver, me);
		ourFerrari.bind(carDriver, you);
		ourFerrari.bind(sportsCarPilot, ayrton);

		assert myBmw.getTargets(carDriver).contains(me);
		assert myBmw.getTargets(carDriver).contains(ayrton);
		assert myBmw.getTargets(carDriver).size() == 2;
		assert yourAudi.getTargets(carDriver).contains(you);
		assert yourAudi.getTargets(carDriver).contains(ayrton);
		assert yourAudi.getTargets(carDriver).size() == 2;
		assert ourFerrari.getTargets(carDriver).contains(me);
		assert ourFerrari.getTargets(carDriver).contains(you);
		assert ourFerrari.getTargets(carDriver).contains(ayrton);

	}

	// public void testSimpleRelationReverse() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	//
	// Type car = cache.newType("Car");
	// Type sportsCar = car.newSubType("SportsCar");
	//
	// Type person = cache.newType("Person");
	// Type pilot = person.newSubType("Pilot");
	//
	// Relation carDriver = car.setRelation("driver", person);
	// Relation sportsCarPilot = sportsCar.setRelation("driver", pilot);
	//
	// Generic myBmw = car.newInstance("myBmw");
	// Generic yourAudi = car.newInstance("yourAudi");
	// Generic ourFerrari = sportsCar.newInstance("ourFerrari");
	//
	// Generic me = person.newInstance("me");
	// Generic you = person.newInstance("you");
	// Generic ayrton = pilot.newInstance("Ayrton");
	//
	// me.bind(carDriver, myBmw);
	// ayrton.bind(carDriver, myBmw);
	//
	// you.bind(carDriver, yourAudi);
	// ayrton.bind(carDriver, yourAudi);
	// me.bind(carDriver, ourFerrari);
	// you.bind(carDriver, ourFerrari);
	// ayrton.bind(sportsCarPilot, ourFerrari);
	//
	// assert myBmw.getTargets(carDriver).contains(me);
	// assert myBmw.getTargets(carDriver).contains(ayrton);
	// assert myBmw.getTargets(carDriver).size() == 2;
	// assert yourAudi.getTargets(carDriver).contains(you);
	// assert yourAudi.getTargets(carDriver).contains(ayrton);
	// assert yourAudi.getTargets(carDriver).size() == 2;
	// assert ourFerrari.getTargets(carDriver).contains(me);
	// assert ourFerrari.getTargets(carDriver).contains(you);
	// assert ourFerrari.getTargets(carDriver).contains(ayrton);
	//
	// assert me.getTargets(carDriver).contains(myBmw);
	// assert ayrton.getTargets(carDriver).contains(myBmw);
	// assert !ayrton.getTargets(sportsCarPilot).contains(myBmw) : ayrton.getTargets(carDriver);
	// assert me.getTargets(carDriver).size() == 2;
	// assert you.getTargets(carDriver).contains(yourAudi);
	// assert ayrton.getTargets(carDriver).contains(yourAudi);
	// assert you.getTargets(carDriver).size() == 2;
	// assert me.getTargets(carDriver).contains(ourFerrari);
	// assert you.getTargets(carDriver).contains(ourFerrari);
	// assert ayrton.getTargets(carDriver).contains(ourFerrari);
	//
	// }

	public void testCountAncestor() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation relation = vehicle.setRelation("pilot", human);
		assert relation.getComponents().size() == 2 : relation.getComponents().size();
	}

	public void testDependency() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation relationPilot = vehicle.setRelation("pilot", human);
		assert cache.getMetaRelation().getInheritings().contains(relationPilot);
	}

	public void testIsRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation relation = vehicle.setRelation("pilot", human);
		assert !relation.inheritsFrom(vehicle);
		assert !relation.inheritsFrom(human);
		assert relation.isAttributeOf(vehicle);
	}

	public void testIsTernaryRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type level = cache.newType("Level");
		Type human = cache.newType("Human");
		Relation humanGames = human.setRelation("Games", car, level);
		assert humanGames.isAttributeOf(human);
		assert !humanGames.inheritsFrom(human);
		assert !humanGames.inheritsFrom(car);
		assert !humanGames.inheritsFrom(level);
	}

	public void testOnegetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		Relation carHuman = car.setRelation("pilot", human);
		assert car.getRelations().size() == 1 : car.getRelations();
		assert car.getRelations().contains(carHuman) : car.getRelations();
	}

	public void testTwogetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type human = cache.newType("Human");
		Relation pilot = car.setRelation("pilot", human);
		Relation passenger = car.setRelation("passenger", human);
		assert car.getRelations().size() == 2 : car.getRelations();
		assert car.getRelations().contains(pilot) : car.getRelations();
		assert car.getRelations().contains(passenger) : car.getRelations();
	}

	public void testSuperOnegetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type human = cache.newType("Human");
		Relation vehicleHuman = vehicle.setRelation("pilot", human);
		assert car.getRelations().size() == 1 : car.getRelations();
		assert car.getRelations().contains(vehicleHuman) : car.getRelations();
	}

	public void testSuperTwogetRelationsSnapshot() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type car = vehicle.newSubType("Car");
		Type human = cache.newType("Human");
		Relation pilot = vehicle.setRelation("pilot", human);
		Relation passenger = vehicle.setRelation("passenger", human);
		assert car.getRelations().size() == 2 : car.getRelations();
		assert car.getRelations().contains(pilot) : car.getRelations();
		assert car.getRelations().contains(passenger) : car.getRelations();
	}

	public void testDuplicateRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation humanPilotVehicle = vehicle.setRelation("pilot", human);
		Relation humanPilotVehicle2 = vehicle.setRelation("pilot", human);
		assert vehicle.getAttributes().contains(humanPilotVehicle);
		assert humanPilotVehicle == humanPilotVehicle2;
	}

	public void testGetRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation humanPilotVehicle = human.setRelation("pilot", vehicle);
		Relation getRelation = human.getRelation("pilot");
		assert getRelation != null;
		assert getRelation.equals(humanPilotVehicle);
		assert human.getRelation("passenger") == null;
	}

	public void testGetRelationWithTargets() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type bike = cache.newType("Bike");
		Type human = cache.newType("Human");
		Relation humanPilot = human.setRelation("pilot", vehicle, bike);
		Relation getRelation = human.getRelation("pilot");
		assert human.getAttribute("pilot") != null;
		assert getRelation != null;
		assert getRelation.equals(humanPilot);
		assert human.getRelation("passenger") == null;
	}

	// public void testSubRelation() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType( "Car");
	// Type human = cache.newType("Human");
	// Relation possessVehicle = human.addRelation( "HumanPossessVehicle", vehicle);
	// Relation possessCar = human.addSubRelation( possessVehicle, "HumanPossessCar", car);
	// assert human.getRelations().size() == 1;
	// assert human.getRelations().contains(possessCar);
	// assert possessCar.inheritsFrom(possessVehicle);
	// }

	// public void testSubRelationSymetric() {
	// Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
	// Type vehicle = cache.newType("Vehicle");
	// Type car = vehicle.newSubType( "Car");
	// Type human = cache.newType("Human");
	// Type man = human.newSubType( "Man");
	// Relation humanPossessVehicle = human.addRelation( "HumanPossessVehicle", vehicle);
	// Relation manPossessCar = man.addSubRelation( humanPossessVehicle, "ManPossessCar", car);
	// assert human.getRelations().size() == 1;
	// assert human.getRelations().contains(humanPossessVehicle);
	// assert man.getRelations().size() == 1;
	// assert man.getRelations().contains(manPossessCar);
	// assert manPossessCar.inheritsFrom(humanPossessVehicle);
	// }

	public void testTargetsAncestor() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.setRelation("HumanPossessVehicle", vehicle);
		assert possessVehicle.getTargetComponent().equals(vehicle);
	}

	public void testTargetsAncestorWithMultipleTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.setRelation("HumanPossessVehicle", vehicle, human);
		assert possessVehicle.getTargetComponent().equals(vehicle);
		assert possessVehicle.getComponent(Statics.SECOND_TARGET_POSITION).equals(human);
	}

	public void testUnidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.setRelation("HumanPossessVehicle", vehicle);
		assert human.getRelations().size() == 1;
		assert human.getRelations().contains(possessVehicle);
		assert human.getRelation("HumanPossessVehicle").equals(possessVehicle);
		assert vehicle.getRelation("HumanPossessVehicle").equals(possessVehicle);
	}

	public void testBidirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.setRelation("HumanPossessVehicle", vehicle);

		Snapshot<Relation> vehicleRelations = vehicle.getRelations();
		// possessVehicle.enableMultiDirectional();
		assert human.getRelations().contains(possessVehicle);
		assert vehicleRelations.contains(possessVehicle) : vehicleRelations + " " + possessVehicle;
	}

	public void testRelationToHimself() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Relation brother = human.setRelation("brother", human);
		// brother.enableMultiDirectional();
		assert human.getRelations().size() == 1;
		assert human.getRelations().contains(brother);
		assert human.getRelation("brother").equals(brother);

		Generic michael = human.newInstance("michael");
		Generic quentin = human.newInstance("quentin");
		Link holder = michael.bind(brother, quentin);

		assert Statics.BASE_POSITION == michael.getBasePos(brother);
		assert ((GenericImpl) brother).getComponentsPositions(michael, quentin).equals(Arrays.asList(0, 1));
		Type otherType = cache.newType("OtherType");
		assert ((GenericImpl) brother).getComponentsPositions(michael, quentin, otherType).equals(Arrays.asList(0, 1, 2));
		assert ((GenericImpl) brother).getComponentsPositions(otherType, michael, quentin).equals(Arrays.asList(2, 0, 1));

		Snapshot<Integer> positions = ((GenericImpl) michael).getPositions(brother);
		assert positions.size() == 2;
		assert positions.get(0) == 0;
		assert positions.get(1) == 1;
	}

	public void testGetLinkFromTarget() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Relation possessVehicle = human.setRelation("HumanPossessVehicle", vehicle);
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic myHuman = human.newInstance("myHuman");
		Link possession = myHuman.setLink(possessVehicle, "possession", myVehicle);
		assert possessVehicle.getTargetComponent().equals(vehicle);

		assert myVehicle.getLinks(possessVehicle).size() == 1;
		assert myHuman.getLinks(possessVehicle).size() == 1;
		assert myVehicle.getLinks(possessVehicle).contains(possession);
		assert myHuman.getLinks(possessVehicle).contains(possession);
	}

	public void testGetLinkFromTarget2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type human = cache.newType("Human");
		Type road = cache.newType("Road");
		Relation drivesOn = human.setRelation("DrivingOn", vehicle, road);
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic myHuman = human.newInstance("myHuman");
		Generic myRoad = road.newInstance("myRoad");
		Link driving = myHuman.setLink(drivesOn, "myDrivingOn", myVehicle, myRoad);
		assert drivesOn.getTargetComponent().equals(vehicle);

		assert myRoad.getLinks(drivesOn).size() == 1;
		assert myVehicle.getLinks(drivesOn).size() == 1;
		assert myHuman.getLinks(drivesOn).size() == 1;
		assert myRoad.getLinks(drivesOn).contains(driving);
		assert myVehicle.getLinks(drivesOn).contains(driving);
		assert myHuman.getLinks(drivesOn).contains(driving);
	}

	public void testDefaultReverseLinks2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance("myBmw");
		Generic myAudi = car.newInstance("myAudi");
		Generic myMercedes = car.newInstance("myMercedes");
		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");

		Relation carColor = car.setRelation("carColor", color);
		car.bind(carColor, red);

		assert red.getLinks(carColor, 1).size() == 3 : red.getLinks(carColor);
		assert red.getTargets(carColor, 1, 0).containsAll(Arrays.asList(new Generic[] { myBmw, myAudi, myMercedes }));
	}

	public void testDefaultReverseLinks() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Generic myBmw = car.newInstance("myBmw");
		Generic myAudi = car.newInstance("myAudi");
		Generic myMercedes = car.newInstance("myMercedes");
		Type color = cache.newType("Color");
		Generic red = color.newInstance("red");
		Generic blue = color.newInstance("blue");
		Relation carColor = car.setRelation("carColor", color).enableSingularConstraint();
		Link carRed = car.bind(carColor, red);
		myBmw.bind(carColor, red);
		myAudi.setLink(carRed, null, red);
		myAudi.bind(carColor, blue);

		assert red.getLinks(carColor, 1).size() == 2 : red.getLinks(carColor);
		assert red.getTargets(carColor, 1, 0).containsAll(Arrays.asList(new Generic[] { myMercedes, myBmw }));
	}

	public void testDiamantKO() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type person = cache.newType("Person");
		final Attribute age = person.setProperty("Age");
		person.setValue(age, "25");

		final Type student = person.newSubType("Student");
		student.setValue(age, "30");

		final Type teacher = person.newSubType("Teacher");
		teacher.setValue(age, "20");

		try {
			cache.newSubType("doctoral", student, teacher).getValue(age);
		} catch (IllegalStateException ignore) {

		}
	}

	public void testDiamantOK() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type person = cache.newType("Person");
		Attribute age = person.setProperty("Age");
		person.setValue(age, "25");

		Type student = person.newSubType("Student");
		student.setValue(age, "30");

		Type teacher = person.newSubType("Teacher");

		assert "30" == cache.newSubType("doctoral", student, teacher).getValue(age);
	}

}
