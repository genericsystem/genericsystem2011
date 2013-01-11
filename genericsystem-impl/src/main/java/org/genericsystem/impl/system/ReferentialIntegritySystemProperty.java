package org.genericsystem.impl.system;

import java.io.Serializable;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;

@SystemGeneric
@Components(Engine.class)
// @Dependencies(value = { ReferentialIntegritySystemProperty.DefaultValueMetaType.class, ReferentialIntegritySystemProperty.DefaultValueMetaAttribute.class, ReferentialIntegritySystemProperty.BaseDefaultValueMetaRelation.class,
// ReferentialIntegritySystemProperty.TargetDefaultValueMetaRelation.class })
public class ReferentialIntegritySystemProperty extends AbstractSystemProperty {

	// @SystemGeneric(SystemGeneric.CONCRETE)
	// @Components(Engine.class)
	// @IntValue(0)
	// @Interfaces(ReferentialIntegritySystemProperty.class)
	// public static class DefaultValueMetaType {
	// }
	//
	// @SystemGeneric(SystemGeneric.CONCRETE)
	// @Components(MetaAttribute.class)
	// @PhantomValue
	// @Interfaces(DefaultValueMetaType.class)
	// public static class DefaultValueMetaAttribute {
	// }
	//
	// @SystemGeneric(SystemGeneric.CONCRETE)
	// @Components(MetaRelation.class)
	// @IntValue(0)
	// @Interfaces(ReferentialIntegritySystemProperty.class)
	// public static class BaseDefaultValueMetaRelation {
	// }
	//
	// @SystemGeneric(SystemGeneric.CONCRETE)
	// @Components(MetaRelation.class)
	// @IntValue(1)
	// @Interfaces(ReferentialIntegritySystemProperty.class)
	// public static class TargetDefaultValueMetaRelation {
	// }

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Serializable> T getDefaultValue(Generic generic) {
		assert false;
		if (generic.isReallyAttribute())
			return (T) Boolean.FALSE;
		return (T) Boolean.TRUE;
	}

}
