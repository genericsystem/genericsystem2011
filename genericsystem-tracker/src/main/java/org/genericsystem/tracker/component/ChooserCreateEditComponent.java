//package org.genericsystem.tracker.component;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.genericsystem.core.Generic;
//import org.genericsystem.framework.component.AbstractComponent;
//import org.genericsystem.tracker.component.generic.TypeComponent;
//
//public class ChooserCreateEditComponent extends AbstractComponent {
//
//	Generic generic;
//
//	public ChooserCreateEditComponent(AbstractComponent parent, Generic generic) {
//		super(parent);
//		this.generic = generic;
//	}
//
//	@Override
//	public List<? extends AbstractComponent> initChildren() {
//		return Arrays.asList(new TypeComponent(this, generic));
//	}
//
// }
