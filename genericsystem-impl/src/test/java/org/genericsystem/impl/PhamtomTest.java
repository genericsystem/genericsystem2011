//package org.genericsystem.impl;
//
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.Iterator;
//import org.genericsystem.core.Cache;
//import org.genericsystem.core.Generic;
//import org.genericsystem.core.GenericImpl;
//import org.genericsystem.core.GenericSystem;
//import org.genericsystem.core.Snapshot;
//import org.genericsystem.core.Statics;
//import org.genericsystem.exception.UniqueStructuralValueConstraintViolationException;
//import org.genericsystem.generic.Attribute;
//import org.genericsystem.generic.Holder;
//import org.genericsystem.generic.Link;
//import org.genericsystem.generic.Relation;
//import org.genericsystem.generic.Type;
//import org.testng.annotations.Test;
//
//@Test
//public class PhamtomTest extends AbstractTest {
//
//	public void testRemovePhantoms() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//		Attribute power = vehicle.setAttribute("power");
//		Holder defaultPowerForVehicule = vehicle.setValue(power, "1");
//		Holder defaultPowerForCar = car.setValue(power, "2");
//		Generic myCar = car.newInstance("myCar");
//
//		Snapshot<Serializable> powerValues = myCar.getValues(power);
//		assert powerValues.size() == 2 : powerValues.size();
//		assert powerValues.containsAll(Arrays.asList("1", "2")) : powerValues;
//
//		myCar.removeHolder(defaultPowerForVehicule);
//		myCar.removeHolder(defaultPowerForCar);
//
//		Snapshot<Serializable> powerValues2 = myCar.getValues(power);
//		assert powerValues2.isEmpty() : powerValues2.size();
//
//		myCar.removePhantoms(power);
//
//		Snapshot<Serializable> powerValues3 = myCar.getValues(power);
//		assert powerValues3.size() == 2 : powerValues3.size();
//		assert powerValues3.containsAll(Arrays.asList("1", "2")) : powerValues3;
//	}
//
//	public void testGetHolders() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Attribute power = vehicle.setAttribute("power");
//		Holder defaultPower = vehicle.setValue(power, "123");
//
//		Generic myVehicle = vehicle.newInstance("myCar");
//		assert myVehicle.getValue(power) == "123";
//
//		Snapshot<Holder> holdersWithoutPhantoms = myVehicle.getHolders(power, false);
//		assert holdersWithoutPhantoms.size() == 1 : holdersWithoutPhantoms.size();
//		assert holdersWithoutPhantoms.get(0).getValue().equals("123") : holdersWithoutPhantoms.get(0).getValue();
//
//		myVehicle.removeHolder(defaultPower);
//		assert myVehicle.getValue(power) == null;
//
//		Snapshot<Holder> holdersWithPhantoms = myVehicle.getHolders(power, true);
//		assert holdersWithPhantoms.size() == 1 : holdersWithPhantoms.size();
//		assert holdersWithPhantoms.get(0).getValue() == null : holdersWithPhantoms.get(0).getValue();
//	}
//
//	public void testRemoveHolder() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//		Attribute power = vehicle.setAttribute("power");
//		Holder defaultPower = car.setValue(power, "123");
//
//		Generic myCar = car.newInstance("myCar");
//
//		myCar.removeHolder(defaultPower);
//		assert myCar.getValue(power) == null;
//
//		Holder holder200 = myCar.setValue(power, "200");
//
//		myCar.removeHolder(holder200);
//		assert myCar.getValue(power) == null : myCar.getValue(power);
//	}
//
//	public void testRemoveHolderMultipleValues() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//		Attribute power = vehicle.setAttribute("power");
//		Holder defaultPower = car.setValue(power, "123");
//
//		Generic myCar = car.newInstance("myCar");
//
//		myCar.removeHolder(defaultPower);
//		assert myCar.getValue(power) == null;
//
//		Holder holder100 = myCar.setValue(power, "100");
//		Holder holder200 = myCar.setValue(power, "200");
//		assert myCar.getValues(power).size() == 2 : myCar.getValues(power).size();
//		assert myCar.getValues(power).containsAll(Arrays.asList("100", "200")) : myCar.getValues(power);
//
//		myCar.removeHolder(holder100);
//		assert myCar.getValues(power).size() == 1 : myCar.getValues(power).size();
//		assert myCar.getValue(power).equals("200") : myCar.getValues(power);
//
//		myCar.removeHolder(holder200);
//		assert myCar.getValues(power).size() == 0 : myCar.getValues(power).size();
//		assert myCar.getValue(power) == null : myCar.getValues(power);
//	}
//
//	public void testRemoveHolderOverrideInheritedValue() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Attribute power = car.setAttribute("power");
//		Holder defaultHolder = car.setValue(power, "123");
//
//		Generic myCar = car.newInstance("myCar");
//		assert myCar.getValue(power).equals("123");
//
//		Holder holder200 = myCar.setValue(power, "200");
//
//		Holder holder100 = myCar.setValue(defaultHolder, "100");
//		assert myCar.getValues(power).containsAll(Arrays.asList("100", "200")) : myCar.getValues(power);
//
//		myCar.removeHolder(holder200);
//		assert myCar.getValue(power).equals("100") : myCar.getValues(power);
//
//		myCar.removeHolder(holder100);
//		assert myCar.getValue(power).equals("123") : myCar.getValues(power);
//	}
//
//	public void testRemoveHolderOverrideInheritedValue2() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Attribute power = car.setAttribute("power");
//		Holder defaultHolder = car.setValue(power, "123");
//
//		Generic myCar = car.newInstance("myCar");
//		assert myCar.getValue(power).equals("123");
//
//		Holder holder200 = myCar.setValue(power, "200");
//
//		Holder holder100 = myCar.setValue(defaultHolder, "100");
//		assert myCar.getValues(power).containsAll(Arrays.asList("100", "200")) : myCar.getValues(power);
//
//		myCar.removeHolder(holder100);
//		assert myCar.getValues(power).containsAll(Arrays.asList("123", "200")) : myCar.getValues(power);
//
//		myCar.removeHolder(holder200);
//		assert myCar.getValue(power).equals("123") : myCar.getValues(power);
//	}
//
//	public void testAliveWithStructural() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//		Attribute vehiclePower = vehicle.setAttribute("power");
//
//		assert car.getAttributes().contains(vehiclePower);
//		car.cancelAll(vehiclePower, Statics.STRUCTURAL);
//		Iterator<Generic> iterator = ((GenericImpl) car).holdersIterator(Statics.STRUCTURAL, vehiclePower, Statics.MULTIDIRECTIONAL);
//		Generic phantom = iterator.next();
//		// car.restore( vehiclePower);
//		car.clearAll(vehiclePower, Statics.STRUCTURAL);
//		assert !phantom.isAlive();
//	}
//
//	public void testAliveWithConcrete() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Attribute vehiclePower = vehicle.setAttribute("power");
//		Holder defaultPower = vehicle.setValue(vehiclePower, "123");
//		Generic myVehicle = vehicle.newInstance("myVehicle");
//
//		assert myVehicle.getValue(vehiclePower) == "123";
//		myVehicle.setValue(defaultPower, null);
//		Generic phantom = ((GenericImpl) myVehicle).getHolderByValue(Statics.CONCRETE, defaultPower, null);
//		myVehicle.setValue(defaultPower, "123");
//		assert !phantom.isAlive();
//
//		myVehicle.setValue(defaultPower, null);
//		phantom = ((GenericImpl) myVehicle).getHolderByValue(Statics.CONCRETE, defaultPower, null);
//		myVehicle.setValue(defaultPower, "555");
//		assert phantom.isAlive();
//
//		vehiclePower.enableSingularConstraint();
//		assert defaultPower.isSingularConstraintEnabled();
//		myVehicle.setValue(defaultPower, "235");
//		assert myVehicle.getValue(vehiclePower) == "235";
//	}
//
//	public void cancelAttribute() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		final Type vehicle = cache.newType("Vehicle");
//		final Type car = vehicle.newSubType("Car");
//		final Attribute vehiclePower = vehicle.setAttribute("power");
//
//		assert vehicle.getAttributes().contains(vehiclePower);
//		vehicle.setValue(vehiclePower, null);
//		assert vehicle.getAttributes().contains(vehiclePower);
//		((GenericImpl) car).setSubAttribute(vehiclePower, null);
//		assert vehicle.getAttributes().contains(vehiclePower);
//		assert !car.getAttributes().contains(vehiclePower) : car.getAttributes();
//		car.getAttribute(null).remove();
//		assert vehicle.getAttributes().contains(vehiclePower);
//		assert car.getAttributes().contains(vehiclePower);
//	}
//
//	public void cancelAttributeWithInheritsBase() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//		Attribute vehiclePower = vehicle.setAttribute("power");
//
//		assert car.getAttributes().contains(vehiclePower);
//		((GenericImpl) car).setSubAttribute(vehiclePower, null);
//		assert !car.getAttributes().contains(vehiclePower);
//	}
//
//	public void cancelAttributeWithInheritsAttribute() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		final Type car = vehicle.newSubType("Car");
//		final Attribute vehiclePower = vehicle.setAttribute("power");
//
//		assert car.getAttributes().contains(vehiclePower);
//		Attribute carPower = car.setAttribute("power");
//		assert car.getAttributes().contains(carPower);
//		assert carPower.inheritsFrom(vehiclePower);
//
//		((GenericImpl) car).setSubAttribute(vehiclePower, null);
//		assert !carPower.isAlive();
//	}
//
//	public void cancelAndRestoreRelation() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type human = cache.newType("Human");
//		Relation vehicleHuman = vehicle.setRelation("VehicleHuman", human);
//		Type car = vehicle.newSubType("Car");
//
//		assert vehicle.getRelations().size() == 1;
//		assert vehicle.getRelations().contains(vehicleHuman);
//		assert car.getRelations().size() == 1;
//		assert car.getRelations().contains(vehicleHuman);
//
//		// car.cancel( vehicleHuman);
//		((GenericImpl) car).setSubAttribute(vehicleHuman, null, human);
//
//		assert vehicle.getRelations().size() == 1;
//		assert car.getRelations().isEmpty() : car.getRelations();
//
//		// car.restore( vehicleHuman);
//		car.getAttribute(vehicleHuman, null).remove();
//
//		assert vehicle.getRelations().size() == 1;
//		assert vehicle.getRelations().contains(vehicleHuman);
//		assert car.getRelations().size() == 1 : car.getRelations();
//		assert car.getRelations().contains(vehicleHuman) : car.getRelations();
//	}
//
//	public void testPhantomHierarchyRelation() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type human = cache.newType("Human");
//		Relation vehicleHuman = vehicle.setRelation("VehicleHuman", human);
//		Type car = vehicle.newSubType("Car");
//		Type convertible = car.newSubType("Convertible");
//
//		assert vehicle.getRelations().contains(vehicleHuman);
//		assert car.getRelations().contains(vehicleHuman);
//		assert convertible.getRelations().contains(vehicleHuman);
//
//		car.cancelAll(vehicleHuman, Statics.STRUCTURAL);
//		assert vehicle.getRelations().contains(vehicleHuman);
//		assert car.getRelations().size() == 0;
//		assert !car.getRelations().contains(vehicleHuman);
//		assert convertible.getRelations().size() == 0;
//		assert !convertible.getRelations().contains(vehicleHuman);
//
//		car.clearAll(vehicleHuman, Statics.STRUCTURAL);
//		assert vehicle.getRelations().contains(vehicleHuman);
//		assert car.getRelations().contains(vehicleHuman);
//		assert convertible.getRelations().contains(vehicleHuman);
//
//		convertible.cancelAll(vehicleHuman, Statics.STRUCTURAL);
//		assert vehicle.getRelations().contains(vehicleHuman);
//		assert car.getRelations().contains(vehicleHuman);
//		assert convertible.getRelations().size() == 0;
//		assert !convertible.getRelations().contains(vehicleHuman);
//	}
//
//	public void testPhantomMultiHierachyRelation() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type color = cache.newType("Color");
//		Relation vehicleColor = vehicle.setRelation("VehicleColor", color);
//		Type car = vehicle.newSubType("Car");
//		Type breakVehicle = car.newSubType("BreakVehicle");
//		Type convertible = car.newSubType("Convertible");
//
//		car.cancelAll(vehicleColor, Statics.STRUCTURAL);
//		assert vehicle.getRelations().size() == 1;
//		assert car.getRelations().size() == 0;
//		assert breakVehicle.getRelations().size() == 0;
//		assert convertible.getRelations().size() == 0;
//
//		car.clearAll(vehicleColor, Statics.STRUCTURAL);
//		assert vehicle.getRelations().size() == 1;
//		assert car.getRelations().get(0).equals(vehicleColor) : car.getRelations();
//		assert breakVehicle.getRelations().get(0).equals(vehicleColor);
//		assert convertible.getRelations().get(0).equals(vehicleColor);
//	}
//
//	public void testRelationsWithSameName() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		final Type vehicle = cache.newType("Vehicle");
//		Type human = cache.newType("Human");
//		final Type color = cache.newType("Color");
//
//		Relation r1 = vehicle.setRelation("VehicleHuman", human);
//		cache.flush();
//		new RollbackCatcher() {
//
//			@Override
//			public void intercept() {
//				vehicle.setRelation("VehicleHuman", color);
//			}
//		}.assertIsCausedBy(UniqueStructuralValueConstraintViolationException.class);
//		assert r1 == cache.getEngine().getSubType("VehicleHuman") : cache.getEngine().getSubType("VehicleHuman").info();
//	}
//
//	public void testAttributeWithGetInstances() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//		Attribute vehiclePower = vehicle.setRelation("power");
//
//		Generic myVehicle = vehicle.newInstance("myVehicle");
//		Generic myCar = car.newInstance("myCar");
//
//		myVehicle.setValue(vehiclePower, "123");
//		myCar.setValue(vehiclePower, "256");
//
//		assert vehiclePower.getInstances().filter(new Snapshot.Filter<Generic>() {
//
//			@Override
//			public boolean isSelected(Generic element) {
//				return element.getValue() == null;
//			}
//		}).isEmpty();
//		assert vehiclePower.getAllInstances().filter(new Snapshot.Filter<Generic>() {
//
//			@Override
//			public boolean isSelected(Generic element) {
//				return element.getValue() == null;
//			}
//		}).isEmpty();
//	}
//
//	public void testAttributeWithGetDirectSubTypes() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//		Attribute vehiclePower = vehicle.setRelation("power");
//
//		Generic myVehicle = vehicle.newInstance("myVehicle");
//		Generic myCar = car.newInstance("myCar");
//
//		myVehicle.setValue(vehiclePower, "123");
//		myCar.setValue(vehiclePower, "256");
//
//		assert vehiclePower.getDirectSubTypes().isEmpty();
//		assert vehiclePower.getSubTypes().isEmpty();
//	}
//
//	public void testAttributeWithGetInheritings() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type vehicle = cache.newType("Vehicle");
//		Type car = vehicle.newSubType("Car");
//		Attribute vehiclePower = vehicle.setRelation("power");
//
//		Generic myVehicle = vehicle.newInstance("myVehicle");
//		Generic myCar = car.newInstance("myCar");
//
//		myVehicle.setValue(vehiclePower, "123");
//		myCar.setValue(vehiclePower, "256");
//
//		assert vehiclePower.getInheritings().filter(new Snapshot.Filter<Generic>() {
//			@Override
//			public boolean isSelected(Generic element) {
//				return element.getValue() == null;
//			}
//		}).isEmpty();
//	}
//
//	public void cancelDefaultAttribute() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Attribute carPower = car.setProperty("power");
//		Holder defaultPower = car.setValue(carPower, "233");
//		Generic myCar = car.newInstance("myCar");
//		assert myCar.getHolder(Statics.CONCRETE, carPower).equals(defaultPower);
//
//		myCar.setValue(carPower, null);
//		Generic phantom = ((GenericImpl) myCar).getHolderByValue(Statics.CONCRETE, defaultPower, null);
//		assert ((GenericImpl) myCar).getHolderByValue(Statics.CONCRETE, defaultPower, null).getValue() == null;
//		assert myCar.getHolder(Statics.CONCRETE, carPower) == null : myCar.getHolder(Statics.CONCRETE, carPower);
//
//		myCar.setValue(carPower, "233");
//		assert !phantom.isAlive();
//		assert myCar.getValue(carPower).equals("233");
//		assert ((GenericImpl) myCar).getHolderByValue(Statics.CONCRETE, defaultPower, null) == null;
//	}
//
//	public void cancelDefaultAttributeKo() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		final Attribute carPower = car.setProperty("power");
//		car.setValue(carPower, "233");
//		final Generic mycar = car.newInstance("myCar");
//		assert mycar.getValue(carPower).equals("233");
//		mycar.setValue(carPower, null);
//		assert mycar.getHolder(Statics.CONCRETE, carPower) == null;
//	}
//
//	public void cancelDefaultRelation() {
//		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Type color = cache.newType("Color");
//		final Relation carColor = car.setRelation("carColor", color);
//		final Generic red = color.newInstance("red");
//		final Link defaultColor = car.setLink(carColor, "defaultColor", red);
//		assert defaultColor.isConcrete();
//		final Generic myCar = car.newInstance("myCar");
//		assert myCar.getTargets(carColor).contains(red);
//		myCar.setLink(carColor, null, red);
//		myCar.setLink(defaultColor, null, red);
//		assert ((GenericImpl) myCar).getHolder(Statics.CONCRETE, defaultColor, null, red) == null;
//	}
//
//	public void cancelDefaultRelationKo() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Generic myCar = car.newInstance("myCar");
//		Type color = cache.newType("Color");
//		Generic red = color.newInstance("red");
//		Generic green = color.newInstance("green");
//		Relation carColor = car.setRelation("carColor", color);
//
//		car.bind(carColor, red);
//		car.bind(carColor, green);
//		assert myCar.getTargets(carColor).contains(red);
//		assert myCar.getTargets(carColor).contains(green);
//
//		try {
//			myCar.setValue(carColor, null);
//		} catch (IllegalStateException ignore) {
//
//		}
//		myCar.cancelAll(carColor, Statics.CONCRETE, red);
//		assert !myCar.getTargets(carColor).contains(red);
//		assert myCar.getTargets(carColor).contains(green);
//		myCar.bind(carColor, red);
//		assert myCar.getTargets(carColor).contains(red);
//		assert myCar.getTargets(carColor).contains(green);
//		myCar.cancelAll(carColor, Statics.CONCRETE);
//		assert !myCar.getTargets(carColor).contains(green);
//		assert !myCar.getTargets(carColor).contains(red);
//		assert myCar.getTargets(carColor).isEmpty();
//	}
//
//	public void testTwoCancel() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Attribute carPower = car.setProperty("power");
//		Holder defaultPower = car.setValue(carPower, "233");
//		Generic mycar = car.newInstance("myCar");
//		assert mycar.getValue(carPower).equals("233");
//		mycar.cancelAll(defaultPower, Statics.CONCRETE);
//		assert mycar.getValue(carPower) == null;
//		mycar.cancelAll(defaultPower, Statics.CONCRETE);
//		assert mycar.getValue(carPower) == null;
//		mycar.clearAll(defaultPower, Statics.CONCRETE);
//		assert mycar.getValue(carPower).equals("233");
//	}
//
//	public void testTwoRestore() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Attribute carPower = car.setProperty("power");
//		Generic myCar = car.newInstance("myCar");
//		assert myCar.setValue(carPower, null) == null; // Do nothing;
//
//		Holder defaultPower = car.setValue(carPower, "233");
//		assert myCar.getValue(carPower).equals("233");
//		myCar.setValue(defaultPower, null);
//		assert myCar.getValue(carPower) == null;
//		// ((GenericImpl) myCar).getHolderByValue( defaultPower, null).remove();
//		myCar.clearAll(defaultPower, Statics.CONCRETE);
//		assert myCar.getValue(carPower).equals("233");
//		myCar.clearAll(defaultPower, Statics.CONCRETE);
//		assert myCar.getValue(carPower).equals("233");
//		myCar.cancelAll(defaultPower, Statics.CONCRETE);
//		assert myCar.getValue(carPower) == null;
//	}
//
//	public void testAnyCancelRestore() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Attribute carPower = car.setProperty("power");
//		Holder defaultPower = car.setValue(carPower, "233");
//		Generic mycar = car.newInstance("myCar");
//		assert mycar.getValue(carPower).equals("233");
//		mycar.cancelAll(defaultPower, Statics.CONCRETE);
//		mycar.clearAll(defaultPower, Statics.CONCRETE);
//		mycar.clearAll(defaultPower, Statics.STRUCTURAL);
//		mycar.cancelAll(defaultPower, Statics.CONCRETE);
//		mycar.cancelAll(defaultPower, Statics.CONCRETE);
//		mycar.clearAll(defaultPower, Statics.CONCRETE);
//		assert mycar.getValue(carPower).equals("233");
//	}
//
//	public void testTwoCancelRelation() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Type color = cache.newType("Color");
//		Relation carColor = car.setRelation("CarColor", color);
//		Generic red = color.newInstance("Red");
//		Link defaultCarColor = car.setLink(carColor, "defaultCarColor", red);
//		Generic myCar = car.newInstance("myCar");
//		assert myCar.getTargets(carColor).contains(red);
//		myCar.cancelAll(defaultCarColor, Statics.CONCRETE);
//		assert myCar.getTargets(carColor).isEmpty();
//		myCar.cancelAll(defaultCarColor, Statics.CONCRETE);
//		assert myCar.getTargets(carColor).isEmpty();
//		myCar.clearAll(defaultCarColor, Statics.CONCRETE);
//		assert myCar.getTargets(carColor).contains(red);
//	}
//
//	public void testTwoRestoreRelation() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Type color = cache.newType("Color");
//		Relation carColor = car.setProperty("CarColor", color);
//		Generic red = color.newInstance("Red");
//		Link defaultCarColor = car.setLink(carColor, "defaultCarColor", red);
//		Generic myCar = car.newInstance("myCar");
//		assert myCar.getTargets(carColor).contains(red);
//		// myCar.cancel( defaultCarColor);
//
//		// myCar.setHolder( defaultCarColor, null, red);// phantomize
//		// assert myCar.getTargets( carColor).isEmpty();
//		//
//		// ((GenericImpl) myCar).getHolderByValue( defaultCarColor, null, red).remove();// restore
//		// assert myCar.getLink( carColor, red).equals(defaultCarColor);
//
//		myCar.setLink(defaultCarColor, null, red); // phantomize
//		assert myCar.getTargets(carColor).isEmpty();
//
//		Link link = myCar.setLink(carColor, "toto", red);// restore
//		assert myCar.getLink(carColor, red).equals(link);
//
//	}
//
//	public void testAnyCancelRestoreRelation() {
//		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
//		Type car = cache.newType("Car");
//		Type color = cache.newType("Color");
//		Relation carColor = car.setRelation("CarColor", color);
//		Generic red = color.newInstance("Red");
//		Link defaultCarColor = car.setLink(carColor, "defaultCarColor", red);
//		Generic myCar = car.newInstance("myCar");
//		assert myCar.getTargets(carColor).contains(red);
//		myCar.cancelAll(defaultCarColor, Statics.CONCRETE);
//		myCar.clearAll(defaultCarColor, Statics.CONCRETE);
//		myCar.clearAll(defaultCarColor, Statics.STRUCTURAL);
//		myCar.cancelAll(defaultCarColor, Statics.CONCRETE);
//		myCar.cancelAll(defaultCarColor, Statics.CONCRETE);
//		myCar.clearAll(defaultCarColor, Statics.CONCRETE);
//		assert myCar.getTargets(carColor).contains(red);
//	}
//
// }
