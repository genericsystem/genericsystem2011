package org.genericsystem.example;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.IntValue;
import org.genericsystem.annotation.value.StringValue;

public class Example {

	@SystemGeneric
	public static class Vehicle {

	}

	@SystemGeneric
	@StringValue("myVehicle")
	@Extends(meta = Vehicle.class)
	public static class MyVehicle {

	}

	@SystemGeneric
	@Components(Vehicle.class)
	public static class Power {

	}

	@SystemGeneric
	@Components(Vehicle.class)
	@IntValue(123)
	@Extends(meta = Power.class)
	public static class V123 {

	}

	@SystemGeneric
	@Components(MyVehicle.class)
	@IntValue(136)
	@Extends(meta = Power.class)
	public static class V136 {

	}

	@SystemGeneric
	public static class Color {

	}

	@SystemGeneric
	@StringValue("red")
	@Extends(meta = Color.class)
	public static class Red {

	}

	@SystemGeneric
	@StringValue("yellow")
	@Extends(meta = Color.class)
	public static class Yellow {

	}

	@SystemGeneric
	@Components({ Vehicle.class, Color.class })
	public static class VehicleColor {

	}

	@SystemGeneric
	@Components({ MyVehicle.class, Red.class })
	@StringValue("myVehicleRed")
	@Extends(meta = VehicleColor.class)
	public static class MyVehicleRed {

	}

	@SystemGeneric
	@Components({ Vehicle.class, Yellow.class })
	@StringValue("myVehicleYellow")
	@Extends(meta = VehicleColor.class)
	public static class MyVehicleYellow {

	}

	@SystemGeneric
	public static class Car extends Vehicle {

	}

	@SystemGeneric
	@StringValue("myCar")
	@Extends(meta = Car.class)
	public static class MyCar {

	}

	@SystemGeneric
	public static class Time {

	}

	@SystemGeneric
	@StringValue("myTime")
	@Extends(meta = Time.class)
	public static class MyTime {

	}

	@SystemGeneric
	@Components({ Vehicle.class, Color.class, Time.class })
	public static class VehicleColorTime {

	}

	@SystemGeneric
	@Components({ MyVehicle.class, Red.class, MyTime.class })
	@StringValue("myVehicleColorTime")
	@Extends(meta = VehicleColorTime.class)
	public static class MyVehicleColorTime {

	}

	@SystemGeneric
	public static class Human {

	}

	@SystemGeneric
	@StringValue("Nicolas")
	@Extends(meta = Human.class)
	public static class Nicolas {

	}

	@SystemGeneric
	@StringValue("Michael")
	@Extends(meta = Human.class)
	public static class Michael {

	}

	@SystemGeneric
	@StringValue("Quentin")
	@Extends(meta = Human.class)
	public static class Quentin {

	}

	@SystemGeneric
	@Components({ Human.class, Human.class })
	public static class IsTallerOrEqualThan {

	}

	@SystemGeneric
	@Components({ Nicolas.class, Michael.class })
	@StringValue("nicolasIsTallerOrEqualThanMichael")
	@Extends(meta = IsTallerOrEqualThan.class)
	public static class NicolasIsTallerOrEqualThanMichael {

	}

	// @SystemGeneric(SystemGeneric.CONCRETE)
	// @Components({ Nicolas.class, Nicolas.class })
	// @StringValue("nicolasIsTallerOrEqualThanNicolas")
	// public static class NicolasIsTallerOrEqualThanNicolas extends IsTallerOrEqualThan {
	//
	// }

	@SystemGeneric
	@Components({ Human.class, Human.class })
	public static class IsBrotherOf {

	}

	@SystemGeneric
	@Components({ Quentin.class, Michael.class })
	@StringValue("quentinIsBrotherOfMichael")
	@Extends(meta = IsBrotherOf.class)
	public static class QuentinIsBrotherOfMichael {

	}

	@SystemGeneric
	@Components({ Human.class, Human.class })
	public static class IsBossOf {

	}

	@SystemGeneric
	@Components({ Nicolas.class, Michael.class })
	@StringValue("nicolasIsBossOfMichael")
	@Extends(meta = IsBossOf.class)
	public static class NicolasIsBossOfMichael extends IsBossOf {

	}

	@SystemGeneric
	@Extends({ Human.class, Vehicle.class })
	public static class Transformer {

	}

	@SystemGeneric
	@StringValue("myTransformer")
	@Extends(meta = Transformer.class)
	public static class MyTransformer extends Transformer {

	}
}
