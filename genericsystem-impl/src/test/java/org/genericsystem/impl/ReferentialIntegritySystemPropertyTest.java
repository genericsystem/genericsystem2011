package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.exception.ReferentialIntegrityConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Link;
import org.genericsystem.api.generic.Relation;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.Statics;
import org.genericsystem.impl.system.ReferentialIntegritySystemProperty;
import org.testng.annotations.Test;

@Test
public class ReferentialIntegritySystemPropertyTest extends AbstractTest {

	public void testAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Attribute metaAttribute = cache.getMetaAttribute();
		assert !metaAttribute.isReferentialIntegrity(cache, Statics.BASE_POSITION);

		assert !cache.getMetaRelation().isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert cache.getMetaRelation().isReferentialIntegrity(cache, Statics.TARGET_POSITION);
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

		metaAttribute.enableSystemProperty(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert metaAttribute.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(cache, Statics.TARGET_POSITION);

		metaRelation.enableSystemProperty(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert metaAttribute.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(cache, Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(cache, Statics.TARGET_POSITION);
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carOutsideColor = car.addRelation(cache, "outside", color);
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
		Attribute power = car.addAttribute(cache, "power");
		car.remove(cache);
		assert !power.isAlive(cache);
	}

	public void testAttributeIsRefenrentialIntegrity() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.addAttribute(cache, "power");

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
		man.addRelation(cache, "drive", car);
		man.remove(cache);
	}

	public void testRemoveTypeWithRelationIntegrity() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type man = cache.newType("Man");
		Type car = cache.newType("Car");
		man.addRelation(cache, "drive", car).enableReferentialIntegrity(cache, Statics.BASE_POSITION);
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
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		Generic myObjet = human.newInstance(cache, "myObjet");
		assert myObjet.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
	}

	public void testComportementValueWithSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Type man = human.newSubType(cache, "man");
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert man.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
	}

	public void testComportementValueWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Attribute weight = human.addAttribute(cache, "weight");
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert !weight.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		Generic myck = human.newInstance(cache, "myck");
		Value myckWeight90 = myck.setValue(cache, weight, 90);
		assert myck.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert !myckWeight90.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
	}

	public void testComportementValueWithAttribute2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Attribute weight = human.addAttribute(cache, "weight");
		weight.enableSystemProperty(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert weight.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		Generic myck = human.newInstance(cache, "myck");
		Value myckWeight90 = myck.setValue(cache, weight, 90);
		assert myck.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert myckWeight90.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
	}

	public void testComportementValueWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Human");
		Relation humanDriveVehicle = human.addRelation(cache, "drive", vehicle);
		assert !humanDriveVehicle.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		assert humanDriveVehicle.getImplicit().isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
		Generic myck = human.newInstance(cache, "myck");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Link myckMyDriveMyVehicle = myck.setLink(cache, humanDriveVehicle, "myDrive", myVehicle);
		assert !myckMyDriveMyVehicle.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, Statics.BASE_POSITION);
	}

}
