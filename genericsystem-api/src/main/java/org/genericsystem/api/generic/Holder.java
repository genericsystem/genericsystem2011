package org.genericsystem.api.generic;

import org.genericsystem.api.core.Generic;

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
	 * @param componentPos
	 *            The component position.
	 * @return Return the component if it exist else null.
	 */
	<T extends Generic> T getComponent(int componentPos);

}
