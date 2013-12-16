package org.genericsystem.generic;

import java.io.Serializable;

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
	 * @param value
	 *            The root name.
	 * @return Return the root.
	 */
	<T extends Node> T addRoot(Serializable value);

	/**
	 * Create a new root.
	 * 
	 * @param value
	 *            The root name.
	 * @param dim
	 *            Dimension of the root.
	 * @return Return the root.
	 */
	<T extends Node> T addRoot(Serializable value, int dim);

	/**
	 * Set a new root.
	 * 
	 * @param value
	 *            The root name.
	 * @param dim
	 *            Dimension of the root.
	 * @return Return the root.
	 */
	<T extends Node> T setRoot(Serializable value, Node root);

	<T extends Node> Snapshot<T> getRoots();

	<T extends Node> T getRootByValue(Serializable value);
}
