package org.genericsystem.systemproperties;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.SystemPropertiesMapProvider.MapInstance;
import org.genericsystem.map.SystemPropertiesMapProvider.SystemPropertyKey;
import org.genericsystem.map.SystemPropertiesMapProvider.SystemPropertyValue;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(meta = SystemPropertyKey.class)
@Components(MapInstance.class)
@Dependencies(NoReferentialIntegritySystemProperty.DefaultValue.class)
@AxedConstraintValue(value = NoReferentialIntegritySystemProperty.class, axe = 0)
public class NoReferentialIntegritySystemProperty extends GenericImpl {

	@SystemGeneric
	@Extends(meta = SystemPropertyValue.class)
	@Components(NoReferentialIntegritySystemProperty.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {}

}
