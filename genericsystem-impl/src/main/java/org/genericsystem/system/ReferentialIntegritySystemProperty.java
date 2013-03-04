package org.genericsystem.system;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.annotation.value.ComponentPosBoolean;
import org.genericsystem.core.Engine;
import org.genericsystem.system.ReferentialIntegritySystemProperty.DefaultValue;

@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@Dependencies(DefaultValue.class)
@InstanceClassConstraint(ComponentPosValue.class)
public class ReferentialIntegritySystemProperty {

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(MetaAttribute.class)
	@ComponentPosBoolean(false)
	public static class DefaultValue extends ReferentialIntegritySystemProperty {
	}

}
