package org.genericsystem.impl.system;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Engine;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint
public class MultiDirectionalSystemProperty {
}
