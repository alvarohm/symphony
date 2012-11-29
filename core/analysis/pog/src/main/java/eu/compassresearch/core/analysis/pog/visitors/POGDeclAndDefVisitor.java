package eu.compassresearch.core.analysis.pog.visitors;

//POG-related imports
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.pog.obligation.FuncPostConditionObligation;
import org.overture.pog.obligation.POContextStack;
import org.overture.pog.obligation.POFunctionDefinitionContext;
import org.overture.pog.obligation.POFunctionResultContext;
import org.overture.pog.obligation.ParameterPatternObligation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.obligation.SubTypeObligation;
import org.overture.typechecker.TypeComparator;

import eu.compassresearch.ast.analysis.QuestionAnswerCMLAdaptor;
import eu.compassresearch.ast.declarations.PDeclaration;
import eu.compassresearch.ast.definitions.AActionDefinition;
import eu.compassresearch.ast.definitions.AActionParagraphDefinition;
import eu.compassresearch.ast.definitions.AChannelNameDefinition;
import eu.compassresearch.ast.definitions.AChannelParagraphDefinition;
import eu.compassresearch.ast.definitions.AChansetDefinition;
import eu.compassresearch.ast.definitions.AChansetParagraphDefinition;
import eu.compassresearch.ast.definitions.AClassParagraphDefinition;
import eu.compassresearch.ast.definitions.AExplicitOperationDefinition;
import eu.compassresearch.ast.definitions.AFunctionParagraphDefinition;
import eu.compassresearch.ast.definitions.AOperationParagraphDefinition;
import eu.compassresearch.ast.definitions.AProcessDefinition;
import eu.compassresearch.ast.definitions.AProcessParagraphDefinition;
import eu.compassresearch.ast.definitions.ATypesParagraphDefinition;
import eu.compassresearch.ast.definitions.AValueParagraphDefinition;
import eu.compassresearch.ast.process.PProcess;
import eu.compassresearch.core.analysis.pog.obligations.CMLPOContextStack;
import eu.compassresearch.core.analysis.pog.obligations.CMLPOFunctionDefinitionContext;
import eu.compassresearch.core.analysis.pog.obligations.CMLProofObligationList;

@SuppressWarnings("serial")
public class POGDeclAndDefVisitor extends

QuestionAnswerCMLAdaptor<POContextStack, ProofObligationList> {

    // Errors and other things are recorded on this guy
    private ProofObligationGenerator parentPOG;

    public POGDeclAndDefVisitor(ProofObligationGenerator parent) {
	this.parentPOG = parent;
    }

    @Override
    public ProofObligationList defaultPDeclaration(PDeclaration node,
	    POContextStack question) throws AnalysisException {
	System.out
		.println("Reached POGDeclAndDefVisitor - defaultPDeclaration");
	System.out.println(node.getClass());
	return new CMLProofObligationList();
    }

    @Override
    public ProofObligationList defaultPDefinition(PDefinition node,
	    POContextStack question) throws AnalysisException {
	System.out.println("Reached POGDeclAndDefVisitor - defaultPDefinition");
	System.out.println(node.getClass());
	return new CMLProofObligationList();
    }

   
    /**
     * 
     * VDM ELEMENT - Types
     * 
     */
    @Override
    public ProofObligationList caseATypesParagraphDefinition(
	    ATypesParagraphDefinition node, POContextStack question)
	    throws AnalysisException {
    	CMLProofObligationList pol = new CMLProofObligationList();
	
    	for (PDefinition def : node.getTypes()) {
    		pol.addAll(def.apply(this, question));
    	}

    	return pol;
    }

    @Override
    public ProofObligationList caseATypeDefinition(ATypeDefinition node,
	    POContextStack question) throws AnalysisException {

    	System.out.println("----------***----------");
		System.out.println("ATypeDefinition");
		System.out.println(node.toString());
		System.out.println("----------***----------");
	
		CMLProofObligationList pol = new CMLProofObligationList();
		AExplicitFunctionDefinition invDef = node.getInvdef();
		
		
		if (invDef != null)
		{
			pol.addAll(invDef.apply(this, question));
		}
		return pol;
    }


    /**
     * 
     * VDM ELEMENT - Values
     * 
     */
    @Override
    public ProofObligationList caseAValueParagraphDefinition(
	    AValueParagraphDefinition node, POContextStack question)
	    throws AnalysisException {
    	
		CMLProofObligationList pol = new CMLProofObligationList();
		LinkedList<PDefinition> list = node.getValueDefinitions();
		
		for (PDefinition def : list) {
		   pol.addAll(def.apply(parentPOG, question));
		}
	
		return pol;
    }

    @Override
    public ProofObligationList caseAValueDefinition(AValueDefinition node,
	    POContextStack question) throws AnalysisException {

    	System.out.println("----------***----------");
		System.out.println("AValueDefinition");
		System.out.println(node.toString());
		System.out.println("----------***----------");
		
		CMLProofObligationList pol = new CMLProofObligationList();
		return pol;
    }


    /**
     * 
     * VDM ELEMENT - Functions
     * 
     */
    @Override
    public ProofObligationList caseAFunctionParagraphDefinition(
	    AFunctionParagraphDefinition node, POContextStack question)
	    throws AnalysisException {
		CMLProofObligationList obligations = new CMLProofObligationList();
	
		for (PDefinition def : node.getFunctionDefinitions()) {
			obligations.addAll(def.apply(parentPOG, question));
		}
	
		return obligations;
    }

    @Override
    public ProofObligationList caseAExplicitFunctionDefinition(
	    AExplicitFunctionDefinition node, POContextStack question)
	    throws AnalysisException {

		CMLProofObligationList obligations = new CMLProofObligationList();
	
		System.out.println("----------***----------");
		System.out.println("caseAExplicitFunctionDefinition");
		System.out.println(node.toString());
		System.out.println("----------***----------");
	
		LexNameList pids = new LexNameList();
	
		// add all defined names from the function parameter list
		for (List<PPattern> patterns : node.getParamPatternList())
			for (PPattern p : patterns)
				for (PDefinition def : p.getDefinitions())
					pids.add(def.getName());
	
		// check for duplicates
		if (pids.hasDuplicates()){
			obligations.add(new ParameterPatternObligation(node, question));
		}
	
		// do proof obligations for the pre-condition
		PExp precondition = node.getPrecondition();
		if (precondition != null) {
		    question.push(new POFunctionDefinitionContext(node, false));
		    obligations.addAll(precondition.apply(parentPOG, question));
		    question.pop();
		}
	
	
		// do proof obligations for the post-condition
		PExp postcondition = node.getPostcondition();
		if (postcondition != null){
			question.push(new POFunctionDefinitionContext(node, false));
			obligations.add(new FuncPostConditionObligation(node, question));
			question.push(new POFunctionResultContext(node));
			obligations.addAll(postcondition.apply(parentPOG, question));
			question.pop();
			question.pop();
		}
		
		// do proof obligations for the function body
		question.push(new CMLPOFunctionDefinitionContext(node, true));
		PExp body = node.getBody();
		int sizeBefore = question.size();
		ProofObligationList pol = body.apply(parentPOG,question);
		obligations.addAll(body.apply(parentPOG, question));
		assert sizeBefore <= question.size();
		
		
		// do proof obligation for the return type
		//if (node.getIsUndefined()
		//		|| !TypeComparator.isSubType(node.getActualResult(), node.getExpectedResult()))
		//{
		//	obligations.add(new SubTypeObligation(node, node.getExpectedResult(), node.getActualResult(), question));
		//}
		question.pop();
	
		return obligations;
	}

    
    @Override
    public ProofObligationList caseAImplicitFunctionDefinition(
	    AImplicitFunctionDefinition node, POContextStack question)
	    throws AnalysisException {
    	
    	System.out.println("----------***----------");
		System.out.println("AImplicitFunctionDefinition");
		System.out.println(node.toString());
		System.out.println("----------***----------");
    	
		CMLProofObligationList obligations = new CMLProofObligationList();
		
		LexNameList pids = new LexNameList();
	
		//check for duplicates? 
		for (APatternListTypePair pltp : node.getParamPatterns()){
			for (PPattern p : pltp.getPatterns()){
				for (PDefinition def : p.getDefinitions())
					pids.add(def.getName());
			}
		}
	
		if (pids.hasDuplicates()){
			obligations.add(new ParameterPatternObligation(node, question));
		}
	
		// if implicit function has a precondition, dispatch for PO checking
		if (node.getPrecondition() != null) {
		    obligations.addAll(node.getPrecondition()
			    .apply(parentPOG, question));
		}
		
		// if has a postcondition should generate SatisfiabilityObligation
		if (node.getPostcondition() != null) {
	    	question.push(new CMLPOFunctionDefinitionContext(node, false));
		    //if there is also a body, should generate a FuncPostConditionObligation
		    if (node.getBody() != null){
				obligations.add(new FuncPostConditionObligation(node, question));
			}
		    //if there is no body should generate SatisfiabilityObligation
		    else{
		    	System.out.println("SatisfiabilityObligation is not working.");
	//	    	obligations.add(new SatisfiabilityObligation(node, question));
		    }
		    question.pop();
		    //dispatch postconditon expression for PO checking
		    question.push(new POFunctionResultContext(node));
			obligations.addAll(node.getPostcondition().apply(parentPOG, question));
			question.pop();
		}
		   
		//if has a body, should check for POs in body
		if (node.getBody() != null)	{
			question.push(new POFunctionDefinitionContext(node, false));
			obligations.addAll(node.getBody().apply(parentPOG, question));
	
			if (node.getIsUndefined()
					|| !TypeComparator.isSubType(node.getActualResult(), node.getType().getResult())){
				obligations.add(new SubTypeObligation(node, node.getType().getResult(), node.getActualResult(), question));
			}
			question.pop();
		}	
		
		return obligations;
    }

    

    /**
     * 
     * VDM ELEMENT - Operations
     * 
     */
    @Override
    public ProofObligationList caseAOperationParagraphDefinition(
	    AOperationParagraphDefinition node, POContextStack question)
	    throws AnalysisException {
    	CMLProofObligationList pol = new CMLProofObligationList();
		for (PDefinition def : node.getOperations()) {
	    	pol.addAll(def.apply(this, question));
		}
		return pol;
    }

    @Override
    public ProofObligationList caseAExplicitOperationDefinition(
	    AExplicitOperationDefinition node, POContextStack question)
	    throws AnalysisException {
    	System.out.println("----------***----------");
		System.out.println("AExplicitOperationDefinition");
		System.out.println(node.toString());
		System.out.println("----------***----------");
		
    	CMLProofObligationList pol = new CMLProofObligationList();

		return pol;
    }

    
    
    /**
     * 
     * CML ELEMENT - Chansets
     * 
     */
    @Override
    public ProofObligationList caseAChansetParagraphDefinition(
	    AChansetParagraphDefinition node, POContextStack question)
	    throws AnalysisException {
	
		LinkedList<AChansetDefinition> subNodes = node.getChansets();
		CMLProofObligationList pol = new CMLProofObligationList();
		
		for (AChansetDefinition d : subNodes) {
		    pol.addAll(d.apply(this, question));
		}
	
		return pol;
    }

    @Override
    public ProofObligationList caseAChansetDefinition(
	    AChansetDefinition node, POContextStack question)
	    throws AnalysisException {
    	
    	System.out.println("----------***----------");
		System.out.println("AChansetDefinition");
		System.out.println(node.toString());
		System.out.println("----------***----------");
		
		CMLProofObligationList pol = new CMLProofObligationList();
	
		return pol;
    }
    
    @Override
     public ProofObligationList caseAChannelParagraphDefinition(
    	AChannelParagraphDefinition node, POContextStack question)
    	throws AnalysisException{
    	
    	CMLProofObligationList pol = new CMLProofObligationList();
    
    	LinkedList<AChannelNameDefinition> cns = node.getChannelNameDeclarations();
    	for (AChannelNameDefinition c : cns){
    		pol.addAll(c.apply(this, question));
    	}
    
    	return pol;
     }
    
    /**
     * 
     * CML ELEMENT - Classes
     * 
     */
    @Override
    public CMLProofObligationList caseAClassParagraphDefinition(
	    AClassParagraphDefinition node, POContextStack question)
	    throws AnalysisException {
	System.out.println("------");
	System.out
		.println("Reached POGDeclAndDefVisitor - caseAClassParagraphDefinition");

	CMLProofObligationList pol = new CMLProofObligationList();

	for (PDefinition def : node.getDefinitions()) {
	    System.out.println("In defn Paragraph Loop: " + def.toString());
	    pol.addAll(def.apply(parentPOG, question));
	}

	return pol;
    }


    /**
     * 
     * CML ELEMENT - Processes
     * 
     */
    @Override
    public ProofObligationList caseAProcessParagraphDefinition(
	    AProcessParagraphDefinition node, POContextStack question)
	    throws AnalysisException {
		System.out.println("- caseAProcessParagraphDefinition - ");
	
		AProcessDefinition pdef = node.getProcessDefinition();
		return pdef.apply(this, question);
    }

    @Override
    public ProofObligationList caseAProcessDefinition(
	    AProcessDefinition node, POContextStack question)
	    throws AnalysisException {
		System.out.println("- caseAProcessDefinition - ");
	
		System.out.println("----------***----------");
		System.out.println(node.toString());
	
		// NEED TO WORK OUT CONTENTS OF PROCESS AND DISPATCH RELEVANT PARTS
		// LinkedList<> lstate = node.getLocalState();
		// for ( s : lstate)
		// {
		// System.out.println("----------***----------");
		// CMLProofObligationList td = s.apply(this, question);
		// System.out.println("----------***----------");
		// }
	
		PProcess pdef = node.getProcess();
		System.out.println("----------***----------");
		ProofObligationList pol = pdef.apply(this, question);
		System.out.println(pdef.toString());
		System.out.println("----------***----------");
	
		// CMLProofObligationList td = d.apply(this, question);
		System.out.println("----------***----------");
	
		return pol;
    }
    
    /**
     * 
     * CML ELEMENT - Actions
     * 
     */
    @Override
    public ProofObligationList caseAActionParagraphDefinition(
	    AActionParagraphDefinition node, POContextStack question)
	    throws AnalysisException {
	System.out.println("------");
	System.out
		.println("Reached POGDeclAndDefVisitor - caseAActionParagraphDefinition");

	LinkedList<AActionDefinition> actions = node.getActions();
	for (AActionDefinition action : actions) {
	    System.out
		    .println("In Action Paragraph Loop: " + action.toString());
	}

	return new CMLProofObligationList();
    }
    //
    // @Override
    // public ProofObligationList caseAProcessParagraphDefinition(
    // AProcessParagraphDefinition node, POContextStack question)
    // throws AnalysisException
    // {
    // System.out.println("- caseAProcessParagraphDefinition - ");
    //
    // AProcessDefinition pdef = node.getProcessDefinition();
    // ProofObligationList pol = pdef.apply(this, question);
    //
    // return new ProofObligationList();
    // }
    //
    // @Override
    // public ProofObligationList caseAProcessDefinition(
    // AProcessDefinition node, POContextStack question)
    // throws AnalysisException
    // {
    // System.out.println("- caseAProcessDefinition - ");
    //
    // System.out.println("----------***----------");
    // System.out.println(node.toString());
    //
    // //NEED TO WORK OUT CONTENTS OF PROCESS AND DISPATCH RELEVANT PARTS
    // //LinkedList<> lstate = node.getLocalState();
    // //for ( s : lstate)
    // //{
    // // System.out.println("----------***----------");
    // // ProofObligationList td = s.apply(this, question);
    // // System.out.println("----------***----------");
    // //}
    //
    // PProcess pdef = node.getProcess();
    // System.out.println("----------***----------");
    // ProofObligationList pol = pdef.apply(this, question);
    // System.out.println(pdef.toString());
    // System.out.println("----------***----------");
    //
    //
    //
    // //ProofObligationList td = d.apply(this, question);
    // System.out.println("----------***----------");
    //
    // return new ProofObligationList();
    // }
    
    //
    // @Override
    // public ProofObligationList caseAChansetParagraphDefinition(
    // AChansetParagraphDefinition node, POContextStack question)
    // throws AnalysisException
    // {
    // System.out.println("- caseAChansetParagraphDefinition -");
    //
    // LinkedList<AChansetDefinition> subNodes = node.getChansets();
    // for (AChansetDefinition d : subNodes)
    // {
    // System.out.println("----------***----------");
    // ProofObligationList td = d.apply(this, question);
    // System.out.println("----------***----------");
    // }
    //
    // return new ProofObligationList();
    // }
    //
    // @Override
    // public ProofObligationList caseAChansetDefinition(
    // AChansetDefinition node, POContextStack question)
    // throws AnalysisException
    // {
    // System.out.println("- caseAChansetDefinition -");
    //
    // System.out.println(node.toString());
    // ProofObligationList list = new ProofObligationList();
    //
    // return list;
    // }
    //
    //
    // @Override
    // public ProofObligationList caseAChannelParagraphDefinition(
    // AChannelParagraphDefinition node, POContextStack question)
    // throws AnalysisException
    // {
    // System.out.println("- caseAChannelParagraphDefinition -");
    //
    //
    // LinkedList<AChannelNameDeclaration> cns = node.getChannelNames();
    // for (AChannelNameDeclaration decl : cns)
    // {
    //
    // System.out.println("----------***----------");
    // System.out.println(decl.toString());
    // CMLProofObligationList typeBack = decl.apply(this, question);
    // System.out.println("----------***----------");
    // }
    //
    // return new CMLProofObligationList();
    // }
}