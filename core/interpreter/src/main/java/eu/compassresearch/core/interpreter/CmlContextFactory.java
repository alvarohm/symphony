package eu.compassresearch.core.interpreter;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.debug.DBGPReader;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ObjectContext;
import org.overture.interpreter.runtime.StateContext;
import org.overture.interpreter.values.CPUValue;
import org.overture.interpreter.values.ObjectValue;

import eu.compassresearch.core.interpreter.api.CmlInterpreter;
import eu.compassresearch.core.interpreter.assistant.CmlInterpreterAssistantFactory;
import eu.compassresearch.core.interpreter.debug.CmlDBGPReader;

class CmlContextFactory
{
	final static IInterpreterAssistantFactory factory = new CmlInterpreterAssistantFactory();

	public static final String PARAMETRISED_PROCESS_CONTEXT_NAME = "Parametrised process context";

	private static DBGPReader dbgp;

	/**
	 * This creates a normal context which
	 * 
	 * @param location
	 *            The location the scope context covers
	 * @param title
	 *            A description of the context
	 * @param outer
	 *            The parent context of this context
	 * @return
	 */
	public static Context newContext(ILexLocation location, String title,
			Context outer)
	{
		Context context = new Context(factory, location, title, outer);
		context.setThreadState(newDBGPReader(), CPUValue.vCPU);
		return context;
	}

	public static DBGPReader newDBGPReader()
	{
		if (dbgp == null)
		{
			throw new RuntimeException("DBGPReader not configures!");
		}
		return dbgp;
	}

	public static void configureDBGPReader(CmlInterpreter interpreter)
	{
		dbgp = new CmlDBGPReader(interpreter);
	}

	/**
	 * This creates a ObjectContext which is used for operation and function calls. It makes sure that actions and
	 * expression inside this scope can only access values from it own scope and the global context. Meaning that it
	 * jumps to the the global top level context.
	 * 
	 * @param location
	 * @param title
	 * @param outer
	 * @param self
	 * @return
	 */
	public static ObjectContext newObjectContext(ILexLocation location,
			String title, Context outer, ObjectValue self)
	{
		ObjectContext objectContext = new ObjectContext(factory, location, title, outer, self);
		objectContext.setThreadState(newDBGPReader(), CPUValue.vCPU);
		return objectContext;
	}

	public static StateContext newStateContext(ILexLocation location,
			String title)
	{
		StateContext stateContext = new StateContext(factory, location, title);
		stateContext.setThreadState(newDBGPReader(), CPUValue.vCPU);
		return stateContext;
	}
}
