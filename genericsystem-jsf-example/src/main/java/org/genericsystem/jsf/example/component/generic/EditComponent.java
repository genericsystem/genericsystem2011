package org.genericsystem.jsf.example.component.generic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.InstanceRow;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.jsf.example.structure.Attributes;
import org.genericsystem.jsf.example.structure.Relations;

public class EditComponent extends AbstractGenericComponent {

	private String newValue;

	public EditComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
	}

	public EditComponent(TypeComponent typeComponent, InstanceRow instanceRow) {
		this(typeComponent, instanceRow.getInstance());
		initChildren();
	}

	@Override
	public List<RowComponent> initChildren() {

		return ((Type) getGeneric()).getAttributes().filter(new Filter<Attribute>() {
			@Override
			public boolean isSelected(Attribute candidate) {
				Class<?> clazz = candidate.<Class<?>> getValue().getEnclosingClass();
				return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
			}
		}).project(new Projector<RowComponent, Attribute>() {
			private final Map<Attribute, RowComponent> map = new HashMap<Attribute, RowComponent>() {
				private static final long serialVersionUID = -1162281462201347017L;

				@Override
				public RowComponent get(Object key) {
					RowComponent result = super.get(key);
					if (result == null)
						put((Attribute) key, result = new RowComponent(EditComponent.this, (Attribute) key));
					return result;
				}
			};

			@Override
			public RowComponent project(Attribute attribute) {
				return map.get(attribute);
			}
		});

	}

	public String getInstanceName() {
		return Objects.toString(getGeneric().toString());
	}

	public void setInstanceName(String name) {
		newValue = name;
	}

	public void modify() {
		if (!getInstanceName().equals(newValue))
			setGeneric(getGeneric().setValue(newValue));

		List<RowComponent> list = getChildren();

		for (RowComponent row : list) {
			List<AbstractComponent> listSelectItem = row.<AbstractComponent> getChildren();
			for (AbstractComponent selectItem : listSelectItem) {
				if (selectItem instanceof SelectItemComponent) {
					Generic instance = ((Type) getGeneric().getOtherTargets((Attribute) ((SelectItemComponent) selectItem).getGeneric()).get(0)).getInstance(((SelectItemComponent) selectItem).getStringSelected());
					getGeneric().bind((Relation) ((SelectItemComponent) selectItem).getGeneric(), instance);
				} else if (selectItem instanceof InputTextComponent) {
					getGeneric().setValue((Attribute) ((InputTextComponent) selectItem).getGeneric(), (((InputTextComponent) selectItem).getValue()).toString());
				}
			}
		}
	}

	@Override
	public boolean isRelation() {
		return getGeneric().isRelation();
	}

	@Override
	public String getXhtmlPath() {
		return "edit.xhtml";
	}

	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		// TODO Auto-generated method stub
		return false;
	}
}
