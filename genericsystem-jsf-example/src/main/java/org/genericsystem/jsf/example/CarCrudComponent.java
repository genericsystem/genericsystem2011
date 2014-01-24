package org.genericsystem.jsf.example;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;

public class CarCrudComponent extends TypeCrudComponent {

	private final Attribute attribute;
	private final Attribute attribute2;
	private final Relation relation;
	private String addAttributeValue;
	private String addAttributeValue2;

	private String addLinkTargetValue;

	public CarCrudComponent(Type type, Attribute attribute, Attribute attribute2, Relation relation) {
		super(type);
		this.attribute = attribute;
		this.attribute2 = attribute2;
		this.relation = relation;
	}

	public String getAddAttributeValue() {
		return addAttributeValue;
	}

	public void setAddAttributeValue(String addAttributeValue) {
		this.addAttributeValue = addAttributeValue;
	}

	@Override
	public void add() {
		Generic instance = type.setInstance(addInstanceValue);
		instance.setValue(attribute, Integer.parseInt(addAttributeValue));
		instance.setValue(attribute, Integer.parseInt(addAttributeValue2));
		instance.bind(relation, relation.<Type> getTargetComponent().getInstance(addLinkTargetValue));
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/carcrud.xhtml";
	}

	@Override
	public List<CarRow> getInstanceRows() {
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

					@Override
					public String getEditAttributeValue2() {
						return Objects.toString(instance.getValue(attribute2));
					}

					@Override
					public String getEditRelationValue() {
						return (instance.getLink(relation) != null) ? Objects.toString(instance.getLink(relation).getTargetComponent().getValue()) : null;
					}
				};
			}
		});
	}

	public List<TargetRow> getTargetRows() {
		return relation.<Type> getTargetComponent().getAllInstances().<TargetRow> project(new Projector<TargetRow, Generic>() {
			@Override
			public TargetRow project(final Generic instance) {
				return new TargetRow() {
					@Override
					public String getEditLabel() {
						return instance.getValue();
					}

					@Override
					public String getEditValue() {
						return instance.getValue();
					}
				};
			}
		});
	}

	public String getAddLinkTargetValue() {
		return addLinkTargetValue;
	}

	public void setAddLinkTargetValue(String addLinkTargetValue) {
		this.addLinkTargetValue = addLinkTargetValue;
	}

	public static interface CarRow extends InstanceRow {
		String getEditAttributeValue();

		String getEditAttributeValue2();

		String getEditRelationValue();
	}

	public static interface TargetRow {
		String getEditLabel();

		String getEditValue();

	}

	@Override
	public boolean isChildComponent() {
		return true;
	}

	public String getAddAttributeValue2() {
		return addAttributeValue2;
	}

	public void setAddAttributeValue2(String addAttributeValue2) {
		this.addAttributeValue2 = addAttributeValue2;
	}

}
