//package org.genericsystem.systemproperties.constraints;
//
//import org.genericsystem.annotation.Components;
//import org.genericsystem.annotation.Dependencies;
//import org.genericsystem.annotation.Extends;
//import org.genericsystem.annotation.SystemGeneric;
//import org.genericsystem.annotation.value.AxedConstraintValue;
//import org.genericsystem.annotation.value.BooleanValue;
//import org.genericsystem.core.Generic;
//import org.genericsystem.core.GenericImpl;
//import org.genericsystem.core.Snapshot;
//import org.genericsystem.exception.ConstraintViolationException;
//import org.genericsystem.exception.SingularConstraintViolationException;
//import org.genericsystem.generic.Attribute;
//import org.genericsystem.generic.Holder;
//import org.genericsystem.generic.Relation;
//import org.genericsystem.generic.Type;
//import org.genericsystem.map.ConstraintsMapProvider;
//import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
//import org.genericsystem.map.ConstraintsMapProvider.MapInstance;
//
//@SystemGeneric
//@Extends(meta = ConstraintKey.class)
//@Components(MapInstance.class)
//@Dependencies(SingularConstraintImpl.DefaultValue.class)
//@AxedConstraintValue(SingularConstraintImpl.class)
//public class SingularConstraintImpl extends AbstractBooleanAxedConstraintImpl implements Holder {
//
//	@SystemGeneric
//	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
//	@Components(SingularConstraintImpl.class)
//	@BooleanValue(false)
//	public static class DefaultValue extends GenericImpl implements Holder {}
//
//	@Override
//	public void check(Generic base, Generic attribute, int axe) throws ConstraintViolationException {
//		Snapshot<Holder> holders = base.getHolders((Attribute) attribute, axe);
//		if (holders.size() > 1)
//			throw new SingularConstraintViolationException("Multiple links of attribute " + attribute + " on component " + base + " (n° " + axe + ") : " + holders.get(0).info() + holders.get(1).info());
//		for (Generic generic : ((GenericImpl) base).getAllInheritings()) {
//			holders = generic.getHolders((Relation) attribute, axe);
//			if (holders.size() > 1)
//				throw new SingularConstraintViolationException("Multiple links of attribute " + attribute + " on component " + generic + " (n° " + axe + ") : " + holders.get(0).info() + holders.get(1).info());
//		}
//	}
//
//	@Override
//	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
//		return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE) || (modified.getValue() == null && checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE));
//	}
//
//	@Override
//	public void checkConsistency(Generic base, Holder value, int axe) throws ConstraintViolationException {
//		for (Generic link : ((Type) base).getInstances()) {
//			Generic instance = link.getComponents().get(axe);
//			if (instance != null && instance.getHolders((Relation) base).size() >= 2)
//				throw new SingularConstraintViolationException("Multiple links of attribute " + base + " on component " + instance + " (n° " + axe + ")");
//		}
//	}
//}
package org.genericsystem.constraints;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.SingularConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@Dependencies(SingularConstraintImpl.DefaultValue.class)
@AxedConstraintValue(SingularConstraintImpl.class)
public class SingularConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(SingularConstraintImpl.class)
	@BooleanValue(false)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	@Override
	public void check(Generic modified, Generic baseConstraint, int axe) throws ConstraintViolationException {
		Snapshot<Holder> holders = modified.getHolders((Relation) baseConstraint, axe);
		if (holders.size() > 1)
			throw new SingularConstraintViolationException("Multiple links of attribute " + baseConstraint + " on component " + modified + " (n° " + axe + ") : " + holders);
		else {
			for (Generic instance : ((Type) baseConstraint.getComponents().get(axe)).getAllInstances())
				if ((instance.getHolders((Relation) baseConstraint, axe)).size() > 1)
					throw new SingularConstraintViolationException("Multiple links of attribute " + baseConstraint + " on component " + modified + " (n° " + axe + ")");
		}
	}

	@Override
	public boolean isCheckedAt(Generic modified, CheckingType checkingType) {
		return checkingType.equals(CheckingType.CHECK_ON_ADD_NODE) || (((GenericImpl) modified).isPhantomGeneric() && checkingType.equals(CheckingType.CHECK_ON_REMOVE_NODE));
	}

}