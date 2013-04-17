package org.genericsystem.core;

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

}
