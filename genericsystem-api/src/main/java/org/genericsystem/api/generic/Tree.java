package org.genericsystem.api.generic;

import java.io.Serializable;

import org.genericsystem.api.core.Cache;

/**
 * @author Nicolas Feybesse
 * 
 */
public interface Tree extends Attribute {

	<T extends Node> T newRoot(Cache cache, Serializable value);

	public <T extends Node> T newRoot(Cache cache, Serializable value, int dim);

}
