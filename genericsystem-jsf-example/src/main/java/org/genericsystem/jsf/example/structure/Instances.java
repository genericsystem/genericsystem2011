package org.genericsystem.jsf.example.structure;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Meta;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.IntValue;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.jsf.example.structure.Attributes.Power;
import org.genericsystem.jsf.example.structure.Types.Cars;
import org.genericsystem.jsf.example.structure.Types.Colors;

public class Instances {

	// @SystemGeneric
	// @Meta(Colors.class)
	// @StringValue("blue")
	// public static class Blue extends GenericImpl {
	// }
	//
	@SystemGeneric
	@Meta(Colors.class)
	@StringValue("red")
	public static class Red extends GenericImpl {
	}

	@SystemGeneric
	@Meta(Cars.class)
	@StringValue("myBMW")
	public static class MyBMW extends GenericImpl {
	}

	@SystemGeneric
	@Meta(Power.class)
	@Components(MyBMW.class)
	@IntValue(233)
	public static class p233 extends GenericImpl {
	}

}
