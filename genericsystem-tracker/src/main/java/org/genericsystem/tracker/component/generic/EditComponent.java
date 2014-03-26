package org.genericsystem.tracker.component.generic;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractCollectableChildrenComponent;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.framework.component.generic.GenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.InstanceRow;
import org.genericsystem.tracker.structure.Attributes;
import org.genericsystem.tracker.structure.Relations;

public class EditComponent extends AbstractCollectableChildrenComponent implements GenericComponent, ValuedComponent {
	// AbstractGenericComponent
	private String newValue;
	private Generic generic;

	public EditComponent(AbstractComponent parent, Generic generic) {
		super(parent);
		this.generic = generic;
	}

	public EditComponent(TypeComponent typeComponent, InstanceRow instanceRow) {
		this(typeComponent, instanceRow.getInstance());
		initChildren();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return (Snapshot<T>) ((Type) getGeneric()).getAttributes();
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		Class<?> clazz = candidate.<Class<?>> getValue().getEnclosingClass();
		return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U genericValue) {
		return (T) new RowComponent(EditComponent.this, genericValue);
	}

	// @Override
	// public List<RowComponent> initChildren() {
	//
	// return ((Type) getGeneric()).getAttributes().filter(new Filter<Attribute>() {
	// @Override
	// public boolean isSelected(Attribute candidate) {
	// Class<?> clazz = candidate.<Class<?>> getValue().getEnclosingClass();
	// return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
	// }
	// }).project(new Projector<RowComponent, Attribute>() {
	// private final Map<Attribute, RowComponent> map = new HashMap<Attribute, RowComponent>() {
	// private static final long serialVersionUID = -1162281462201347017L;
	//
	// @Override
	// public RowComponent get(Object key) {
	// RowComponent result = super.get(key);
	// if (result == null)
	// put((Attribute) key, result = new RowComponent(EditComponent.this, (Attribute) key));
	// return result;
	// }
	// };
	//
	// @Override
	// public RowComponent project(Attribute attribute) {
	// return map.get(attribute);
	// }
	// });
	//
	// }

	public String getInstanceName() {
		return Objects.toString(getGeneric().toString());
	}

	public void setInstanceName(String name) {
		newValue = name;
	}

	public void modify() {
		if (!getInstanceName().equals(newValue))
			generic = getGeneric().setValue(newValue);
		// setGeneric(getGeneric().setValue(newValue));

		List<RowComponent> list = getChildren();

		for (RowComponent row : list) {
			List<AbstractComponent> listSelectItem = row.<AbstractComponent> getChildren();
			for (AbstractComponent selectItem : listSelectItem) {
				if (selectItem instanceof SelectItemComponent) {
					Generic instance = ((Type) getGeneric().getOtherTargets((Attribute) ((SelectItemComponent) selectItem).getGeneric()).get(0)).getInstance(((SelectItemComponent) selectItem).getValue());
					getGeneric().bind((Relation) ((SelectItemComponent) selectItem).getGeneric(), instance);
				} else if (selectItem instanceof InputTextComponent) {
					getGeneric().setValue((Attribute) ((InputTextComponent) selectItem).getGeneric(), (((InputTextComponent) selectItem).getValue()).toString());
				}
			}
		}
	}

	// @Override
	public boolean isRelation() {
		return getGeneric().isRelation();
	}

	public String updateMsg() {
		return "update";
	}

	@Override
	public String getXhtmlPath() {
		return "edit.xhtml";
	}

	@Override
	public String getNewValue() {
		return newValue;
	}

	@Override
	public Generic getGeneric() {
		return generic;
	}
}
