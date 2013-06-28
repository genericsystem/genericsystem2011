package org.genericsystem.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.core.Statics.AnonymousReference;
import org.genericsystem.core.Statics.TsGenerator;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.map.PropertiesMapProvider;
import org.genericsystem.systemproperties.CascadeRemoveSystemProperty;
import org.genericsystem.systemproperties.MetaAttribute;
import org.genericsystem.systemproperties.MetaRelation;
import org.genericsystem.systemproperties.MultiDirectionalSystemProperty;
import org.genericsystem.systemproperties.NoInheritanceSystemProperty;
import org.genericsystem.systemproperties.ReferentialIntegritySystemProperty;
import org.genericsystem.systemproperties.constraints.InstanceClassConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.RequiredConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.SingularConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.SizeConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.AliveConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.ConcreteInheritanceConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.EngineConsistencyConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.OptimisticLockConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.PhantomConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.PropertyConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.SingularInstanceConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.SuperRuleConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.UnduplicateBindingConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.UniqueConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.UniqueStructuralValueConstraintImpl;
import org.genericsystem.systemproperties.constraints.simple.VirtualConstraintImpl;

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

	public EngineImpl(Config config, Class<?>... userClasses) {
		factory = config.getFactory();
		archiver = new Archiver(this, config.getDirectoryPath());
		systemCache.init(userClasses);
		archiver.startScheduler();
	}

	void restoreEngine() {
		restoreEngine(pickNewTs(), pickNewTs(), 0L, Long.MAX_VALUE);
	}

	final void restoreEngine(long designTs, long birthTs, long lastReadTs, long deathTs) {
		restore(ENGINE_VALUE, SystemGeneric.META, designTs, birthTs, lastReadTs, deathTs, new Generic[] { this }, Statics.EMPTY_GENERIC_ARRAY, false);
		assert components.length == 0;
	}

	@Override
	public Factory getFactory() {
		return factory;
	}

	@SuppressWarnings("unchecked")
	<T extends Generic> T buildComplex(Class<?> clazz, Generic implicit, Generic[] supers, Generic[] components, boolean automatic) {
		return (T) ((GenericImpl) getFactory().newGeneric(clazz)).initializeComplex(implicit, supers, components, automatic);
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
	public <T extends Generic> T find(Class<?> clazz) {
		return (T) systemCache.get(clazz);
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

	private ThreadLocal<Cache> cacheLocal = new ThreadLocal<>();

	public Cache start(Cache cache) {
		cacheLocal.set(cache);
		return cache;
	}

	public void stop(Cache cache) {
		assert cacheLocal.get() == cache;
		cacheLocal.set(null);
	}

	public Cache getCurrentCache() {
		Cache currentCache = cacheLocal.get();
		if (currentCache == null)
			currentCache = start(factory.getCacheLocal());
		return currentCache;
	}

	private class SystemCache extends HashMap<Class<?>, Generic> {

		private static final long serialVersionUID = 1150085123612887245L;

		private boolean startupTime = true;

		SystemCache init(Class<?>... userClasses) {
			put(Engine.class, EngineImpl.this);
			List<Class<?>> classes = Arrays.<Class<?>> asList(MetaAttribute.class, MetaRelation.class, NoInheritanceSystemProperty.class, MultiDirectionalSystemProperty.class, PropertyConstraintImpl.class, ReferentialIntegritySystemProperty.class,
					OptimisticLockConstraintImpl.class, RequiredConstraintImpl.class, SingularInstanceConstraintImpl.class, SingularConstraintImpl.class, InstanceClassConstraintImpl.class, VirtualConstraintImpl.class, AliveConstraintImpl.class,
					UniqueConstraintImpl.class, CascadeRemoveSystemProperty.class, ConcreteInheritanceConstraintImpl.class, SuperRuleConstraintImpl.class, EngineConsistencyConstraintImpl.class, PhantomConstraintImpl.class,
					UnduplicateBindingConstraintImpl.class, UniqueStructuralValueConstraintImpl.class, /* FlushableConstraintImpl.class, */SizeConstraintImpl.class, PropertiesMapProvider.class);

			// TODO clean
			CacheImpl cache = (CacheImpl) start(newCache());// new CacheImpl(new Transaction(EngineImpl.this));
			for (Class<?> clazz : classes)
				if (get(clazz) == null)
					bind(clazz);
			for (Class<?> clazz : userClasses)
				if (get(clazz) == null)
					bind(clazz);
			cache.flush();
			stop(cache);
			startupTime = false;
			return this;
		}

		@SuppressWarnings("unchecked")
		public <T extends Generic> T get(Class<?> clazz) {
			T systemProperty = (T) super.get(clazz);
			if (systemProperty != null)
				return systemProperty;
			if (startupTime && getCurrentCache() instanceof Cache)
				return bind(clazz);
			throw new IllegalStateException("Class : " + clazz + " has not been built at startup");
		}

		@SuppressWarnings("unchecked")
		private <T extends Generic> T bind(Class<?> clazz) {
			T result;
			CacheImpl cache = (CacheImpl) getCurrentCache();
			if (Engine.class.equals(clazz))
				result = (T) EngineImpl.this;
			if (MetaAttribute.class.equals(clazz)) {
				result = cache.<T> findMeta(new Generic[] { EngineImpl.this }, new Generic[] { EngineImpl.this });
				if (result == null)
					result = cache.insert(new GenericImpl().initializeComplex(EngineImpl.this, new Generic[] { EngineImpl.this }, new Generic[] { EngineImpl.this }, false));
			} else if (MetaRelation.class.equals(clazz)) {
				result = cache.<T> findMeta(new Generic[] { EngineImpl.this }, new Generic[] { EngineImpl.this, EngineImpl.this });
				if (result == null)
					result = cache.insert(new GenericImpl().initializeComplex(get(MetaAttribute.class).getImplicit(), new Generic[] { get(MetaAttribute.class) }, new Generic[] { EngineImpl.this, EngineImpl.this }, false));
			} else
				result = cache.<T> bind(clazz);
			put(clazz, result);
			((GenericImpl) result).mountConstraints(clazz);
			cache.triggersDependencies(clazz);
			return result;
		}
	}

	@Override
	public void close() {
		archiver.close();
	}

}
