package org.genericsystem.generic;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Context;
import org.genericsystem.core.Generic;

/**
 * An Attribute.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Attribute extends Holder, Type {

	/**
	 * Enable multidirectional.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * 
	 * @return Return this.
	 */
	<T extends Attribute> T enableMultiDirectional(Cache cache);

	/**
	 * Disable multidirectional.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * 
	 * @return Return this.
	 */
	<T extends Attribute> T disableMultiDirectional(Cache cache);

	/**
	 * Returns true if the multidirectional system property enabled.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * 
	 * @return Return true if the multidirectional system propertyt enabled.
	 */
	boolean isMultiDirectional(Context context);

	/**
	 * Enable Size Constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * @param size
	 *            The size.
	 * 
	 * @return Return this.
	 */
	<T extends Generic> T enableSizeConstraint(Cache cache, int basePos, Integer size);

	/**
	 * Disable Size Constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * 
	 * @return Return this.
	 */
	<T extends Generic> T disableSizeConstraint(Cache cache, int basePos);

	/**
	 * Returns the size implicated by the constraint.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * 
	 * @return Return the size.
	 */
	Integer getSizeConstraint(Cache cache, int basePos);

	// TODO clean
	// /**
	// * Do all necessary inductions on this attribute.
	// *
	// * @param cache
	// * The reference Cache.
	// */
	// void deduct(Cache cache);
}
