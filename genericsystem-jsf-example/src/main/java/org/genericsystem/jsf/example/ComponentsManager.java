package org.genericsystem.jsf.example;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.core.Cache;
import org.genericsystem.jsf.example.structure.Types.Cars;
import org.genericsystem.jsf.example.structure.Types.Colors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@SessionScoped
public class ComponentsManager implements Serializable {

	protected static Logger log = LoggerFactory.getLogger(ComponentsManager.class);

	private static final long serialVersionUID = 5181144829797170813L;

	@Inject
	private Cache cache;

	private List<TypeCrudComponent> components;

	@PostConstruct
	public void init() {
		components = Arrays.asList(new TypeCrudComponent(cache.<Cars> find(Cars.class)), new TypeCrudComponent(cache.<Colors> find(Colors.class)));
	}

	public List<TypeCrudComponent> getComponents() {
		return components;
	}
}
