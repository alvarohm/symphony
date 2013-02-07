package eu.compassresearch.core.interpreter.runtime;

import org.overture.ast.lex.LexLocation;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.ObjectValue;

public class CmlContextFactory {

	public static Context newContext(LexLocation location, String title, Context outer)
	{
		Context context = new Context(location, title, outer);
		context.setThreadState(null, CPUValue.vCPU);
		return context;
	}
	
	public static ObjectContext newObjectContext(LexLocation location,String title,Context outer,ObjectValue self)
	{
		ObjectContext objectContext = new ObjectContext(location, title, outer, self);
		objectContext.setThreadState(null, CPUValue.vCPU);
		return objectContext;
	}
}