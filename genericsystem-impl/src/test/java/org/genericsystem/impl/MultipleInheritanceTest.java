package org.genericsystem.impl;

import org.genericsystem.core.Cache;
import org.genericsystem.core.Generic;
import org.genericsystem.core.GenericImpl;
import org.genericsystem.core.GenericSystem;
import org.genericsystem.core.Snapshot;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Holder;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class MultipleInheritanceTest extends AbstractTest {

	public void testInheritsFrom() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert selectableWindow.inheritsFrom(selectable);
		assert selectableWindow.inheritsFrom(window);
		assert selectableWindow.inheritsFrom(graphicComponent);
	}

	public void testgetDirectSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type window = cache.newType("Window");
		Type selectable = cache.newType("Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);
		assert window.getDirectSubTypes(cache).size() == 1 : window.getDirectSubTypes(cache);
		assert window.getDirectSubTypes(cache).contains(selectableWindow) : window.getDirectSubTypes(cache);

		assert selectable.getDirectSubTypes(cache).size() == 1 : selectable.getDirectSubTypes(cache);
		assert selectable.getDirectSubTypes(cache).contains(selectableWindow);

		assert selectableWindow.getDirectSubTypes(cache).size() == 0 : selectableWindow.getDirectSubTypes(cache);
	}

	public void testgetDirectSubTypesWithDiamondProblem() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert graphicComponent.getDirectSubTypes(cache).size() == 2 : graphicComponent.getDirectSubTypes(cache);
		assert graphicComponent.getSubTypes(cache).size() == 3 : graphicComponent.getSubTypes(cache);
		assert graphicComponent.getDirectSubTypes(cache).contains(selectable);
		assert graphicComponent.getDirectSubTypes(cache).contains(window) : graphicComponent.getDirectSubTypes(cache);

		assert window.getDirectSubTypes(cache).size() == 1 : window.getDirectSubTypes(cache);
		assert window.getDirectSubTypes(cache).contains(selectableWindow) : window.getDirectSubTypes(cache);

		assert selectable.getDirectSubTypes(cache).size() == 1 : selectable.getDirectSubTypes(cache);
		assert selectable.getDirectSubTypes(cache).contains(selectableWindow);

		assert selectableWindow.getDirectSubTypes(cache).size() == 0;
	}

	public void testGetAllSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type window = cache.newType("Window");
		Type selectable = cache.newType("Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		Snapshot<Generic> allInstances = cache.getEngine().getAllInstances(cache);
		assert !allInstances.contains(cache.getEngine());
		assert allInstances.contains(window);
		assert allInstances.contains(selectable);
		assert allInstances.contains(selectableWindow);

		Snapshot<Generic> allSubTypes = window.getSubTypes(cache);
		assert allSubTypes.size() == 1;
		assert allSubTypes.contains(selectableWindow);

		allSubTypes = selectable.getSubTypes(cache);
		assert allSubTypes.size() == 1;
		assert allSubTypes.contains(selectableWindow);

		allSubTypes = selectableWindow.getSubTypes(cache);
		assert allSubTypes.isEmpty();
	}

	public void testGetAllSubTypesWithDiamond() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		Snapshot<Generic> allSubTypes = graphicComponent.getSubTypes(cache);
		assert allSubTypes.size() == 3;
		assert graphicComponent.getSubTypes(cache).contains(window);
		assert graphicComponent.getSubTypes(cache).contains(selectable);
		assert graphicComponent.getSubTypes(cache).contains(selectableWindow);

		assert window.getSubTypes(cache).size() == 1 : window.getSubTypes(cache);
		assert window.getSubTypes(cache).contains(selectableWindow);

		assert selectable.getSubTypes(cache).size() == 1 : selectable.getSubTypes(cache);
		assert selectable.getSubTypes(cache).contains(selectableWindow);

		assert selectableWindow.getSubTypes(cache).isEmpty();
	}

	public void testgetDirectSubTypesWithDiamond() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		Snapshot<Generic> subTypes = graphicComponent.getDirectSubTypes(cache);
		assert subTypes.size() == 2;
		assert subTypes.contains(window);
		assert subTypes.contains(selectable);

		Snapshot<Generic> allSubTypes = graphicComponent.getSubTypes(cache);

		assert allSubTypes.size() == 3;
		assert allSubTypes.contains(window);
		assert allSubTypes.contains(selectable);
		assert allSubTypes.contains(selectableWindow);

		subTypes = window.getDirectSubTypes(cache);
		assert subTypes.size() == 1;
		assert subTypes.contains(selectableWindow);

		subTypes = selectable.getDirectSubTypes(cache);
		assert subTypes.size() == 1;
		assert subTypes.contains(selectableWindow);

		assert selectableWindow.getDirectSubTypes(cache).size() == 0;
	}

	public void testgetDirectSubTypesWithImplicit() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type window = cache.newType("Window");
		Type selectable = cache.newType("Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert selectableWindow.inheritsFrom(selectable);
		assert selectableWindow.inheritsFrom(window);
	}

	public void testgetDirectSubTypesWithImplicitWithDiamond() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();

		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		Snapshot<Generic> subTypes = graphicComponent.getDirectSubTypes(cache);
		Snapshot<Generic> allSubTypes = graphicComponent.getSubTypes(cache);
		assert subTypes.size() == 2 : subTypes;
		assert !subTypes.contains(graphicComponent);
		assert subTypes.contains(window);
		assert subTypes.contains(selectable);
		assert !subTypes.contains(selectableWindow);

		assert allSubTypes.size() == 3 : allSubTypes;
		assert allSubTypes.contains(window);
		assert allSubTypes.contains(selectable);
		assert allSubTypes.contains(selectableWindow);

		assert selectableWindow.inheritsFrom(selectable);
		assert selectableWindow.inheritsFrom(window);
		assert selectableWindow.inheritsFrom(graphicComponent);
	}

	public void testAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);
		Attribute size = graphicComponent.setAttribute(cache, "size");
		Attribute selectedSelectable = selectable.setAttribute(cache, "Selected");
		Attribute height = selectableWindow.setAttribute(cache, "height");

		assert !selectableWindow.isAttributeOf(selectableWindow) : ((GenericImpl) selectableWindow).getBaseComponent();
		assert selectableWindow.getAttributes(cache).contains(size);
		assert selectableWindow.getAttributes(cache).contains(selectedSelectable);
		assert selectableWindow.getAttributes(cache).contains(height);
	}

	public void testValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);
		Attribute size = graphicComponent.setAttribute(cache, "size");
		Attribute selectedSelectable = selectable.setAttribute(cache, "Selected");
		Generic mySelectableWindow = selectableWindow.newInstance(cache, "mySelectableWindow");

		Holder v12 = mySelectableWindow.setValue(cache, size, 12);
		Holder vTrue = mySelectableWindow.setValue(cache, selectedSelectable, true);

		assert selectableWindow.getInstances(cache).size() == 1 : selectableWindow.getInstances(cache);
		assert selectableWindow.getInstances(cache).contains(mySelectableWindow);
		assert mySelectableWindow.getHolders(cache, size).size() == 1 : mySelectableWindow.getHolders(cache, size);
		assert mySelectableWindow.getHolders(cache, size).contains(v12);
		assert mySelectableWindow.getHolders(cache, selectedSelectable).size() == 1;
		assert mySelectableWindow.getHolders(cache, selectedSelectable).contains(vTrue);
	}

	public void testBaseComponent() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert ((Holder) selectableWindow).getBaseComponent() == null : ((Holder) selectableWindow).getBaseComponent();
	}

	public void testTargetComponent() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine().start();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert ((GenericImpl) selectableWindow).getTargetComponent() == null : ((GenericImpl) selectableWindow).getTargetComponent();
	}

}
