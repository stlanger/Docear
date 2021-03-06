/**
 * author: Marcel Genzmehr
 * 22.07.2011
 */
package org.freeplane.plugin.workspace.nodes;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.apache.commons.io.FileUtils;
import org.freeplane.core.util.LogUtils;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.mode.mindmapmode.MModeController;
import org.freeplane.plugin.workspace.WorkspaceController;
import org.freeplane.plugin.workspace.WorkspaceUtils;
import org.freeplane.plugin.workspace.components.menu.WorkspacePopupMenu;
import org.freeplane.plugin.workspace.components.menu.WorkspacePopupMenuBuilder;
import org.freeplane.plugin.workspace.dnd.WorkspaceTransferable;
import org.freeplane.plugin.workspace.event.WorkspaceActionEvent;
import org.freeplane.plugin.workspace.model.AWorkspaceTreeNode;

/**
 * 
 */
public class FolderFileNode extends DefaultFileNode {
	private static final Icon FOLDER_OPEN_ICON = new ImageIcon(DefaultFileNode.class.getResource("/images/16x16/folder-orange_open.png"));
	private static final Icon FOLDER_CLOSED_ICON = new ImageIcon(DefaultFileNode.class.getResource("/images/16x16/folder-orange.png"));
	private static final Icon NOT_EXISTING = new ImageIcon(DefaultFileNode.class.getResource("/images/16x16/folder-orange-missing.png"));
	
	private static final long serialVersionUID = 1L;
	
	private static WorkspacePopupMenu popupMenu = null;
	
	/***********************************************************************************
	 * CONSTRUCTORS
	 **********************************************************************************/
	
	/**
	 * @param name
	 * @param file
	 */
	public FolderFileNode(String name, File file) {
		super(name, file);
	}

	/***********************************************************************************
	 * METHODS
	 **********************************************************************************/
	public void delete() {
		delete(getFile());
	}
	
	private void delete(File file) {
		if(file.isDirectory()) {
			for(File child : file.listFiles()) {
				delete(child);
			}			
		}
		file.delete();	
	}
	
	public boolean setIcons(DefaultTreeCellRenderer renderer) {
		if(getFile() == null || !getFile().exists()) {
			renderer.setLeafIcon(NOT_EXISTING);
			renderer.setOpenIcon(NOT_EXISTING);
			renderer.setClosedIcon(NOT_EXISTING);
			return true;
		}
		renderer.setOpenIcon(FOLDER_OPEN_ICON);
		renderer.setClosedIcon(FOLDER_CLOSED_ICON);
		renderer.setLeafIcon(FOLDER_CLOSED_ICON);
		return true;
	}
	
	public AWorkspaceTreeNode clone() {
		FolderFileNode node = new FolderFileNode(getName(), getFile());
		return clone(node);
	}
	
	public void refresh() {
		try {
			if (getFile().isDirectory()) {
				WorkspaceUtils.getModel().removeAllElements(this);
				WorkspaceController.getController().getFilesystemMgr().scanFileSystem(this, getFile());
				WorkspaceUtils.getModel().reload(this);				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void initializePopup() {
		if (popupMenu == null) {
					
			if (popupMenu == null) {			
				popupMenu = new WorkspacePopupMenu();
				WorkspacePopupMenuBuilder.addActions(popupMenu, new String[] {
						WorkspacePopupMenuBuilder.createSubMenu(TextUtils.getRawText("workspace.action.new.label")),
						"workspace.action.node.new.folder",
						"workspace.action.file.new.mindmap",
						//WorkspacePopupMenuBuilder.SEPARATOR,
						//"workspace.action.file.new.file",
						WorkspacePopupMenuBuilder.endSubMenu(),
						WorkspacePopupMenuBuilder.SEPARATOR,
						"workspace.action.node.open.location",
						WorkspacePopupMenuBuilder.SEPARATOR,
						"workspace.action.node.cut",
						"workspace.action.node.copy", 
						"workspace.action.node.paste",
						WorkspacePopupMenuBuilder.SEPARATOR,
						"workspace.action.node.rename",
						"workspace.action.node.remove",
						"workspace.action.file.delete",
						WorkspacePopupMenuBuilder.SEPARATOR,
						"workspace.action.node.refresh"		
				});
			}
		}
	}
	
	public WorkspacePopupMenu getContextMenu() {
		if (popupMenu == null) {
			initializePopup();
		}
		return popupMenu;
	}
	
	private void processWorkspaceNodeDrop(List<AWorkspaceTreeNode> nodes, int dropAction) {
		try {	
			File targetDir = getFile();
			for(AWorkspaceTreeNode node : nodes) {
				if(node instanceof DefaultFileNode) {					
					if(targetDir != null && targetDir.isDirectory()) {
						if(dropAction == DnDConstants.ACTION_COPY) {
							((DefaultFileNode) node).copyTo(targetDir);
						} 
						else if(dropAction == DnDConstants.ACTION_MOVE) {							
							File oldFile = ((DefaultFileNode) node).getFile();
							if(oldFile.equals(targetDir)) return;
							((DefaultFileNode) node).moveTo(targetDir);
							File newFile = new File(targetDir, ((DefaultFileNode) node).getName());
							AWorkspaceTreeNode parent = node.getParent();							
							WorkspaceUtils.getModel().cutNodeFromParent(node);
							parent.refresh();
							WorkspaceUtils.getModel().nodeMoved(node, oldFile, newFile);
						}
					}
				}
				else if(node instanceof LinkTypeFileNode) {
					File srcFile = WorkspaceUtils.resolveURI(((LinkTypeFileNode) node).getLinkPath());
					if(targetDir != null && targetDir.isDirectory()) {
						FileUtils.copyFileToDirectory(srcFile, targetDir);
						if(dropAction == DnDConstants.ACTION_MOVE) {
							AWorkspaceTreeNode parent = node.getParent();
							WorkspaceUtils.getModel().cutNodeFromParent(node);
							parent.refresh();
							WorkspaceUtils.getModel().nodeMoved(node, srcFile, new File(targetDir, srcFile.getName()));
						}
					}
				}
			}			
		}
		catch (Exception e) {
			LogUtils.warn(e);
		}
		WorkspaceController.getController().getExpansionStateHandler().addPathKey(this.getKey());
		refresh();
	}
	
	private void processFileListDrop(List<File> files, int dropAction) {
		try {
			File targetDir = getFile();			
			for(File srcFile : files) {
				if(srcFile.isDirectory()) {
						FileUtils.copyDirectoryToDirectory(srcFile, targetDir);
				}
				else {
					FileUtils.copyFileToDirectory(srcFile, targetDir, true);
				}				
			}
			refresh();
		}
		catch (Exception e) {
			LogUtils.warn(e);
		}
		refresh();
	}
	
	private void processUriListDrop(List<URI> uris, int dropAction) {
		try {
			File targetDir = getFile();			
			for(URI uri : uris) {
				File srcFile = new File(uri);
				if(srcFile == null || !srcFile.exists()) {
					continue;
				}
				if(srcFile.isDirectory()) {
					FileUtils.copyDirectoryToDirectory(srcFile, targetDir);
				}
				else {
					FileUtils.copyFileToDirectory(srcFile, targetDir, true);
				}				
			}
			refresh();
		}
		catch (Exception e) {
			LogUtils.warn(e);
		}
		refresh();		
	}
	
	public boolean acceptDrop(DataFlavor[] flavors) {
		for(DataFlavor flavor : flavors) {
			if(WorkspaceTransferable.WORKSPACE_FILE_LIST_FLAVOR.equals(flavor)
				|| WorkspaceTransferable.WORKSPACE_URI_LIST_FLAVOR.equals(flavor)
				|| WorkspaceTransferable.WORKSPACE_NODE_FLAVOR.equals(flavor)
			) {
				return true;
			}
		}
		return false;
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
		
	
	/***********************************************************************************
	 * REQUIRED METHODS FOR INTERFACES
	 **********************************************************************************/
	public void handleAction(WorkspaceActionEvent event) {	
		if(event.getType() == WorkspaceActionEvent.WSNODE_CHANGED) {
			Controller.getCurrentController().selectMode(MModeController.MODENAME);
			if(rename(event.getBaggage().toString())) {
				setName(event.getBaggage().toString());
				if(event.getSource() instanceof AWorkspaceTreeNode) {
					Enumeration<AWorkspaceTreeNode> childs = ((AWorkspaceTreeNode)event.getSource()).children();
					while(childs.hasMoreElements()) {
						AWorkspaceTreeNode node = ((AWorkspaceTreeNode) childs.nextElement());
						if(node instanceof DefaultFileNode) {
							((DefaultFileNode)node).relocateFile(getFile());							
						}
					}
				}
			}
			else {
				LogUtils.warn("Could not rename File("+getName()+") to File("+event.getBaggage()+")");
			}
			
		} 
		else if(event.getType() == WorkspaceActionEvent.WSNODE_OPEN_DOCUMENT) {
//			try {
//				Controller.getCurrentController().getViewController().openDocument(Compat.fileToUrl(getFile()));
//				event.consume();
//			} catch (Exception e) {
//				LogUtils.warn(e);
//			}
		}
		else {
			super.handleAction(event);
		}
	}
	
	
}
