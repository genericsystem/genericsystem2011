package org.genericsystem.impl;

import org.genericsystem.api.core.Cache;
import org.genericsystem.api.core.Generic;
import org.genericsystem.api.core.GenericSystem;
import org.genericsystem.api.core.Snapshot;
import org.genericsystem.api.generic.Attribute;
import org.genericsystem.api.generic.Type;
import org.genericsystem.api.generic.Value;
import org.genericsystem.impl.core.GenericImpl;
import org.testng.annotations.Test;

@Test
public class MultipleInheritanceTest extends AbstractTest {

	public void testInheritsFrom() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert selectableWindow.inheritsFrom(selectable);
		assert selectableWindow.inheritsFrom(window);
		assert selectableWindow.inheritsFrom(graphicComponent);
	}

	public void testGetSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type window = cache.newType("Window");
		Type selectable = cache.newType("Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);
		assert window.getSubTypes(cache).size() == 1 : window.getSubTypes(cache);
		assert window.getSubTypes(cache).contains(selectableWindow) : window.getSubTypes(cache);

		assert selectable.getSubTypes(cache).size() == 1 : selectable.getSubTypes(cache);
		assert selectable.getSubTypes(cache).contains(selectableWindow);

		assert selectableWindow.getSubTypes(cache).size() == 0 : selectableWindow.getSubTypes(cache);
	}

	public void testGetSubTypesWithDiamondProblem() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert graphicComponent.getSubTypes(cache).size() == 2 : graphicComponent.getSubTypes(cache);
		assert graphicComponent.getAllSubTypes(cache).size() == 4 : graphicComponent.getAllSubTypes(cache);
		assert graphicComponent.getSubTypes(cache).contains(selectable);
		assert graphicComponent.getSubTypes(cache).contains(window) : graphicComponent.getSubTypes(cache);

		assert window.getSubTypes(cache).size() == 1 : window.getSubTypes(cache);
		assert window.getSubTypes(cache).contains(selectableWindow) : window.getSubTypes(cache);

		assert selectable.getSubTypes(cache).size() == 1 : selectable.getSubTypes(cache);
		assert selectable.getSubTypes(cache).contains(selectableWindow);

		assert selectableWindow.getSubTypes(cache).size() == 0;
	}

	public void testGetAllSubTypes() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type window = cache.newType("Window");
		Type selectable = cache.newType("Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		Snapshot<Generic> allInstances = cache.getEngine().getAllInstances(cache);
		assert !allInstances.contains(cache.getEngine());
		assert allInstances.contains(window);
		assert allInstances.contains(selectable);
		assert allInstances.contains(selectableWindow);

		Snapshot<Generic> allSubTypes = window.getAllSubTypes(cache);
		assert allSubTypes.size() == 2;
		assert allSubTypes.contains(window);
		assert allSubTypes.contains(selectableWindow);

		allSubTypes = selectable.getAllSubTypes(cache);
		assert allSubTypes.size() == 2;
		assert allSubTypes.contains(selectable);
		assert allSubTypes.contains(selectableWindow);

		allSubTypes = selectableWindow.getAllSubTypes(cache);
		assert allSubTypes.contains(selectableWindow);
		assert allSubTypes.size() == 1;
	}

	public void testGetAllSubTypesWithDiamond() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		Snapshot<Generic> allSubTypes = graphicComponent.getAllSubTypes(cache);
		assert allSubTypes.size() == 4;
		assert graphicComponent.getAllSubTypes(cache).contains(graphicComponent);
		assert graphicComponent.getAllSubTypes(cache).contains(window);
		assert graphicComponent.getAllSubTypes(cache).contains(selectable);
		assert graphicComponent.getAllSubTypes(cache).contains(selectableWindow);

		assert window.getAllSubTypes(cache).size() == 2 : window.getAllSubTypes(cache);
		assert window.getAllSubTypes(cache).contains(window);
		assert window.getAllSubTypes(cache).contains(selectableWindow);

		assert selectable.getAllSubTypes(cache).size() == 2 : selectable.getAllSubTypes(cache);
		assert selectable.getAllSubTypes(cache).contains(selectable);
		assert selectable.getAllSubTypes(cache).contains(selectableWindow);

		assert selectableWindow.getAllSubTypes(cache).size() == 1;
		assert selectableWindow.getAllSubTypes(cache).contains(selectableWindow);
	}

	public void testGetSubTypesWithDiamond() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		Snapshot<Generic> subTypes = graphicComponent.getSubTypes(cache);
		assert subTypes.size() == 2;
		assert subTypes.contains(window);
		assert subTypes.contains(selectable);

		Snapshot<Generic> allSubTypes = graphicComponent.getAllSubTypes(cache);

		assert allSubTypes.size() == 4;
		assert allSubTypes.contains(graphicComponent);
		assert allSubTypes.contains(window);
		assert allSubTypes.contains(selectable);
		assert allSubTypes.contains(selectableWindow);

		subTypes = window.getSubTypes(cache);
		assert subTypes.size() == 1;
		assert subTypes.contains(selectableWindow);

		subTypes = selectable.getSubTypes(cache);
		assert subTypes.size() == 1;
		assert subTypes.contains(selectableWindow);

		assert selectableWindow.getSubTypes(cache).size() == 0;
	}

	public void testGetSubTypesWithImplicit() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type window = cache.newType("Window");
		Type selectable = cache.newType("Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert selectableWindow.inheritsFrom(selectable);
		assert selectableWindow.inheritsFrom(window);
	}

	public void testGetSubTypesWithImplicitWithDiamond() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();

		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		Snapshot<Generic> subTypes = graphicComponent.getSubTypes(cache);
		Snapshot<Generic> allSubTypes = graphicComponent.getAllSubTypes(cache);
		assert subTypes.size() == 2 : subTypes;
		assert !subTypes.contains(graphicComponent);
		assert subTypes.contains(window);
		assert subTypes.contains(selectable);
		assert !subTypes.contains(selectableWindow);

		assert allSubTypes.size() == 4 : allSubTypes;
		assert allSubTypes.contains(window);
		assert allSubTypes.contains(selectable);
		assert allSubTypes.contains(selectableWindow);
		assert allSubTypes.contains(graphicComponent);

		assert selectableWindow.inheritsFrom(selectable);
		assert selectableWindow.inheritsFrom(window);
		assert selectableWindow.inheritsFrom(graphicComponent);
	}

	public void testAttribute() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);
		Attribute size = graphicComponent.addAttribute(cache, "size");
		Attribute selectedSelectable = selectable.addAttribute(cache, "Selected");
		Attribute height = selectableWindow.addAttribute(cache, "height");

		assert !selectableWindow.isAttributeOf(selectableWindow) : ((GenericImpl) selectableWindow).getBaseComponent();
		assert selectableWindow.getAttributes(cache).contains(size);
		assert selectableWindow.getAttributes(cache).contains(selectedSelectable);
		assert selectableWindow.getAttributes(cache).contains(height);
	}

	public void testValue() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);
		Attribute size = graphicComponent.addAttribute(cache, "size");
		Attribute selectedSelectable = selectable.addAttribute(cache, "Selected");
		Generic mySelectableWindow = selectableWindow.newInstance(cache, "mySelectableWindow");

		Value v12 = mySelectableWindow.addValue(cache, size, 12);
		Value vTrue = mySelectableWindow.addValue(cache, selectedSelectable, true);

		assert selectableWindow.getInstances(cache).size() == 1 : selectableWindow.getInstances(cache);
		assert selectableWindow.getInstances(cache).contains(mySelectableWindow);
		assert mySelectableWindow.getValueHolders(cache, size).size() == 1 : mySelectableWindow.getValueHolders(cache, size);
		assert mySelectableWindow.getValueHolders(cache, size).contains(v12);
		assert mySelectableWindow.getValueHolders(cache, selectedSelectable).size() == 1;
		assert mySelectableWindow.getValueHolders(cache, selectedSelectable).contains(vTrue);
	}

	public void testBaseComponent() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert ((Value) selectableWindow).getBaseComponent() == null : ((Value) selectableWindow).getBaseComponent();
	}

	public void testTargetComponent() {
		Cache cache = GenericSystem.newCacheOnANewInMemoryEngine();
		Type graphicComponent = cache.newType("graphicComponent");
		Type window = graphicComponent.newSubType(cache, "Window");
		Type selectable = graphicComponent.newSubType(cache, "Selectable");
		Type selectableWindow = cache.newSubType("selectableWindow", selectable, window);

		assert ((GenericImpl) selectableWindow).getTargetComponent() == null : ((GenericImpl) selectableWindow).getTargetComponent();
	}

}
