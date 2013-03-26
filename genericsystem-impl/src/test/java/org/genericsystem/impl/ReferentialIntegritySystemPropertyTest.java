package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.system.ReferentialIntegritySystemProperty;
import org.testng.annotations.Test;

@Test
public class ReferentialIntegritySystemPropertyTest extends AbstractTest {

	public void testAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Attribute metaAttribute = cache.getMetaAttribute();
		assert !metaAttribute.isReferentialIntegrity(cache, Statics.BASE_POSITION);

		assert !cache.getMetaRelation().isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert cache.getMetaRelation().isReferentialIntegrity(cache, Statics.TARGET_POSITION);
		assert cache.getMetaRelation().isReferentialIntegrity(cache, Statics.SECOND_TARGET_POSITION);
	}

	public void testAttribute2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Attribute metaAttribute = cache.getEngine().getMetaAttribute();
		assert !metaAttribute.isReferentialIntegrity(cache, Statics.BASE_POSITION);

		Relation metaRelation = cache.getEngine().getMetaRelation();
		assert !metaRelation.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(cache, Statics.TARGET_POSITION);

		metaRelation.disableSystemProperty(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert !metaAttribute.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(cache, Statics.TARGET_POSITION);

		metaRelation.disableSystemProperty(cache, ReferentialIntegritySystemProperty.class, Statics.TARGET_POSITION);
		assert !metaAttribute.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(cache, Statics.TARGET_POSITION);

		metaAttribute.enableSystemProperty(cache, ReferentialIntegritySystemProperty.class);
		assert metaAttribute.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(cache, Statics.TARGET_POSITION);

		metaRelation.enableSystemProperty(cache, ReferentialIntegritySystemProperty.class);
		assert metaAttribute.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(cache, Statics.TARGET_POSITION);
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carOutsideColor = car.setRelation(cache, "outside", color);
		assert !carOutsideColor.isReferentialIntegrity(cache, Statics.BASE_POSITION);
	}

	public void testRemoveTypeWithInstance() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type car = cache.newType("Car");
		car.newInstance(cache, "myCar");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.remove(cache);
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveTypeWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute(cache, "power");
		car.remove(cache);
		assert !power.isAlive(cache);
	}

	public void testAttributeIsRefenrentialIntegrity() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute(cache, "power");

		assert !vehiclePower.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		vehiclePower.enableReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert vehiclePower.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.remove(cache);
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveTypeWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type man = cache.newType("Man");
		Type car = cache.newType("Car");
		man.setRelation(cache, "drive", car);
		man.remove(cache);
	}

	public void testRemoveTypeWithRelationIntegrity() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type man = cache.newType("Man");
		Type car = cache.newType("Car");
		man.setRelation(cache, "drive", car).enableReferentialIntegrity(cache, Statics.BASE_POSITION);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				man.remove(cache);
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testComportementValueWithType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		Generic myObjet = human.newInstance(cache, "myObjet");
		assert myObjet.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
	}

	public void testComportementValueWithSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Type man = human.newSubType(cache, "man");
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		assert man.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
	}

	public void testComportementValueWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Attribute weight = human.setAttribute(cache, "weight");
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		assert !weight.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		Generic myck = human.newInstance(cache, "myck");
		Holder myckWeight90 = myck.setValue(cache, weight, 90);
		assert myck.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		assert !myckWeight90.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
	}

	public void testComportementValueWithAttribute2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Attribute weight = human.setAttribute(cache, "weight");
		weight.enableSystemProperty(cache, ReferentialIntegritySystemProperty.class);
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		assert weight.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		Generic myck = human.newInstance(cache, "myck");
		Holder myckWeight90 = myck.setValue(cache, weight, 90);
		assert myck.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		assert myckWeight90.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
	}

	public void testComportementValueWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Human");
		Relation humanDriveVehicle = human.setRelation(cache, "drive", vehicle);
		assert !humanDriveVehicle.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		assert humanDriveVehicle.getImplicit().isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
		Generic myck = human.newInstance(cache, "myck");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Link myckMyDriveMyVehicle = myck.setLink(cache, humanDriveVehicle, "myDrive", myVehicle);
		assert !myckMyDriveMyVehicle.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class);
	}

}
