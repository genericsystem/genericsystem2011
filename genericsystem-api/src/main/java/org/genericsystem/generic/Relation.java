package org.genericsystem.generic;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Context;

/**
 * A Relation <br/>
 * Link any Type
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Relation extends Attribute, Link {
	
	/**
	 * Enable cascade remove for the component position.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param basePos
	 *            The component position.
	 * @return Return this.
	 */
	<T extends Relation> T enableCascadeRemove(Cache cache, int basePos);
	
	/**
	 * Disable cascade remove for the component position
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param basePos
	 *            The base position.
	 * @return Return this.
	 */
	<T extends Relation> T disableCascadeRemove(Cache cache, int basePos);
	
	/**
	 * Returns true if the cascade remove enabled for the component position
	 * 
	 * @param context
	 *            The reference Context.
	 * @param basePos
	 *            The base position.
	 * @return true if the cascade remove enabled for the component position
	 */
	boolean isCascadeRemove(Context context, int basePos);
	
}
