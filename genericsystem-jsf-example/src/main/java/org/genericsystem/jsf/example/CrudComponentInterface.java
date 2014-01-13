package org.genericsystem.jsf.example;

import java.util.List;

import org.genericsystem.core.Generic;

public interface CrudComponentInterface {

	String getXhtmlPath();

	List<? extends Row> getRows();

	String getTitleMsg();

	String getRemoveMsg();

	String getAddMsg();

	void add();

	String getAddInstanceValue();

	void setAddInstanceValue(String instanceValue);

	void remove(Row row);

	public static interface Row {
		String getEditInstance();

		Generic getInstance();
	}
}
