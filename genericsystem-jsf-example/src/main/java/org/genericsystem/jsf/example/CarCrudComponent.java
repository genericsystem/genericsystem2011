package org.genericsystem.jsf.example;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Type;

public class CarCrudComponent extends TypeCrudComponent {

	private final Attribute attribute;
	private String addAttributeValue;

	public CarCrudComponent(Type type, Attribute attribute) {
		super(type);
		this.attribute = attribute;
	}

	public String getAddAttributeValue() {
		return addAttributeValue;
	}

	public void setAddAttributeValue(String attributeValue) {
		this.addAttributeValue = attributeValue;
	}

	@Override
	public void add() {
		Generic instance = type.setInstance(addInstanceValue);
		instance.setValue(attribute, Integer.parseInt(addAttributeValue));
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/carcrud.xhtml";
	}

	public static interface CarRow extends Row {
		String getEditAttributeValue();
	}

	@Override
	public List<CarRow> getRows() {
		return type.getAllInstances().<CarRow> project(new Projector<CarRow, Generic>() {
			@Override
			public CarRow project(final Generic instance) {
				return new CarRow() {
					@Override
					public String getEditInstance() {
						return Objects.toString(instance.getValue());
					}

					@Override
					public Generic getInstance() {
						return instance;
					}

					@Override
					public String getEditAttributeValue() {
						return Objects.toString(instance.getValue(attribute));
					}
				};
			}
		});
	}

}
