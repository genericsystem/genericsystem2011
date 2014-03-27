package org.genericsystem.tracker.component.generic;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractCreateAndEditComponent;
import org.genericsystem.framework.component.generic.GenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.InstanceRow;
import org.genericsystem.tracker.structure.Attributes;
import org.genericsystem.tracker.structure.Relations;

@SuppressWarnings("unchecked")
public class CreateAndEditComponent extends AbstractCreateAndEditComponent {
	private String title;

	public CreateAndEditComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
		initChildren();
		setTitle("add");
	}

	public CreateAndEditComponent(TypeComponent typeComponent, InstanceRow instanceRow) {
		this(typeComponent, instanceRow.getInstance());
		setMode(MODE.EDITION);
		setTitle("update");
		setNewValue(Objects.toString(getGeneric().toString()));
	}

	@Override
	public <T extends Generic> Snapshot<T> getGenerics() {
		return (Snapshot<T>) ((Type) getGeneric()).getAttributes();
	}

	@Override
	public <T extends Generic> boolean isSelected(T candidate) {
		Class<?> clazz = candidate.<Class<?>> getValue().getEnclosingClass();
		return clazz != null && (Attributes.class.equals(clazz) || Relations.class.equals(clazz));
	}

	@Override
	public <T extends AbstractComponent, U extends Generic> T buildComponent(U generic) {
		return (T) new RowComponent(CreateAndEditComponent.this, generic);
	}

	@Override
	public void create() {
		Generic newInstance = ((Type) generic).setInstance(newValue);
		for (RowComponent row : this.<RowComponent> getChildren()) {
			List<AbstractComponent> rows = row.getChildren();
			for (int i = 0; i < rows.size(); i++) {
				Attribute attribute = (Attribute) ((OutputTextComponent) rows.get(i)).getGeneric();
				AbstractComponent abstractComponent = rows.get(++i);
				if (abstractComponent instanceof InputTextComponent) {
					String value = ((InputTextComponent) abstractComponent).getValue();
					newInstance.setValue(attribute, value);
				} else if (abstractComponent instanceof SelectItemComponent) {
					String value = ((SelectItemComponent) abstractComponent).getValue();
					Generic instance = ((Type) getGeneric().getOtherTargets((Attribute) ((SelectItemComponent) abstractComponent).getGeneric()).get(0)).getInstance(value);
					newInstance.bind((Relation) attribute, instance);
				}
			}
		}
		setNewValue(newValue);
	}

	@Override
	public void modify() {
		if (!getInstanceName().equals(newValue))
			setGeneric(getGeneric().setValue(newValue));

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
		setNewValue(Objects.toString(getGeneric().toString()));
	}

	public void exec() {
		if (getMode().equals(MODE.CREATION))
			create();
		else
			modify();
	}

	public boolean isRelation() {
		return getGeneric().isRelation();
	}

	// DATE
	// public boolean validateDate(String date) {
	// Pattern p = Pattern.compile(DATE_PATTERN);
	// Matcher m = p.matcher(date);
	// return (m.matches());
	// }

	public String getColumnTitleAttribute() {
		if (!isRelation())
			return Objects.toString(getGeneric());
		else
			return Objects.toString(((GenericComponent) this.getParent()).getGeneric().<Type> getOtherTargets((Attribute) getGeneric()).get(0).<Class<?>> getValue().getSimpleName());
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/createandedit.xhtml";
	}
}
