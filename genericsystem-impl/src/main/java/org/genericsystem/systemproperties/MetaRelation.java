package org.genericsystem.systemproperties;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.StringValue;
import org.genericsystem.core.EngineImpl;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Statics;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
// @Extends(meta = MetaAttribute.class)
@Components({ EngineImpl.class, EngineImpl.class })
@StringValue(Statics.ROOT_NODE_VALUE)
public class MetaRelation extends GenericImpl {
	@Override
	public String toString() {
		return "MetaRelation";
	}
}