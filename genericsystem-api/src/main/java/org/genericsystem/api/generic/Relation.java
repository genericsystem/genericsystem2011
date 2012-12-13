package org.genericsystem.api.generic;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Context;

/**
 * A Relation <br/>
 * Link any Type
 * 
 * @author Nicolas Feybesse
 */
public interface Relation extends Attribute, Link {

	/**
	 * Enable cascade remove for the component position
	 * 
	 * @return this
	 */
	<T extends Relation> T enableCascadeRemove(Cache cache, int componentPos);

	/**
	 * Disable cascade remove for the component position
	 * 
	 * @return this
	 */
	<T extends Relation> T disableCascadeRemove(Cache cache, int componentPos);

	/**
	 * Returns true if the cascade remove enabled for the component position
	 * 
	 * @return true if the cascade remove enabled for the component position
	 */
	boolean isCascadeRemove(Context context, int componentPos);

}
