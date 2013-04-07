package org.genericsystem.generic;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Context;

/**
 * An Attribute.
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Attribute extends Holder, Type {

	/**
	 * Enable multidirectional.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * 
	 * @return Return this.
	 */
	<T extends Attribute> T enableMultiDirectional(Cache cache);

	/**
	 * Disable multidirectional.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * 
	 * @return Return this.
	 */
	<T extends Attribute> T disableMultiDirectional(Cache cache);

	/**
	 * Returns true if the multidirectional system property enabled.
	 * 
	 * @param cache
	 *            The reference Cache.
	 * 
	 * @return Return true if the multidirectional system propertyt enabled.
	 */
	boolean isMultiDirectional(Context context);

	// TODO clean
	// /**
	// * Do all necessary inductions on this attribute.
	// *
	// * @param cache
	// * The reference Cache.
	// */
	// void deduct(Cache cache);
}
