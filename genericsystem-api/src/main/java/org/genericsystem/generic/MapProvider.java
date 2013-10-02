package org.genericsystem.generic;

import java.io.Serializable;

import org.genericsystem.core.Generic;

/**
 * @author Nicolas Feybesse
 * 
 */
public interface MapProvider extends Attribute {

	<Key extends Serializable, Value extends Serializable> ExtendedMap<Key, Value> getMap(Generic generic);

}
