package org.genericsystem.jsf.example;

import java.util.List;
import java.util.Objects;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot.Projector;
import org.genericsystem.generic.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeCrudComponent implements CrudComponentInterface {
	protected static Logger log = LoggerFactory.getLogger(TypeCrudComponent.class);

	protected final Type type;

	protected String addInstanceValue;

	public TypeCrudComponent(Type type) {
		this.type = type;
	}

	@Override
	public List<? extends Row> getRows() {
		return type.getAllInstances().<Row> project(new Projector<Row, Generic>() {
			@Override
			public Row project(final Generic instance) {
				return new Row() {
					@Override
					public String getEditInstance() {
						return Objects.toString(instance.getValue());
					}

					@Override
					public Generic getInstance() {
						return instance;
					}

				};
			}
		});
	}

	@Override
	public String getTitleMsg() {
		return type.<Class<?>> getValue().getSimpleName() + " Management";
	}

	@Override
	public void add() {
		type.setInstance(addInstanceValue);
	}

	@Override
	public String getAddMsg() {
		return "Add instance";
	}

	@Override
	public String getRemoveMsg() {
		return "Remove instance";
	}

	@Override
	public void remove(Row row) {
		row.getInstance().remove();
	}

	@Override
	public String getAddInstanceValue() {
		return addInstanceValue;
	}

	@Override
	public void setAddInstanceValue(String addInstanceValue) {
		this.addInstanceValue = addInstanceValue;
	}

	@Override
	public String getXhtmlPath() {
		return "/pages/crud.xhtml";
	}

}
