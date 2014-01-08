package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SuperRuleConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Dependencies({ SuperRuleConstraintImpl.DefaultKey.class, SuperRuleConstraintImpl.DefaultValue.class })
public class SuperRuleConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = SuperRuleConstraintImpl.class)
	@Components(ConstraintsMapProvider.class)
	@AxedConstraintValue(SuperRuleConstraintImpl.class)
	public static class DefaultKey {}

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(DefaultKey.class)
	@BooleanValue(true)
	public static class DefaultValue {}

	@Override
	public void check(Generic constraintBase, Generic modified) throws ConstraintViolationException {
		for (Generic directSuper : modified.supers())
			if (!((GenericImpl) directSuper).isSuperOf(((GenericImpl) modified).getHomeTreeNode(), ((GenericImpl) modified).supers(), ((GenericImpl) modified).components()))
				throw new SuperRuleConstraintViolationException(constraintBase.info() + " should inherits from : " + directSuper.info());
	}
}
