package org.genericsystem.myadmin.beans;

import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import org.genericsystem.core.Generic;
import org.genericsystem.myadmin.beans.qualifier.TreeSelection;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

@Named
@RequestScoped
public class TreeBean {

	@Inject
	@TreeSelection
	private Event<Generic> selected;

	public void change(TreeSelectionChangeEvent selectionChangeEvent) {

		List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
		if (!selection.isEmpty()) {
			Object currentSelectionKey = selection.get(0);
			UITree tree = (UITree) selectionChangeEvent.getSource();
			Object storedKey = tree.getRowKey();
			tree.setRowKey(currentSelectionKey);
			selected.fire((Generic) tree.getRowData());
			tree.setRowKey(storedKey);
		}
	}

}
