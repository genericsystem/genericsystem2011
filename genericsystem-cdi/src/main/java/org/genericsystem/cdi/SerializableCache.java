package org.genericsystem.cdi;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;

import org.genericsystem.core.AbstractContext;
import org.genericsystem.core.AbstractWriter;
import org.genericsystem.core.AbstractWriter.AbstractLoader;
import org.genericsystem.core.Cache;
import org.genericsystem.core.CacheImpl;
import org.genericsystem.core.Engine;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.HomeTreeNode;
import org.genericsystem.core.Transaction;
import org.jboss.solder.beanManager.BeanManagerLocator;
import org.jboss.solder.beanManager.BeanManagerUtils;
import org.jboss.solder.core.Veto;

@Veto
public class SerializableCache extends CacheImpl implements Externalizable {

	public SerializableCache() {
		super((Cache) null);
	}

	public SerializableCache(Cache cache) {
		super(cache);
	}

	public SerializableCache(Engine engine) {
		super(engine);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		new SerializableWriter(out).writeGenericsAndFlush();
	}

	private class SerializableWriter extends AbstractWriter {
		private ObjectOutput out;

		public SerializableWriter(ObjectOutput out) {
			this.out = out;
		}

		@Override
		public void writeGenerics() throws IOException {
			if (subContext instanceof CacheImpl) {
				out.writeBoolean(true);
				out.writeObject(subContext);
			} else {
				out.writeBoolean(false);
				out.writeLong(((Transaction) subContext).getTs());
			}
			Map<Long, HomeTreeNode> homeTreeMap = new HashMap<>();
			out.writeInt(adds.size());
			for (Generic add : adds)
				writeGeneric((GenericImpl) add, homeTreeMap);
			out.writeInt(automatics.size());
			for (Generic automatic : automatics)
				writeGeneric((GenericImpl) automatic, homeTreeMap);
			out.writeInt(removes.size());
			for (Generic remove : removes)
				out.writeLong(((GenericImpl) remove).getDesignTs());
		}

		@Override
		public ObjectOutputStream getFormalOutputStream() {
			return (ObjectOutputStream) out;
		}

		@Override
		public ObjectOutputStream getContentOutputStream() {
			return getFormalOutputStream();
		}

	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		BeanManager beanManager = getBeanManager();
		Engine engine = BeanManagerUtils.getContextualInstance(beanManager, Engine.class);
		Cache currentCache = engine.getCurrentCache();
		try {
			subContext = in.readBoolean() ? (AbstractContext) in.readObject() : new Transaction(in.readLong(), engine);
			start();
			Map<Long, HomeTreeNode> homeTreeMap = new HashMap<>();
			Map<Long, Generic> genericMap = new HashMap<>();
			int addSize = in.readInt();
			SerializableLoader loader = new SerializableLoader(in);
			for (int i = 0; i < addSize; i++)
				adds.add(loader.loadGeneric(engine, homeTreeMap, genericMap));
			int automaticSize = in.readInt();
			for (int i = 0; i < automaticSize; i++)
				automatics.add(loader.loadGeneric(engine, homeTreeMap, genericMap));
			int removeSize = in.readInt();
			for (int i = 0; i < removeSize; i++)
				removes.add(findByDesignTs(engine, in.readLong(), genericMap));
		} finally {
			currentCache.start();
		}
	}

	protected BeanManager getBeanManager() {
		BeanManager beanManager = null;
		try {
			beanManager = new BeanManagerLocator().getBeanManager();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		if (beanManager == null)
			throw new IllegalStateException();
		return beanManager;
	}

	private static class SerializableLoader extends AbstractLoader {
		private ObjectInput in;

		public SerializableLoader(ObjectInput in) {
			this.in = in;
		}

		@Override
		public ObjectInputStream getContentInputStream() {
			return getFormalInputStream();
		}

		@Override
		public ObjectInputStream getFormalInputStream() {
			return (ObjectInputStream) in;
		}

		@Override
		protected void plug(GenericImpl generic) {
			generic.getCurrentCache().plug(generic);
		}

		@Override
		protected Generic loadAncestor(Engine engine, long ts, Map<Long, Generic> genericMap) {
			return ((CacheImpl) engine.getCurrentCache()).findByDesignTs(engine, ts, genericMap);
		}
	}

}
