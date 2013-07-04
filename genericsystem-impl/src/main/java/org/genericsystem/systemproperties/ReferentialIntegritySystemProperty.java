package org.genericsystem.systemproperties;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.IntValue;
import org.genericsystem.core.Engine;
import org.genericsystem.systemproperties.ReferentialIntegritySystemProperty.DefaultValue;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(defaultBehavior = true)
@Components(Engine.class)
@Dependencies(DefaultValue.class)
public class ReferentialIntegritySystemProperty implements BooleanSystemProperty {

	@SystemGeneric(SystemGeneric.CONCRETE)
	@Components(MetaAttribute.class)
	@IntValue(0)
	public static class DefaultValue extends ReferentialIntegritySystemProperty {
	}

}
