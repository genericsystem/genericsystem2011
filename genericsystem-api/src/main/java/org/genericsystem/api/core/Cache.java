package org.genericsystem.api.core;

import java.io.Serializable;

import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.exception.RollbackException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Tree;
import org.genericsystem.api.generic.Type;

/**
 * A cache is an environment where generics are temporarily stored. <br />
 * It is mount on a sub-context that is a cache or a transaction. <br/>
 * A cache is never threadsafe.
 * 
 * @author Nicolas Feybesse
 */
public interface Cache extends Context {

	/**
	 * Create a new type or get the type with this value if it already exists
	 * 
	 * @param value
	 *            Type name
	 * 
	 * @return a new type or the existing type
	 */
	<T extends Type> T newType(Serializable value);

	/**
	 * Create a new subtype or get the subtype if exists
	 * 
	 * @param value
	 *            Type name
	 * @param superTypes
	 *            array of super types
	 * 
	 * @return a new subtype or the existing subtype
	 */
	<T extends Type> T newSubType(Serializable value, Type... superTypes);

	/**
	 * Create a new subtype or get the subtype if exists
	 * 
	 * @param value
	 *            Type name
	 * @param superTypes
	 *            array of super types
	 * @param components
	 *            array of components
	 * 
	 * @return a new subtype or the existing subtype
	 */
	<T extends Type> T newSubType(Serializable value, Type[] superTypes, Generic... components);

	/**
	 * Flush cache in the subcontext (that is a another cache or a transaction)
	 * 
	 * @throws RollbackException
	 */
	void flush() throws RollbackException;

	/**
	 * Clear the cache without flushing in the subcontext
	 */
	void clear();

	/**
	 * Checks if a generic is alive in this cache
	 * 
	 * @param generic
	 *            checked
	 * @return true if generic is alive
	 */
	boolean isAlive(Generic generic);

	/**
	 * Find the generic defined by class param or create it if it doesn't exist.<br/>
	 * This class must be @SystemGeneric annotated.
	 * 
	 * @see SystemGeneric
	 * 
	 * @param clazz
	 *            must be @SystemGeneric annotated
	 * @return a generic
	 */
	<T extends Generic> T find(Class<?> clazz);

	/**
	 * Create a new tree or get the tree with this value if it already exists
	 * 
	 * @param value
	 *            Type name
	 * 
	 * @return a new tree or the existing type
	 */
	<T extends Tree> T newTree(Serializable value);

	/**
	 * Create a new tree or get the tree with this value if it already exists
	 * 
	 * @param value
	 *            Type name
	 * @param dim
	 *            the dimension of tree
	 * 
	 * @return a new tree or the existing type
	 */
	<T extends Tree> T newTree(Serializable value, int dim);

	/**
	 * @return a new super cache
	 */
	Cache newSuperCache();

	<T extends Attribute> T getMetaAttribute();

	<T extends Relation> T getMetaRelation();

}
