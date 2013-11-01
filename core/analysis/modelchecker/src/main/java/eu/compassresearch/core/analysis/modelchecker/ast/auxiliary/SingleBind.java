package eu.compassresearch.core.analysis.modelchecker.ast.auxiliary;

import eu.compassresearch.core.analysis.modelchecker.ast.MCNode;
import eu.compassresearch.core.analysis.modelchecker.ast.types.MCPCMLType;
import eu.compassresearch.core.analysis.modelchecker.graphBuilder.type.Type;

public class SingleBind {
	protected String variableName;
	protected MCPCMLType variableValue;
	
	public SingleBind(String variableName, MCPCMLType variableValue) {
		this.variableName = variableName;
		this.variableValue = variableValue;
	}
	public String getVariableName() {
		return variableName;
	}
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	public SingleBind copy(){
		SingleBind copy = new SingleBind(variableName, variableValue.copy());
		return copy;
	}
	@Override
	public boolean equals(Object obj) {
		boolean resp = false;
		if(obj instanceof SingleBind){
			resp = this.variableName.equals(((SingleBind) obj).variableName) 
					&& this.variableValue.equals(((SingleBind) obj).variableValue);
		}
		return resp;
	}
	@Override
	public String toString() {
		return "(" + variableName + "," + variableValue.toString() + ")";
	}
	
	public String toFormula(String option) {
		String result = "SingleBind";
		switch (option) {
		case MCNode.DEFAULT:
			result = "SingleBind(\"" + variableName + "\"," + variableValue.toFormula(option) + ")";
			break;

		case MCNode.NAMED:
			result = "SingleBind(\"" + variableName + "\"," + variableName + ")";
			break;
			
		case MCNode.GENERIC:
			result = "SingleBind(\"" + variableName + "\"," + variableValue.toFormula(option) + ")";
			break;

		default:
			break;
		}
		
		return result;
	}
	public MCPCMLType getVariableValue() {
		return variableValue;
	}
	public void setVariableValue(MCPCMLType variableValue) {
		this.variableValue = variableValue;
	}

	
	
}
