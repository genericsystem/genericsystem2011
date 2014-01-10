package org.genericsystem.jsf.example;

import java.util.ArrayList;
import java.util.List;

import org.genericsystem.core.Generic;
import org.genericsystem.generic.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypeComponent implements GraphicComponent {

	private Logger log = LoggerFactory.getLogger(TypeComponent.class);

	private Generic generic;

	private TypeComponent father;

	private List<GraphicComponent> children = new ArrayList<GraphicComponent>();

	public TypeComponent(TypeComponent father, Generic generic) {
		this.father = father;
		this.generic = generic;
	}

	@Override
	public String getSrc() {
		return "/pages/type.xhtml";
	}

	@Override
	public List<GraphicComponent> getChildren() {
		return children;
	}

	public String getValue() {
		return generic.getValue().toString();
	}

	public boolean isType() {
		return generic.isType() && generic.isStructural();
	}

	public boolean isConcrete() {
		return generic.isConcrete();
	}

	public void clearInstances() {
		assert isConcrete();
		assert father != null;
		father.children.clear();
	}

	public void viewInstances() {
		children.clear();
		for (Generic instance : ((Type) generic).getInstances())
			children.add(new TypeComponent(this, instance));
	}

	@Override
	public String toString() {
		return getValue();
	}

}
