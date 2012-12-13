package org.genericsystem.impl.constraints;

import org.genericsystem.api.annotation.Components;
import org.genericsystem.api.annotation.SystemGeneric;
import org.genericsystem.api.annotation.constraints.PropertyConstraint;
import org.genericsystem.api.annotation.constraints.SingularConstraint;
import org.genericsystem.api.core.Context;
import org.genericsystem.api.core.Engine;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.exception.ConstraintViolationException;
import org.genericsystem.api.exception.RequiredConstraintViolationException;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.GenericImpl;
import org.genericsystem.impl.core.Statics;

@SystemGeneric
@Components(Engine.class)
@PropertyConstraint
@SingularConstraint(Statics.BASE_POSITION)
public class RequiredConstraintImpl extends AbstractConstraint {
	
	private static final long serialVersionUID = -6429972259714036057L;
	
	@Override
	public void check(Context context, final Generic modified) throws ConstraintViolationException {
		/*
		 * We check the attribute corresponding to the value that's being
		 * removed has at least one value.
		 */
		if (!((GenericImpl) modified).isPrimary() && !modified.isAlive(context)) {
			Attribute requiredAttribute = ((Value) modified).getMeta();
			if (requiredAttribute == null || !requiredAttribute.isSystemPropertyEnabled(context, this.getClass()))
				return;
			Generic base = ((Value) modified).getBaseComponent();
			
			assert ((GenericImpl) requiredAttribute).isSystemPropertyEnabled(context, this.getClass());
			if (base.getValues(context, requiredAttribute).size() < 1)
				throw new RequiredConstraintViolationException("The generic " + base + " has no value for the attribute " + requiredAttribute + ".");
		}
		/*
		 * Check the instance's required attributes have been instantiated.
		 */
		else
			if (SystemGeneric.CONCRETE == modified.getMetaLevel()) {
				Type typeuh = modified.getMeta();
				
				Snapshot<Attribute> attributes = typeuh.getAttributes(context);
				Generic base = modified;
				
				boolean foundConstrainedAttribute = false;
				for (Attribute attribute : attributes) {
					if (((GenericImpl) attribute).isSystemPropertyEnabled(context, this.getClass()) && base.getValues(context, attribute).size() < 1) { throw new RequiredConstraintViolationException(
							"The generic " + base + " has no value for the attribute " + attribute + "."); }
					foundConstrainedAttribute = true;
				}
				assert foundConstrainedAttribute : "Should not be testing requirement on this object.";
			}
	}
	
	@Override
	public boolean isCheckedAt(CheckingType type) {
		return type.equals(CheckingType.CHECK_ON_REMOVE_NODE) || type.equals(CheckingType.CHECK_ON_ADD_NODE);
	}
	
	@Override
	public boolean isImmediatelyCheckable() {
		return false;
	}
	
}
