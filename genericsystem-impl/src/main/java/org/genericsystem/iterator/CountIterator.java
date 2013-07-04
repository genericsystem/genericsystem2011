package org.genericsystem.iterator;

public class CountIterator extends AbstractAwareIterator<Integer> {
	private int size;

	public CountIterator(int size) {
		this.size = size;
		next = size != 0 ? 0 : null;
		toRead = false;
	}

	@Override
	protected void advance() {
		next = (next < size - 1 ? ++next : null);
	}
}
