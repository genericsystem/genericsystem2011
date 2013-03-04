package org.genericsystem.core;

import java.io.Serializable;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;

/**
 * A Cache is an environment where Generic are temporarily stored. <br />
 * It is mount on a sub-context that is a Cache or a transaction. <br/>
 * A Cache is never threadsafe.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Cache extends Context {

	/**
	 * Create a new type or get the type with this value if it already exists.
	 * 
	 * @param value
	 *            The type name.
	 * 
	 * @return A new type or the existing type.
	 */
	<T extends Type> T newType(Serializable value);

	/**
	 * Create a new subtype or get the subtype with this value if it already
	 * exists.
	 * 
	 * @param value
	 *            The type name.
	 * @param superTypes
	 *            The array of super types.
	 * 
	 * @return A new subtype or the existing subtype.
	 */
	<T extends Type> T newSubType(Serializable value, Type... superTypes);

	/**
	 * Create a new subtype or get the subtype with this value if it already
	 * exists.
	 * 
	 * @param value
	 *            The type name.
	 * @param superTypes
	 *            The array of super types.
	 * @param components
	 *            The array of components.
	 * 
	 * @return A new subtype or the existing subtype.
	 */
	<T extends Type> T newSubType(Serializable value, Type[] superTypes,
			Generic... components);

	/**
	 * Flush Cache in the subcontext (that is a another Cache or a transaction).
	 * 
	 * @throws RollbackException
	 */
	void flush() throws RollbackException;

	/**
	 * Clear the Cache without flushing in the subcontext.
	 */
	void clear();

	/**
	 * Checks if a Generic is alive in this Cache.
	 * 
	 * @param Generic
	 *            the Generic checked.
	 * @return True if Generic is alive.
	 */
	boolean isAlive(Generic generic);

	/**
	 * Find the Generic defined by class param or create it if it doesn't exist.<br/>
	 * This class must be @SystemGeneric annotated.
	 * 
	 * @see SystemGeneric
	 * 
	 * @param clazz
	 *            The class must be @SystemGeneric annotated.
	 * @return A new Generic or the existing Generic.
	 */
	<T extends Generic> T find(Class<?> clazz);

	/**
	 * Create a new tree or get the tree with this value if it already exists.
	 * 
	 * @param value
	 *            The type name.
	 * 
	 * @return A new tree or the existing type.
	 */
	<T extends Tree> T newTree(Serializable value);

	/**
	 * Create a new tree or get the tree with this value if it already exists.
	 * 
	 * @param value
	 *            The type name.
	 * @param dim
	 *            The dimension of tree.
	 * 
	 * @return A new tree or the existing type.
	 */
	<T extends Tree> T newTree(Serializable value, int dim);

	/**
	 * Create a new Cache on the current Cache.
	 * 
	 * @return The new super Cache.
	 */
	Cache newSuperCache();

	/**
	 * Returns the meta Attribute.
	 * 
	 * @return The meta attribute.
	 */
	<T extends Attribute> T getMetaAttribute();

	/**
	 * Returns the meta Relation.
	 * 
	 * @return The meta Relation.
	 */
	<T extends Relation> T getMetaRelation();

}