package org.genericsystem.impl.core;

import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.genericsystem.api.core.Generic;
import org.genericsystem.api.exception.ConcurrencyControlException;
import org.genericsystem.api.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.impl.core.AbstractContext.TimestampedDependencies;
import org.genericsystem.impl.iterator.AbstractGeneralAwareIterator;

public class LifeManager {

	private final long designTs;
	private long birthTs;
	private long deathTs;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private AtomicLong lastReadTs;
	EngineDependencies engineDirectInheritings = new EngineDependencies();
	EngineDependencies engineComposites = new EngineDependencies();

	public LifeManager(long designTs, long birthTs, long lastReadTs, long deathTs) {
		this.designTs = designTs;
		this.birthTs = birthTs;
		this.lastReadTs = new AtomicLong(lastReadTs);
		this.deathTs = deathTs;
	}

	public void beginLife(long birthTs) {
		assert isWriteLockedByCurrentThread();
		assert this.birthTs == Long.MAX_VALUE : "Generic is already born";
		this.birthTs = birthTs;
	}

	// public void notConcurrentBeginLife(long birthTs) {
	// this.birthTs = birthTs;
	// }

	void cancelBeginLife() {
		assert isWriteLockedByCurrentThread();
		this.birthTs = Long.MAX_VALUE;
	}

	public boolean isAlive(long contextTs) {
		// NotThreadSafe at all
		if (contextTs < birthTs)// Pas de reference à deathTs ici
			return false;
		readLock();
		try {
			atomicAdjustLastReadTs(contextTs);
			return contextTs >= birthTs && contextTs < deathTs;
		} finally {
			readUnlock();
		}
	}

	void checkMvcc(long contextTs) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
		if (deathTs != Long.MAX_VALUE)
			throw new OptimisticLockConstraintViolationException("Attempt to kill a generic that is already killed by another thread");
		assert isWriteLockedByCurrentThread();
		if (contextTs < lastReadTs.get())
			throw new ConcurrencyControlException("" + contextTs + " " + lastReadTs.get());
	}

	void kill(long contextTs) {
		assert lock.isWriteLockedByCurrentThread();
		assert contextTs >= birthTs : "Can not kill a generic that is not yet born";
		assert deathTs == Long.MAX_VALUE : "Can not kill a generic that will die in the future";
		assert contextTs >= getLastReadTs() : "Mvcc rule violation";
		atomicAdjustLastReadTs(contextTs);
		deathTs = contextTs;
	}

	void resurect() {
		assert isWriteLockedByCurrentThread();
		deathTs = Long.MAX_VALUE;
	}

	long getLastReadTs() {
		return lastReadTs.get();
	}

	long getDesignTs() {
		return this.designTs;
	}

	long getDeathTs() {
		return this.deathTs;
	}

	public void atomicAdjustLastReadTs(long contextTs) {
		for (;;) {
			long current = lastReadTs.get();
			if (contextTs <= current)
				break;
			if (lastReadTs.compareAndSet(current, contextTs))
				break;
		}
	}

	void writeLock() {
		lock.writeLock().lock();
		// try {
		// if (!lock.writeLock().tryLock(Statics.TIMEOUT,
		// TimeUnit.MILLISECONDS))
		// throw new IllegalStateException("Can't acquire a write lock");
		// } catch (InterruptedException e) {
		// throw new IllegalStateException("Can't acquire a write lock");
		// }
	}

	void writeUnlock() {
		lock.writeLock().unlock();
	}

	public void readLock() {
		lock.readLock().lock();
		// try {
		// if (!lock.readLock().tryLock(Statics.TIMEOUT, TimeUnit.MILLISECONDS))
		// throw new IllegalStateException("Can't acquire a read lock");
		// } catch (InterruptedException e) {
		// throw new IllegalStateException("Can't acquire a read lock");
		// }
	}

	public void readUnlock() {
		lock.readLock().unlock();
	}

	public boolean isWriteLockedByCurrentThread() {
		return lock.isWriteLockedByCurrentThread();
	}

	long getBirthTs() {
		return birthTs;
	}

	public boolean willDie() {
		return deathTs != Long.MAX_VALUE;
	}

	public class EngineDependencies implements TimestampedDependencies, Serializable {

		private static final long serialVersionUID = 8737884947844732434L;
		private node head = null;
		private node tail = null;

		@Override
		public void add(Generic element) {
			// assert isWriteLockedByCurrentThread();
			assert element != null;

			node newNode = new node(element);
			if (head == null)
				head = newNode;
			else
				tail.next = newNode;
			tail = newNode;
		}

		@Override
		public void remove(Generic generic) {
			assert isWriteLockedByCurrentThread();
			assert generic != null : "generic is null";
			assert head != null : "head is null";

			node currentNode = head;

			Generic currentContent = currentNode.content;
			if (generic.equals(currentContent)) {
				node next = currentNode.next;
				head = next != null ? next : null;
				return;
			}

			node nextNode = currentNode.next;
			while (nextNode != null) {
				Generic nextGeneric = nextNode.content;
				node nextNextNode = nextNode.next;
				if (generic.equals(nextGeneric)) {
					nextNode.content = null;
					if (nextNextNode == null)
						tail = currentNode;
					currentNode.next = nextNextNode;
					return;
				}
				currentNode = nextNode;
				nextNode = nextNextNode;
			}
			throw new IllegalStateException("Generic not found");
		}

		@Override
		public Iterator<Generic> iterator(final long ts) {
			return new InternalIterator(ts);
		}

		private class InternalIterator extends AbstractGeneralAwareIterator<node, Generic> {

			private long ts;

			private InternalIterator(long iterationTs) {
				this.ts = iterationTs;
			}

			@Override
			protected void advance() {
				do {
					node nextNode = (next == null) ? head : next.next;
					if (nextNode == null) {
						/*
						 * if (iterationTs <= lock.getLastReadTs()) { currentNode = head; if (currentNode == null) return; } else {
						 */
						readLock();
						try {
							nextNode = (next == null) ? head : next.next;
							if (nextNode == null) {
								next = null;
								atomicAdjustLastReadTs(ts);
								return;
							}
						} finally {
							readUnlock();
						}
						// }
					}
					next = nextNode;
				} while (next.content == null || !((GenericImpl) next.content).isAlive(ts));
			}

			@Override
			protected Generic project() {
				return next.content;
			}
		}

		public boolean isEmpty() {
			return head == null;
		}
	}

	private static class node implements Serializable {
		private static final long serialVersionUID = -2908772989746062209L;
		Generic content;
		node next;

		private node(Generic content) {
			this.content = content;
		}
	}
}