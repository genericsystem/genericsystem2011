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
import org.testng.annotations.Test;

@Test
public class ReferentialIntegritySystemPropertyTest extends AbstractTest {

	public void testAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Attribute metaAttribute = cache.getMetaAttribute();
		assert !metaAttribute.isReferentialIntegrity(Statics.BASE_POSITION);

		assert !cache.getMetaRelation().isReferentialIntegrity(Statics.BASE_POSITION);
		assert cache.getMetaRelation().isReferentialIntegrity(Statics.TARGET_POSITION);
		assert cache.getMetaRelation().isReferentialIntegrity(Statics.SECOND_TARGET_POSITION);
	}

	public void testAttribute2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Attribute metaAttribute = cache.getEngine().getMetaAttribute();
		assert !metaAttribute.isReferentialIntegrity(Statics.BASE_POSITION);

		Relation metaRelation = cache.getEngine().getMetaRelation();
		assert !metaRelation.isReferentialIntegrity(Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(Statics.TARGET_POSITION);

		metaRelation.disableReferentialIntegrity(Statics.BASE_POSITION);
		assert !metaAttribute.isReferentialIntegrity(Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(Statics.TARGET_POSITION);

		metaRelation.disableReferentialIntegrity(Statics.TARGET_POSITION);
		assert !metaAttribute.isReferentialIntegrity(Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(Statics.BASE_POSITION);
		assert !metaRelation.isReferentialIntegrity(Statics.TARGET_POSITION);

		metaRelation.enableReferentialIntegrity(Statics.TARGET_POSITION);
		metaRelation.enableReferentialIntegrity(Statics.BASE_POSITION);
		assert !metaAttribute.isReferentialIntegrity(Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(Statics.TARGET_POSITION);

		metaAttribute.enableReferentialIntegrity(Statics.BASE_POSITION);
		assert metaAttribute.isReferentialIntegrity(Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(Statics.TARGET_POSITION);

		metaRelation.enableReferentialIntegrity(Statics.BASE_POSITION);
		assert metaAttribute.isReferentialIntegrity(Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(Statics.BASE_POSITION);
		assert metaRelation.isReferentialIntegrity(Statics.TARGET_POSITION);
	}

	public void testRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Type color = cache.newType("Color");
		Relation carOutsideColor = car.setRelation("outside", color);
		assert !carOutsideColor.isReferentialIntegrity(Statics.BASE_POSITION);
	}

	public void testRemoveTypeWithInstance() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type car = cache.newType("Car");
		car.newInstance("myCar");
		new RollbackCatcher() {
			@Override
			public void intercept() {
				car.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveTypeWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type car = cache.newType("Car");
		Attribute power = car.setAttribute("power");
		car.remove();
		assert !power.isAlive();
	}

	public void testAttributeIsRefenrentialIntegrity() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type vehicle = cache.newType("Vehicle");
		Attribute vehiclePower = vehicle.setAttribute("power");

		assert !vehiclePower.isReferentialIntegrity(Statics.BASE_POSITION);
		vehiclePower.enableReferentialIntegrity(Statics.BASE_POSITION);
		assert vehiclePower.isReferentialIntegrity(Statics.BASE_POSITION);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				vehicle.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testRemoveTypeWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type man = cache.newType("Man");
		Type car = cache.newType("Car");
		man.setRelation("drive", car);
		man.remove();
	}

	public void testRemoveTypeWithRelationIntegrity() {
		final Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		final Type man = cache.newType("Man");
		Type car = cache.newType("Car");
		man.setRelation("drive", car).enableReferentialIntegrity(Statics.BASE_POSITION);
		new RollbackCatcher() {
			@Override
			public void intercept() {
				man.remove();
			}
		}.assertIsCausedBy(ReferentialIntegrityConstraintViolationException.class);
	}

	public void testComportementValueWithAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Attribute weight = human.setAttribute("weight");
		assert !weight.isReferentialIntegrity(Statics.BASE_POSITION);
		Generic myck = human.newInstance("myck");
		Holder myckWeight90 = myck.setValue(weight, 90);
		assert !myckWeight90.isReferentialIntegrity(Statics.BASE_POSITION);
	}

	public void testComportementValueWithAttribute2() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Attribute weight = human.setAttribute("weight");
		weight.enableReferentialIntegrity(Statics.BASE_POSITION);
		assert weight.isReferentialIntegrity(Statics.BASE_POSITION);
		Generic myck = human.newInstance("myck");
		Holder myckWeight90 = myck.setValue(weight, 90);
		assert myckWeight90.isReferentialIntegrity(Statics.BASE_POSITION);
	}

	public void testComportementValueWithRelation() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type human = cache.newType("Human");
		Type vehicle = cache.newType("Human");
		Relation humanDriveVehicle = human.setRelation("drive", vehicle);
		assert !humanDriveVehicle.isReferentialIntegrity(Statics.BASE_POSITION);
		Generic myck = human.newInstance("myck");
		Generic myVehicle = vehicle.newInstance("myVehicle");
		Link myckMyDriveMyVehicle = myck.setLink(humanDriveVehicle, "myDrive", myVehicle);
		assert !myckMyDriveMyVehicle.isReferentialIntegrity(Statics.BASE_POSITION);
	}

}
