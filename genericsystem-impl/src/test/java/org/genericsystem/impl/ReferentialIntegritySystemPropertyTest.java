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
import org.genericsystem.impl.system.ReferentialIntegritySystemProperty;
import org.testng.annotations.Test;

@Test
public class ReferentialIntegritySystemPropertyTest extends AbstractTest {

	public void testAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Attribute metaAttribute = cache.getMetaAttribute();
		assert !metaAttribute.isReferentialIntegrity(cache, 0);

		assert cache.getMetaRelation().isReferentialIntegrity(cache, 0);
		assert cache.getMetaRelation().isReferentialIntegrity(cache, 1);
	}

	public void testAttribute2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Attribute metaAttribute = cache.getEngine().getMetaAttribute();
		assert !metaAttribute.isReferentialIntegrity(cache, 0);

		Relation metaRelation = cache.getEngine().getMetaRelation();
		assert metaRelation.isReferentialIntegrity(cache, 0);
		assert metaRelation.isReferentialIntegrity(cache, 1);
		// assert metaRelation.isReferentialIntegrity(cache, 2);

		metaRelation.disableSystemProperty(cache, ReferentialIntegritySystemProperty.class, 0);
		assert !metaAttribute.isReferentialIntegrity(cache, 0);
		assert !metaRelation.isReferentialIntegrity(cache, 0);
		assert metaRelation.isReferentialIntegrity(cache, 1);
		// assert metaRelation.isReferentialIntegrity(cache, 2);

		metaRelation.disableSystemProperty(cache, ReferentialIntegritySystemProperty.class, 1);
		assert !metaAttribute.isReferentialIntegrity(cache, 0);
		assert !metaRelation.isReferentialIntegrity(cache, 0);
		assert !metaRelation.isReferentialIntegrity(cache, 1);
		// assert metaRelation.isReferentialIntegrity(cache, 2);

		metaAttribute.enableSystemProperty(cache, ReferentialIntegritySystemProperty.class, 0);
		assert metaAttribute.isReferentialIntegrity(cache, 0);
		// metaRelation inherits metaAttribute
		assert metaRelation.isReferentialIntegrity(cache, 0);
		assert !metaRelation.isReferentialIntegrity(cache, 1);
		// assert metaRelation.isReferentialIntegrity(cache, 2);

		metaRelation.enableSystemProperty(cache, ReferentialIntegritySystemProperty.class, 0);
		assert metaAttribute.isReferentialIntegrity(cache, 0);
		assert metaRelation.isReferentialIntegrity(cache, 0);
		assert !metaRelation.isReferentialIntegrity(cache, 1);
		// assert metaRelation.isReferentialIntegrity(cache, 2);
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carOutsideColor = car.addRelation(cache, "outside", color);
		assert color.isReferentialIntegrity(cache, 0);
		assert carOutsideColor.isReferentialIntegrity(cache, 0);
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

		vehiclePower.enableReferentialIntegrity(cache, 0);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.remove(cache);
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveTypeWithRelation() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type man = cache.newType("Man");
		Type car = cache.newType("Car");
		man.addRelation(cache, "drive", car);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				man.remove(cache);
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveTypeWithRelationWithReferentialIntegrity() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		final Type man = cache.newType("Man");
		Type car = cache.newType("Car");
		Relation manDriveCar = man.addRelation(cache, "drive", car);
		manDriveCar.enableReferentialIntegrity(cache, 2);
		assert manDriveCar.isReferentialIntegrity(cache, 2);
		assert manDriveCar.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 2);
		assert manDriveCar.isReferentialIntegrity(cache, 1);
		assert manDriveCar.isReferentialIntegrity(cache, 0);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				man.remove(cache);
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
		assert !manDriveCar.isAlive(cache);
	}

	public void testComportementValueWithType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		Generic myObjet = human.newInstance(cache, "myObjet");
		assert myObjet.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
	}

	public void testComportementValueWithSubType() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Type man = human.newSubType(cache, "man");
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		assert man.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
	}

	public void testComportementValueWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Attribute weight = human.addAttribute(cache, "weight");
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		assert !weight.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		Generic myck = human.newInstance(cache, "myck");
		Value myckWeight90 = myck.addValue(cache, weight, 90);
		assert myck.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		assert !myckWeight90.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
	}

	public void testComportementValueWithAttribute2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Attribute weight = human.addAttribute(cache, "weight");
		weight.enableSystemProperty(cache, ReferentialIntegritySystemProperty.class, 0);
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		assert weight.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		Generic myck = human.newInstance(cache, "myck");
		Value myckWeight90 = myck.addValue(cache, weight, 90);
		assert myck.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		assert myckWeight90.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
	}

	public void testComportementValueWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		cache.find(ReferentialIntegritySystemProperty.class);
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Human");
		Relation humanDriveVehicle = human.addRelation(cache, "drive", vehicle);
		assert human.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		assert humanDriveVehicle.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		assert humanDriveVehicle.getImplicit().isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
		Generic myck = human.newInstance(cache, "myck");
		Generic myVehicle = vehicle.newInstance(cache, "myVehicle");
		Link myckMyDriveMyVehicle = myck.addLink(cache, humanDriveVehicle, "myDrive", myVehicle);
		assert myckMyDriveMyVehicle.isSystemPropertyEnabled(cache, ReferentialIntegritySystemProperty.class, 0);
	}

}
