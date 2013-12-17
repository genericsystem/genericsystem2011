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
@SystemGeneric
@Extends(meta = SystemPropertyKey.class)
@Components(MapInstance.class)
@AxedConstraintValue(CascadeRemoveSystemProperty.class)
public class NoInheritanceProperty extends GenericImpl {

}
