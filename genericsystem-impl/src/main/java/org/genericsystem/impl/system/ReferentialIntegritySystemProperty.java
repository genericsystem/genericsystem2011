package org.genericsystem.impl.system;

import org.genericsystem.api.annotation.ComponentPosBoolean;
import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.Dependencies;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.InstanceClassConstraint;
import org.genericsystem.api.core.Engine;
import org.genericsystem.impl.system.ReferentialIntegritySystemProperty.DefaultValue;

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
