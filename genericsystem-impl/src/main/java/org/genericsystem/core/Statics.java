package org.genericsystem.core;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public class Statics {

	private static Logger log = LoggerFactory.getLogger(Statics.class);

	private static ThreadLocal<Long> threadDebugged = new ThreadLocal<Long>();

	public static final Flag FLAG = new Flag();
	public static final Generic[] EMPTY_GENERIC_ARRAY = new Generic[] {};
	public static final String ROOT_NODE_VALUE = "Engine";

	public static final int META = 0;
	public static final int STRUCTURAL = 1;
	public static final int CONCRETE = 2;
	public static final int SENSOR = 3;

	public static final int MULTIDIRECTIONAL = -1;
	public static final int BASE_POSITION = 0;
	public static final int TARGET_POSITION = 1;
	public static final int SECOND_TARGET_POSITION = 2;

	public static final int TYPE_SIZE = 0;
	public static final int ATTRIBUTE_SIZE = 1;
	public static final int RELATION_SIZE = 2;
	public static final int TERNARY_RELATION_SIZE = 3;

	public static final int ATTEMPT_SLEEP = 15; // ms
	public static final int ATTEMPTS = 50;

	public static final long serialVersionUID = -4186936523588998107L;
	public static final String GS_EXTENSION = ".gs";
	public static final String FORMAL_EXTENSION = ".formal";
	public static final String CONTENT_EXTENSION = ".content";
	public static final String PART_EXTENSION = ".part";
	public static final String LOG_PATTERN = "yyyy.MM.dd  HH:mm:ss  SSSS";
	public static final String PATTERN = "yyyy.MM.dd_HH-mm-ss.SSS";
	public static final String MATCHING_REGEX = "[0-9]{4}.[0-9]{2}.[0-9]{2}_[0-9]{2}-[0-9]{2}-[0-9]{2}.[0-9]{3}---[0-9]+";
	public static final String LOCK_FILE_NAME = ".lock";
	public static final String ZIP_EXTENSION = GS_EXTENSION + ".zip";
	public static final long MILLI_TO_NANOSECONDS = 1000000L;
	public static final long ARCHIVER_COEFF = 5L;
	public static final long SNAPSHOTS_PERIOD = 1000L;
	public static final long SNAPSHOTS_INITIAL_DELAY = 1000L;
	public static final long GARBAGE_PERIOD = 1000L;
	public static final long GARBAGE_INITIAL_DELAY = 1000L;
	public static final long LIFE_TIME_OUT = 1386174608777L;// 30 minutes

	public static Generic[] insertIntoArray(Generic generic, Generic[] targets, int basePos) {
		if (basePos < 0 || basePos > targets.length)
			throw new IllegalStateException("Unable to find a valid base position");
		Generic[] result = new Generic[targets.length + 1];
		System.arraycopy(targets, 0, result, 0, basePos);
		result[basePos] = generic;
		System.arraycopy(targets, basePos, result, basePos + 1, result.length - basePos - 1);
		return result;
	}

	static Generic[] insertFirst(Generic first, Generic... others) {
		Generic[] result = new Generic[others.length + 1];
		result[0] = first;
		System.arraycopy(others, 0, result, 1, others.length);
		return result;
	}

	static HomeTreeNode[] insertFirst(HomeTreeNode first, HomeTreeNode... others) {
		HomeTreeNode[] result = new HomeTreeNode[others.length + 1];
		result[0] = first;
		System.arraycopy(others, 0, result, 1, others.length);
		return result;
	}

	public static Generic[] insertLastIntoArray(Generic last, Generic... others) {
		Generic[] result = new Generic[others.length + 1];
		result[result.length - 1] = last;
		System.arraycopy(others, 0, result, 0, others.length);
		return result;
	}

	static Generic[] truncate(int i, Generic[] generics) {
		Generic[] result = new Generic[generics.length - 1];
		System.arraycopy(generics, 0, result, 0, i);
		System.arraycopy(generics, i + 1, result, i, generics.length - 1 - i);
		return result;
	}

	static HomeTreeNode[] truncate(int i, HomeTreeNode[] nodes) {
		HomeTreeNode[] result = new HomeTreeNode[nodes.length - 1];
		System.arraycopy(nodes, 0, result, 0, i);
		System.arraycopy(nodes, i + 1, result, i, nodes.length - 1 - i);
		return result;
	}

	static HomeTreeNode[] truncate(HomeTreeNode[] nodes, HomeTreeNode node) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].equals(node))
				return truncate(i, nodes);
		}
		return nodes;
	}

	static HomeTreeNode[] replace(HomeTreeNode[] homeTreeNodes, HomeTreeNode old, HomeTreeNode newNode) {
		HomeTreeNode[] copy = homeTreeNodes.clone();
		for (int i = 0; i < copy.length; i++)
			if (copy[i].equals(old)) {
				copy[i] = newNode;
				return copy;
			}
		return copy;
	}

	static Generic[] replace(int i, Generic[] generics, Generic generic) {
		Generic[] copy = generics.clone();
		copy[i] = generic;
		return copy;
	}

	public static void debugCurrentThread() {
		threadDebugged.set(System.currentTimeMillis());
	}

	public static void stopDebugCurrentThread() {
		threadDebugged.remove();
	}

	public static boolean isCurrentThreadDebugged() {
		return threadDebugged.get() != null;
	}

	public static void logTimeIfCurrentThreadDebugged(String message) {
		if (isCurrentThreadDebugged())
			log.info(message + " : " + (System.currentTimeMillis() - threadDebugged.get()));
	}

	static class Flag implements Serializable {

		private static final long serialVersionUID = 5132361685064649558L;

		private Flag() {}

		@Override
		public String toString() {
			return "Flag";
		}
	}

	static class AnonymousReference implements Serializable {

		private static final long serialVersionUID = 3786479423748661994L;

		private final long reference;

		public AnonymousReference(long reference) {
			this.reference = reference;
		}

		@Override
		public String toString() {
			return Long.toString(reference);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof AnonymousReference))
				return false;
			return ((AnonymousReference) obj).reference == reference;
		}

		@Override
		public int hashCode() {
			return (int) (reference ^ (reference >>> 32));
		}
	}

	static class TsGenerator {
		private final long startTime = System.currentTimeMillis() * Statics.MILLI_TO_NANOSECONDS - System.nanoTime();
		private AtomicLong lastTime = new AtomicLong(0L);

		long pickNewTs() {
			long nanoTs;
			long current;
			for (;;) {
				nanoTs = startTime + System.nanoTime();
				current = lastTime.get();
				if (nanoTs > current)
					if (lastTime.compareAndSet(current, nanoTs))
						return nanoTs;
			}
		}

		// static String tsToString(long ts) {
		// return new SimpleDateFormat(Statics.PATTERN).format(new Date(ts / Statics.MILLI_TO_NANOSECONDS)).toString();
		// }

	}

	static String getFilename(final long ts) {
		return new SimpleDateFormat(Statics.PATTERN).format(new Date(ts / Statics.MILLI_TO_NANOSECONDS)) + "---" + ts;
	}

	public static Generic[] enrichComponents(Generic[] components, Generic[] additionals) {
		List<Generic> result = new ArrayList<>(Arrays.asList(components));
		for (int i = 0; i < additionals.length; i++)
			if (i >= components.length || (components[i] != null && !components[i].inheritsFrom(additionals[i])))
				result.add(additionals[i]);
		return result.toArray(new Generic[result.size()]);
	}

	// public static class Components extends ArrayList<Generic> {
	// private static final long serialVersionUID = -3285243912802228927L;
	//
	// public Components(Generic[] components, Generic... adds) {
	// super(Arrays.asList(components));
	// for (int i = 0; i < adds.length; i++)
	// if (i >= components.length || (components[i] != null && !components[i].inheritsFrom(adds[i])))
	// add(adds[i]);
	// }
	//
	// @Override
	// public Generic[] toArray() {
	// return toArray(new Generic[size()]);
	// }
	// }

	public static class Supers extends TreeSet<Generic> {
		private static final long serialVersionUID = 4756135385933890439L;

		public Supers(Generic... supers) {
			for (Generic superGeneric : supers)
				add(superGeneric);
		}

		public Supers(Generic[] supers, Generic add) {
			this(supers);
			add(add);
		}

		public Supers(Generic[] supers, Generic[] adds) {
			this(supers);
			for (Generic add : adds)
				add(add);
		}

		@Override
		public Generic[] toArray() {
			return toArray(new Generic[size()]);
		}

		@Override
		public boolean add(Generic candidate) {
			for (Generic generic : this)
				if (generic.inheritsFrom(candidate))
					return false;
			Iterator<Generic> it = iterator();
			while (it.hasNext())
				if (candidate.inheritsFrom(it.next()))
					it.remove();
			return super.add(candidate);
		}
	}

	// public static class Primaries extends TreeSet<HomeTreeNode> {
	// private static final long serialVersionUID = 7222889429002770779L;
	//
	// public Primaries(HomeTreeNode homeTreeNode, Generic... supers) {
	// add(homeTreeNode);
	// for (Generic superGeneric : supers)
	// for (HomeTreeNode primary : ((GenericImpl) superGeneric).primaries)
	// add(primary);
	// }
	//
	// public Primaries(Generic... supers) {
	// // add(homeTreeNode);
	// for (Generic superGeneric : supers)
	// for (HomeTreeNode primary : ((GenericImpl) superGeneric).primaries)
	// add(primary);
	// }
	//
	// public Primaries(HomeTreeNode... primaries) {
	// for (HomeTreeNode primary : primaries)
	// add(primary);
	// }
	//
	// public Primaries(Generic generic, HomeTreeNode... primaries) {
	// for (HomeTreeNode primary : primaries)
	// add(primary);
	// for (HomeTreeNode primary : ((GenericImpl) generic).primaries)
	// add(primary);
	// }
	//
	// @Override
	// public boolean add(HomeTreeNode candidate) {
	// for (HomeTreeNode homeTreeNode : this)
	// if (homeTreeNode.inheritsFrom(candidate))
	// return false;
	// Iterator<HomeTreeNode> it = this.iterator();
	// while (it.hasNext())
	// if (candidate.inheritsFrom(it.next()))
	// it.remove();
	// return super.add(candidate);
	// }
	//
	// @Override
	// public HomeTreeNode[] toArray() {
	// return toArray(new HomeTreeNode[size()]);
	// }
	// }

	public static <T extends Generic> Iterator<T> levelFilter(Iterator<T> iterator, final int instanciationLevel) {
		return new AbstractFilterIterator<T>(iterator) {

			@Override
			public boolean isSelected() {
				return instanciationLevel == next.getMetaLevel();
			}
		};
	}

	public static <T extends Generic> Iterator<T> rootFilter(Iterator<T> iterator) {
		return new AbstractFilterIterator<T>(iterator) {

			@Override
			public boolean isSelected() {
				return next.isRoot();
			}
		};
	}

	public static <T extends Generic> Iterator<T> valueFilter(Iterator<T> iterator, final Serializable value) {
		return new AbstractFilterIterator<T>(iterator) {

			@Override
			public boolean isSelected() {
				return Objects.equals(value, next.getValue());
			}
		};
	}

	public static <T extends Generic> Iterator<T> nullFilter(Iterator<T> iterator) {
		return new AbstractFilterIterator<T>(iterator) {

			@Override
			public boolean isSelected() {
				return next.getValue() != null;
			}
		};
	}
}
