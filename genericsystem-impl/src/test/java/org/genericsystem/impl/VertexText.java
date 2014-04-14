package org.genericsystem.impl;

import java.util.Arrays;
import org.genericsystem.impl.Vertex.Engine;
import org.testng.annotations.Test;

@Test
public class VertexText extends AbstractTest {

	public void test() {
		Vertex engine = new Engine();
		Vertex vehicle = engine.addInstance("Vehicle");
		assert vehicle == engine.setInstance("Vehicle");
		Vertex car = engine.addInstance(new Vertex[] { vehicle }, "Car");
		Vertex power = engine.addInstance("Power", car);
		Vertex myBmw = car.addInstance("myBmw");
		Vertex v233 = power.addInstance(233, myBmw);
		Vertex color = engine.addInstance("Color");
		Vertex red = color.addInstance("red");
		Vertex green = color.addInstance("green");
		Vertex yellow = color.addInstance("yellow");
		Vertex vehicleColor = engine.addInstance("VehicleColor", vehicle, color);
		log.info("" + car.info() + power.info() + myBmw.info() + v233.info());
		assert engine.getInstances().containsAll(Arrays.asList(vehicle, car));
		assert car.getInstances().contains(myBmw);
		assert power.getInstances().contains(v233);
		assert car.getComposites().contains(power);
		assert car.getSupers()[0] == vehicle : car.getSupers()[0].info();
		assert Arrays.asList(car.getSupers()).contains(vehicle);
		assert vehicle.getInheritings().contains(car);
		assert car.getComposites().contains(power);
		assert myBmw.getComposites().contains(v233);
		assert myBmw.isInstanceOf(car);
		assert myBmw.isInstanceOf(vehicle);
		assert !myBmw.isInstanceOf(engine);
		assert vehicle.isInstanceOf(engine);
		assert !vehicle.inheritsFrom(engine) : vehicle.getLevel() + " " + engine.getLevel() + " " + vehicle.equals(engine);
		assert car.inheritsFrom(vehicle);
		assert !car.isInstanceOf(vehicle);
		assert !power.inheritsFrom(engine);
		assert !v233.inheritsFrom(power);
		assert v233.isInstanceOf(power);
		assert engine.getInstance("Car") != null;
		assert power.getInstance(233, myBmw) != null;
		Vertex carRed = vehicleColor.addInstance("CarRed", car, red);
		assert carRed.isSuperOf(vehicleColor, new Vertex[] { carRed }, "myBmwRed", myBmw, red);
		assert !carRed.isSuperOf(vehicleColor, new Vertex[] {}, "myBmwRed", red, red);
		assert carRed.isSuperOf(vehicleColor, new Vertex[] { carRed }, "CarRed", myBmw, red);

		Vertex myBmwRed = vehicleColor.addInstance(new Vertex[] { carRed }, "myBmwRed", myBmw, red);
		assert myBmwRed == vehicleColor.setInstance("myBmwRed", myBmw, red);
		assert myBmwRed == vehicleColor.getInstance("myBmwRed", myBmw, red) : vehicleColor.getInstance("myBmwRed", myBmw, red).info();

		assert myBmwRed.inheritsFrom(carRed);
		assert car.getAttributes(engine).contains(power);
		assert car.getAttributes(engine).size() == 1;
		assert !myBmwRed.inheritsFrom(power);
		assert !v233.inheritsFrom(power);
		assert v233.isInstanceOf(power);

		assert myBmw.getHolders(power).contains(v233);
		assert myBmw.getHolders(power).size() == 1 : myBmw.getHolders(engine);
		assert myBmw.getValues(power).contains(233);
	}
}
