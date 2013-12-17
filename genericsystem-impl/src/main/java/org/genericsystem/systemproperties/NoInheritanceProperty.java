package org.genericsystem.systemproperties;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.map.SystemPropertiesMapProvider;
import org.genericsystem.map.SystemPropertiesMapProvider.SystemPropertyKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(SystemPropertyKey.class)
@Components(SystemPropertiesMapProvider.class)
public class NoInheritanceProperty extends GenericImpl {

}
