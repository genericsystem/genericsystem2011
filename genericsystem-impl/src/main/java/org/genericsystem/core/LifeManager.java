package org.genericsystem.core;

import java.io.Serializable;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.genericsystem.core.AbstractContext.TimestampedDependencies;
import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.iterator.AbstractGeneralAwareIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Nicolas Feybesse
 * 
 */
public class LifeManager {
	protected static Logger log = LoggerFactory.getLogger(LifeManager.class);

	private final long designTs;
	private long birthTs;
	private AtomicLong lastReadTs;
	private long deathTs;
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	@Deprecated
	final EngineDependencies engineInheritingsAndInstances = new EngineDependencies();
	final EngineDependencies engineInstances = new EngineDependencies();
	final EngineDependencies engineInheritings = new EngineDependencies();
	final EngineDependencies engineComposites = new EngineDependencies();

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
		birthTs = Long.MAX_VALUE;
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
		return designTs;
	}

	long getDeathTs() {
		return deathTs;
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

	public void writeUnlock() {
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
		private Node head = null;
		private Node tail = null;

		@Override
		public void add(Generic element) {
			// assert isWriteLockedByCurrentThread();
			assert element != null;

			Node newNode = new Node(element);
			if (head == null)
				head = newNode;
			else
				tail.next = newNode;
			tail = newNode;
		}

		@Override
		public void remove(Generic generic) {
			// assert isWriteLockedByCurrentThread();
			assert generic != null : "generic is null";
			assert head != null : "head is null";

			Node currentNode = head;

			Generic currentContent = currentNode.content;
			if (generic.equals(currentContent)) {
				Node next = currentNode.next;
				head = next != null ? next : null;
				return;
			}

			Node nextNode = currentNode.next;
			while (nextNode != null) {
				Generic nextGeneric = nextNode.content;
				Node nextNextNode = nextNode.next;
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

		private class InternalIterator extends AbstractGeneralAwareIterator<Node, Generic> {

			private long ts;

			private InternalIterator(long iterationTs) {
				ts = iterationTs;
			}

			@Override
			protected void advance() {
				do {
					Node nextNode = (next == null) ? head : next.next;
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

	private static class Node implements Serializable {
		private static final long serialVersionUID = -2908772989746062209L;
		Generic content;
		Node next;

		private Node(Generic content) {
			this.content = content;
		}
	}
}