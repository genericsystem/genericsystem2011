package org.genericsystem.generic;

import org.genericsystem.core.Generic;

/**
 * A Link <br>
 * Link any the instances of the Types.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Link extends Holder {

	/**
	 * Returns the target component.
	 * 
	 * @return Return the target component.
	 */
	<T extends Generic> T getTargetComponent();

}
