package org.genericsystem.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class UnsafeGList extends ArrayList<Generic> {

	private static final long serialVersionUID = 6487730580551161546L;

	public UnsafeGList(Collection<? extends Generic> list) {
		super(list);
		assert !(list instanceof UnsafeGList);
	}

	public UnsafeGList(Generic... array) {
		this(Arrays.asList(array));
	}

	@Override
	public Generic set(int index, Generic element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, Generic element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Generic remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(int index, Collection<? extends Generic> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<Generic> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<Generic> listIterator(final int index) {
		return new ListIterator<Generic>() {
			private final ListIterator<? extends Generic> i = UnsafeGList.super.listIterator(index);

			@Override
			public boolean hasNext() {
				return i.hasNext();
			}

			@Override
			public Generic next() {
				return i.next();
			}

			@Override
			public boolean hasPrevious() {
				return i.hasPrevious();
			}

			@Override
			public Generic previous() {
				return i.previous();
			}

			@Override
			public int nextIndex() {
				return i.nextIndex();
			}

			@Override
			public int previousIndex() {
				return i.previousIndex();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public void set(Generic e) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void add(Generic e) {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public UnsafeGList subList(int fromIndex, int toIndex) {
		return new UnsafeGList(super.subList(fromIndex, toIndex));
	}

	@Override
	public Generic[] toArray() {
		return toArray(new Generic[size()]);
	}

	public static class Supers extends UnsafeGList {

		private static final long serialVersionUID = 1536088085035570991L;

		public Supers(Collection<? extends Generic> list) {
			super(list);
			for (Generic g : this)
				assert g != null;
		}

		public Supers(Generic... array) {
			super(array);
			for (Generic g : array)
				assert g != null;
		}
	}

	public static class Components extends UnsafeComponents {

		private static final long serialVersionUID = 1536088085035570991L;

		public Components(Generic generic, UnsafeComponents uComponents) {
			super(toGList(generic, uComponents));
			for (Generic g : this)
				assert g != null;
		}

		static List<Generic> toGList(Generic generic, UnsafeComponents uComponents) {
			List<Generic> result = new ArrayList<>(uComponents);
			for (int i = 0; i < result.size(); i++)
				if (result.get(i) == null)
					result.set(i, generic);
			return result;
		}

	}

	public static class UnsafeComponents extends UnsafeGList {

		private static final long serialVersionUID = 6107510998671533734L;

		public UnsafeComponents(Collection<? extends Generic> list) {
			super(list);
		}

		public UnsafeComponents(Generic... array) {
			super(array);
		}
	}
}
