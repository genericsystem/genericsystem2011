package org.genericsystem.api.generic;

import java.io.Serializable;

import org.genericsystem.api.core.Cache;

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

}
