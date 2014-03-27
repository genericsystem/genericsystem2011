package org.genericsystem.tracker.component.generic;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractCreateAndEditComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.framework.component.generic.GenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.structure.Attributes;
import org.genericsystem.tracker.structure.Relations;

@SuppressWarnings("unchecked")
public class CreateAndEditComponent extends AbstractCreateAndEditComponent {
	private String title;

	public CreateAndEditComponent(AbstractComponent parent, Generic generic, MODE mode) {
		super(parent, generic, mode);
		if (mode.equals(MODE.CREATION))
			title = "add";
		else {
			title = "update";
			setNewValue(Objects.toString(getGeneric()));
		}
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
	public void execute() {
		List<RowComponent> list = getChildren();
		if (this.mode.equals(MODE.CREATION)) {
			Generic newInstance = ((Type) generic).setInstance(newValue);
			for (RowComponent row : list) {
				List<AbstractComponent> rows = row.getChildren();
				for (int i = 0; i < rows.size(); i++) {
					Attribute attribute = (Attribute) ((OutputTextComponent) rows.get(i)).getGeneric();
					AbstractComponent abstractComponent = rows.get(++i);
					create(newInstance, abstractComponent, attribute);
				}
				setNewValue(newValue);
			}
		} else {
			setGeneric(getGeneric().setValue(newValue));
			for (RowComponent row : list) {
				edit(row);
			}
			setNewValue(Objects.toString(getGeneric().toString()));
		}
	}

	@Override
	public void create(Generic newInstance, AbstractComponent abstractComponent, Attribute attribute) {
		if (abstractComponent instanceof InputTextComponent) {
			String value = ((InputTextComponent) abstractComponent).getValue();
			newInstance.setValue(attribute, value);
		} else if (abstractComponent instanceof SelectItemComponent) {
			String value = ((SelectItemComponent) abstractComponent).getValue();
			Generic instance = ((Type) getGeneric().getOtherTargets((Attribute) ((SelectItemComponent) abstractComponent).getGeneric()).get(0)).getInstance(value);
			newInstance.bind((Relation) attribute, instance);
		}
	}

	@Override
	public void edit(AbstractGenericComponent listItem) {
		for (AbstractComponent selectItem : listItem.getChildren()) {
			if (selectItem instanceof SelectItemComponent) {
				Generic instance = ((Type) getGeneric().getOtherTargets((Attribute) ((SelectItemComponent) selectItem).getGeneric()).get(0)).getInstance(((SelectItemComponent) selectItem).getValue());
				getGeneric().bind((Relation) ((SelectItemComponent) selectItem).getGeneric(), instance);
			} else if (selectItem instanceof InputTextComponent) {
				getGeneric().setValue((Attribute) ((InputTextComponent) selectItem).getGeneric(), (((InputTextComponent) selectItem).getValue()).toString());
			}
		}
	}

	public String getColumnTitleAttribute() {
		if (!getGeneric().isRelation())
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
