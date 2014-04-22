package org.genericsystem.impl.vertex;

import java.io.Serializable;
import java.util.HashMap;

public class Engine extends Vertex {
	private final static String ENGINE_VALUE = "Engine";
	private ValueCache valueCache;

	public Engine() {
		super(null, new Vertex[] {}, ENGINE_VALUE, new Vertex[] {});
	}

	@Override
	public boolean isEngine() {
		return true;
	}

	@Override
	public Engine getEngine() {
		return this;
	}

	@Override
	public int getLevel() {
		return 0;
	}

	public Serializable getCachedValue(Serializable value) {
		return (valueCache != null ? valueCache : (valueCache = new ValueCache())).get(value);
	}

	public static class ValueCache extends HashMap<Serializable, Serializable> {
		private static final long serialVersionUID = 8474952153415905986L;

		@Override
		public Serializable get(Object key) {
			Serializable result = super.get(key);
			if (result == null)
				put(result = (Serializable) key, result);
			return result;
		}
	}
}
