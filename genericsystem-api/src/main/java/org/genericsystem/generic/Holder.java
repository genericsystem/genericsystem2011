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
