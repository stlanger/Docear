package org.docear.plugin.services.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.docear.plugin.services.ServiceController;
import org.docear.plugin.services.components.dialog.DocearIRChoiceDialogPanel;
import org.docear.plugin.services.listeners.DocearServiceSettingsDialogListener;
import org.freeplane.core.ui.AFreeplaneAction;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.TextUtils;

public class DocearAllowUploadChooserAction extends AFreeplaneAction {

	public static final String KEY = "docear.allow.upload.action";
	
	public DocearAllowUploadChooserAction() {
		super(KEY);
	}


	private static final long serialVersionUID = 1L;

	
	public void actionPerformed(ActionEvent e) {
		DocearAllowUploadChooserAction.showDialog(false);
	}

	public static boolean showDialog(boolean exitOnCancel) {
		final DocearIRChoiceDialogPanel chooser = new DocearIRChoiceDialogPanel(!exitOnCancel);
		
		ArrayList<JButton> buttonsList = new ArrayList<JButton>();
		buttonsList.add(new JButton(TextUtils.getText("docear.uploadchooser.button.ok")));
		buttonsList.add(new JButton(TextUtils.getText("docear.uploadchooser.button.cancel")));		
		
		JButton[] dialogButtons = buttonsList.toArray(new JButton[] {});	
		
		chooser.integrateButtons(dialogButtons);
		chooser.addActionListener(new DocearServiceSettingsDialogListener());
		
		int result = JOptionPane.showOptionDialog(UITools.getFrame(), chooser, TextUtils.getText("docear.uploadchooser.title"), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, dialogButtons, 1);
		if (result == 0) {			
			ServiceController.getController().setBackupEnabled(chooser.allowBackup());
			ServiceController.getController().setInformationRetrievalCode(chooser.getIrCode());
			return true;
		} 
		else {
			if(exitOnCancel) {
				System.exit(0);
			}
		}
		return false;
		
	}
	
	


}
