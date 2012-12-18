package eu.compassresearch.core.typechecker;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ANilExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.visitor.TypeCheckerDefinitionVisitor;
import org.overture.typechecker.visitor.TypeCheckerExpVisitor;
import org.overture.typechecker.visitor.TypeCheckerPatternVisitor;

import eu.compassresearch.ast.analysis.QuestionAnswerCMLAdaptor;
import eu.compassresearch.ast.expressions.ABracketedExp;
import eu.compassresearch.ast.expressions.AUnresolvedPathExp;
import eu.compassresearch.core.typechecker.api.CmlTypeChecker;
import eu.compassresearch.core.typechecker.api.TypeIssueHandler;

/**
 * The OvertureRootCMLAdapter functions as a root visitor for Overture Visitor.
 * This visitor delegates all typecal Overture type checking to Overture and
 * diverts control to the VanillaCmlTypeChecker for cases that are special to
 * CML.
 * 
 * 
 * @author rwl
 * 
 */
@SuppressWarnings("serial")
public class OvertureRootCMLAdapter extends
		QuestionAnswerCMLAdaptor<org.overture.typechecker.TypeCheckInfo, PType> {

	private TypeCheckerExpVisitor overtureExpressionVisitor;
	private TypeCheckerDefinitionVisitor overtureDefinitionVisitor;
	private TypeCheckerPatternVisitor overturePatternVisitor;
	private CmlTypeChecker parent;
	private TypeIssueHandler issueHandler;

	public OvertureRootCMLAdapter(CmlTypeChecker cmlTC,
			TypeIssueHandler issueHandler) {
		overtureDefinitionVisitor = new TypeCheckerDefinitionVisitor(this);
		overtureExpressionVisitor = new TypeCheckerExpVisitor(this);
		overturePatternVisitor = new TypeCheckerPatternVisitor(this);
		parent = cmlTC;
		this.issueHandler = issueHandler;
	}

	
	
	@Override
	public PType caseAAssignmentDefinition(AAssignmentDefinition node,
			TypeCheckInfo question) throws AnalysisException {
		return escapeFromOvertureContext(node, question);
	}



	private PType escapeFromOvertureContext(INode node,
			org.overture.typechecker.TypeCheckInfo question) {
		try {
			return node.apply((VanillaCmlTypeChecker) parent, question);
		} catch (AnalysisException e) {
			e.printStackTrace();
		}
		return escapeFromOvertureContext(node, question);
	}

	@Override
	public PType defaultPExp(PExp node, TypeCheckInfo question)
			throws AnalysisException {
		return node.apply(overtureExpressionVisitor, question);
	}

	@Override
	public PType defaultPDefinition(PDefinition node, TypeCheckInfo question)
			throws AnalysisException {
		try {
			eu.compassresearch.core.typechecker.CmlTypeCheckInfo cmlEnv = CmlTCUtil.getCmlEnv(question);
			if (cmlEnv != null) question.contextSet(eu.compassresearch.core.typechecker.CmlTypeCheckInfo.class, cmlEnv);
			PType type = node.apply(overtureDefinitionVisitor, question);
			if (cmlEnv != null) question.contextRem(eu.compassresearch.core.typechecker.CmlTypeCheckInfo.class);
			return type;
		} catch (TypeCheckException e)
		{
			return issueHandler.addTypeError(node, e.getMessage());	
		}
	}

	@Override
	public PType caseAVariableExp(AVariableExp node, TypeCheckInfo question)
			throws AnalysisException {
		return escapeFromOvertureContext(node, question);
	}
		
	

	@Override
	public PType caseANilExp(ANilExp node, TypeCheckInfo question)
			throws AnalysisException {
		return escapeFromOvertureContext(node, question);
	}

	@Override
	public PType caseAUnresolvedPathExp(AUnresolvedPathExp node,
			TypeCheckInfo question) throws AnalysisException {

		
		
		return escapeFromOvertureContext(node, question);
	}

	@Override
	public PType caseABracketedExp(ABracketedExp node, TypeCheckInfo question)
			throws AnalysisException {
		return escapeFromOvertureContext(node, question);
	}

	@Override
	public PType defaultINode(INode node, TypeCheckInfo question)
			throws AnalysisException {
		return escapeFromOvertureContext(node, question);
	}

	@Override
	public PType defaultPMultipleBind(PMultipleBind node, TypeCheckInfo question)
			throws AnalysisException {
		
		try {
  		  return node.apply(overturePatternVisitor, question);
		} catch (TypeCheckException e)
		{
			return issueHandler.addTypeError(node, e.getMessage());
		}
	}

}