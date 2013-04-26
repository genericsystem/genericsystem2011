package org.genericsystem.web.beans.converters;
//package org.genericsystem.web.beans.converters;
//
//import java.io.Serializable;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.UUID;
//import java.util.WeakHashMap;
//
//import javax.enterprise.context.SessionScoped;
//import javax.faces.component.UIComponent;
//import javax.faces.context.FacesContext;
//import javax.faces.convert.Converter;
//import javax.faces.convert.FacesConverter;
//
//@SessionScoped
//@FacesConverter("genericConverter")
//public class GenericConverter implements Converter, Serializable {
//
//	private static final long serialVersionUID = -1094217369055614047L;
//	private static Map<Object, String> generics = new WeakHashMap<Object, String>();
//
//	@Override
//	public String getAsString(FacesContext context, UIComponent component, Object generic) {
//		synchronized (generics) {
//			if (!generics.containsKey(generic)) {
//				String uuid = UUID.randomUUID().toString();
//				generics.put(generic, uuid);
//				return uuid;
//			} else {
//				return generics.get(generic);
//			}
//		}
//	}
//
//	@Override
//	public Object getAsObject(FacesContext context, UIComponent component, String uuid) {
//		for (Entry<Object, String> entry : generics.entrySet()) {
//			if (entry.getValue().equals(uuid)) {
//				return entry.getKey();
//			}
//		}
//		return null;
//	}
//
// }
