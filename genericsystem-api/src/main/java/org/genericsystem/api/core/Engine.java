package org.genericsystem.api.core;

import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;

/**
 * Instance of class Engine represents a unit of persistence, but also the root of internal graph attached to it.
 * 
 * @author Nicolas Feybesse
 */
public interface Engine extends Type {
	
	/**
	 * Close engine and do a last snapshot if persistent
	 */
	void close();
	
	/**
	 * Returns the meta attribute.
	 * 
	 * @return the meta attribute.
	 */
	<T extends Attribute> T getMetaAttribute();
	
	/**
	 * Returns the meta relation.
	 * 
	 * @return the meta relation.
	 */
	<T extends Relation> T getMetaRelation();
	
	/**
	 * @return a new cache for this engine
	 */
	Cache newCache();
	
	/**
	 * pick a new unique timestamp
	 * 
	 * @return the timestamp
	 */
	long pickNewTs();
}
