package eu.compassresearch.core.interpreter.api.values;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.interpreter.values.Value;

public class MultiConstraint implements ValueConstraint
{

	List<ValueConstraint> constraints;
	
	private MultiConstraint()
	{
	}

	public MultiConstraint(List<ValueConstraint> constraints)
	{
		this.constraints = constraints;
	}

	public MultiConstraint(ValueConstraint firstConstraint,
			ValueConstraint secondConstraint)
	{
		this.constraints = new LinkedList<ValueConstraint>();
		this.constraints.add(firstConstraint);
		this.constraints.add(secondConstraint);
	}

	@Override
	public boolean isValid(Value val) throws AnalysisException
	{

		for (ValueConstraint vc : this.constraints)
		{
			if (!vc.isValid(val))
			{
				return false;
			}
		}

		return true;
	}

}
