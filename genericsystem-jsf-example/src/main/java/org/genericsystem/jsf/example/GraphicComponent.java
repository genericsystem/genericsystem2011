package org.genericsystem.jsf.example;

import java.util.List;

public interface GraphicComponent {

	public String getSrc();

	public List<GraphicComponent> getChildren();

}
