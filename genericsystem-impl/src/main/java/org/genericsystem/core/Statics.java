package org.genericsystem.core;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Comparator;
import java.util.Date;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public class Statics {

	private static Logger log = LoggerFactory.getLogger(Statics.class);

	private static ThreadLocal<Boolean> threadDebugged = new ThreadLocal<Boolean>();

	public static final Flag FLAG = new Flag();
	public static final Generic[] EMPTY_GENERIC_ARRAY = new Generic[] {};

	public static final int NO_POSITION = -1;
	public static final int BASE_POSITION = 0;
	public static final int TARGET_POSITION = 1;
	public static final int SECOND_TARGET_POSITION = 2;

	public static final int TYPE_SIZE = 1;
	public static final int ATTRIBUTE_SIZE = 2;
	public static final int RELATION_SIZE = 3;
	public static final int TERNARY_RELATION_SIZE = 4;

	public static final int ATTEMPT_SLEEP = 15; // ms
	public static final int ATTEMPTS = 50;

	public static final long serialVersionUID = -4186936523588998107L;
	public static final String SNAPSHOT_EXTENSION = ".snapshot";
	public static final String PART_EXTENSION = ".part";
	public static final String PATTERN = "yyyy.MM.dd_HH-mm-ss.SSS";
	public static final String MATCHING_REGEX = "[0-9]{4}.[0-9]{2}.[0-9]{2}_[0-9]{2}-[0-9]{2}-[0-9]{2}.[0-9]{3}---[0-9]+";
	public static final String LOCK_FILE_NAME = ".lock";
	public static final String ZIP_EXTENSION = SNAPSHOT_EXTENSION + ".zip";
	public static final long MILLI_TO_NANOSECONDS = 1000000L;
	public static final long SNAPSHOTS_PERIOD = 1000L;
	public static final long SESSION_TIMEOUT = 1000L;
	public static final long ARCHIVER_COEFF = 5L;

	public static final Serializable PHAMTOM = new Phantom();

	private static class Phantom implements Serializable {
		private static final long serialVersionUID = -5467290057275284040L;

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Phantom;
		}

		@Override
		public int hashCode() {
			return 0;
		}
	}

	@SuppressWarnings("rawtypes")
	private static final Iterator EMPTY_ITERATOR = new Iterator<Object>() {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Object next() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	};

	@SuppressWarnings("unchecked")
	public static <T> Iterator<T> emptyIterator() {
		return EMPTY_ITERATOR;
	}

	public static Generic[] insertIntoArray(Generic generic, Generic[] targets, int basePos) {
		Generic[] result = new Generic[targets.length + 1];
		System.arraycopy(targets, 0, result, 0, basePos);
		result[basePos] = generic;
		System.arraycopy(targets, basePos, result, basePos + 1, result.length - basePos - 1);
		return result;
	}

	static Generic[] insertFirstIntoArray(Generic first, Generic... others) {
		Generic[] result = new Generic[others.length + 1];
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

	static Generic[] replace(int i, Generic[] generics, Generic generic) {
		Generic[] copy = generics.clone();
		copy[i] = generic;
		return copy;
	}

	public static void debugCurrentThread() {
		threadDebugged.set(true);
	}

	public static void stopDebugCurrentThread() {
		threadDebugged.remove();
	}

	public static boolean isCurrentThreadDebugged() {
		return Boolean.TRUE.equals(threadDebugged.get());
	}

	public static void logIfCurrentThreadDebugged(String message) {
		if (isCurrentThreadDebugged())
			log.info(message);
	}

	private static class Flag implements Serializable {

		private static final long serialVersionUID = 5132361685064649558L;

		private Flag() {
		}

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

	// static class ThreadContextManager implements ContextManager {
	//
	// private ThreadLocal<Context> contextLocal = new ThreadLocal<Context>() {
	// @Override
	// protected Context initialValue() {
	// return null;
	// }
	// };
	//
	// @Override
	// public Context get() {
	// return contextLocal.get();
	// }
	//
	// @Override
	// public void set(Context newContext) {
	// contextLocal.set(newContext);
	// }
	// }

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
	}

	static String getFilename(final long ts) {
		return new SimpleDateFormat(Statics.PATTERN).format(new Date(ts / Statics.MILLI_TO_NANOSECONDS)) + "---" + ts;
	}

	public static class Primaries extends TreeSet<Generic> {
		private static final long serialVersionUID = 7222889429002770779L;

		public Primaries(Generic... generics) {
			super(new Comparator<Generic>() {

				@Override
				public int compare(Generic generic1, Generic generic2) {
					Deque<Generic> deque1 = getPrimariesStack(generic1);
					Deque<Generic> deque2 = getPrimariesStack(generic2);

					while (!deque1.isEmpty() && !deque2.isEmpty()) {
						int compare = deque1.pop().compareTo(deque2.pop());
						if (compare != 0)
							return compare;
					}
					return deque1.isEmpty() ? !deque2.isEmpty() ? 1 : 0 : -1;
				}
			});
			add(generics);
		}

		static Deque<Generic> getPrimariesStack(final Generic generic) {
			return new ArrayDeque<Generic>() {
				private static final long serialVersionUID = 1525213213728043363L;
				{
					push(generic);
				}

				@Override
				public void push(Generic generic) {
					if (!generic.isEngine()) {
						super.push(generic);
						push(((GenericImpl) generic).supers[0]);
					}
				}
			};
		}

		public boolean add(Generic[] generics) {
			boolean modified = false;
			for (Generic generic : generics)
				if (add(generic))
					modified = true;
			return modified;
		}

		@Override
		public boolean add(Generic generic) {
			if (((GenericImpl) generic).isPrimary())
				return restrictedAdd(generic);
			boolean adds = false;
			for (Generic directSuper : ((GenericImpl) generic).supers)
				if (add(directSuper))
					adds = true;
			return adds;
		}

		private boolean restrictedAdd(Generic candidate) {
			assert ((GenericImpl) candidate).isPrimary();

			for (Generic generic : this)
				if (generic.inheritsFrom(candidate)) {
					return false;
				}
			Iterator<Generic> it = this.iterator();
			while (it.hasNext()) {
				Generic next = it.next();
				if (candidate.inheritsFrom(next)) {
					it.remove();
				}
			}
			return super.add(candidate);
		};

		@Override
		public Generic[] toArray() {
			return toArray(new Generic[size()]);
		}
	}

	public static <T extends Generic> Iterator<T> levelFilter(Iterator<T> iterator, final int instanciationLevel) {
		return new AbstractFilterIterator<T>(iterator) {

			@Override
			public boolean isSelected() {
				return instanciationLevel == next.getMetaLevel();
			}
		};
	}

	public static <T extends Generic> Iterator<T> targetsFilter(Iterator<T> iterator, Attribute relation, final Generic... targets) {
		final Map<Generic, Integer> positions = ((GenericImpl) relation).getPositions(targets);
		return new AbstractFilterIterator<T>(iterator) {
			@Override
			public boolean isSelected() {
				for (Generic target : targets)
					if (!target.equals(((Holder) next).getComponent(positions.get(target))))
						return false;
				return true;
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

	public static <T extends Generic> Iterator<T> phantomsFilter(Iterator<T> iterator) {
		return new AbstractFilterIterator<T>(iterator) {

			@Override
			public boolean isSelected() {
				return !((GenericImpl) next).isPhantom();
			}
		};
	}

	static <T> T unambigousFirst(Iterator<T> iterator) {
		if (!iterator.hasNext())
			return null;
		T result = iterator.next();
		if (iterator.hasNext()) {
			StringBuilder sb = new StringBuilder(result.toString());
			while (iterator.hasNext())
				sb.append(" , " + iterator.next());
			throw new IllegalStateException("Ambigous reponse : " + sb.toString());
		}
		return result;
	}
}
