package org.genericsystem.myadmin.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.exception.NotRemovableException;
import org.genericsystem.generic.MapProvider;
import org.genericsystem.map.ConstraintsMapProvider;
import org.genericsystem.map.PropertiesMapProvider;
import org.genericsystem.map.SystemPropertiesMapProvider;
import org.genericsystem.myadmin.util.GsMessages;
import org.genericsystem.systemproperties.CascadeRemoveSystemProperty;
import org.genericsystem.systemproperties.NoInheritanceSystemType;
import org.genericsystem.systemproperties.NoReferentialIntegritySystemProperty;
import org.genericsystem.systemproperties.constraints.axed.RequiredConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.SingularConstraintImpl;
import org.genericsystem.systemproperties.constraints.axed.SizeConstraintImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean for manipulation of maps with MyAdmin Edit.
 * 
 * @version 0.5
 * @author Alexei KLENIN
 */
@Named
@SessionScoped
public class MapProviderBean implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static Logger log = LoggerFactory.getLogger(MapProviderBean.class);

	@Inject private GenericTreeBean genericTreeBean;	// generic tree bean
	@Inject private PopupPanelBean popupPanelBean;		// Popup Panel bean
	@Inject private GsMessages messages;				// messages bean

	/**
	 * Return map of map provider.
	 * 
	 * @param mapProvider - map provider.
	 * 
	 * @return map object.
	 */
	public Map<Serializable, Serializable> getMap(MapProvider mapProvider) {
		return mapProvider.getMap(genericTreeBean.getSelectedTreeNodeGeneric());
	}

	/**
	 * Adds a couple of key and value to the map.
	 * 
	 * @param key - key.
	 * @param value - value.
	 */
	public void addKeyValueToMap(Map<Serializable, Serializable> map, Serializable key, Serializable value) {
		map.put(key, value);
	}

	/**
	 * Adds a couple of key and value to the map.
	 * 
	 * @param map - map object.
	 * @param entry - an entry.
	 */
	public void addEntryToMap(Map<Serializable, Serializable> map, Entry<Serializable, Serializable> entry) {
		map.put(entry.getKey(), entry.getValue());
	}

	/**
	 * Remove an entry from the map.
	 * 
	 * @param map - map object.
	 * @param entry - couple of key and value.
	 */
	public void removeEntryFromMap(Map<Serializable, Serializable> map, Entry<Serializable, Serializable> entry) {
		try {
			map.remove(entry.getKey());
			messages.info("remove", entry.getKey());
		} catch (NotRemovableException e) {
			messages.info("cannotremove", e.getMessage());
		}
	}

	/**
	 * Returns the list of possible classes for objects that figure in the key
	 * positions of the map.
	 * 
	 * @param mapProviderClass - class of map provider.
	 * 
	 * @return list of SelectItems for drop-down list in the popup.
	 */
	public <T extends MapProvider> List<SelectItem> getKeyClasses(Class<T> mapProviderClass) {
		// TODO: Implement introspection of packages to dynamically get available constraint and
		// properties classes.
		List<SelectItem> items = new ArrayList<>();
		if (mapProviderClass == ConstraintsMapProvider.class) {
			items.add(new SelectItem(RequiredConstraintImpl.class, RequiredConstraintImpl.class.getSimpleName()));
			items.add(new SelectItem(SingularConstraintImpl.class, SingularConstraintImpl.class.getSimpleName()));
			items.add(new SelectItem(SizeConstraintImpl.class, SizeConstraintImpl.class.getSimpleName()));
		} else {
			if (mapProviderClass == PropertiesMapProvider.class) {
				// TODO: What to do?
			} else {
				if (mapProviderClass == SystemPropertiesMapProvider.class) {
					items.add(new SelectItem(CascadeRemoveSystemProperty.class, CascadeRemoveSystemProperty.class.getSimpleName()));
					items.add(new SelectItem(NoInheritanceSystemType.class, NoInheritanceSystemType.class.getSimpleName()));
					items.add(new SelectItem(NoReferentialIntegritySystemProperty.class, NoReferentialIntegritySystemProperty.class.getSimpleName()));
				}
			}
		}
		return items;
	}

}
