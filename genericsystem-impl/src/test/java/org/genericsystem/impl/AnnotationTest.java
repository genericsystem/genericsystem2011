package org.genericsystem.impl;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AnnotationTest extends AbstractTest {

	public void testType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Human.class, Myck.class);
		Type vehicle = cache.find(Vehicle.class);
		Type human = cache.find(Human.class);
		Type myck = cache.find(Myck.class);
		assert vehicle.isStructural();
		assert human.isStructural();
		assert myck.isConcrete();
	}

	public void testSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Car.class);
		Type vehicle = cache.find(Vehicle.class);
		Type car = cache.find(Car.class);
		assert vehicle.isStructural();
		assert car.isStructural();
		assert vehicle.getDirectSubTypes(cache).size() == 1;
		assert vehicle.getDirectSubTypes(cache).contains(car);
		assert car.getSupers().size() == 2 : car.getSupers();
		assert car.getSupers().contains(vehicle);
		assert car.getSupers().contains(car.getImplicit());
		assert !car.equals(car.getImplicit());
	}

	public void testAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Power.class);
		Type vehicle = cache.find(Vehicle.class);
		Attribute power = cache.find(Power.class);
		assert power.isStructural();
		assert vehicle.getAttributes(cache).contains(power);
	}

	public void testSubAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Car.class, ElectrikPower.class);
		Type car = cache.find(Car.class);
		Attribute electrikPowerCar = cache.find(ElectrikPower.class);
		assert car.getAttributes(cache).contains(electrikPowerCar) : car.getAttributes(cache);
	}

	public void testAttributeOnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(ElectrikPower.class, Unit.class);
		Attribute electrikPowerCar = cache.find(ElectrikPower.class);
		Attribute unit = cache.find(Unit.class);
		assert unit.isAttributeOf(electrikPowerCar);
		assert unit.isAttribute();
		assert unit.isStructural();
		assert electrikPowerCar.getAttributes(cache).contains(unit);
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Human.class, HumanPossessVehicle.class);
		cache.find(Vehicle.class);
		Type human = cache.find(Human.class);
		Relation possess = cache.find(HumanPossessVehicle.class);
		assert human.getRelations(cache).size() == 1;
		assert human.getRelations(cache).contains(possess);
	}

	public void testSubRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Car.class, Human.class, HumanPossessVehicle.class, HumanPossessCar.class);
		cache.find(Car.class);
		Type human = cache.find(Human.class);
		Relation possessVehicle = cache.find(HumanPossessVehicle.class);
		Relation possessCar = cache.find(HumanPossessCar.class);
		assert human.getRelations(cache).size() == 1 : human.getRelations(cache);
		assert human.getRelations(cache).contains(possessCar) : human.getRelations(cache);
		assert possessCar.inheritsFrom(possessVehicle);
	}

	public void testSubRelationSymetric() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Car.class, Human.class, Man.class, HumanPossessVehicle.class, ManPossessCar.class);
		cache.find(Car.class);
		Type human = cache.find(Human.class);
		Type man = cache.find(Man.class);
		Relation humanPossessVehicle = cache.find(HumanPossessVehicle.class);
		Relation manPossessCar = cache.find(ManPossessCar.class);
		assert human.getRelations(cache).size() == 1;
		assert human.getRelations(cache).contains(humanPossessVehicle);
		assert man.getRelations(cache).size() == 1;
		assert man.getRelations(cache).contains(manPossessCar) : man.getRelations(cache);
		assert manPossessCar.inheritsFrom(humanPossessVehicle);
	}

	public void testRelationTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Human.class, Time.class, HumanPossessVehicleTime.class);
		cache.find(Vehicle.class);
		Type human = cache.find(Human.class);
		cache.find(Time.class);
		Relation possess = cache.find(HumanPossessVehicleTime.class);
		assert human.getRelations(cache).size() == 1;
		assert human.getRelations(cache).contains(possess);
	}

	public void testgetDirectSubTypesWithDiamondProblem() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(GraphicComponent.class, Window.class, Selectable.class, SelectableWindow.class);
		Type graphicComponent = cache.find(GraphicComponent.class);
		Type window = cache.find(Window.class);
		Type selectable = cache.find(Selectable.class);
		Type selectableWindow = cache.find(SelectableWindow.class);

		assert graphicComponent.getDirectSubTypes(cache).size() == 2 : graphicComponent.getDirectSubTypes(cache);
		assert graphicComponent.getDirectSubTypes(cache).contains(selectable);
		assert graphicComponent.getDirectSubTypes(cache).contains(window) : graphicComponent.getDirectSubTypes(cache);

		assert graphicComponent.getSubTypes(cache).size() == 3 : graphicComponent.getSubTypes(cache);
		assert graphicComponent.getSubTypes(cache).contains(selectable);
		assert graphicComponent.getSubTypes(cache).contains(window);
		assert graphicComponent.getSubTypes(cache).contains(selectableWindow);

		assert window.getDirectSubTypes(cache).size() == 1 : window.getDirectSubTypes(cache);
		assert window.getDirectSubTypes(cache).contains(selectableWindow) : window.getDirectSubTypes(cache);

		assert selectable.getDirectSubTypes(cache).size() == 1 : selectable.getDirectSubTypes(cache);
		assert selectable.getDirectSubTypes(cache).contains(selectableWindow) : selectable.getDirectSubTypes(cache);

		assert selectableWindow.getDirectSubTypes(cache).size() == 0;
		assert selectableWindow.inheritsFrom(selectable);
		assert selectableWindow.inheritsFrom(window);
		assert selectableWindow.inheritsFrom(graphicComponent);
	}

	public void testValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(SelectableWindow.class, Size.class, Selected.class, MySelectableWindow.class).start();
		Type selectableWindow = cache.find(SelectableWindow.class);
		Attribute size = cache.find(Size.class);
		Attribute selectedSelectable = cache.find(Selected.class);
		Generic mySelectableWindow = cache.find(MySelectableWindow.class);
		assert mySelectableWindow.inheritsFrom(selectableWindow) : mySelectableWindow.info() + selectableWindow.info();

		assert mySelectableWindow.inheritsFrom(cache.find(Selectable.class));
		Holder vTrue = mySelectableWindow.setValue(cache, selectedSelectable, true);
		Holder v12 = mySelectableWindow.setValue(cache, size, 12);

		assert selectableWindow.getInstances(cache).size() == 1 : selectableWindow.getInstances(cache);
		assert selectableWindow.getInstances(cache).contains(mySelectableWindow);
		assert mySelectableWindow.getHolders(cache, size).size() == 1 : mySelectableWindow.getHolders(cache, size);
		assert mySelectableWindow.getHolders(cache, size).contains(v12);
		assert mySelectableWindow.getHolders(cache, selectedSelectable).size() == 1;
		assert mySelectableWindow.getHolders(cache, selectedSelectable).contains(vTrue);
	}

	public void testMultiInheritanceComplexStructural() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Games.class, Children.class, Vehicle.class, Human.class, ChildrenGames.class, Transformer.class, TransformerChildrenGames.class);

		Type games = cache.find(Games.class);
		Type children = cache.find(Children.class);
		Type vehicle = cache.find(Vehicle.class);
		Type human = cache.find(Human.class);
		Type childrenGames = cache.find(ChildrenGames.class);
		Type transformer = cache.find(Transformer.class);
		Type transformerChildrenGames = cache.find(TransformerChildrenGames.class);

		assert transformerChildrenGames.inheritsFrom(games);
		assert transformerChildrenGames.inheritsFrom(children);
		assert transformerChildrenGames.inheritsFrom(vehicle);
		assert transformerChildrenGames.inheritsFrom(human);

		assert transformerChildrenGames.inheritsFrom(childrenGames);
		assert transformerChildrenGames.getSupers().contains(childrenGames) : transformerChildrenGames.info();
		assert transformerChildrenGames.getSupers().contains(transformer);
		assert transformerChildrenGames.getSupers().contains(transformerChildrenGames.getImplicit());
		assert transformerChildrenGames.getInheritings(cache).size() == 0;
		assert transformerChildrenGames.getComposites(cache).size() == 0;

		assert childrenGames.getSupers().contains(games);
		assert childrenGames.getSupers().contains(children);
		assert childrenGames.getSupers().contains(childrenGames.getImplicit());
		assert childrenGames.getInheritings(cache).contains(transformerChildrenGames);
		assert childrenGames.getComposites(cache).size() == 0;

		assert transformer.getSupers().contains(vehicle);
		assert transformer.getSupers().contains(human);
		assert transformer.getSupers().contains(transformer.getImplicit());
		assert transformer.getInheritings(cache).contains(transformerChildrenGames);
		assert transformer.getComposites(cache).size() == 0;
	}

	public void testMultiInheritanceComplexValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(MyGames.class, MyChildren.class, MyVehicle.class, Myck.class, MyChildrenGames.class, ChildrenGames.class, MyTransformer.class, Transformer.class, TransformerChildrenGames.class,
				MyTransformerChildrenGames.class);
		Generic myGames = cache.find(MyGames.class);
		Generic myChildren = cache.find(MyChildren.class);
		Generic myVehicle = cache.find(MyVehicle.class);
		Generic myck = cache.find(Myck.class);
		Generic myChildrenGames = cache.find(MyChildrenGames.class);
		Type childrenGames = cache.find(ChildrenGames.class);
		Generic myTransformer = cache.find(MyTransformer.class);
		Type transformer = cache.find(Transformer.class);
		Type transformerChildrenGames = cache.find(TransformerChildrenGames.class);
		Generic myTransformerChildrenGames = cache.find(MyTransformerChildrenGames.class);

		assert myTransformerChildrenGames.inheritsFrom(transformerChildrenGames);
		assert ((GenericImpl) transformerChildrenGames).isSuperOf(myTransformerChildrenGames);

		assert !myTransformerChildrenGames.inheritsFrom(myGames);
		assert !myTransformerChildrenGames.inheritsFrom(myChildren);
		assert !myTransformerChildrenGames.inheritsFrom(myVehicle);
		assert !myTransformerChildrenGames.inheritsFrom(myck);
		assert !myTransformerChildrenGames.inheritsFrom(myChildrenGames);
		assert !myTransformerChildrenGames.inheritsFrom(myTransformer);
		assert myTransformerChildrenGames.getSupers().contains(transformerChildrenGames);
		assert myTransformerChildrenGames.getSupers().contains(myTransformerChildrenGames.getImplicit());
		assert myTransformerChildrenGames.getInheritings(cache).size() == 0;
		assert myTransformerChildrenGames.getComposites(cache).size() == 0;

		assert transformerChildrenGames.getInheritings(cache).contains(myTransformerChildrenGames);
		assert myTransformerChildrenGames.isInstanceOf(transformerChildrenGames);

		assert !myChildrenGames.inheritsFrom(myGames);
		assert !myChildrenGames.inheritsFrom(myChildren);
		assert myChildrenGames.getSupers().contains(childrenGames);
		assert myChildrenGames.getSupers().contains(myChildrenGames.getImplicit());
		assert myChildrenGames.getInheritings(cache).size() == 0;
		assert myChildrenGames.getComposites(cache).size() == 0;

		assert childrenGames.getInheritings(cache).contains(myChildrenGames);
		assert myChildrenGames.isInstanceOf(childrenGames);

		assert !myTransformer.inheritsFrom(myVehicle);
		assert !myTransformer.inheritsFrom(myck);
		assert myTransformer.getSupers().contains(transformer);
		assert myTransformer.getSupers().contains(myTransformer.getImplicit());
		assert myTransformer.getInheritings(cache).size() == 0;
		assert myTransformer.getComposites(cache).size() == 0;

		assert transformer.getInheritings(cache).contains(myTransformer);
		assert myTransformer.isInstanceOf(transformer);
	}

	@SystemGeneric
	public static class Games {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	public static class MyGames extends Games {
	}

	@SystemGeneric
	public static class Children {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	public static class MyChildren extends Children {
	}

	@SystemGeneric
	@Extends(value = Engine.class, others = { Games.class, Children.class })
	public static class ChildrenGames {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	public static class MyChildrenGames extends ChildrenGames {
	}

	@SystemGeneric
	@Extends(value = Engine.class, others = { Human.class, Vehicle.class })
	public static class Transformer {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	public static class MyTransformer extends Transformer {
	}

	@SystemGeneric
	@Extends(value = Transformer.class, others = { Transformer.class, ChildrenGames.class })
	public static class TransformerChildrenGames {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	public static class MyTransformerChildrenGames extends TransformerChildrenGames {
	}

	@SystemGeneric
	public static class GraphicComponent {

	}

	@SystemGeneric
	@Components(GraphicComponent.class)
	public static class Size {

	}

	@SystemGeneric
	public static class Window extends GraphicComponent {

	}

	@SystemGeneric
	@Extends(value = GraphicComponent.class, others = { GraphicComponent.class })
	public static class Selectable {

	}

	@SystemGeneric
	@Components(Selectable.class)
	public static class Selected {

	}

	@SystemGeneric
	@Extends(value = Engine.class, others = { Selectable.class, Window.class })
	public static class SelectableWindow {

	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Extends(value = SelectableWindow.class, others = { SelectableWindow.class })
	public static class MySelectableWindow {

	}

	@SystemGeneric
	public static class Vehicle {

	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	public static class MyVehicle extends Vehicle {
	}

	@SystemGeneric
	@Components(Vehicle.class)
	public static class Power {

	}

	@SystemGeneric
	public static class Car extends Vehicle {

	}

	@SystemGeneric
	@Components(Car.class)
	public static class ElectrikPower extends Power {

	}

	@SystemGeneric
	@Components(ElectrikPower.class)
	public static class Unit {

	}

	@SystemGeneric
	public static class Human {
	}

	@SystemGeneric
	public static class Man extends Human {
	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	public static class Myck extends Human {
	}

	@SystemGeneric
	public static class Time {
	}

	@SystemGeneric
	@Components({ Human.class, Vehicle.class })
	public static class HumanPossessVehicle {
	}

	@SystemGeneric
	@Components({ Human.class, Car.class })
	public static class HumanPossessCar extends HumanPossessVehicle {
	}

	@SystemGeneric
	@Components({ Man.class, Car.class })
	public static class ManPossessCar extends HumanPossessVehicle {
	}

	@SystemGeneric
	@Components({ Human.class, Vehicle.class, Time.class })
	public static class HumanPossessVehicleTime {
	}

}
