package org.genericsystem.cdi;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.impl.core.Statics;
import org.testng.annotations.Test;

@Test
public class AppliWebTest extends AbstractTest {

	// Types
	public void testCreateType() {
		Type vehicle = expressions.evaluateMethodExpression("#{cache.newType('Vehicle')}", Type.class);
		assert vehicle.isAlive(cache);
		assert !vehicle.isAttribute();
		assert !vehicle.isEngine();
		assert vehicle.isInstanceOf(expressions.evaluateValueExpression("#{engine}", Generic.class));
		assert vehicle.isType();
	}

	// Attributes
	public void testCreateAttribute() {
		Attribute vehiclePower = expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addAttribute(cache, 'power')}", Attribute.class);
		assert vehiclePower.isAlive(cache);
		assert vehiclePower.isAttribute();
		assert !vehiclePower.isEngine();
		assert vehiclePower.isAttributeOf(expressions.evaluateMethodExpression("#{cache.newType('Vehicle')}", Generic.class));
		assert vehiclePower.isInstanceOf(expressions.evaluateValueExpression("#{engine}", Generic.class));
		assert !vehiclePower.isInstanceOf(expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addAttribute(cache, 'power').getImplicit()}", Generic.class));
		assert !vehiclePower.isType();
	}

	public void testAttributeDependency() {
		Type vehicle1 = expressions.evaluateMethodExpression("#{cache.newType('Vehicle')}", Type.class);
		Attribute vehiclePower = vehicle1.addAttribute(cache, "power");

		Type power = vehiclePower.getImplicit();
		assert cache.getEngine().getInheritings(cache).contains(power);
		assert cache.getEngine().getInheritings(cache).contains(vehicle1);
		assert power.getInheritings(cache).contains(vehiclePower);
		assert vehicle1.getComposites(cache).contains(vehiclePower) : cache.getEngine().getComposites(cache);
	}

	public void testDuplicateAttribute() {
		Attribute attribute1 = expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addAttribute(cache, 'power')}", Attribute.class);
		Attribute attribute2 = expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addAttribute(cache, 'power')}", Attribute.class);
		assert attribute1 == attribute2;
	}

	public void testPropertyIsAttribute() {
		Attribute vehiclePower = expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addProperty(cache, 'power')}");
		assert vehiclePower.isAttribute();
		assert vehiclePower.isAttributeOf(expressions.evaluateMethodExpression("#{cache.newType('Vehicle')}", Type.class));
	}

	public void testGetAttributeWithInheritance() {
		Attribute vehiclePower = expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addAttribute(cache, 'power')}");
		assert (vehiclePower.equals(expressions.evaluateMethodExpression("#{cache.newType('Vehicle').getAttribute(cache, 'power')}")));
		assert (vehiclePower.equals(expressions.evaluateMethodExpression("#{cache.newType('Vehicle').newSubType(cache, 'Car').getAttribute(cache, 'power')}")));
	}

	public void testGetAttributeWithValue() {
		expressions.evaluateMethodExpression("#{cache.newType('Vehicle').newInstance(cache, 'myVehicle').setValue(cache, cache.newType('Vehicle').addAttribute(cache, 'power'), '123')}", Generic.class);
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').newInstance(cache, 'myVehicle').getValueHolders(cache, cache.newType('Vehicle').addAttribute(cache, 'power'))}", Snapshot.class).size() == 1;

		expressions.evaluateMethodExpression("#{cache.newType('Vehicle').newInstance(cache, 'myVehicle').setValue(cache, cache.newType('Vehicle').addAttribute(cache, 'power'), '123')}");
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').newInstance(cache, 'myVehicle').getValueHolders(cache, cache.newType('Vehicle').addAttribute(cache, 'power')).size()}", Integer.class) == 1;

		expressions.evaluateMethodExpression("#{cache.newType('Vehicle').newInstance(cache, 'myVehicle').setValue(cache, cache.newType('Vehicle').addAttribute(cache, 'power'), '126')}");
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').newInstance(cache, 'myVehicle').getValueHolders(cache, cache.newType('Vehicle').addAttribute(cache, 'power')).size()}", Integer.class) == 2;
	}

	public void testOverrideAttribute() {
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').newSubType(cache, 'Car').addSubAttribute(cache, cache.newType('Vehicle').addAttribute(cache, 'power'), 'power').isAttribute()}", Boolean.class);
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').getAttributes(cache).contains(cache.newType('Vehicle').addAttribute(cache, 'power'))}", Boolean.class);
		assert expressions.evaluateMethodExpression(
				"#{cache.newType('Vehicle').newSubType(cache, 'Car').getAttributes(cache).contains(cache.newType('Vehicle').newSubType(cache, 'Car').addSubAttribute(cache, cache.newType('Vehicle').addAttribute(cache, 'power'), 'power'))}", Boolean.class);
		assert expressions.evaluateMethodExpression(
				"#{cache.newType('Vehicle').newSubType(cache, 'Car').addSubAttribute(cache, cache.newType('Vehicle').addAttribute(cache, 'power'), 'power').inheritsFrom(cache.newType('Vehicle').addAttribute(cache, 'power'))}", Boolean.class);
	}

	public void testJumpOverrideAttribute() {
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').getAttributes(cache).contains(cache.newType('Vehicle').addAttribute(cache, 'power'))}", Boolean.class);
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').newSubType(cache, 'Car').newSubType(cache, 'SuperCar').getAttributes(cache).contains(cache.newType('Vehicle').addAttribute(cache, 'power'))}", Boolean.class);
	}

	// Relations
	public void testBind() {
		Relation carOwner = expressions.evaluateMethodExpression("#{cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner')).enableSingularConstraint(cache, 0)}", Relation.class);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = expressions.evaluateMethodExpression("#{cache.newType('Car').newInstance(cache, 'myBmw')}", Generic.class);
		Generic sven = expressions.evaluateMethodExpression("#{cache.newType('Owner').newInstance(cache, 'sven')}", Generic.class);

		myBmw.bind(cache, carOwner, sven);
		assert myBmw.getLinks(cache, carOwner).size() == 1;
	}

	public void testToOne() {
		Relation carOwner = expressions.evaluateMethodExpression("#{cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner'))}", Relation.class);
		carOwner.enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert carOwner.isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Generic myBmw = expressions.evaluateMethodExpression("#{cache.newType('Car').newInstance(cache, 'myBmw')}", Generic.class);
		Generic yourAudi = expressions.evaluateMethodExpression("#{cache.newType('Car').newInstance(cache, 'yourAudi')}", Generic.class);
		Generic sven = expressions.evaluateMethodExpression("#{cache.newType('Owner').newInstance(cache, 'sven')}", Generic.class);

		myBmw.bind(cache, carOwner, sven);
		yourAudi.bind(cache, carOwner, sven);

		assert myBmw.getLinks(cache, carOwner).size() == 1 : myBmw.getLinks(cache, carOwner);
		assert yourAudi.getLinks(cache, carOwner).size() == 1 : yourAudi.getLinks(cache, carOwner);
	}

	public void testSimpleReverse() {
		cache.newType("Person").newInstance(cache, "me")
				.bind(cache, (Relation) cache.newType("Car").addRelation(cache, "driver", cache.newType("Person")).enableSingularConstraint(cache, Statics.TARGET_POSITION), Statics.TARGET_POSITION, cache.newType("Car").newInstance(cache, "myBmw"));
		assert cache.newType("Person").newInstance(cache, "me").getLinks(cache, (Relation) cache.newType("Car").addRelation(cache, "driver", cache.newType("Person")).enableSingularConstraint(cache, Statics.TARGET_POSITION), Statics.TARGET_POSITION).size() == 1;
	}

	// public void testOneToManyManyToManyImpl() {
	// final Relation carPassenger = expressions.evaluateMethodExpression("#{cache.newType('Car').addRelation(cache, 'CarPassenger', cache.newType('Passenger')).enableSingularConstraint(cache, 1)}");
	// assert carPassenger.isSingularConstraintEnabled(cache, 1);
	//
	// expressions.evaluateMethodExpression("#cache.newType('Car').newInstance(cache, 'myBmw').setLink(cache, carPassenger, '30%', cache.newType('Passenger').newInstance(cache, 'michael'))}");
	// expressions.evaluateMethodExpression("#cache.newType('Car').newInstance(cache, 'myBmw').setLink(cache, carPassenger, '40%', cache.newType('Passenger').newInstance(cache, 'nicolas'))}");
	// new RollbackCatcher() {
	// @Override
	// public void intercept() {
	// expressions.evaluateMethodExpression("#{cache.newType('Car').newInstance(cache, 'myBmw').setLink(cache, carPassenger, '60%', cache.newType('Passenger').newInstance(cache, 'michael'))}");
	// }
	// }.assertIsCausedBy(SingularConstraintViolationException.class);
	// }

	// public void testToOneInheritance() {
	// assert expressions.evaluateMethodExpression(
	// "#{cache.newType('Car').newInstance(cache, 'myBmw').getLink(cache, (Property) cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner'))).getBaseComponent().equals(cache.newType('Car'))}", Boolean.class);
	// assert expressions
	// .evaluateMethodExpression(
	// "#{cache.newType('Car').newInstance(cache, 'myBmw').getLink(cache, (Property) cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner'))).equals(cache.newType('Car').newInstance(cache, 'myBmw').bind(cache, cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner')), cache.newType('Owner').newInstance(cache, 'you'))) : cache.newType('Car').newInstance(cache, 'myBmw').getLinks(cache, (Property) cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner')))}",
	// Boolean.class);
	// assert !expressions
	// .evaluateMethodExpression(
	// "#{cache.newType('Car').newInstance(cache, 'myBmw').getLinks(cache, cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner'))).contains(cache.newType('Car').setLink(cache, (Relation)cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner')).enableSingularConstraint(cache, 1), 'defaultOwner', cache.newType('Owner').newInstance(cache, 'me'))) : cache.newType('Car').newInstance(cache, 'myBmw').getLinks(cache, (Property) cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner')))}",
	// Boolean.class);
	// }

	public void testToOneSameValue() {

		cache.newType("Car").addRelation(cache, "CarOwner", cache.newType("Owner")).enableSingularConstraint(cache, Statics.BASE_POSITION);
		assert cache.newType("Car").addRelation(cache, "CarOwner", cache.newType("Owner")).isSingularConstraintEnabled(cache, Statics.BASE_POSITION);

		Link myBmwMe1 = cache.newType("Car").newInstance(cache, "myBmw").setLink(cache, cache.newType("Car").addRelation(cache, "CarOwner", cache.newType("Owner")), "value1", cache.newType("Owner").newInstance(cache, "me"));
		Link myBmwMe2 = cache.newType("Car").newInstance(cache, "myBmw").setLink(cache, cache.newType("Car").addRelation(cache, "CarOwner", cache.newType("Owner")), "value1", cache.newType("Owner").newInstance(cache, "me"));
		assert myBmwMe1 == myBmwMe2;

		assert cache.newType("Car").newInstance(cache, "myBmw").getLinks(cache, cache.newType("Car").addRelation(cache, "CarOwner", cache.newType("Owner"))).size() == 1;
	}

	// public void testToOneDifferentValue() {
	// SimpleContext context = new SimpleContext();
	// MethodExpression methodExpression = expressions.createMethodExpression(context, "#{cache.newType('Car')}", Generic.class, new Class[] {});// .addRelation(cache, 'CarOwner', cache.newType('Owner')).enableSingularConstraint(cache,
	// // 0)}", String.class, new Class[] {});// "#{cache.newType('Car').addRelation(cache, 'CarOwner',
	// // cache.newType('Owner')).enableSingularConstraint(cache, 0)}");
	// Generic g = (Generic) methodExpression.invoke(context, null);
	// g.log();
	//
	// // assert expressions.createMethodExpression(context, "#{cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner')).isSingularConstraintEnabled(cache, 0)}", Boolean.class, new Class[] {}).invoke(context, new Object[]{});
	//
	// // expressions.evaluateMethodExpression("#{cache.newType('Owner').newInstance(cache, 'me')}");
	// //
	// // expressions.createMethodExpression(new SimpleContext(),
	// // "#{cache.newType('Car').newInstance(cache, 'myBmw').setLink(cache, cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner')), 'value1', cache.newType('Owner').newInstance(cache, 'me'))}", String.class, new Class[] {});
	//
	// // Link myBmwMe2 = expressions.evaluateMethodExpression(
	// // "#{cache.newType('Car').newInstance(cache, 'myBmw').setLink(cache, cache.newType('Car').addRelation(cache, 'CarOwner', cache.newType('Owner')), 'value2', cache.newType('Owner').newInstance(cache, 'me'))}", Link.class);
	// // assert myBmwMe1 != myBmwMe2;
	// //
	// // assert cache.newType("Car").newInstance(cache, "myBmw").getLinks(cache, cache.newType("Car").addRelation(cache, "CarOwner", cache.newType("Owner"))).size() == 1 : cache.newType("Car").newInstance(cache, "myBmw")
	// // .getLinks(cache, (Property) cache.newType("Car").addRelation(cache, "CarOwner", cache.newType("Owner")));
	// // assert cache.newType("Car").newInstance(cache, "myBmw").getLinks(cache, cache.newType("Car").addRelation(cache, "CarOwner", cache.newType("Owner"))).findFirst("value1") == null;
	// // assert cache.newType("Car").newInstance(cache, "myBmw").getLinks(cache, cache.newType("Car").addRelation(cache, "CarOwner", cache.newType("Owner"))).findFirst("value2") != null;
	// }

	public void testCreateRelation() {
		Relation manPilotVehicle = expressions.evaluateMethodExpression("#{cache.newType('Man').addRelation(cache, 'pilot', cache.newType('Vehicle'))}");
		assert manPilotVehicle.isAlive(cache);
		assert manPilotVehicle.isAttribute();
		assert !manPilotVehicle.isEngine();
		assert manPilotVehicle.isAttributeOf(expressions.evaluateValueExpression("#{cache.newType('Vehicle')}", Generic.class));
		assert manPilotVehicle.isAttributeOf(expressions.evaluateValueExpression("#{cache.newType('Man')}", Generic.class));
		assert manPilotVehicle.isInstanceOf(expressions.evaluateValueExpression("#{engine}", Generic.class)) : manPilotVehicle.getMeta();
		assert manPilotVehicle.isRelation();
		assert !manPilotVehicle.isType();
	}

	public void testRentCar() {
		expressions.evaluateMethodExpression("#{cache.newType('Man').addRelation(cache, 'rent', cache.newType('Vehicle'))}");
		expressions.evaluateMethodExpression("#{cache.newType('Man').addRelation(cache, 'rent', cache.newType('Vehicle'), cache.newType('Vehicle'))}");

		// String manRentVehicle = "#{cache.newType('Man').addRelation('rent', (Object) cache.newType('Vehicle'))}";
		//
		// Attribute dateDeb = expressions.evaluateMethodExpression(manRentVehicle + ".addAttribute('dateDeb')}");
		// Attribute nbJour = expressions.evaluateMethodExpression(manRentVehicle + ".addAttribute('nbJour')}");
		// Attribute prixJour = expressions.evaluateMethodExpression(manRentVehicle + ".addAttribute('prixJour')}");

	}

	public void testSimpleTernary() {
		Relation carPersonTime = expressions.evaluateMethodExpression("#{cache.newType('Car').addRelation(cache, 'driver', cache.newType('Person'), cache.newType('Time'))}", Relation.class);
		assert carPersonTime.isAlive(cache);
		assert carPersonTime.isAttribute();
		assert !carPersonTime.isEngine();
		assert carPersonTime.isAttributeOf(expressions.evaluateValueExpression("#{cache.newType('Car')}", Generic.class));
		assert carPersonTime.isAttributeOf(expressions.evaluateValueExpression("#{cache.newType('Person')}", Generic.class));
		assert carPersonTime.isInstanceOf(expressions.evaluateValueExpression("#{engine}", Generic.class)) : carPersonTime.getMeta();
		assert carPersonTime.isRelation();
		assert !carPersonTime.isType();
	}

	public void testAddSingularConstraint() {
		Relation carPassengerTime = expressions.evaluateMethodExpression("#{cache.newType('Car').addRelation(cache, 'CarPassengerTime', cache.newType('Passenger'), cache.newType('time'))}", Relation.class);
		carPassengerTime.enableSingularConstraint(cache, Statics.TARGET_POSITION);
		assert carPassengerTime.isSingularConstraintEnabled(cache, Statics.TARGET_POSITION);
	}

	public void testCountAncestor() {
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addRelation(cache, 'pilot', cache.newType('Human')).getComponents().size()}", Integer.class) == 2;
	}

	public void testOnegetRelationsSnapshot() {
		expressions.evaluateMethodExpression("#{cache.newType('Car1').addRelation(cache, 'pilot', cache.newType('Human'))}");
		assert expressions.evaluateMethodExpression("#{cache.newType('Car1').getRelations(cache).size()}", Integer.class) == 1;
	}

	public void testDependency() {
		assert expressions.evaluateMethodExpression(
				"#{cache.getEngine().getInheritings(cache).containsAll(Arrays.asList(cache.newType('Vehicle'), cache.newType('Human'), cache.newType('Vehicle').addRelation(cache, 'pilot', cache.newType('Human')).getImplicit()))}", Boolean.class);
	}

	public void testIsRelation() {
		assert !expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addRelation(cache, 'pilot', cache.newType('Human')).inheritsFrom(cache.newType('Vehicle'))}", Boolean.class);
		assert !expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addRelation(cache, 'pilot', cache.newType('Human')).inheritsFrom(cache.newType('Human'))}", Boolean.class);
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addRelation(cache, 'pilot', cache.newType('Human')).inheritsFrom(cache.newType('Vehicle').addRelation(cache, 'pilot', cache.newType('Human')).getImplicit())}", Boolean.class);
		assert expressions.evaluateMethodExpression("#{cache.newType('Vehicle').addRelation(cache, 'pilot', cache.newType('Human')).isAttributeOf(cache.newType('Vehicle'))}", Boolean.class);
	}

	public void testIsTernaryRelation() {
		assert expressions.evaluateMethodExpression("#{cache.newType('Human').addRelation(cache, 'Games', cache.newType('Car'), cache.newType('Level')).isAttributeOf(cache.newType('Human'))}", Boolean.class);
		assert expressions.evaluateMethodExpression(
				"#{cache.newType('Human').addRelation(cache, 'Games', cache.newType('Car'), cache.newType('Level')).inheritsFrom(cache.newType('Human').addRelation(cache, 'Games', cache.newType('Car'), cache.newType('Level')).getImplicit())}",
				Boolean.class);
		assert !expressions.evaluateMethodExpression("#{cache.newType('Human').addRelation(cache, 'Games', cache.newType('Car'), cache.newType('Level')).inheritsFrom(cache.newType('Human'))}", Boolean.class);
		assert !expressions.evaluateMethodExpression("#{cache.newType('Human').addRelation(cache, 'Games', cache.newType('Car'), cache.newType('Level')).inheritsFrom(cache.newType('Car'))}", Boolean.class);
		assert !expressions.evaluateMethodExpression("#{cache.newType('Human').addRelation(cache, 'Games', cache.newType('Car'), cache.newType('Level')).inheritsFrom(cache.newType('Level'))}", Boolean.class);
	}

	public void testTwogetRelationsSnapshot() {
		assert cache.newType("Car").getRelations(cache).size() == 2 : cache.newType("Car").getRelations(cache);
		assert cache.newType("Car").getRelations(cache).contains(cache.newType("Car").addRelation(cache, "pilot", cache.newType("Human"))) : cache.newType("Car").getRelations(cache);
		assert cache.newType("Car").getRelations(cache).contains(cache.newType("Car").addRelation(cache, "passenger", cache.newType("Human"))) : cache.newType("Car").getRelations(cache);
	}

	// super cache
	public void testCacheOnCacheWithFlush() {
		Cache cache2 = expressions.evaluateValueExpression("#{cache.newSuperCache()}", Cache.class);
		Type vehicle = cache2.newType("Vehicle1");
		assert cache2.getEngine().getInheritings(cache2).contains(vehicle);
		assert !cache.getEngine().getInheritings(cache).contains(vehicle);
		cache2.flush();
		assert cache.getEngine().getInheritings(cache).contains(vehicle);
	}

}
