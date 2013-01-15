package org.genericsystem.impl.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Config;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Factory;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.impl.constraints.InstanceClassConstraintImpl;
import org.genericsystem.impl.constraints.RequiredAxedConstraintImpl;
import org.genericsystem.impl.constraints.RequiredConstraintImpl;
import org.genericsystem.impl.constraints.axed.SingularConstraintImpl;
import org.genericsystem.impl.constraints.simple.AliveConstraintImpl;
import org.genericsystem.impl.constraints.simple.ConcreteInheritanceConstraintImpl;
import org.genericsystem.impl.constraints.simple.NotNullConstraintImpl;
import org.genericsystem.impl.constraints.simple.OptimisticLockConstraintImpl;
import org.genericsystem.impl.constraints.simple.PhantomConstraintImpl;
import org.genericsystem.impl.constraints.simple.PropertyConstraintImpl;
import org.genericsystem.impl.constraints.simple.SingularInstanceConstraintImpl;
import org.genericsystem.impl.constraints.simple.UniqueConstraintImpl;
import org.genericsystem.impl.core.Statics.AnonymousReference;
import org.genericsystem.impl.core.Statics.TsGenerator;
import org.genericsystem.impl.system.CascadeRemoveSystemProperty;
import org.genericsystem.impl.system.MetaAttribute;
import org.genericsystem.impl.system.MetaRelation;
import org.genericsystem.impl.system.MultiDirectionalSystemProperty;
import org.genericsystem.impl.system.NoInheritanceSystemProperty;
import org.genericsystem.impl.system.ReferentialIntegritySystemProperty;

/**
 * @author Nicolas Feybesse
 * 
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
	
	private class SystemCache extends HashMap<Class<?>, Generic> {
		
		private static final long serialVersionUID = 1150085123612887245L;
		
		private boolean startupTime = true;
		
		SystemCache init(Class<?>... userClasses) {
			put(Engine.class, EngineImpl.this);
			CacheImpl cache = new CacheImpl(new Transaction(EngineImpl.this));
			List<Class<?>> classes = Arrays.<Class<?>> asList(MetaAttribute.class, MetaRelation.class, NoInheritanceSystemProperty.class, MultiDirectionalSystemProperty.class, PropertyConstraintImpl.class, ReferentialIntegritySystemProperty.class,
					OptimisticLockConstraintImpl.class, RequiredConstraintImpl.class, RequiredAxedConstraintImpl.class, SingularInstanceConstraintImpl.class, SingularConstraintImpl.class, NotNullConstraintImpl.class, InstanceClassConstraintImpl.class,
					PhantomConstraintImpl.class, AliveConstraintImpl.class, UniqueConstraintImpl.class, CascadeRemoveSystemProperty.class, ConcreteInheritanceConstraintImpl.class);
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
					result = cache.insert(new GenericImpl().initialize(EngineImpl.ENGINE_VALUE, SystemGeneric.META, new Generic[] { EngineImpl.this }, new Generic[] { EngineImpl.this }));
			} else if (MetaRelation.class.equals(clazz)) {
				result = cache.<T> findMeta(new Generic[] { EngineImpl.this }, new Generic[] { EngineImpl.this, EngineImpl.this });
				if (result == null)
					result = cache.insert(new GenericImpl().initialize(EngineImpl.ENGINE_VALUE, SystemGeneric.META, new Generic[] { get(MetaAttribute.class) }, new Generic[] { EngineImpl.this, EngineImpl.this }));
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
