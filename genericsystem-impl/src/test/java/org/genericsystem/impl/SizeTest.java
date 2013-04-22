package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Statics;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.systemproperties.constraints.axed.SizeConstraintImpl;
import org.testng.annotations.Test;

@Test
public class SizeTest extends AbstractTest {

	public void mountSize() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type sizeConstraint = cache.find(SizeConstraintImpl.class);
		assert sizeConstraint != null;
		assert sizeConstraint.getAttribute(cache, "Size") != null : sizeConstraint.info() + " " + sizeConstraint.getAttributes(cache);
	}

	public void enableSize() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type vehicle = cache.newType("Vehicle");
		Type color = cache.newType("Color");
		Relation vehicleColor = vehicle.setRelation(cache, "VehicleColor", color);
		((GenericImpl) vehicleColor).enableSizeConstraint(cache, Statics.BASE_POSITION, 1);
		((GenericImpl) vehicleColor).isSizeConstraintEnabled(cache, Statics.BASE_POSITION);
		((GenericImpl) vehicleColor).disableSizeConstraint(cache, Statics.BASE_POSITION, 1);
	}

}
