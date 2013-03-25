package org.genericsystem.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.constraints.InstanceClassConstraintImpl;
import org.genericsystem.constraints.axed.RequiredConstraintImpl;
import org.genericsystem.constraints.axed.SingularConstraintImpl;
import org.genericsystem.constraints.simple.AliveConstraintImpl;
import org.genericsystem.constraints.simple.ConcreteInheritanceConstraintImpl;
import org.genericsystem.constraints.simple.DuplicateStructuralValueConstraintImpl;
import org.genericsystem.constraints.simple.EngineConsistencyConstraintImpl;
import org.genericsystem.constraints.simple.NotNullConstraintImpl;
import org.genericsystem.constraints.simple.OptimisticLockConstraintImpl;
import org.genericsystem.constraints.simple.PhantomConstraintImpl;
import org.genericsystem.constraints.simple.PropertyConstraintImpl;
import org.genericsystem.constraints.simple.SingularInstanceConstraintImpl;
import org.genericsystem.constraints.simple.SuperRuleConstraintImpl;
import org.genericsystem.constraints.simple.UnduplicateBindingConstraintImpl;
import org.genericsystem.constraints.simple.UniqueConstraintImpl;
import org.genericsystem.constraints.simple.VirtualConstraintImpl;
import org.genericsystem.core.Statics.AnonymousReference;
import org.genericsystem.core.Statics.TsGenerator;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.system.CascadeRemoveSystemProperty;
import org.genericsystem.system.MetaAttribute;
import org.genericsystem.system.MetaRelation;
import org.genericsystem.system.MultiDirectionalSystemProperty;
import org.genericsystem.system.NoInheritanceSystemProperty;
import org.genericsystem.system.ReferentialIntegritySystemProperty;

/**
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public class EngineImpl extends GenericImpl implements Engine {

	static final String ENGINE_VALUE = "Engine";

	private SystemCache systemCache = new SystemCache();

	private TsGenerator generator = new TsGenerator();

	private Factory factory;

	private Archiver archiver;

	// private Map<Generic, Serializable> valuesMap = new HashMap<>();

	public EngineImpl(Config config, Class<?>... userClasses) {
		factory = config.getFactory();
		archiver = new Archiver(this, config.getDirectoryPath());
		systemCache.init(userClasses);
		archiver.startScheduler();
	}

	// public Serializable getValue(Generic generic) {
	// return valuesMap.get(generic);
	// }
	//
	// public void putValue(Generic generic, Serializable value) {
	// valuesMap.put(generic, value);
	// }

	void restoreEngine() {
		restoreEngine(pickNewTs(), pickNewTs(), 0L, Long.MAX_VALUE);
	}

	final void restoreEngine(long designTs, long birthTs, long lastReadTs, long deathTs) {
		restore(ENGINE_VALUE, SystemGeneric.META, designTs, birthTs, lastReadTs, deathTs, new Generic[] { this }, Statics.EMPTY_GENERIC_ARRAY);
		assert components.length == 0;
	}

	public Factory getFactory() {
		return factory;
	}

	@Override
	public Cache newCache() {
		return getFactory().newCache(new Transaction(this));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Attribute> T getMetaAttribute() {
		return (T) systemCache.get(MetaAttribute.class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Relation> T getMetaRelation() {
		return (T) systemCache.get(MetaRelation.class);
	}

	@SuppressWarnings("unchecked")
	public <T extends Generic> T find(Context context, Class<?> clazz) {
		return (T) systemCache.get(context, clazz);
	}

	@Override
	public EngineImpl getEngine() {
		return this;
	}

	@Override
	public boolean isEngine() {
		return true;
	}

	@Override
	public long pickNewTs() {
		return generator.pickNewTs();
	}

	public AnonymousReference pickNewAnonymousReference() {
		return new AnonymousReference(pickNewTs());
	}

	@Override
	public boolean inheritsFrom(Generic generic) {
		return this.equals(generic);
	}

	@Override
	public int getMetaLevel() {
		return SystemGeneric.META;
	}

	private class SystemCache extends HashMap<Class<?>, Generic> {

		private static final long serialVersionUID = 1150085123612887245L;

		private boolean startupTime = true;

		SystemCache init(Class<?>... userClasses) {
			put(Engine.class, EngineImpl.this);
			CacheImpl cache = new CacheImpl(new Transaction(EngineImpl.this));
			List<Class<?>> classes = Arrays.<Class<?>> asList(MetaAttribute.class, MetaRelation.class, NoInheritanceSystemProperty.class, MultiDirectionalSystemProperty.class, PropertyConstraintImpl.class, ReferentialIntegritySystemProperty.class,
					OptimisticLockConstraintImpl.class, RequiredConstraintImpl.class, SingularInstanceConstraintImpl.class, SingularConstraintImpl.class, NotNullConstraintImpl.class, InstanceClassConstraintImpl.class, VirtualConstraintImpl.class,
					AliveConstraintImpl.class, UniqueConstraintImpl.class, CascadeRemoveSystemProperty.class, ConcreteInheritanceConstraintImpl.class, SuperRuleConstraintImpl.class, EngineConsistencyConstraintImpl.class, PhantomConstraintImpl.class,
					UnduplicateBindingConstraintImpl.class, DuplicateStructuralValueConstraintImpl.class);
			for (Class<?> clazz : classes)
				if (get(clazz) == null)
					bind(cache, clazz);
			for (Class<?> clazz : userClasses)
				if (get(clazz) == null)
					bind(cache, clazz);
			cache.flush();
			startupTime = false;
			return this;
		}

		@SuppressWarnings("unchecked")
		public <T extends Generic> T get(Context context, Class<?> clazz) {
			T systemProperty = (T) super.get(clazz);
			if (systemProperty != null)
				return systemProperty;
			if (startupTime && context instanceof Cache)
				return bind((CacheImpl) context, clazz);
			throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
		}

		@SuppressWarnings("unchecked")
		private <T extends Generic> T bind(CacheImpl cache, Class<?> clazz) {
			T result;
			if (Engine.class.equals(clazz))
				result = (T) EngineImpl.this;
			if (MetaAttribute.class.equals(clazz)) {
				result = cache.<T> findMeta(new Generic[] { EngineImpl.this }, new Generic[] { EngineImpl.this });
				if (result == null)
					result = cache.insert(new GenericImpl().initializeComplex(EngineImpl.this, new Generic[] { EngineImpl.this }, new Generic[] { EngineImpl.this }));
			} else if (MetaRelation.class.equals(clazz)) {
				result = cache.<T> findMeta(new Generic[] { EngineImpl.this }, new Generic[] { EngineImpl.this, EngineImpl.this });
				if (result == null)
					result = cache.insert(new GenericImpl().initializeComplex(get(MetaAttribute.class).getImplicit(), new Generic[] { get(MetaAttribute.class) }, new Generic[] { EngineImpl.this, EngineImpl.this }));
			} else
				result = cache.<T> bind(clazz);
			put(clazz, result);
			((GenericImpl) result).mountConstraints(cache, clazz);
			cache.triggersDependencies(clazz);
			return result;
		}
	}

	@Override
	public void close() {
		archiver.close();
	}

}
