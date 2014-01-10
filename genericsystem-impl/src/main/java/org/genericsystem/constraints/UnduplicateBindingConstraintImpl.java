package org.genericsystem.constraints;

import java.util.Iterator;
import org.genericsystem.annotation.Components;
import org.genericsystem.annotation.Dependencies;
import org.genericsystem.annotation.Extends;
import org.genericsystem.annotation.Meta;
import org.genericsystem.annotation.Priority;
import org.genericsystem.annotation.SystemGeneric;
import org.genericsystem.annotation.value.AxedConstraintValue;
import org.genericsystem.annotation.value.BooleanValue;
import org.genericsystem.constraints.AbstractConstraintImpl.AbstractBooleanNoAxedConstraintImpl;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.HomeTreeNode;
import org.genericsystem.core.UnsafeGList.Supers;
import org.genericsystem.exception.ConstraintViolationException;
import org.genericsystem.exception.UnduplicateBindingConstraintViolationException;
import org.genericsystem.generic.Holder;
import org.genericsystem.iterator.AbstractFilterIterator;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.ConstraintsMapProvider.ConstraintKey;

/**
 * @author Nicolas Feybesse
 * 
 */
@SystemGeneric
@Extends(ConstraintKey.class)
@Components(ConstraintsMapProvider.class)
@Dependencies({ UnduplicateBindingConstraintImpl.DefaultKey.class, UnduplicateBindingConstraintImpl.DefaultValue.class })
@Priority(-1)
public class UnduplicateBindingConstraintImpl extends AbstractBooleanNoAxedConstraintImpl implements Holder {

	@SystemGeneric
	@Meta(UnduplicateBindingConstraintImpl.class)
	@Components(ConstraintsMapProvider.class)
	@AxedConstraintValue(UnduplicateBindingConstraintImpl.class)
	public static class DefaultKey {}

	@SystemGeneric
	@Meta(ConstraintsMapProvider.ConstraintValue.class)
	@Components(DefaultKey.class)
	@BooleanValue(true)
	public static class DefaultValue {}

	@Override
	public void check(Generic constraintBase, final Generic modified) throws ConstraintViolationException {
		final Supers supers = ((GenericImpl) modified).supers();
		final org.genericsystem.core.UnsafeGList.Components components = ((GenericImpl) modified).components();

		final HomeTreeNode homeTreeNode = ((GenericImpl) modified).getHomeTreeNode();
		Iterator<Generic> iterator = components.isEmpty() || components.get(0) == null ? supers.get(0).getInheritings().iterator() : components.get(0).getComposites().iterator();
		iterator = new AbstractFilterIterator<Generic>(iterator) {
			@Override
			public boolean isSelected() {
				return !next.equals(modified) && ((GenericImpl) next).equiv(homeTreeNode, supers, components);
			}
		};
		if (iterator.hasNext())
			throw new UnduplicateBindingConstraintViolationException();
	}

	@Override
	public boolean isImmediatelyCheckable() {
		return true;
	}
}
