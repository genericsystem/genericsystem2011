package org.genericsystem.generic;

import org.genericsystem.core.Generic;

/**
 * The Holder of the value.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Holder extends Generic {

	/**
	 * Returns the size implicated by the constraint.
	 * 
	 * @param basePos
	 *            The component position implicated by the constraint.
	 * 
	 * @return Return the size.
	 */
	Integer getSizeConstraint(int basePos);

	/**
	 * Returns true if the singular constraint enabled
	 * 
	 * @return true if the singular constraint enabled
	 */
	boolean isSingularConstraintEnabled();

	/**
	 * Returns true if the singular constraint enabled for the base position
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return true if the singular constraint enabled for the base position
	 */
	boolean isSingularConstraintEnabled(int componentPos);

	/**
	 * Returns true if the property constraint enabled
	 * 
	 * @return true if the property constraint enabled
	 */
	boolean isPropertyConstraintEnabled();

	/**
	 * Returns true if the required constraint enabled
	 * 
	 * @return true if the required constraint enabled
	 */
	boolean isRequiredConstraintEnabled();

	/**
	 * Returns true if the required constraint enabled for the base position
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return true if the required constraint enabled for the base position
	 */
	boolean isRequiredConstraintEnabled(int componentPos);

	/**
	 * Returns true if the unique value constraint enabled.
	 * 
	 * @return true if the unique value constraint enabled.
	 */
	boolean isUniqueValueConstraintEnabled();

	/**
	 * Returns the base component.
	 * 
	 * @return Return the base component.
	 */
	<T extends Generic> T getBaseComponent();

	/**
	 * Returns the component for the position.
	 * 
	 * @param basePos
	 *            The base position.
	 * @return Return the component if it exist else null.
	 */
	<T extends Generic> T getComponent(int basePos);

}
