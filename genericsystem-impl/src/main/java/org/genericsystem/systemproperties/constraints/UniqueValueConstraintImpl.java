package org.genericsystem.systemproperties.constraints;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.constraints.SingularConstraint;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.Snapshot;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UniqueValueConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;
import org.genericsystem.map.ConstraintsMapProvider.MapInstance;

/**
 * 
 * 
 * @author Nicolas Feybesse
 */
@SystemGeneric
@Extends(meta = ConstraintKey.class)
@Components(MapInstance.class)
@SingularConstraint
@Dependencies(UniqueValueConstraintImpl.DefaultValue.class)
@AxedConstraintValue(UniqueValueConstraintImpl.class)
public class UniqueValueConstraintImpl extends AbstractBooleanConstraintImpl implements Holder {

	@SystemGeneric
	@Extends(meta = ConstraintsMapProvider.ConstraintValue.class)
	@Components(UniqueValueConstraintImpl.class)
	@BooleanValue(true)
	public static class DefaultValue extends GenericImpl implements Holder {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void check(Generic modified, Generic type, int axe) throws ConstraintViolationException {

		log.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ALEXEI @@@@@@@@@@");
		log.info(">>> Modified: ");
		modified.log();
		log.info(" Type: ");
		type.log();

		/* Variables */
		Snapshot<Generic> children = modified.getInheritings();
		Snapshot<Generic> parents = modified.getSupers();
		List<Generic> brothers = new ArrayList<Generic>();
		List<Generic> concurents = new ArrayList<Generic>();

		for (Generic parent : parents) {
			for (Generic child : parent.getInheritings())
				if (!child.equals(modified))
					brothers.add(child);
		}

		log.info(" >>>Parents: " + parents);
		log.info(" >>>Children: " + children);
		log.info(" >>>Brothers: " + children);

		concurents.addAll(children);
		concurents.addAll(brothers);
		concurents.addAll(parents);

		for (Generic concurent : concurents)
			if (concurent.getValue().equals(modified.getValue()))
				throw new UniqueValueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + type + ".");





		//		log.info(" (((Type) type).getAllInstances()): " + (((Type) type).getAllInstances()));
		//		// if (!modified.isStructural()) {
		//		for (Generic generic : (((Type) type).getAllInstances()))
		//
		//			if (!generic.equals(modified) && generic.getValue().equals(modified.getValue())) {
		//				log.info("generic.getValue() " + generic.getValue() + " modified.getValue() " + modified.getValue());
		//				generic.log();
		//				modified.log();
		//				throw new UniqueValueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + type + ".");
		//			}







		// } else {
		// Iterator<Generic> iterator = Statics.valueFilter(((GenericImpl) modified).<Generic> directInheritingsIterator(), modified.getValue());
		// }

		// if (!modified.isStructural()) {
		// for (Generic generic : ((Type) type).getAllInstances())
		// if (!generic.equals(modified) && generic.getValue().equals(modified.getValue()))
		// throw new UniqueValueConstraintViolationException("Holder " + modified.getValue() + " is duplicate for type " + type + ".");
		// } else {
		// Iterator<Generic> iterator = Statics.valueFilter(((GenericImpl) modified).<Generic> directInheritingsIterator(), modified.getValue());
		// }

		/*
		 * } else { /* Get all instances of modified structural and checks that there is only one instance Iterator<Generic> iterator = Statics.valueFilter(((GenericImpl) modified).<Generic> directInheritingsIterator(), modified.getValue()); if
		 * (iterator.hasNext()) { iterator.next(); >>>>>>> constraints:genericsystem-impl/src/main/java/org/genericsystem/systemproperties/constraints/simple/UniqueValueConstraintImpl.java if (iterator.hasNext()) { throw new
		 * UniqueStructuralValueConstraintViolationException("modified : " + modified.info()); } } }
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void checkConsistency(Generic base, Holder valueHolder, int axe) throws ConstraintViolationException {
		log.info(">>> Base: ");
		base.log();
		log.info(" Holder: ");
		valueHolder.log();
		Set<Serializable> values = new HashSet<>();
		for (Generic attributeNode : ((Type) base).getAllInstances()) {
			Serializable value = attributeNode.getValue();
			if (value != null)
				if (!values.add(value))
					throw new UniqueValueConstraintViolationException("Duplicate value : " + value);
		}
	}

}
