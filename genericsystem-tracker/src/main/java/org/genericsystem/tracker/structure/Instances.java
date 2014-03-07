package org.genericsystem.tracker.structure;

import org.genericsystem.annotation.Meta;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.tracker.structure.Types.Priorities;

public class Instances {

	@SystemGeneric
	@Meta(Priorities.class)
	@StringValue("Minor")
	public static class Minor extends GenericImpl {

	}

	@SystemGeneric
	@Meta(Priorities.class)
	@StringValue("Major")
	public static class Major extends GenericImpl {

	}

}
