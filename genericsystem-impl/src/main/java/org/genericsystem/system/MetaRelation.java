package org.genericsystem.system;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Engine;

@SystemGeneric
@Components({ Engine.class, Engine.class })
public class MetaRelation {
}