package eu.compassresearch.core.analysis.modelchecker.graphBuilder;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import eu.compassresearch.core.analysis.modelchecker.graphBuilder.binding.NullBinding;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.event.Event;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.event.Tau;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.process.Process;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.process.Skip;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.process.Stop;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.transition.Transition;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.util.GraphResult;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.util.LinkedListTransition;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.util.Utilities;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.util.Utilities.Constructor;


public class GraphBuilder {
	
	private static int STATE_NUMBER = 1; 
	
	Utilities util;
	public GraphBuilder() {
		util = new Utilities();
	}
	
	
	public LinkedList<Object> loadLTSObjects(String filePath) throws IOException{
		InputStream is = Utilities.class.getResourceAsStream(filePath);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bfReader = new BufferedReader(isr);
		LinkedList<Object> result = new LinkedList<Object>();
		try {
			String line = "";
			while((line = bfReader.readLine() )!= null){
				Constructor c = util.determineConstructor(line);
				if(c != null){
					if (c.equals(Constructor.GivenProc ) ||
					    c.equals(Constructor.ProcDef ) ||
					    c.equals(Constructor.Transition ) )  {
						result.add(util.createObject(line));					
					}
				}
			}
				
		} finally{
			bfReader.close();
		}
		return result;
	}
	
	public LinkedList<Object> loadLTSObjects(StringBuilder content) throws IOException{
		ByteArrayInputStream is = new ByteArrayInputStream(content.toString().getBytes());
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader bfReader = new BufferedReader(isr);
		LinkedList<Object> result = new LinkedList<Object>();
		try {
			String line = "";
			while((line = bfReader.readLine() )!= null){
				Constructor c = util.determineConstructor(line);
				if(c != null){
					if (c.equals(Constructor.GivenProc ) ||
					    c.equals(Constructor.ProcDef ) ||
					    c.equals(Constructor.Transition ) )  {
						Object o = util.createObject(line);
						result.add(o);					
					}
				}
			}
				
		} finally{
			bfReader.close();
			isr.close();
			is.close();
		}
		return result;
	}

	public String generateDot(String input, String property) throws IOException{
		STATE_NUMBER = 1; //resets the state number
		StringBuilder result = new StringBuilder();
		LinkedList<Object> objects = this.loadLTSObjects(input);
		GraphResult graph = new GraphResult();
		if(property.equals(Utilities.DEADLOCK)){
			graph = this.shortestPathToDeadlock(objects);
		}else if(property.equals(Utilities.LIVELOCK)){
			graph = this.shortestPathToLivelock(objects);
			
			
			
			
			
			
			
			
			
		}else if(property.equals(Utilities.NONDETERMINISM)){
			graph = this.shortestPathToNondeterminism(objects);
		}
		writeDotToBuffer(result, graph);
		
		return result.toString();
	}
	public String generateDot(StringBuilder input, String property) throws IOException{
		STATE_NUMBER = 1; //resets the state number
		StringBuilder result = new StringBuilder();
		LinkedList<Object> objects = this.loadLTSObjects(input);
		GraphResult graph = new GraphResult();
		if(property.equals(Utilities.DEADLOCK)){
			graph = this.shortestPathToDeadlock(objects);
		}else if(property.equals(Utilities.LIVELOCK)){
			graph = this.shortestPathToLivelock(objects);
		}else if(property.equals(Utilities.NONDETERMINISM)){
			graph = this.shortestPathToNondeterminism(objects);
		}
		writeDotToBuffer(result, graph);
		
		return result.toString();
	}
	/*
	public String generateDot(String input) throws IOException{
		STATE_NUMBER = 1; //resets the state number
		StringBuilder result = new StringBuilder();
		
		LinkedList<Object> objects = this.loadLTSObjects(input);
		GraphResult graph = this.shortestPathToLivelock(objects);
		
		writeDotToBuffer(result, graph);
		return result.toString();
		
	}
	*/

	/*
	public String generateDot(StringBuilder formulaOutput) throws IOException{
		STATE_NUMBER = 1; //resets the state number
		StringBuilder result = new StringBuilder();
		LinkedList<Object> objects = this.loadLTSObjects(formulaOutput);
		GraphResult graph = this.shortestPathToDeadlock(objects);
		
		writeDotToBuffer(result, graph);
		
		return result.toString();
		
	}
	*/

	private void writeDotToBuffer(StringBuilder result, GraphResult graph) {
		result.append( "digraph { \n ");
		result.append( "rankdir=\"LR\";\n ");
		
		for (State state : graph.getStates()) {
			result.append( state.toString() + " [shape=" + state.getShape() + ",style=filled,fillcolor="+state.getFillCollor()+",label= " +"\"" + state.getNumber() + "\"]\n" );
			
		}
		
		result.append( "\n");
		for (Transition trans : graph.getTransitions()) {
			result.append(((Transition) trans).getSourceState().toString()
					+ "->" + ((Transition) trans).getTargetState().toString() 
					+ " [label= " + "\""
					+ ((Transition) trans).getEvent().toString() + "\"]" + "\n");
			
		}
		
		result.append("\n }");
	}
		
	private String getMainProcessName(LinkedList<Object> objects){
		String result = "";
		for (Object object : objects) {
			if (object instanceof GivenProc) {
				result = ((GivenProc) object).getName();
			}
		}
		return result;
	}
	
	private Process getMainProcessBody(LinkedList<Object> objects) {
		String givenProcName = this.getMainProcessName(objects);
		Process mainProcessBody = null;
		
		for (Object object : objects) {
			if ((object instanceof ProcDef)) {
				if (((ProcDef) object).getName().equals(givenProcName)) {
					mainProcessBody = ((ProcDef) object).getProcess();
				}
			} 

		}
		return mainProcessBody;
	}
	

	private LinkedList<Transition> filterTransitions(LinkedList<Object> objetos){
		LinkedList<Transition> result = new LinkedList<Transition>();
		for (Object o : objetos) {
			if(o instanceof Transition){
				result.add((Transition)o);
			}
		}
		return result;
	}
	
	public State getInitialState(LinkedList<Object> objects){
		State initialState = null;
		
		String mainProcessName = this.getMainProcessName(objects);
		Process mainProcessBody = this.getMainProcessBody(objects);
		for (Object object : objects) {
			if(object instanceof Transition){
				if(((Transition) object).getSourceState().getProcess().equals(mainProcessBody)){
					initialState = ((Transition) object).getSourceState();
				}
			}
		}
		if(initialState == null){
			initialState = new State(1,new NullBinding(),mainProcessName,mainProcessBody);
		}
		return initialState;
	}
	
	
	
	private LinkedList<Transition> getAllTransitionsFrom(LinkedList<Transition> transitions, State sourceState){
		LinkedList<Transition> result = new LinkedList<Transition>();
		
		for (Transition transition : transitions) {
			if(transition.getSourceState().equals(sourceState)){
				result.add(transition);
			}
		}
		
		return result;
	}
	
	
	private LinkedList<Transition> getAllTransitionsTo(LinkedList<Transition> transitions, State targetState){
		LinkedList<Transition> result = new LinkedList<Transition>();
		
		for (Transition transition : transitions) {
			if(transition.getTargetState().equals(targetState)){
				result.add(transition);
			}
		}
		
		return result;
	}
	
	public GraphResult shortestPathToDeadlock(LinkedList<Object> objects){
		GraphResult result = new GraphResult();
		
		//this removes all procdefs and givenproc from the list 
		State initialState = this.getInitialState(objects);
		initialState.setShape("doublecircle");
		
		State realFinalState = null; //this will be the real deadlock state found in the graph
		
		State basicDeadlock = new State(0, new NullBinding(),"Deadlock", new Stop());
		//if(initialState.getProcess() instanceof Stop){
		if(initialState.getProcess().isDeadlock()){
			basicDeadlock = initialState;
			realFinalState = basicDeadlock;
		}
	
		State basicTermination = new State(0, new NullBinding(),"Skip", new Skip());
		if(initialState.getProcess() instanceof Skip){
			basicTermination = initialState;
			realFinalState = basicTermination;
		}
		
		//it is better to see transitions as Transition objects
		//LinkedList<Transition> transitions = new LinkedList<Transition>();
		//for (Object current : objects) {
		//	transitions.add((Transition)current);
		//}
		LinkedList<Transition> transitions = this.filterTransitions(objects);
		
		ArrayDeque<State> toVisit = new ArrayDeque<State>();
		//LinkedList<State> visitedStates = new LinkedList<State>();
		LinkedList<Transition> visitedTransitions = new LinkedList<Transition>(); 
		
		toVisit.addLast(initialState);
		while(toVisit.size() > 0){
			State current = toVisit.pollFirst();
			current.setVisited(true);
			//visitedStates.add(current);
			LinkedList<Transition> transitionsFrom = this.getAllTransitionsFrom(transitions, current);
			if(transitionsFrom.size() == 0){ //if there is outgoing transition
				if (current.equals(basicDeadlock)){ //if the state is basic deadlock
					realFinalState = basicDeadlock;
					break;
				}else if(current.equals(basicTermination)) { //if the state is termination
					//do nothing
				} else {
					//a new deadlock situation (not default) was found
					realFinalState = current;
					break;
				}
			}else{
				for (Transition transition : transitionsFrom) {
					State target = transition.getTargetState();
					
					if(!target.isVisited()){
						toVisit.addLast(target);
					}
					if(!visitedTransitions.contains(transition)){
						visitedTransitions.add(transition);
					}
				}
			}
			
		}
		
		//at the end of this loop visitedTransitions contains transitions used to  
		//reach a deadlock. We must build the path from initial state to the real deadlock state.
		//we use a reverse path construction: from the deadlock state to the first state.  
		LinkedList<Transition> reversePath = new LinkedList<Transition>();
		buildReversePath(reversePath, visitedTransitions, initialState, realFinalState);
		//at the end, reversePath contains the transitions of our interest to build the graph
		//but we need to build a single path from initial state to deadlock
		LinkedList<Transition> singlePath = new LinkedList<Transition>();
		buildSinglePath(reversePath, singlePath, initialState);
		
		//singlePath contains the single path with numbered states. we separate states and transitions
		LinkedList<State> pathStates = this.getSourceStates(singlePath);
		//if(pathStates.size() == 0){
			//realFinalState.setNumber(STATE_NUMBER++);
			//realFinalState.setFillCollor("\"#FF9696\"");
		//}
		LinkedList<State> targetStates = this.getTargetStates(singlePath);
		for (State state : targetStates) {
			if(!pathStates.contains(state)){
				pathStates.add(state);
			}
		}
		
		if(pathStates.size() == 0){
			realFinalState.setFillCollor("\"#FF9696\"");
			pathStates.add(realFinalState);
		}
		result.setStates(pathStates);
		result.setTransitions(singlePath);
		
		
		return result;
	}
	
	public GraphResult shortestPathToNondeterminism(LinkedList<Object> objects){
		GraphResult result = new GraphResult();
		
		//this removes all procdefs and givenproc from the list 
		State initialState = this.getInitialState(objects);
		initialState.setShape("doublecircle");
		
		State nonDeterministicState = null; 
		
		LinkedList<Transition> transitions = this.filterTransitions(objects);
		ArrayDeque<State> toVisit = new ArrayDeque<State>();
		LinkedList<Transition> visitedTransitions = new LinkedList<Transition>(); 
		
		toVisit.addLast(initialState);
		while(toVisit.size() > 0){
			State current = toVisit.pollFirst();
			current.setVisited(true);
			if(this.isNondeterministic(current, transitions)){
				nonDeterministicState = current;
				break;
			}else{
				//vai continuando a BFS at� encontrar um estado que seja nao deterministico
				LinkedList<Transition> transitionsFrom = this.getAllTransitionsFrom(transitions, current);
				for (Transition transition : transitionsFrom) {
					State target = transition.getTargetState();
					
					if(!target.isVisited()){
						toVisit.addLast(target);
					}
					if(!visitedTransitions.contains(transition)){
						visitedTransitions.add(transition);
					}
				}
			}
		}
		
		//at the end of this loop visitedTransitions contains transitions used to  
		//reach the first nondeterminist state and the replicated transitions from it.
		// We must build the path from initial state to the nondeterministic state plus replicated transitions.
		LinkedList<Transition> reversePath = new LinkedList<Transition>();
		buildReversePath(reversePath, visitedTransitions, initialState, nonDeterministicState);
		
		LinkedList<Transition> singlePath = new LinkedList<Transition>();
		buildSinglePath(reversePath, singlePath, initialState);
		
		//adds the replicated transitions from the nondeterministic state
		int[] indexes = this.indexesOfReplicatedTransition(nonDeterministicState, transitions);
		for (int i : indexes) {
			singlePath.add(transitions.get(i));
		}
		
		//singlePath contains the single path with numbered states. we separate states and transitions
		LinkedList<State> pathStates = this.getSourceStates(singlePath);
		//if(pathStates.size() == 0){
			//realFinalState.setNumber(STATE_NUMBER++);
			//realFinalState.setFillCollor("\"#FF9696\"");
		//}
		LinkedList<State> targetStates = this.getTargetStates(singlePath);
		for (State state : targetStates) {
			if(!pathStates.contains(state)){
				pathStates.add(state);
			}
		}
		
		//if(pathStates.size() == 0){
		//	nonDeterministicState.setFillCollor("\"#FF9696\"");
		//	pathStates.add(nonDeterministicState);
		//}
		result.setStates(pathStates);
		result.setTransitions(singlePath);
		
		
		return result;
	}
	
	private HashMap<Event,LinkedList<Transition>> createMap(LinkedList<Transition> transitions){
		HashMap<Event,LinkedList<Transition>> map = new HashMap<Event,LinkedList<Transition>>();
		for (Transition transition : transitions) {
			if(map.containsKey(transition.getEvent())){
				map.get(transition.getEvent()).add(transition);
			}else{
				LinkedList<Transition> list = new LinkedList<Transition>();
				list.add(transition);
				map.put(transition.getEvent(), list);
			}
			
		}
		return map;
	}
	
	private boolean isNondeterministic(State state, LinkedList<Transition> transitions){
		boolean result = false;
		LinkedList<Transition> transFrom = this.getAllTransitionsFrom(transitions, state);
		HashMap<Event,LinkedList<Transition>> map = this.createMap(transFrom);

		for (Event key : map.keySet()) {
			if(map.get(key).size() > 1){
				LinkedList<Transition> transSameEvent = map.get(key);
				LinkedList<State> targetStates = this.getTargetStates(transSameEvent);
				for (int i = 0; i < targetStates.size() - 1; i++) {
					for (int j = i; j < targetStates.size(); j++) {
						LinkedList<Event> firstEvents = this.getAllEvents(this.getAllTransitionsFrom(transitions, targetStates.get(i)));
						LinkedList<Event> secondEvents = this.getAllEvents(this.getAllTransitionsFrom(transitions, targetStates.get(j)));
						result = !firstEvents.containsAll(secondEvents)  || firstEvents.contains(new Tau());
						if(result){
							return result;
						}
					}
				}
			}
		}

			return result;
	}
	private LinkedList<Event> getAllEvents(LinkedList<Transition> transitions) {
		LinkedList<Event> result = new LinkedList<Event>();
		for (Transition transition : transitions) {
			result.add(transition.getEvent());
		}
		
		return result;
	}
	
	private boolean checkRefusals(	LinkedList<Transition> replicatedTransitions, 
									LinkedList<Transition> originalTransitions){
		boolean result = false;
		LinkedList<State> targetStates = this.getTargetStates(replicatedTransitions);
		while(targetStates.size() > 0){
			State current = targetStates.removeFirst();
			LinkedList<Transition> from = this.getAllTransitionsFrom(originalTransitions, current);
			for (State otherState : targetStates) {
				LinkedListTransition fromOtherState = new LinkedListTransition(this.getAllTransitionsFrom(originalTransitions, otherState));
				for (Transition currFrom : from) {
					if(!fromOtherState.containsByEvent(currFrom, 0)){
						result = true;
						return result;
					}
				}
			}
		}
		return result;
	}
	
	private int[] indexesOfReplicatedTransition(State state, LinkedList<Transition> transitions){
		int[] indexesOf = new int[0];
		
		LinkedListTransition outTransitions = new LinkedListTransition(this.getAllTransitionsFrom(transitions, state));
		
		for (int i = 0; i < outTransitions.size(); i++) {
			Transition current = outTransitions.get(i);
			indexesOf = outTransitions.indexesOf(current);
			if(indexesOf.length > 1){
				break;
			}
		}
		
		return indexesOf;
	}
	
	private void getCicles(State state, LinkedList<Transition> transitions,
			LinkedList<Transition> visitedTrans,
			LinkedList<LinkedList<Transition>> cicles) {
		
		LinkedList<Transition> auxList = new LinkedList<Transition>();
		LinkedList<Transition> transFrom = this.getAllTransitionsFrom(transitions, state);
		if (transFrom.size() > 0) {
			for (Transition transition : transFrom) {
				if (isCicle(visitedTrans, transition.getTargetState())) {// IS
																			// CICLE
					auxList.addAll(visitedTrans);
					auxList.add(transition);
					cicles.add(auxList);

				} else {
					visitedTrans.add(transition);
					getCicles(transition.getTargetState(), transitions,visitedTrans, cicles);
					visitedTrans.removeLast();
				}
			}

		}
	}
	
	private boolean isCicleOfTau(LinkedList<Transition> cicle){
		LinkedList<Transition> auxList = new LinkedList<Transition>();
		boolean result = false;
		for (Transition transition : cicle) {
			if (transition.getEvent().equals(new Tau())) {
				auxList.add(transition);
				result  = this.isCicle(auxList, transition.getSourceState());
				if(result)
					return result;
			}		
		}
		return result;
		
	}
		
	
	private boolean isCicle(LinkedList<Transition> transitions,State current){
		boolean result = false;
		if(transitions.size() != 0){
			for (int i = 0; i < transitions.size(); i++) {
				result = transitions.get(i).getTargetState().equals(current);
				if(result)
					return result || transitions.getFirst().getSourceState().equals(current) ;
			}
		}
		
		return result;
		
	}
	
	private LinkedList<Transition> getShortPath(LinkedList<LinkedList<Transition>> cicles) {
		LinkedList<Transition> result = cicles.getFirst(); 
		for (LinkedList<Transition> cicle : cicles) {
			if (cicle.size() > result.size()) {
				result = cicle;
			}
		}
		
		return result;
	}

	public GraphResult shortestPathToLivelock(LinkedList<Object> objects){
		GraphResult result = new GraphResult();
		//this removes all procdefs and givenproc from the list 
		State initialState = this.getInitialState(objects);
		LinkedList<LinkedList<Transition>> cicles = new LinkedList<LinkedList<Transition>>();
		LinkedList<Transition> transitions = this.filterTransitions(objects);		
		LinkedList<Transition> visitedTransitions = new LinkedList<Transition>(); 
		this.getCicles(initialState, transitions, visitedTransitions, cicles);
		
		for (LinkedList<Transition> cicle : cicles) {
			if (!this.isCicleOfTau(cicle)) {
				cicles.remove(cicle);
			}
		}
		
		LinkedList<Transition> shortCicle = this.getShortPath(cicles); 	
		initialState = shortCicle.getFirst().getSourceState();
		State finalState = shortCicle.getLast().getSourceState();
		
		LinkedList<Transition> reversePath = new LinkedList<Transition>();
		buildReversePath(reversePath, shortCicle, initialState,finalState);
		//but we need to build a single path from initial state to deadlock
		LinkedList<Transition> singlePath = new LinkedList<Transition>();
		buildSinglePath(reversePath, singlePath, initialState);
		
		//singlePath contains the single path with numbered states. we separate states and transitions
		LinkedList<State> pathStates = this.getSourceStates(singlePath);
		if(pathStates.size() == 0){
			shortCicle.getLast().getTargetState().setNumber(STATE_NUMBER++);
		}
		LinkedList<State> targetStates = this.getTargetStates(singlePath);
		for (State state : targetStates) {
			if(!pathStates.contains(state)){
				pathStates.add(state);
			}
		}
		
		if(pathStates.size() == 0){
			pathStates.add(shortCicle.getLast().getTargetState());
		}
		result.setStates(pathStates);
		result.setTransitions(singlePath);
		
		return result;
	}

	
	private void buildReversePath(	LinkedList<Transition> reversePath, 
									LinkedList<Transition> transitions, 
									State initialState, State finalState){

		State currentState = finalState;
		if(currentState != null){
			if(!currentState.equals(initialState)){
				LinkedList<Transition> transitionsTo = this.getAllTransitionsTo(transitions,finalState);
				for (Transition transition : transitionsTo) {
					reversePath.add(transition);
					transitions.remove(transition);
				}
				LinkedList<State> previousStates = this.getSourceStates(transitionsTo);
				for (State previousState : previousStates) {
					buildReversePath(reversePath, transitions, initialState, previousState);
				}
			}
		}
	}
	
	private void buildSinglePath(LinkedList<Transition> transitions,LinkedList<Transition> singlePath, State initialState){
		
		LinkedList<Transition> currentTransitions = this.getAllTransitionsFrom(transitions,initialState);
		initialState.setNumber(STATE_NUMBER++);
		
		if(currentTransitions.size() > 0){
			
			Transition currentTransition = currentTransitions.getFirst();
			currentTransition.setSourceState(initialState);
			singlePath.add(currentTransition);
			State newInitialState = currentTransition.getTargetState();
			currentTransitions.remove(currentTransition);
			transitions.remove(currentTransition);
			transitions.removeAll(currentTransitions); //for optimization
						
			buildSinglePath(transitions, singlePath, newInitialState);
		}else{ //initialState is a state withour outgoing transitions = deadlock
			initialState.setFillCollor("\"#FF9696\"");
		}
		//at the end singlePath contains a single path
	}
	
	private LinkedList<State> getSourceStates(LinkedList<Transition> transitions){
		LinkedList<State> result = new LinkedList<State>();
		for (Transition transition : transitions) {
			result.add(transition.getSourceState());
		}
		return result;
	}
	
	private LinkedList<State> getTargetStates(LinkedList<Transition> transitions){
		LinkedList<State> result = new LinkedList<State>();
		for (Transition transition : transitions) {
			result.add(transition.getTargetState());
		}
		return result;
	}
	
	
	public static void main(String[] args) throws IOException {
		GraphBuilder gb = new GraphBuilder();
		String filePath = "/examples/action-nondet.facts.ND.txt";
		String dotCode = gb.generateDot(filePath,Utilities.NONDETERMINISM);
		System.out.println(dotCode);
	}
	
	
//	public static void main(String[] args) throws IOException {
	//	Buffer bf = new Buffer();
	//	List<Object> objs = bf.listObjects("TC01.D.facts.txt");
	//	System.out.println(bf.firstState(objs));
//	}
	

}
