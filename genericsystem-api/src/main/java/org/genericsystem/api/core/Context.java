package org.genericsystem.api.core;

/**
 * A context
 * 
 * @author Nicolas Feybesse
 */
public interface Context {

	/**
	 * Return the engine on witch this context has bean built. If sub context is another cache, return the engine of this another cache.
	 * 
	 * @return the engine
	 */
	<T extends Engine> T getEngine();
}
