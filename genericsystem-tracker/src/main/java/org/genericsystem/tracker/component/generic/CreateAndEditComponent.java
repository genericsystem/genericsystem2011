package org.genericsystem.tracker.component.generic;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.ValuedComponent;
import org.genericsystem.framework.component.generic.AbstractGenericCollectableChildrenComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.framework.component.generic.GenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.structure.Attributes;
import org.genericsystem.tracker.structure.Relations;

@SuppressWarnings("unchecked")
public class CreateAndEditComponent extends AbstractGenericCollectableChildrenComponent implements ValuedComponent {
	private String title;
	private final MODE mode;
	private String newValue;

	public static enum MODE {
		CREATION, EDITION
	};

	public CreateAndEditComponent(AbstractComponent parent, Generic generic, MODE mode) {
		super(parent, generic);
		this.mode = mode;
		if (mode.equals(MODE.CREATION))
			title = "add";
		else {
			title = "update";
			newValue = Objects.toString(getGeneric());
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
			}
		} else {
			setGeneric(getGeneric().setValue(newValue));
			for (RowComponent row : list) {
				edit(row);
			}
			newValue = Objects.toString(getGeneric().toString());
		}
	}

	public void create(Generic newInstance, AbstractComponent abstractComponent, Attribute attribute) {
		if (abstractComponent instanceof InputTextComponent) {
			String value = ((InputTextComponent) abstractComponent).getNewValue();
			newInstance.setValue(attribute, value);
		} else if (abstractComponent instanceof SelectItemComponent) {
			String value = ((SelectItemComponent) abstractComponent).getNewValue();
			Generic instance = ((Type) getGeneric().getOtherTargets((Attribute) ((SelectItemComponent) abstractComponent).getGeneric()).get(0)).getInstance(value);
			newInstance.bind((Relation) attribute, instance);
		}
	}

	public void edit(AbstractGenericComponent listItem) {
		for (AbstractComponent selectItem : listItem.getChildren()) {
			if (selectItem instanceof SelectItemComponent) {
				Generic instance = ((Type) getGeneric().getOtherTargets((Attribute) ((SelectItemComponent) selectItem).getGeneric()).get(0)).getInstance(((SelectItemComponent) selectItem).getNewValue());
				getGeneric().bind((Relation) ((SelectItemComponent) selectItem).getGeneric(), instance);
			} else if (selectItem instanceof InputTextComponent) {
				getGeneric().setValue((Attribute) ((InputTextComponent) selectItem).getGeneric(), (((InputTextComponent) selectItem).getNewValue()).toString());
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
	public String getNewValue() {
		switch (mode) {
		case EDITION:
			return Objects.toString(getGeneric());
		case CREATION:
			return newValue;
		default:
			throw new IllegalStateException();
		}
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/createandedit.xhtml";
	}
}
