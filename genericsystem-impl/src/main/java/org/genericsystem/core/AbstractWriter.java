package org.genericsystem.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import org.genericsystem.core.AbstractWriter.AbstractLoader.GSClassNotFound;
import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.core.UnsafeGList.UnsafeComponents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWriter {

	protected static Logger log = LoggerFactory.getLogger(AbstractWriter.class);

	public abstract ObjectOutputStream getFormalOutputStream();

	public abstract ObjectOutputStream getContentOutputStream();

	public void writeGenericsAndFlush() throws IOException {
		writeGenerics();
		getFormalOutputStream().flush();
		getContentOutputStream().flush();
	}

	public abstract void writeGenerics() throws IOException;

	protected void writeGeneric(GenericImpl generic, Map<Long, HomeTreeNode> homeTreeMap) throws IOException {
		writeTs(generic);
		HomeTreeNode homeTreeNode = generic.homeTreeNode();
		getContentOutputStream().writeLong(homeTreeNode.ts);
		if (!homeTreeMap.containsKey(homeTreeNode.ts)) {
			getContentOutputStream().writeLong(homeTreeNode.metaNode.ts);

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

			objectOutputStream.writeObject(homeTreeNode.getValue());
			objectOutputStream.flush();

			getContentOutputStream().writeObject(byteArrayOutputStream.toByteArray());
			homeTreeMap.put(homeTreeNode.ts, homeTreeNode);
		}
		if (generic.isEngine())
			return;
		writeAncestors(generic.getSupers());
		writeAncestors(generic.getComponents());
		getFormalOutputStream().writeObject(GSClassNotFound.class.isAssignableFrom(generic.getClass()) ? ((GSClassNotFound) generic).getClassName() : generic.getClass().getName());
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

		protected abstract Generic loadAncestor(Engine engine, long ts, Map<Long, Generic> genericMap);

		public Generic loadGeneric(Engine engine, Map<Long, HomeTreeNode> homeTreeMap, Map<Long, Generic> genericMap) throws IOException, ClassNotFoundException {
			long[] ts = loadTs();
			long homeTreeNodeTs = getContentInputStream().readLong();
			HomeTreeNode homeTreeNode = homeTreeMap.get(homeTreeNodeTs);
			if (null == homeTreeNode) {
				long metaTs = getContentInputStream().readLong();
				homeTreeNode = ((GenericImpl) engine).homeTreeNode().ts == metaTs ? ((GenericImpl) engine).homeTreeNode() : homeTreeMap.get(metaTs);
				homeTreeNode = homeTreeNode.bindInstanceNode(homeTreeNodeTs, getGenericValue());
			}
			Supers supers = new Supers(loadAncestors(engine, genericMap));
			UnsafeComponents uComponents = new UnsafeComponents(loadAncestors(engine, genericMap));
			Generic generic = newGeneric(engine);
			plug(((GenericImpl) generic).restore(new UnsafeVertex(homeTreeNode, supers, uComponents), ts[0], ts[1], ts[2], ts[3]));
			if (!homeTreeMap.containsKey(homeTreeNodeTs))
				homeTreeMap.put(homeTreeNodeTs, ((GenericImpl) generic).homeTreeNode());
			genericMap.put(ts[0], generic);
			return generic;
		}

		private Serializable getGenericValue() throws IOException {
			try {
				byte[] bytes = (byte[]) getContentInputStream().readObject();
				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
				try {
					return (Serializable) new ObjectInputStream(byteArrayInputStream).readObject();
				} catch (Exception e) {
					return bytes;
				}

				// Serializable serializable = (Serializable) getContentInputStream().readObject();
				// if (serializable.getClass().isAssignableFrom(GSClassNotFound.class))
				// return Class.forName(((GSClassNotFound) serializable).getClassName());
				// return serializable;
			} catch (ClassNotFoundException e) {
				log.warn("ClassNotFoundException " + e.getMessage());
				return new GSClassNotFound(e.getMessage());
			}
		}

		private Generic newGeneric(Engine engine) throws IOException, ClassNotFoundException {
			String className = (String) getFormalInputStream().readObject();
			try {
				return engine.getFactory().newGeneric(Class.forName(className));
			} catch (ClassNotFoundException e) {
				log.warn("ClassNotFoundException " + e.getMessage());
				GSClassNotFound generic = (GSClassNotFound) engine.getFactory().newGeneric(GSClassNotFound.class);
				generic.setClassName(className);
				return generic;
			}
		}

		public static class GSClassNotFound extends GenericImpl implements Serializable {

			private static final long serialVersionUID = 8872715326590973682L;

			private String className;

			public GSClassNotFound() {}

			public GSClassNotFound(String className) {
				this.className = className;
			}

			private void writeObject(ObjectOutputStream oos) throws IOException {
				oos.writeObject(className);
			}

			public String getClassName() {
				return className;
			}

			public void setClassName(String className) {
				this.className = className;
			}

			@Override
			public String toString() {
				return "GSClassNotFound " + className;
			}

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
				ancestors[index] = loadAncestor(engine, getFormalInputStream().readLong(), genericMap);
			return ancestors;
		}

	}

}
