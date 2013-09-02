package org.genericsystem.systemproperties;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.map.SystemPropertiesMapProvider.MapInstance;
import org.genericsystem.map.SystemPropertiesMapProvider.SystemPropertyKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric(SystemGeneric.CONCRETE)
@Components(MapInstance.class)
@Extends(SystemPropertyKey.class)
@AxedConstraintValue(CascadeRemoveSystemProperty.class)
public class CascadeRemoveSystemProperty extends GenericImpl {

}
