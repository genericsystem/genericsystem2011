package org.genericsystem.impl.system;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Engine;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@SingularConstraint(Statics.BASE_POSITION)
@PropertyConstraint
public class MultiDirectionalSystemProperty extends AbstractSystemProperty {
}
