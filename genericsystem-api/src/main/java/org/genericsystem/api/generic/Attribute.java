package org.genericsystem.api.generic;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Context;

/**
 * An Attribute
 * 
 * @author Nicolas Feybesse
 */
public interface Attribute extends Value, Type {
	
	/**
	 * Enable multidirectional
	 * 
	 * @return this
	 */
	<T extends Attribute> T enableMultiDirectional(Cache cache);
	
	/**
	 * Disable multidirectional
	 * 
	 * @return this
	 */
	<T extends Attribute> T disableMultiDirectional(Cache cache);
	
	/**
	 * Returns true if the multidirectional system property enabled
	 * 
	 * @return true if the multidirectional system propertyt enabled
	 */
	boolean isMultiDirectional(Context context);
	
	/**
	 * Do all necessary inductions on this attribute
	 * 
	 * @param cache
	 */
	void induce(Cache cache);
}
