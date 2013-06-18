package org.generic.map;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.RequiredConstraint;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.generic.Attribute;

@SystemGeneric
@Components(PropertyKey.class)
@SingularConstraint
@RequiredConstraint
public class PropertyValue extends GenericImpl implements Attribute {

}
