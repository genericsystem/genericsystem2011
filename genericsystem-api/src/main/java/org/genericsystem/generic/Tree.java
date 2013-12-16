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
	 * Create a new root.Throws an exception if already exists.
	 * 
	 * @param value
	 *            The root name.
	 * @return Return the root.
	 */
	<T extends Node> T addRoot(Serializable value);

	/**
	 * Create a new root.Throws an exception if already exists.
	 * 
	 * @param value
	 *            The root name.
	 * @param dim
	 *            Dimension of the root.
	 * @return Return the root.
	 */
	<T extends Node> T addRoot(Serializable value, int dim);

	/**
	 * Creates a root or returns this root if this root already exists.
	 * 
	 * @param value
	 *            The root name.
	 * @return Return the root.
	 */
	<T extends Node> T setRoot(Serializable value);

	/**
	 * Creates a root or returns this root if this root already exists.
	 * 
	 * @param value
	 *            The root name.
	 * @param dim
	 *            Dimension of the root.
	 * @return Return the root.
	 */
	<T extends Node> T setRoot(Serializable value, int dim);

	/**
	 * Returns the root elements.
	 * 
	 * @see Snapshot
	 * @return the root elements.
	 */
	<T extends Node> Snapshot<T> getRoots();

	/**
	 * Returns the root by value or null.
	 * 
	 * @param value
	 *            root name
	 * 
	 * @see Snapshot
	 * @return the root or null.
	 */
	<T extends Node> T getRootByValue(Serializable value);
}
