package org.genericsystem.tracker.component.generic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.framework.component.AbstractComponent;
import org.genericsystem.framework.component.generic.AbstractGenericComponent;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.genericsystem.tracker.structure.Attributes;
import org.genericsystem.tracker.structure.Relations;

public class CreateComponent extends AbstractGenericComponent {

	private static final String DATE_PATTERN = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";

	private String newValue;

	private String error;

	public CreateComponent(AbstractComponent parent, Generic generic) {
		super(parent, generic);
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
						put((Attribute) key, result = new RowComponent(CreateComponent.this, (Attribute) key));
					return result;
				}
			};

			@Override
			public RowComponent project(Attribute attribute) {
				return map.get(attribute);
			}
		});

	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

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
	}

	public boolean validateDate(String date) {
		Pattern p = Pattern.compile(DATE_PATTERN);
		Matcher m = p.matcher(date);
		return (m.matches());
	}

	@Override
	public boolean isRelation() {
		return getGeneric().isRelation();
	}

	public String addMsg() {
		return "add";
	}

	@Override
	public String getXhtmlPath() {
		return "create.xhtml";
	}
}
