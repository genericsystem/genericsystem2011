package org.genericsystem.impl;

import java.util.List;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.IntValue;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AnnotationTest extends AbstractTest {

	public void testMultiDirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Generic michael = human.newInstance("Michael");
		Generic quentin = human.newInstance("Quentin");
		Relation isBrotherOf = human.setRelation("isBrotherOf", human);
		isBrotherOf.enableMultiDirectional();
		Link link = quentin.bind(isBrotherOf, michael);

		List<Generic> targetsFromQuentin = quentin.getOtherTargets(link);
		assert targetsFromQuentin.size() == 1 : targetsFromQuentin.size();
		assert targetsFromQuentin.contains(michael) : targetsFromQuentin;
		assert !targetsFromQuentin.contains(quentin) : targetsFromQuentin;

		List<Generic> targetsFromMichael = michael.getOtherTargets(link);
		assert targetsFromMichael.size() == 1 : targetsFromMichael.size();
		assert targetsFromMichael.contains(quentin) : targetsFromMichael;
		assert !targetsFromMichael.contains(michael) : targetsFromMichael;

		assert michael.getTargets(isBrotherOf, 0).contains(quentin) : michael.getTargets(isBrotherOf);
	}

	public void testSimpleDirectionalRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Generic red = color.newInstance("red");
		Relation vehicleColor = vehicle.setRelation("vehicleColor", color);
		vehicleColor.enableMultiDirectional();
		Link link = myVehicle.bind(vehicleColor, red);

		List<Generic> targetsFromMyVehicle = myVehicle.getOtherTargets(link);
		assert targetsFromMyVehicle.size() == 1 : targetsFromMyVehicle.size();
		assert targetsFromMyVehicle.contains(red) : targetsFromMyVehicle;
		assert !targetsFromMyVehicle.contains(myVehicle) : targetsFromMyVehicle;

		List<Generic> targetsFromRed = red.getOtherTargets(link);
		assert targetsFromRed.size() == 1 : targetsFromRed.size();
		assert targetsFromRed.contains(myVehicle) : targetsFromRed;
		assert !targetsFromRed.contains(red) : targetsFromRed;

		red.getLinks(vehicleColor).get(0);
		assert red.getTargets(vehicleColor, Statics.BASE_POSITION).contains(myVehicle) : red.getTargets(vehicleColor);
	}

	public void testType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Human.class, Myck.class);
		Type vehicle = cache.find(Vehicle.class);
		Type human = cache.find(Human.class);
		Type myck = cache.find(Myck.class);
		assert !human.isAutomatic();
		assert !myck.isAutomatic();
		assert vehicle.isStructural();
		assert human.isStructural();
		assert myck.isConcrete();
	}

	public void testSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Car.class).start();
		Type vehicle = cache.find(Vehicle.class);
		Type car = cache.find(Car.class);
		assert vehicle.isStructural();
		assert car.isStructural();
		assert vehicle.getDirectSubTypes().size() == 1;
		assert vehicle.getDirectSubTypes().contains(car);
		assert car.getSupers().size() == 2 : car.getSupers();
		assert car.getSupers().contains(vehicle);
		assert car.getSupers().contains(car.getImplicit());
		assert !car.equals(car.getImplicit());
	}

	public void testAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Power.class).start();
		Type vehicle = cache.find(Vehicle.class);
		Attribute power = cache.find(Power.class);
		assert power.isStructural();
		assert vehicle.getAttributes().contains(power);
	}

	public void testAttributeValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(V123.class).start();
		Type myVehicle = cache.find(MyVehicle.class);
		cache.find(V123.class);
		assert myVehicle.getValue(cache.<Attribute> find(Power.class)).equals(new Integer(123)) : myVehicle.getValue(cache.<Attribute> find(Power.class));
	}

	public void testSubAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Car.class, ElectrikPower.class).start();
		Type car = cache.find(Car.class);
		Attribute electrikPowerCar = cache.find(ElectrikPower.class);
		assert car.getAttributes().contains(electrikPowerCar) : car.getAttributes();
	}

	public void testAttributeOnAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(ElectrikPower.class, Unit.class).start();
		Attribute electrikPowerCar = cache.find(ElectrikPower.class);
		Attribute unit = cache.find(Unit.class);
		assert unit.isAttributeOf(electrikPowerCar);
		assert unit.isAttribute();
		assert unit.isStructural();
		assert electrikPowerCar.getAttributes().contains(unit);
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Human.class, HumanPossessVehicle.class).start();
		cache.find(Vehicle.class);
		Type human = cache.find(Human.class);
		Relation possess = cache.find(HumanPossessVehicle.class);
		assert human.getRelations().size() == 1;
		assert human.getRelations().contains(possess);
	}

	public void testSubRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Car.class, Human.class, HumanPossessVehicle.class, HumanPossessCar.class).start();
		cache.find(Car.class);
		Type human = cache.find(Human.class);
		Relation possessVehicle = cache.find(HumanPossessVehicle.class);
		Relation possessCar = cache.find(HumanPossessCar.class);
		assert human.getRelations().size() == 1 : human.getRelations();
		assert human.getRelations().contains(possessCar) : human.getRelations();
		assert possessCar.inheritsFrom(possessVehicle);
	}

	public void testSubRelationSymetric() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Car.class, Human.class, Man.class, HumanPossessVehicle.class, ManPossessCar.class).start();
		cache.find(Car.class);
		Type human = cache.find(Human.class);
		Type man = cache.find(Man.class);
		Relation humanPossessVehicle = cache.find(HumanPossessVehicle.class);
		Relation manPossessCar = cache.find(ManPossessCar.class);
		assert human.getRelations().size() == 1;
		assert human.getRelations().contains(humanPossessVehicle);
		assert man.getRelations().size() == 1;
		assert man.getRelations().contains(manPossessCar) : man.getRelations();
		assert manPossessCar.inheritsFrom(humanPossessVehicle);
	}

	public void testRelationTernary() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Vehicle.class, Human.class, Time.class, HumanPossessVehicleTime.class).start();
		cache.find(Vehicle.class);
		Type human = cache.find(Human.class);
		cache.find(Time.class);
		Relation possess = cache.find(HumanPossessVehicleTime.class);
		assert human.getRelations().size() == 1;
		assert human.getRelations().contains(possess);
	}

	public void testgetDirectSubTypesWithDiamondProblem() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(GraphicComponent.class, Window.class, Selectable.class, SelectableWindow.class).start();
		Type graphicComponent = cache.find(GraphicComponent.class);
		Type window = cache.find(Window.class);
		Type selectable = cache.find(Selectable.class);
		Type selectableWindow = cache.find(SelectableWindow.class);

		assert graphicComponent.getDirectSubTypes().size() == 2 : graphicComponent.getDirectSubTypes();
		assert graphicComponent.getDirectSubTypes().contains(selectable);
		assert graphicComponent.getDirectSubTypes().contains(window) : graphicComponent.getDirectSubTypes();

		assert graphicComponent.getSubTypes().size() == 3 : graphicComponent.getSubTypes();
		assert graphicComponent.getSubTypes().contains(selectable);
		assert graphicComponent.getSubTypes().contains(window);
		assert graphicComponent.getSubTypes().contains(selectableWindow);

		assert window.getDirectSubTypes().size() == 1 : window.getDirectSubTypes();
		assert window.getDirectSubTypes().contains(selectableWindow) : window.getDirectSubTypes();

		assert selectable.getDirectSubTypes().size() == 1 : selectable.getDirectSubTypes();
		assert selectable.getDirectSubTypes().contains(selectableWindow) : selectable.getDirectSubTypes();

		assert selectableWindow.getDirectSubTypes().size() == 0;
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
		Holder vTrue = mySelectableWindow.setValue(selectedSelectable, true);
		Holder v12 = mySelectableWindow.setValue(size, 12);

		assert selectableWindow.getInstances().size() == 1 : selectableWindow.getInstances();
		assert selectableWindow.getInstances().contains(mySelectableWindow);
		assert mySelectableWindow.getHolders(size).size() == 1 : mySelectableWindow.getHolders(size);
		assert mySelectableWindow.getHolders(size).contains(v12);
		assert mySelectableWindow.getHolders(selectedSelectable).size() == 1;
		assert mySelectableWindow.getHolders(selectedSelectable).contains(vTrue);
	}

	public void testMultiInheritanceComplexStructural() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(Games.class, Children.class, Vehicle.class, Human.class, ChildrenGames.class, Transformer.class, TransformerChildrenGames.class).start();

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
		assert transformerChildrenGames.getInheritings().size() == 0;
		assert transformerChildrenGames.getComposites().size() == 0;

		assert childrenGames.getSupers().contains(games);
		assert childrenGames.getSupers().contains(children);
		assert childrenGames.getSupers().contains(childrenGames.getImplicit());
		assert childrenGames.getInheritings().contains(transformerChildrenGames);
		assert childrenGames.getComposites().size() == 0;

		assert transformer.getSupers().contains(vehicle);
		assert transformer.getSupers().contains(human);
		assert transformer.getSupers().contains(transformer.getImplicit());
		assert transformer.getInheritings().contains(transformerChildrenGames);
		assert transformer.getComposites().size() == 0;
	}

	public void testMultiInheritanceComplexValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine(MyGames.class, MyChildren.class, MyVehicle.class, Myck.class, MyChildrenGames.class, ChildrenGames.class, MyTransformer.class, Transformer.class, TransformerChildrenGames.class,
				MyTransformerChildrenGames.class).start();
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
		assert myTransformerChildrenGames.getInheritings().size() == 0;
		assert myTransformerChildrenGames.getComposites().size() == 0;

		assert transformerChildrenGames.getInheritings().contains(myTransformerChildrenGames);
		assert myTransformerChildrenGames.isInstanceOf(transformerChildrenGames);

		assert !myChildrenGames.inheritsFrom(myGames);
		assert !myChildrenGames.inheritsFrom(myChildren);
		assert myChildrenGames.getSupers().contains(childrenGames);
		assert myChildrenGames.getSupers().contains(myChildrenGames.getImplicit());
		assert myChildrenGames.getInheritings().size() == 0;
		assert myChildrenGames.getComposites().size() == 0;

		assert childrenGames.getInheritings().contains(myChildrenGames);
		assert myChildrenGames.isInstanceOf(childrenGames);

		assert !myTransformer.inheritsFrom(myVehicle);
		assert !myTransformer.inheritsFrom(myck);
		assert myTransformer.getSupers().contains(transformer);
		assert myTransformer.getSupers().contains(myTransformer.getImplicit());
		assert myTransformer.getInheritings().size() == 0;
		assert myTransformer.getComposites().size() == 0;

		assert transformer.getInheritings().contains(myTransformer);
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

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(MyVehicle.class)
	@IntValue(123)
	public static class V123 extends Power {

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
