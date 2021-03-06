package org.docear.plugin.core.listeners;

import javax.swing.SwingUtilities;

import org.docear.plugin.core.mindmap.MindmapLinkTypeUpdater;
import org.docear.plugin.core.mindmap.MindmapUpdateController;
import org.freeplane.core.resources.IFreeplanePropertyListener;
import org.freeplane.core.util.TextUtils;

public class PropertyListener implements IFreeplanePropertyListener {

	public void propertyChanged(String propertyName, String newValue, String oldValue) {
		if (propertyName.equals("links") && (!newValue.equals(oldValue))) {
			if(SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(new Runnable() {					
					public void run() {
						doUpdateLinks();						
					}
				});								
			}
			else {
				doUpdateLinks();
			}
		}
	}
	
	private final void doUpdateLinks() {
		MindmapUpdateController mindmapUpdateController = new MindmapUpdateController();
		mindmapUpdateController.addMindmapUpdater(new MindmapLinkTypeUpdater(TextUtils.getText("updating_link_types")));
		mindmapUpdateController.updateRegisteredMindmapsInWorkspace(true);
	}

}
