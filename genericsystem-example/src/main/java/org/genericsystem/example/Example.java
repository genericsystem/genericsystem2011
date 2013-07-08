package org.genericsystem.example;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.IntValue;
import org.genericsystem.annotation.value.StringValue;

public class Example {

	@SystemGeneric
	public static class Vehicle {

	}

	@SystemGeneric(SystemGeneric.CONCRETE)
	@StringValue("myVehicle")
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
}
