package org.genericsystem.core;

import java.io.Serializable;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.exception.RollbackException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Tree;
import org.genericsystem.generic.Type;

/**
 * <p>
 * <tt>Cache</tt> stores modifications before they are applied to <tt>Engine</tt>. Using caches assure concurrency and data integrity.
 * </p>
 * 
 * <p>
 * All modifications pass by cache before been persisted into <tt>Engine</tt>. Modifications in the cache can be persisted into <tt>Engine</tt> or abandoned. In the first case, data in Generic System change and changes become visible to all users. In the
 * second case all modifications in the cache are lost and user return to sub cache version.
 * </p>
 * 
 * <p>
 * <tt>Cache</tt> makes automatic rollback if an error occurred during the work. Rollback cancel all modifications previously bring to the cache.
 * </p>
 * 
 * <p>
 * It is possible to mount one cache on another and so on. To use "supercaches" helps to avoid the loss of the work because of unexpected rollback.
 * </p>
 * 
 * <p>
 * The <tt>Cache</tt> is mount on <tt>Transaction</tt>. <tt>Transaction</tt> is unique for every user of the system.
 * </p>
 * 
 * <p>
 * <tt>Cache</tt> is not treadsafe.
 * </p>
 * 
 * <p>
 * This interface is a part of <tt>Generic System Core</tt>.
 * </p>
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Cache {

	/**
	 * Starts the execution of this cache.
	 * 
	 * @return this cache.
	 */
	Cache start();

	/**
	 * Flush the content of current cache into it's subcache or into current user's transaction. If cache flush it's data into transaction modifications become available to other users.
	 * 
	 * @throws RollbackException
	 */
	void flush() throws RollbackException;

	/**
	 * Clear the cache without flushing.
	 */
	void clear();

	/**
	 * Returns the generic found by it's class. This generic must to be created in startup. To create a startup built generic it's class must to be annotated @SystemGeneric.
	 * 
	 * @param clazz
	 *            the class annotated @SystemGeneric.
	 * 
	 * @return the generic defined by it's class.
	 * 
	 * @see SystemGeneric
	 */
	<T extends Generic> T find(Class<?> clazz);

	/**
	 * Returns the Engine of this cache.
	 * 
	 * @return the engine.
	 */
	<T extends Engine> T getEngine();

	/**
	 * Returns all Types existing in current cache.
	 * 
	 * @return collection of Type.
	 * @see Type
	 */
	Snapshot<Type> getAllTypes();

	/**
	 * Returns the meta Attribute.
	 * 
	 * @return the meta attribute.
	 */
	<T extends Attribute> T getMetaAttribute();

	/**
	 * Returns the meta Relation.
	 * 
	 * @return the meta Relation.
	 */
	<T extends Relation> T getMetaRelation();

	/**
	 * Creates a new type. Throws an exception if the type with the same name already exists.
	 * 
	 * @param name
	 *            the type's name.
	 * @param superTypes
	 *            the array of super types.
	 * 
	 * @return the Type.
	 * @see Type
	 */
	<T extends Type> T addType(Serializable name, Type... superTypes);

	/**
	 * Creates a new type. Throws an exception if the type with the same name already exists.
	 * 
	 * @param name
	 *            the type's name.
	 * @param superTypes
	 *            the array of super types.
	 * @param components
	 *            the array of components.
	 * 
	 * @return the Type.
	 * @see Type
	 */
	<T extends Type> T addType(Serializable name, Type[] superTypes, Generic... components);

	/**
	 * Returns the existing Type or null if it not exists.
	 * 
	 * @param name
	 *            the type's name.
	 * 
	 * @return the Type or null if it not exists.
	 * @see Type
	 */
	<T extends Type> T getType(Serializable name);

	/**
	 * Returns the type. If the type with given name does not exists method creates it.
	 * 
	 * @param name
	 *            the type's name.
	 * @param superTypes
	 *            the array of super types.
	 * 
	 * @return the Type.
	 * @see Type
	 */
	<T extends Type> T setType(Serializable name, Type... superTypes);

	/**
	 * Returns the type. If the type with given name does not exists method creates it.
	 * 
	 * @param name
	 *            the type's name.
	 * @param superTypes
	 *            the array of super types.
	 * @param components
	 *            the array of components.
	 * 
	 * @return the Type.
	 * @see Type
	 */
	<T extends Type> T setType(Serializable name, Type[] superTypes, Generic... components);

	/**
	 * Creates a new Tree. Throws an exception if the tree with the same name already exists.
	 * 
	 * @param name
	 *            the tree's name.
	 * 
	 * @return the Tree.
	 * @see Tree
	 */
	<T extends Tree> T addTree(Serializable name);

	/**
	 * Creates a new Tree. Throws an exception if the tree with the same name already exists.
	 * 
	 * @param name
	 *            the tree's name.
	 * @param dimension
	 *            the dimension of the tree.
	 * 
	 * @return the Tree.
	 * @see Tree
	 */
	<T extends Tree> T addTree(Serializable name, int dimension);

	/**
	 * Returns the existing Tree or creates a new one if it not yet exists.
	 * 
	 * @param name
	 *            the tree's name.
	 * 
	 * @return the Tree.
	 * @see Tree
	 */
	<T extends Tree> T setTree(Serializable name);

	/**
	 * Returns the existing Tree or creates a new one if it not yet exists.
	 * 
	 * @param name
	 *            the tree's name.
	 * @param dimension
	 *            the dimension of the tree.
	 * 
	 * @return the Tree.
	 * @see Tree
	 */
	<T extends Tree> T setTree(Serializable name, int dimension);

	/**
	 * Returns true if the generic was not removed from this cache or from any of it's sub caches.
	 * 
	 * @param generic
	 *            the generic to check.
	 * 
	 * @return true if the generic still present in any of caches in the current cache stack.
	 */
	boolean isAlive(Generic generic);

	/**
	 * Returns true if the generic is removable.
	 * 
	 * @param generic
	 *            the generic.
	 * 
	 * @return true if the generic is removable.
	 */
	boolean isRemovable(Generic generic);

	/**
	 * Mounts and starts the new cache on this cache.
	 * 
	 * @return the new super cache.
	 */
	Cache mountNewCache();

	/**
	 * Flushes this cache into it's sub cache. Returns it's sub cache after the flush. If this is the cache of the first level (cache mount directly on current transaction) function returns the same cache.
	 * 
	 * @return the sub cache.
	 */
	Cache flushAndUnmount();

	/**
	 * Discards changes in this cache and returns the sub cache. If this is the cache of the first level (cache mount directly on current transaction) function returns the same cache.
	 * 
	 * @return the sub cache.
	 */
	Cache discardAndUnmount();

	/**
	 * Returns the level of this cache. Level 1 is equivalent to the cache of first level (cache mount directly on current transaction).
	 * 
	 * @return the level of this cache.
	 */
	int getLevel();

}
