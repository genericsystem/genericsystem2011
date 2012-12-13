package org.genericsystem.web.beans;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.genericsystem.api.core.Generic;
import org.genericsystem.api.generic.Link;
import org.slf4j.Logger;

@Named
@RequestScoped
public class LinkListForm {

	private Map<Generic, Generic> mapTarget = new HashMap<>();
	private Map<Generic, String> mapValue = new HashMap<>();
	
	private Map<Generic, Generic> mapTargetType = new HashMap<>();
	private Map<Generic, String> mapValueType = new HashMap<>();

	@Inject
	private Logger log;

	public WrapperAdd getWrapperAdd(Generic genericKey) {
		return new WrapperAdd(genericKey, mapTarget, mapValue);
	}

	public WrapperModify getWrapperModify(Generic genericKey) {
		return new WrapperModify(genericKey, mapTarget, mapValue);
	}
	
	public WrapperAdd getWrapperAddType(Generic genericKey) {
		return new WrapperAdd(genericKey, mapTargetType, mapValueType);
	}

	public WrapperModify getWrapperModifyType(Generic genericKey) {
		return new WrapperModify(genericKey, mapTargetType, mapValueType);
	}
	

	//
	// public void clear() {
	// mapTarget.clear();
	// mapValue.clear();
	// }
	//

	public class WrapperAdd {
		protected Generic genericKey;
		protected Map<Generic, Generic> mapTarget;
		private Map<Generic, String> mapValue;

		public WrapperAdd(Generic masterGeneric, Map<Generic, Generic> mapTarget, Map<Generic, String> mapValue) {
			this.mapValue = mapValue;
			this.mapTarget = mapTarget;
			this.genericKey = masterGeneric;
		}

		public String getValue() {
			log.info("wrapper => getValue : " + mapValue.get(genericKey));
			return mapValue.get(genericKey);
		}

		public void setValue(String value) {
			log.info("wrapper => setValue : " + value);
			mapValue.put(genericKey, value);
		}

		public Generic getTarget() {
			return mapTarget.get(genericKey);
		}

		public void setTarget(Generic target) {
			mapTarget.put(genericKey, target);
		}

	}

	public class WrapperModify extends WrapperAdd {

		public WrapperModify(Generic masterGeneric, Map<Generic, Generic> mapTarget, Map<Generic, String> mapValue) {
			super(masterGeneric, mapTarget, mapValue);
		}

		@Override
		public String getValue() {
			String value = mapValue.get(genericKey);

			if (value != null) {
				return value;
			}

			return genericKey.getValue().toString();
		}

		@Override
		public Generic getTarget() {
			Generic target = mapTarget.get(genericKey);
			if (target != null)
				return target;

			return ((Link) genericKey).getTargetComponent();
		}
	}
}
