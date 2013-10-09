package eu.compassresearch.ide.collaboration.messages;

import org.eclipse.ecf.core.identity.ID;

public class NewFileMessage extends BaseMessage
{
	private static final long serialVersionUID = 3464157884650995258L;
	
	private final String filename;
	private final String contents;
	
	public NewFileMessage(ID sendBy, ID sendTo, String filename, String contents)
	{
		super(sendBy, sendTo);
		this.filename = filename;
		this.contents = contents;
	}

	public String getContents()
	{
		return contents;
	}

	public String getFilename()
	{
		return filename;
	}
}


