package org.genericsystem.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.core.UnsafeGList.UnsafeComponents;

public abstract class AbstractWriter {

	public abstract ObjectOutputStream getFormalOutputStream();

	public abstract ObjectOutputStream getContentOutputStream();

	public void writeGeneric(GenericImpl generic, Map<Long, HomeTreeNode> homeTreeMap) throws IOException {
		writeTs(generic);
		HomeTreeNode homeTreeNode = generic.homeTreeNode();
		getContentOutputStream().writeLong(homeTreeNode.ts);
		if (!homeTreeMap.containsKey(homeTreeNode.ts)) {
			getContentOutputStream().writeLong(homeTreeNode.metaNode.ts);
			getContentOutputStream().writeObject(homeTreeNode.getValue());
			homeTreeMap.put(homeTreeNode.ts, homeTreeNode);
		}
		if (generic.isEngine())
			return;
		writeAncestors(generic.getSupers());
		writeAncestors(generic.getComponents());
		getFormalOutputStream().writeObject(GenericImpl.class.equals(generic.getClass()) ? null : generic.getClass());
	}

	private void writeTs(Generic generic) throws IOException {
		getFormalOutputStream().writeLong(((GenericImpl) generic).getDesignTs());
		getFormalOutputStream().writeLong(((GenericImpl) generic).getBirthTs());
		getFormalOutputStream().writeLong(((GenericImpl) generic).getLastReadTs());
		getFormalOutputStream().writeLong(((GenericImpl) generic).getDeathTs());
	}

	private void writeAncestors(List<Generic> ancestors) throws IOException {
		getFormalOutputStream().writeInt(ancestors.size());
		for (Generic ancestor : ancestors)
			getFormalOutputStream().writeLong(((GenericImpl) ancestor).getDesignTs());
	}

	public abstract static class AbstractLoader {

		public abstract ObjectInputStream getContentInputStream();

		public abstract ObjectInputStream getFormalInputStream();

		protected abstract void plug(GenericImpl generic);

		protected abstract Generic loadAncestor(Engine engine, Map<Long, Generic> genericMap) throws IOException;

		public Generic loadGeneric(Engine engine, Map<Long, HomeTreeNode> homeTreeMap, Map<Long, Generic> genericMap) throws IOException, ClassNotFoundException {
			long[] ts = loadTs();
			long homeTreeNodeTs = getContentInputStream().readLong();
			HomeTreeNode homeTreeNode = homeTreeMap.get(homeTreeNodeTs);
			if (null == homeTreeNode) {
				long metaTs = getContentInputStream().readLong();
				homeTreeNode = ((GenericImpl) engine).homeTreeNode().ts == metaTs ? ((GenericImpl) engine).homeTreeNode() : homeTreeMap.get(metaTs);
				homeTreeNode = homeTreeNode.bindInstanceNode(homeTreeNodeTs, (Serializable) getContentInputStream().readObject());
			}
			Supers supers = new Supers(loadAncestors(engine, genericMap));
			UnsafeComponents uComponents = new UnsafeComponents(loadAncestors(engine, genericMap));
			Generic generic = engine.getFactory().newGeneric((Class<?>) getFormalInputStream().readObject());
			plug(((GenericImpl) generic).restore(new UnsafeVertex(homeTreeNode, supers, uComponents), ts[0], ts[1], ts[2], ts[3]));
			if (!homeTreeMap.containsKey(homeTreeNodeTs))
				homeTreeMap.put(homeTreeNodeTs, ((GenericImpl) generic).homeTreeNode());
			genericMap.put(ts[0], generic);
			return generic;
		}

		protected long[] loadTs() throws IOException {
			long[] ts = new long[4];
			ts[0] = getFormalInputStream().readLong(); // designTs
			ts[1] = getFormalInputStream().readLong(); // birthTs
			ts[2] = getFormalInputStream().readLong(); // lastReadTs
			ts[3] = getFormalInputStream().readLong(); // deathTs
			return ts;
		}

		private Generic[] loadAncestors(Engine engine, Map<Long, Generic> genericMap) throws IOException {
			int length = getFormalInputStream().readInt();
			Generic[] ancestors = new Generic[length];
			for (int index = 0; index < length; index++)
				ancestors[index] = loadAncestor(engine, genericMap);
			return ancestors;
		}

	}

}
