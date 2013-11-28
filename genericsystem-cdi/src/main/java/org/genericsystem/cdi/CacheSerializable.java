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

import org.genericsystem.core.Archiver.SnapshotLoader;
import org.genericsystem.core.Archiver.SnapshotWriter;
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
public class CacheSerializable extends CacheImpl implements Externalizable {

	public CacheSerializable() {
		// call by serialization
		super((Cache) null);
	}

	public CacheSerializable(Cache cache) {
		super(cache);
	}

	public CacheSerializable(Engine engine) {
		super(engine);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		if (subContext instanceof CacheImpl) {
			out.writeBoolean(true);
			((Externalizable) subContext).writeExternal(out);
		} else {
			out.writeBoolean(false);
			out.writeLong(((Transaction) subContext).getTs());
		}

		Map<Long, HomeTreeNode> homeTreeMap = new HashMap<>();
		out.writeInt(adds.size());
		for (Generic add : adds)
			SnapshotWriter.writeGeneric((GenericImpl) add, (ObjectOutputStream) out, (ObjectOutputStream) out, homeTreeMap);
		out.writeInt(automatics.size());
		for (Generic automatic : automatics)
			SnapshotWriter.writeGeneric((GenericImpl) automatic, (ObjectOutputStream) out, (ObjectOutputStream) out, homeTreeMap);
		out.writeInt(removes.size());
		for (Generic remove : removes)
			out.writeLong(((GenericImpl) remove).getDesignTs());
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		BeanManager beanManager = new BeanManagerLocator().getBeanManager();
		assert beanManager != null;
		Engine engine = BeanManagerUtils.getContextualInstance(beanManager, Engine.class);
		if (in.readBoolean()) {
			subContext = new CacheSerializable();
			((Externalizable) subContext).readExternal(in);
		} else
			subContext = new Transaction(in.readLong(), engine);

		Map<Long, HomeTreeNode> homeTreeMap = new HashMap<>();
		Map<Long, Generic> genericMap = new HashMap<>();
		int addSize = in.readInt();
		for (int i = 0; i < addSize; i++)
			adds.add(SnapshotLoader.loadGeneric(engine, (ObjectInputStream) in, (ObjectInputStream) in, homeTreeMap, genericMap));
		int automaticSize = in.readInt();
		for (int i = 0; i < automaticSize; i++)
			automatics.add(SnapshotLoader.loadGeneric(engine, (ObjectInputStream) in, (ObjectInputStream) in, homeTreeMap, genericMap));
		int removeSize = in.readInt();
		for (int i = 0; i < removeSize; i++)
			removes.add(findByDesignTs(engine, (ObjectInputStream) in, genericMap));
	}
}
