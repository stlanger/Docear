package org.freeplane.plugin.workspace.nodes;

import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.freeplane.core.util.LogUtils;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.mode.Controller;
import org.freeplane.plugin.workspace.WorkspaceController;
import org.freeplane.plugin.workspace.WorkspaceUtils;
import org.freeplane.plugin.workspace.actions.FileNodeDeleteAction;
import org.freeplane.plugin.workspace.actions.FileNodeNewFileAction;
import org.freeplane.plugin.workspace.actions.FileNodeNewMindmapAction;
import org.freeplane.plugin.workspace.actions.NodeCopyAction;
import org.freeplane.plugin.workspace.actions.NodeEnableMonitoringAction;
import org.freeplane.plugin.workspace.actions.NodeNewFolderAction;
import org.freeplane.plugin.workspace.actions.NodeNewLinkAction;
import org.freeplane.plugin.workspace.actions.NodeOpenLocationAction;
import org.freeplane.plugin.workspace.actions.NodePasteAction;
import org.freeplane.plugin.workspace.actions.NodeRefreshAction;
import org.freeplane.plugin.workspace.actions.NodeRemoveAction;
import org.freeplane.plugin.workspace.actions.NodeRenameAction;
import org.freeplane.plugin.workspace.actions.PhysicalFolderSortOrderAction;
import org.freeplane.plugin.workspace.actions.WorkspaceChangeLocationAction;
import org.freeplane.plugin.workspace.actions.WorkspaceCollapseAction;
import org.freeplane.plugin.workspace.actions.WorkspaceExpandAction;
import org.freeplane.plugin.workspace.actions.WorkspaceHideAction;
import org.freeplane.plugin.workspace.components.menu.WorkspacePopupMenu;
import org.freeplane.plugin.workspace.components.menu.WorkspacePopupMenuBuilder;
import org.freeplane.plugin.workspace.config.IConfigurationInfo;
import org.freeplane.plugin.workspace.dnd.IDropAcceptor;
import org.freeplane.plugin.workspace.dnd.WorkspaceTransferable;
import org.freeplane.plugin.workspace.event.IWorkspaceNodeActionListener;
import org.freeplane.plugin.workspace.event.WorkspaceActionEvent;
import org.freeplane.plugin.workspace.io.annotation.ExportAsAttribute;
import org.freeplane.plugin.workspace.model.AWorkspaceTreeNode;

public class WorkspaceRoot extends AFolderNode implements IConfigurationInfo, IWorkspaceNodeActionListener, IDropAcceptor {

	private static final long serialVersionUID = 1L;
	private static Icon DEFAULT_ICON = new ImageIcon(
			FolderLinkNode.class.getResource("/images/16x16/preferences-desktop-filetype-association.png"));
	private static WorkspacePopupMenu popupMenu;

	private String version = WorkspaceController.WORKSPACE_VERSION;
	private Object meta = null;

	public WorkspaceRoot() {
		super(null);
	}

	public final String getTagName() {
		return "workspace";
	}

	public void handleAction(WorkspaceActionEvent event) {
		if (event.getType() == WorkspaceActionEvent.MOUSE_RIGHT_CLICK) {
			showPopup((Component) event.getBaggage(), event.getX(), event.getY());
		}
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@ExportAsAttribute(name="version")
	public String getVersion() {
		return this.version;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

	@ExportAsAttribute(name="meta")
	public Object getMeta() {
		return this.meta;
	}
	
	public boolean isSystem() {
		return super.isSystem();
	}

	public boolean setIcons(DefaultTreeCellRenderer renderer) {
		renderer.setOpenIcon(DEFAULT_ICON);
		renderer.setClosedIcon(DEFAULT_ICON);
		renderer.setLeafIcon(DEFAULT_ICON);
		return true;
	}

	public void initializePopup() {
		if (popupMenu == null) {
			Controller controller = Controller.getCurrentController();
			controller.addAction(new WorkspaceExpandAction());
			controller.addAction(new WorkspaceCollapseAction());
			controller.addAction(new WorkspaceChangeLocationAction());
			controller.addAction(new NodeRefreshAction());
			controller.addAction(new WorkspaceHideAction());
			controller.addAction(new NodeRemoveAction());
			controller.addAction(new NodeNewFolderAction());
			controller.addAction(new NodeNewLinkAction());
			controller.addAction(new NodeEnableMonitoringAction());
			controller.addAction(new NodeOpenLocationAction());
			
			//FIXME: #332
//			controller.addAction(new NodeCutAction());
			controller.addAction(new NodeRenameAction());
			controller.addAction(new NodeCopyAction());
			controller.addAction(new NodePasteAction());
			
			controller.addAction(new FileNodeNewMindmapAction());
			controller.addAction(new FileNodeNewFileAction());
			controller.addAction(new FileNodeDeleteAction());
			
			controller.addAction(new PhysicalFolderSortOrderAction());
			
			popupMenu = new WorkspacePopupMenu();
			WorkspacePopupMenuBuilder.addActions(popupMenu, new String[] {
					WorkspacePopupMenuBuilder.createSubMenu(TextUtils.getRawText("workspace.action.new.label")),
					"workspace.action.node.new.folder",
					"workspace.action.node.new.link",
					WorkspacePopupMenuBuilder.endSubMenu(),
					WorkspacePopupMenuBuilder.SEPARATOR,
					"workspace.action.location.change",
					"workspace.action.node.open.location",
					//"workspace.action.hide",
					WorkspacePopupMenuBuilder.SEPARATOR,
					"workspace.action.node.cut",
					"workspace.action.node.copy",						
					"workspace.action.node.paste",
					WorkspacePopupMenuBuilder.SEPARATOR, 
					"workspace.action.all.expand",
					"workspace.action.all.collapse",					 
					WorkspacePopupMenuBuilder.SEPARATOR,
					"workspace.action.node.refresh"					
			});
		}
	}

	protected AWorkspaceTreeNode clone(WorkspaceRoot node) {
		node.setMeta((String) getMeta());
		node.setVersion(getVersion());
		return super.clone(node);
	}

	public AWorkspaceTreeNode clone() {
		WorkspaceRoot node = new WorkspaceRoot();
		return clone(node);
	}

	public WorkspacePopupMenu getContextMenu() {
		if (popupMenu == null) {
			initializePopup();
		}
		return popupMenu;
	}
	
	public String getName() {
		return WorkspaceController.getController().getPreferences().getWorkspaceProfile() + " (Workspace)"; 
	}
	
	public void refresh() {
		WorkspaceController.getController().refreshWorkspace();
	}

	public boolean acceptDrop(DataFlavor[] flavors) {
		return true;
	}

	@SuppressWarnings("unchecked")
	public boolean processDrop(Transferable transferable, int dropAction) {
		try {
			if(transferable.isDataFlavorSupported(WorkspaceTransferable.WORKSPACE_NODE_FLAVOR)) {
				processWorkspaceNodeDrop((List<AWorkspaceTreeNode>) transferable.getTransferData(WorkspaceTransferable.WORKSPACE_NODE_FLAVOR), dropAction);	
			}
			else if(transferable.isDataFlavorSupported(WorkspaceTransferable.WORKSPACE_FILE_LIST_FLAVOR)) {
				processFileListDrop((List<File>) transferable.getTransferData(WorkspaceTransferable.WORKSPACE_FILE_LIST_FLAVOR), dropAction);
			} 
			else if(transferable.isDataFlavorSupported(WorkspaceTransferable.WORKSPACE_URI_LIST_FLAVOR)) {
				ArrayList<URI> uriList = new ArrayList<URI>();
				String uriString = (String) transferable.getTransferData(WorkspaceTransferable.WORKSPACE_URI_LIST_FLAVOR);
				if (!uriString.startsWith("file://")) {
					return false;
				}
				String[] uriArray = uriString.split("\r\n");
				for(String singleUri : uriArray) {
					try {
						uriList.add(URI.create(singleUri));
					}
					catch (Exception e) {
						LogUtils.info("DOCEAR - "+ e.getMessage());
					}
				}
				processUriListDrop(uriList, dropAction);	
			}
		}
		catch (Exception e) {
			LogUtils.warn(e);
		}
		return true;
	}
	
	public boolean processDrop(DropTargetDropEvent event) {
		event.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
		Transferable transferable = event.getTransferable();
		if(processDrop(transferable, event.getDropAction())) {
			event.dropComplete(true);
			return true;
		}
		event.dropComplete(false);
		return false;
	}
	
	private void processWorkspaceNodeDrop(List<AWorkspaceTreeNode> nodes, int dropAction) {
		try {	
			for(AWorkspaceTreeNode node : nodes) {
				AWorkspaceTreeNode newNode = null;
				if(node instanceof DefaultFileNode) {					
					newNode = createFSNodeLinks(((DefaultFileNode) node).getFile());
				}
				else {
					if(dropAction == DnDConstants.ACTION_COPY) {
						newNode = node.clone();
					} 
					else if (dropAction == DnDConstants.ACTION_MOVE) {
						AWorkspaceTreeNode parent = node.getParent();
						WorkspaceUtils.getModel().cutNodeFromParent(node);
						parent.refresh();
						newNode = node;
					}
				}
				if(newNode == null) {
					continue;
				}
				WorkspaceUtils.getModel().addNodeTo(newNode, this);
				WorkspaceController.getController().getExpansionStateHandler().addPathKey(this.getKey());
			}
			WorkspaceUtils.saveCurrentConfiguration();
			
		}
		catch (Exception e) {
			LogUtils.warn(e);
		}
		refresh();
	}
	
	private void processFileListDrop(List<File> files, int dropAction) {
		try {		
			for(File srcFile : files) {
				WorkspaceUtils.getModel().addNodeTo(createFSNodeLinks(srcFile), this);		
			}
			WorkspaceUtils.saveCurrentConfiguration();
		}
		catch (Exception e) {
			LogUtils.warn(e);
		}
		refresh();
	}
	
	private void processUriListDrop(List<URI> uris, int dropAction) {
		try {			
			for(URI uri : uris) {
				File srcFile = new File(uri);
				if(srcFile == null || !srcFile.exists()) {
					continue;
				}
				WorkspaceUtils.getModel().addNodeTo(createFSNodeLinks(srcFile), this);
			};
			WorkspaceUtils.saveCurrentConfiguration();
		}
		catch (Exception e) {
			LogUtils.warn(e);
		}
		refresh();
		
	}

	/**
	 * @param file
	 * @return
	 */
	private AWorkspaceTreeNode createFSNodeLinks(File file) {
		AWorkspaceTreeNode node = null;
		if(file.isDirectory()) {
			FolderLinkNode pNode = new FolderLinkNode();
			pNode.setPath(WorkspaceUtils.getWorkspaceRelativeURI(file));
			node = pNode;
		}
		else {
			LinkTypeFileNode lNode = new LinkTypeFileNode();
			lNode.setLinkPath(WorkspaceUtils.getWorkspaceRelativeURI(file));
			node = lNode;
		}
		node.setName(file.getName());
		return node;
	}
	
	public URI getPath() {
		// not needed for workspace root
		return null;
	}
}
