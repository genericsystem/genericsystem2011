package org.genericsystem.myadmin.beans;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;

@Named
@RequestScoped
public class PanelBean {

	private Map<String, String> selectedValueMap = new HashMap<>();

	public String selectedValue(String key) {
		String value = selectedValueMap.get(key);
		if (null == value)
			return "";
		return value;
	}

	public void change(@Observes PanelSelectionEvent panelSelectionEvent) {
		selectedValueMap.put(panelSelectionEvent.getId(), panelSelectionEvent.getName());
	}

	public static class PanelSelectionEvent {
		private final String id;
		private final String name;

		public PanelSelectionEvent(String id, String name) {
			this.id = id;
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

	}

}
