package org.genericsystem.generic;

import java.io.Serializable;
import java.util.Map;

import org.genericsystem.core.Generic;

/**
 * @author Nicolas Feybesse
 * 
 */
public interface MapProvider extends Type {

	Map<Serializable, Serializable> getMap(Generic generic);

}
