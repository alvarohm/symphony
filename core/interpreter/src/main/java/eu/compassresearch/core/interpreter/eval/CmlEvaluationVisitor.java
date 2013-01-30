package eu.compassresearch.core.interpreter.eval;

import org.overture.ast.analysis.AnalysisException;

import eu.compassresearch.ast.actions.PAction;
import eu.compassresearch.ast.process.PProcess;
import eu.compassresearch.core.interpreter.cml.CmlBehaviourSignal;
import eu.compassresearch.core.interpreter.runtime.CmlContext;

public class CmlEvaluationVisitor extends AbstractEvaluationVisitor {

	AbstractEvaluationVisitor actionEvalVisitor = new ActionEvaluationVisitor();
	AbstractEvaluationVisitor processEvalVisitor = new ProcessEvaluationVisitor();
	
	@Override
	public void init(ControlAccess controlAccess) {
		super.init(controlAccess);
		actionEvalVisitor.init(controlAccess);
		processEvalVisitor.init(controlAccess);
	}
	
	@Override
	public CmlBehaviourSignal defaultPAction(PAction node, CmlContext question)
			throws AnalysisException {
		
		return node.apply(actionEvalVisitor,question);
	}
	
	@Override
	public CmlBehaviourSignal defaultPProcess(PProcess node, CmlContext question)
			throws AnalysisException {
		
		return node.apply(processEvalVisitor,question);
	}
	
}
