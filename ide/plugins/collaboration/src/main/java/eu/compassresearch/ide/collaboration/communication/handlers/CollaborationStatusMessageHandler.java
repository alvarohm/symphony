package eu.compassresearch.ide.collaboration.communication.handlers;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.sync.SerializationException;
import org.eclipse.swt.widgets.Display;

import eu.compassresearch.ide.collaboration.Activator;
import eu.compassresearch.ide.collaboration.communication.MessageProcessor;
import eu.compassresearch.ide.collaboration.communication.messages.CollaborationStatusMessage;
import eu.compassresearch.ide.collaboration.datamodel.CollaborationDataModelManager;
import eu.compassresearch.ide.collaboration.ui.menu.CollaborationDialogs;

public class CollaborationStatusMessageHandler extends BaseMessageHandler<CollaborationStatusMessage>
{

	public CollaborationStatusMessageHandler(MessageProcessor processor)
	{
		super(CollaborationStatusMessage.class, processor);
	}

	@Override
	public void process(final CollaborationStatusMessage msg)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				CollaborationDataModelManager modelMgm = Activator.getDefault().getDataModelManager();
				ID senderID = msg.getSenderID();
				
				try
				{
					modelMgm.collaboratorJoining(senderID, msg.isJoining(), msg.getProjectID(), true);
				} catch (SerializationException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				String notification;
				if(msg.isJoining()){
					notification = "Joined Collaboration";
				} else{
					notification = "Declined collaboration"; 
				}
				CollaborationDialogs.getInstance().displayNotificationPopup(senderID.getName(), notification);
			}
		});		
	}
}