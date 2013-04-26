package org.genericsystem.generic;

import java.io.Serializable;
import org.genericsystem.core.Cache;
import org.genericsystem.core.Context;
import org.genericsystem.core.Snapshot;

/**
 * A Tree.
 * 
 * @author Nicolas Feybesse
 */
public interface Tree extends Attribute {

	/**
	 * Create a new root.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The root name.
	 * @return Return the root.
	 */
	<T extends Node> T newRoot(Cache cache, Serializable value);

	/**
	 * Create a new root.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * @param value
	 *            The root name.
	 * @param dim
	 *            Dimension of the root.
	 * @return Return the root.
	 */
	<T extends Node> T newRoot(Cache cache, Serializable value, int dim);

	<T extends Node> Snapshot<T> getRoots(Context context);

	<T extends Node> T getRootByValue(Context context, Serializable value);
}
