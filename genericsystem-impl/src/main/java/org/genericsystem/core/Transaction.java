package org.genericsystem.core;

import java.util.HashSet;

import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.OptimisticLockConstraintViolationException;
import org.genericsystem.iterator.AbstractConcateIterator.ConcateIterator;
import org.genericsystem.iterator.AbstractFilterIterator;

/**
 * @author Nicolas Feybesse
 * 
 */
public class Transaction extends AbstractContext {

	private transient long ts;

	private transient final Engine engine;

	public Transaction(Engine engine) {
		this(engine.pickNewTs(), engine);
	}

	public Transaction(long ts, Engine engine) {
		this.ts = ts;
		this.engine = engine;
	}

	@Override
	public long getTs() {
		return ts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public EngineImpl getEngine() {
		return (EngineImpl) engine;
	}

	@Override
	TimestampedDependencies getInheritingsAndInstances(Generic effectiveSuper) {
		return ((GenericImpl) effectiveSuper).getLifeManager().engineInheritingsAndInstances;
	}

	@Override
	TimestampedDependencies getInstances(Generic meta) {
		return ((GenericImpl) meta).getLifeManager().engineInstances;
	}

	@Override
	TimestampedDependencies getComposites(Generic component) {
		return ((GenericImpl) component).getLifeManager().engineComposites;
	}

	@Override
	public boolean isAlive(Generic generic) {
		return ((GenericImpl) generic).isAlive(getTs());
	}

	@Override
	public boolean isScheduledToRemove(Generic generic) {
		return false;
	}

	@Override
	public boolean isScheduledToAdd(Generic generic) {
		return false;
	}

	@Override
	protected void apply(Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, ConstraintViolationException {
		synchronized (getEngine()) {
			LockedLifeManager lockedLifeManager = new LockedLifeManager();
			try {
				lockedLifeManager.writeLockAllAndCheckMvcc(adds, removes);
				super.apply(adds, removes);
			} finally {
				lockedLifeManager.writeUnlockAll();
			}
		}
	}

	@Override
	protected void simpleAdd(Generic generic) {
		((GenericImpl) generic).getLifeManager().beginLife(getTs());
		super.simpleAdd(generic);
	}

	@Override
	protected void simpleRemove(Generic generic) {
		((GenericImpl) generic).getLifeManager().kill(getTs());
		getEngine().getGarbageCollectorManager().add(generic);
	}

	private class LockedLifeManager extends HashSet<LifeManager> {

		private static final long serialVersionUID = -8771313495837238881L;

		private void writeLockAllAndCheckMvcc(Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			for (Generic generic : removes)
				writeLockAndCheckMvcc(((GenericImpl) generic).getLifeManager());
			for (Generic generic : adds) {
				for (Generic effectiveSuper : ((GenericImpl) generic).getSupers())
					writeLockAndCheckMvcc(((GenericImpl) effectiveSuper).getLifeManager());
				for (Generic component : ((GenericImpl) generic).getComponents())
					writeLockAndCheckMvcc(((GenericImpl) component).getLifeManager());
				writeLockAndCheckMvcc(((GenericImpl) generic).getLifeManager());
			}
		}

		private void writeLockAndCheckMvcc(LifeManager manager) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			if (!contains(manager)) {
				manager.writeLock();
				add(manager);
				manager.checkMvcc(getTs());
			}
		}

		private void writeUnlockAll() {
			for (LifeManager lifeManager : this)
				lifeManager.writeUnlock();
		}
	}

	@Override
	public boolean isAutomatic(Generic generic) {
		return false;
	}

	@Override
	int getLevel() {
		return 0;
	}

	@Override
	// TODO KK
	Generic searchByDesignTs(final long ts) {
		return ((EngineImpl) engine).unambigousFirst(new AbstractFilterIterator<Generic>(new ConcateIterator<Generic>(getInheritingsAndInstances(engine).iterator(getTs()), getComposites(engine).iterator(getTs()))) {
			@Override
			public boolean isSelected() {
				return ((GenericImpl) next).getDesignTs() == ts;
			}
		});
	}
}
