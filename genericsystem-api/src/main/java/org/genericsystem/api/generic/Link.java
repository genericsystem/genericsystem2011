package org.genericsystem.api.generic;

import org.genericsystem.api.core.Generic;

/**
 * A Link <br/>
 * Link any the instances of the Types
 * 
 * @author Nicolas Feybesse
 */
public interface Link extends Holder {

	<T extends Generic> T getTargetComponent();

}
