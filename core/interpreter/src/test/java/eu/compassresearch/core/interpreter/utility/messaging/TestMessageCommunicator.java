package eu.compassresearch.core.interpreter.utility.messaging;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.node.INode;
import org.overture.interpreter.assistant.InterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.compassresearch.ast.actions.ASkipAction;
import eu.compassresearch.core.interpreter.api.CmlInterpreterState;
import eu.compassresearch.core.interpreter.api.behaviour.CmlBehaviorState;
import eu.compassresearch.core.interpreter.api.behaviour.CmlBehaviour;
import eu.compassresearch.core.interpreter.api.behaviour.CmlTrace;
import eu.compassresearch.core.interpreter.api.events.CmlBehaviorStateObserver;
import eu.compassresearch.core.interpreter.api.events.EventSource;
import eu.compassresearch.core.interpreter.api.events.TraceObserver;
import eu.compassresearch.core.interpreter.api.transitions.CmlTransition;
import eu.compassresearch.core.interpreter.api.transitions.CmlTransitionSet;
import eu.compassresearch.core.interpreter.api.transitions.ObservableTransition;
import eu.compassresearch.core.interpreter.api.transitions.TimedTransition;
import eu.compassresearch.core.interpreter.debug.CmlDbgStatusMessage;
import eu.compassresearch.core.interpreter.debug.CmlInterpreterStateDTO;
import eu.compassresearch.core.interpreter.debug.messaging.Message;
import eu.compassresearch.core.interpreter.debug.messaging.MessageCommunicator;
import eu.compassresearch.core.interpreter.debug.messaging.MessageContainer;
import eu.compassresearch.core.interpreter.utility.Pair;

public class TestMessageCommunicator
{

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testStartingMessage() throws JsonGenerationException,
			JsonMappingException, IOException
	{
		// 1. Convert Java object to JSON format
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		// MessageContainer message = new MessageContainer(new CmlDbgStatusMessage(CmlDbgpStatus.STARTING));
		Message sentMessage = new CmlDbgStatusMessage(new CmlInterpreterStateDTO(null, CmlInterpreterState.INITIALIZED));
		MessageCommunicator.sendMessage(outStream, sentMessage);

		// ObjectMapper mapper = new ObjectMapper();
		// mapper.enableDefaultTyping();
		// mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// mapper.writeValue(out, message);

		System.out.println(outStream.toString());

		ByteArrayInputStream in = new ByteArrayInputStream(outStream.toByteArray());

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		MessageContainer messageContainter = MessageCommunicator.receiveMessage(reader, new MessageContainer(new CmlDbgStatusMessage(CmlInterpreterState.TERMINATED_BY_USER)));

		Message recvMessage = messageContainter.getMessage();

		// MessageContainer msg2 = mapper.readValue(in, MessageContainer.class);

		System.out.println(recvMessage);
	}

	@Test
	public void testStoppingMessage() throws JsonGenerationException,
			JsonMappingException, IOException
	{
		// 1. Convert Java object to JSON format
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		// MessageContainer message = new MessageContainer(new CmlDbgStatusMessage(CmlDbgpStatus.STARTING));
		CmlInterpreterStateDTO status = new CmlInterpreterStateDTO(new CmlBehaviour()
		{

			@Override
			public boolean waiting()
			{
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean started()
			{
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void replaceState(Context context) throws ValueException
			{
				// TODO Auto-generated method stub

			}

			@Override
			public CmlBehaviour parent()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EventSource<TraceObserver> onTraceChanged()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public EventSource<CmlBehaviorStateObserver> onStateChanged()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String nextStepToString()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public BehaviourName getName()
			{
				return new BehaviourName("A");
			}

			@Override
			public long level()
			{
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public CmlTransitionSet inspect()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean hasChildren()
			{
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public CmlTrace getTraceModel()
			{
				CmlTrace trace = new CmlTrace();

				trace.addEvent(new TimedTransition());
				trace.addEvent(new ObservableTransition()
				{

					@Override
					public SortedSet<CmlBehaviour> getEventSources()
					{
						return new TreeSet<CmlBehaviour>();
					}

					// @Override
					// public CmlTransitionSet getAsAlphabet() {
					// // TODO Auto-generated method stub
					// return null;
					// }

					@Override
					public ObservableTransition synchronizeWith(
							ObservableTransition other)
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public boolean isComparable(ObservableTransition other)
					{
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public String toString()
					{
						return "a";
					}

					@Override
					public boolean isSourcesSubset(CmlTransition other)
					{
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public Set<INode> getSourceNodes()
					{
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public int compareTo(CmlTransition o)
					{
						// TODO Auto-generated method stub
						return 0;
					}

					@Override
					public int getTransitionId()
					{
						// TODO Auto-generated method stub
						return 0;
					}

					@Override
					public SortedSet<Integer> getEventSourcesHash()
					{
						// TODO Auto-generated method stub
						return null;
					}
				});

				return trace;
			}

			@Override
			public CmlBehaviorState getState()
			{
				// TODO Auto-generated method stub
				return CmlBehaviorState.FINISHED;
			}

			@Override
			public CmlBehaviour getRightChild()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Pair<INode, Context> getNextState()
			{
				return new Pair<INode, Context>(new ASkipAction(), new Context(new InterpreterAssistantFactory(), new LexLocation(), "test", null));
			}

			@Override
			public CmlBehaviour getLeftChild()
			{
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public long getCurrentTime()
			{
				return 0;
			}

			@Override
			public boolean finished()
			{
				return true;
			}

			@Override
			public boolean deadlocked()
			{
				return false;
			}

			@Override
			public List<CmlBehaviour> children()
			{
				return new LinkedList<CmlBehaviour>();
			}

			@Override
			public int getId()
			{
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public void execute(CmlTransition selectedTransition)
					throws AnalysisException
			{
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isDivergent()
			{
				// TODO Auto-generated method stub
				return false;
			}

//			@Override
//			public void updateName(ILexNameToken name)
//			{
//				// TODO Auto-generated method stub
//
//			}

			@Override
			public int compareTo(CmlBehaviour o)
			{
				// TODO Auto-generated method stub
				return 0;
			}
		}, CmlInterpreterState.FINISHED);
		Message sentMessage = new CmlDbgStatusMessage(status);
		MessageCommunicator.sendMessage(outStream, sentMessage);

		// ObjectMapper mapper = new ObjectMapper();
		// mapper.enableDefaultTyping();
		// mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// mapper.writeValue(out, message);

		System.out.println(outStream.toString());

		ByteArrayInputStream in = new ByteArrayInputStream(outStream.toByteArray());

		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		MessageContainer messageContainter = MessageCommunicator.receiveMessage(reader, new MessageContainer(new CmlDbgStatusMessage(CmlInterpreterState.TERMINATED_BY_USER)));

		Message recvMessage = messageContainter.getMessage();

		// Assert.assertEquals(sentMessage., recvMessage);

		// MessageContainer msg2 = mapper.readValue(in, MessageContainer.class);

		System.out.println(recvMessage);
	}

}
