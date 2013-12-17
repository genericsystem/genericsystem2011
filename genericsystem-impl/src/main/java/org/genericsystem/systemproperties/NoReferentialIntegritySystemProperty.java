package org.genericsystem.systemproperties;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.map.SystemPropertiesMapProvider;
import org.genericsystem.map.SystemPropertiesMapProvider.SystemPropertyKey;
import org.genericsystem.map.SystemPropertiesMapProvider.SystemPropertyValue;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(SystemPropertyKey.class)
@Components(SystemPropertiesMapProvider.class)
@Dependencies({ NoReferentialIntegritySystemProperty.DefaultKey.class, NoReferentialIntegritySystemProperty.DefaultValue.class })
public class NoReferentialIntegritySystemProperty extends GenericImpl {

	@SystemGeneric
	@Extends(meta = NoReferentialIntegritySystemProperty.class)
	@Components(SystemPropertiesMapProvider.class)
	@AxedConstraintValue(value = NoReferentialIntegritySystemProperty.class, axe = 0)
	public static class DefaultKey {
	}

	@SystemGeneric
	@Extends(meta = SystemPropertyValue.class)
	@Components(DefaultKey.class)
	@BooleanValue(true)
	public static class DefaultValue {
	}

}
