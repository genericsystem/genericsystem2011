package org.generic.map;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;

@SystemGeneric
@Components(PropertiesMapProvider.class)
public class PropertyKey extends GenericImpl implements Attribute {

}
