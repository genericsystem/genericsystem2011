package org.genericsystem.core;

import org.genericsystem.annotation.SystemGeneric;

/**
 * A Context.
 * 
 * @author Nicolas Feybesse
 */
public interface Context {

	/**
	 * Return the Engine on witch this context has bean built. If sub context is another Cache, return the Engine of this another Cache.
	 * 
	 * @return The Engine.
	 */
	<T extends Engine> T getEngine();

	/**
	 * Find the Generic defined by class param. the generic must have been be built at startup<br/>
	 * This class must be @SystemGeneric annotated.
	 * 
	 * @see SystemGeneric
	 * 
	 * @param clazz
	 *            The class must be @SystemGeneric annotated.
	 * @return A new Generic or the existing Generic.
	 */
	<T extends Generic> T find(Class<?> clazz);

}
