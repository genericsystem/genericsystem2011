package org.genericsystem.generic;

import java.io.Serializable;
import java.util.Map;

import org.genericsystem.core.Generic;

/**
 * @author Nicolas Feybesse
 * 
 */
public interface MapProvider extends Type {

	<Key extends Serializable, Value extends Serializable> Map<Key, Value> getMap(Generic generic);

}
