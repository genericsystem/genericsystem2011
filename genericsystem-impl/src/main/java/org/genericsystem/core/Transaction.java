package org.genericsystem.core;

import java.util.HashSet;
import java.util.Set;

import org.genericsystem.exception.ConcurrencyControlException;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.OptimisticLockConstraintViolationException;

/**
 * @author Nicolas Feybesse
 * 
 */
public class Transaction extends AbstractContext {

	private static final long serialVersionUID = 3123447500772450391L;

	private transient long ts;

	private transient final Engine engine;

	private final InternalTransaction internalTransaction = new InternalTransaction();

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
	TimestampedDependencies getDirectInheritingsDependencies(Generic effectiveSuper) {
		return ((GenericImpl) effectiveSuper).getLifeManager().engineDirectInheritings;
	}

	@Override
	TimestampedDependencies getCompositeDependencies(Generic component) {
		return ((GenericImpl) component).getLifeManager().engineComposites;
	}

	@Override
	InternalTransaction getInternalContext() {
		return internalTransaction;
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

	public class InternalTransaction extends InternalContext<Transaction> {

		private static final long serialVersionUID = -85246881502473857L;

		@Override
		protected void apply(Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, ConstraintViolationException {
			synchronized (getEngine()) {
				Set<LifeManager> lockedLifeManagers = new HashSet<LifeManager>();
				try {
					writeLockAllAndCheckMvcc(lockedLifeManagers, adds, removes);
					super.apply(adds, removes);
				} finally {
					writeUnlockAll(lockedLifeManagers);
				}
			}
		}

		@Override
		protected void add(GenericImpl generic) {
			generic.getLifeManager().beginLife(getTs());
//			if (Transaction.this.isFlushable(generic))
				super.add(generic);
		}

		@Override
		protected void cancelAdd(GenericImpl generic) {
			generic.getLifeManager().cancelBeginLife();
			super.cancelAdd(generic);
		}

		@Override
		protected void remove(GenericImpl generic) {
			generic.getLifeManager().kill(getTs());
		}

		@Override
		protected void cancelRemove(GenericImpl generic) {
			generic.getLifeManager().resurect();
		}

		private void writeLockAllAndCheckMvcc(Set<LifeManager> lockedLifeManagers, Iterable<Generic> adds, Iterable<Generic> removes) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			for (Generic generic : removes)
				writeLockAndCheckMvcc(lockedLifeManagers, ((GenericImpl) generic).getLifeManager());
			for (Generic generic : adds) {
				for (Generic effectiveSuper : ((GenericImpl) generic).supers)
					writeLockAndCheckMvcc(lockedLifeManagers, ((GenericImpl) effectiveSuper).getLifeManager());
				for (Generic component : ((GenericImpl) generic).components)
					writeLockAndCheckMvcc(lockedLifeManagers, ((GenericImpl) component).getLifeManager());
				writeLockAndCheckMvcc(lockedLifeManagers, ((GenericImpl) generic).getLifeManager());
			}
		}

		private void writeLockAndCheckMvcc(Set<LifeManager> lockedLifeManagers, LifeManager manager) throws ConcurrencyControlException, OptimisticLockConstraintViolationException {
			if (!lockedLifeManagers.contains(manager)) {
				manager.writeLock();
				lockedLifeManagers.add(manager);
				manager.checkMvcc(getTs());
			}
		}

		private void writeUnlockAll(Set<LifeManager> lockedLifeManagers) {
			for (LifeManager lifeManager : lockedLifeManagers)
				lifeManager.writeUnlock();
		}
	}

}
