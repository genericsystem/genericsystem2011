package org.genericsystem.generic;

import org.genericsystem.core.Generic;

/**
 * An Attribute.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Attribute extends Holder, Type {

	// TODO clean
	// /**
	// * Enable multidirectional.
	// *
	// * @return Return this.
	// */
	// <T extends Attribute> T enableMultiDirectional();
	//
	// /**
	// * Disable multidirectional.
	// *
	// * @return Return this.
	// */
	// <T extends Attribute> T disableMultiDirectional();
	//
	// /**
	// * Returns true if the multidirectional system property enabled.
	// *
	// * @return Return true if the multidirectional system propertyt enabled.
	// */
	// boolean isMultiDirectional();

	/**
	 * Enable Size Constraint.
	 * 
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * @param size
	 *            The size.
	 * 
	 * @return Return this.
	 */
	<T extends Generic> T enableSizeConstraint(int basePos, Integer size);

	/**
	 * Disable Size Constraint.
	 * 
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Generic> T disableSizeConstraint(int basePos);

	/**
	 * Returns the size implicated by the constraint.
	 * 
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * 
	 * @return Return the size.
	 */
	Integer getSizeConstraint(int basePos);

}
