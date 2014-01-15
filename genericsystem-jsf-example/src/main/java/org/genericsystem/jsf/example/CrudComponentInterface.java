package org.genericsystem.jsf.example;

import java.util.List;

import org.genericsystem.core.Generic;

public interface CrudComponentInterface {

	String getXhtmlPath();

	List<? extends InstanceRow> getInstanceRows();

	String getTitleMsg();

	String getRemoveMsg();

	String getAddMsg();

	void add();

	String getAddInstanceValue();

	void setAddInstanceValue(String instanceValue);

	void remove(InstanceRow instanceRow);

	public static interface InstanceRow {
		String getEditInstance();

		Generic getInstance();
	}

}
