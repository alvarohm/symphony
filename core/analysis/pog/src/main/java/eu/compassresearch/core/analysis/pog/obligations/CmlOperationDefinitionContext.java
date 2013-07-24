/*******************************************************************************
	 *
	 *	Copyright (C) 2008 Fujitsu Services Ltd.
	 *
	 *	Author: Nick Battle
	 *
	 *	This file is part of VDMJ.
	 *
	 *	VDMJ is free software: you can redistribute it and/or modify
	 *	it under the terms of the GNU General Public License as published by
	 *	the Free Software Foundation, either version 3 of the License, or
	 *	(at your option) any later version.
	 *
	 *	VDMJ is distributed in the hope that it will be useful,
	 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
	 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 *	GNU General Public License for more details.
	 *
	 *	You should have received a copy of the GNU General Public License
	 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
	 *
	 ******************************************************************************/

package eu.compassresearch.core.analysis.pog.obligations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.AForAllExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexStringToken;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.patterns.AIgnorePattern;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.AStringPattern;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;

import eu.compassresearch.ast.definitions.AImplicitCmlOperationDefinition;

	public class CmlOperationDefinitionContext implements CmlPOContext
	{
		public final ILexNameToken name;
		public final AOperationType deftype;
		public final List<PPattern> paramPatternList;
		public final boolean addPrecond;
		public final PExp precondition;
		public final PDefinition stateDefinition;

		public CmlOperationDefinitionContext(AImplicitCmlOperationDefinition definition,
			boolean precond, PDefinition stateDefinition)
		{
			this.name = definition.getName();
			this.deftype = (AOperationType) definition.getType();
			this.addPrecond = precond;
			this.paramPatternList = getParamPatternList(definition);
			this.precondition = definition.getPrecondition();
			this.stateDefinition = stateDefinition;
		}

		public static List<PPattern> getParamPatternList(AImplicitCmlOperationDefinition definition)
		{
			List<PPattern> plist = new ArrayList<PPattern>();

			for (APatternListTypePair pl: definition.getParameterPatterns() )
			{
				plist.addAll(pl.getPatterns());
			}

			return plist;
		}
		
		@Override
		public String getContext()
		{
			StringBuilder sb = new StringBuilder();

			if (!deftype.getParameters().isEmpty())
			{
				sb.append("forall ");
				String sep = "";
				Iterator<PType> types = deftype.getParameters().iterator();

				for (PPattern p : paramPatternList)
				{
					if (!(p instanceof AIgnorePattern))
					{
						sb.append(sep);
						sb.append(p.toString());
						sb.append(":");
						sb.append(types.next());
						sep = ", ";
					}
				}

				if (stateDefinition != null)
				{
					appendStatePatterns(sb);
				}

				sb.append(" &");

				if (addPrecond && precondition != null)
				{
					sb.append(" ");
					sb.append(precondition);
					sb.append(" =>");
				}
			}

			return sb.toString();
		}
		

		@Override
		public PExp getContextNode(PExp stitch)
		{
			if (!deftype.getParameters().isEmpty())
			{
				AForAllExp forAllExp = new AForAllExp();
				forAllExp.setBindList(makeBinds());

				if (addPrecond && precondition != null)
				{
					AImpliesBooleanBinaryExp impliesExp = new AImpliesBooleanBinaryExp();
					impliesExp.setLeft(precondition);
					impliesExp.setRight(stitch);
					forAllExp.setPredicate(impliesExp);
				} else
				{
					forAllExp.setPredicate(stitch);
				}

				return forAllExp;

			}
			return stitch;

		}
		
		private void appendStatePatterns(StringBuilder sb)
		{
			if (stateDefinition == null)
			{
				return;
			} else if (stateDefinition instanceof AStateDefinition)
			{
				AStateDefinition def = (AStateDefinition) stateDefinition;
				sb.append(", oldstate:");
				sb.append(def.getName().getName());
			} else
			{
				SClassDefinition def = (SClassDefinition) stateDefinition;
				sb.append(", oldself:");
				sb.append(def.getName().getName());
			}
		}
		
		private List<? extends PMultipleBind> makeBinds()
		{
			LinkedList<PMultipleBind> r = new LinkedList<PMultipleBind>();

			ATypeMultipleBind tmBind = new ATypeMultipleBind();
			tmBind.setPlist(paramPatternList);
			tmBind.setType(deftype);
			r.add(tmBind);

			if (stateDefinition != null)
			{
				ATypeMultipleBind tmBind2 = new ATypeMultipleBind();
				AStringPattern pattern = new AStringPattern();

				if (stateDefinition instanceof AStateDefinition)
				{
					AStateDefinition def = (AStateDefinition) stateDefinition;
					tmBind2.setType(def.getType());
					pattern.setValue(new LexStringToken("oldstate", null));
				} else
				{
					SClassDefinition def = (SClassDefinition) stateDefinition;
					tmBind2.setType(def.getType());
					pattern.setValue(new LexStringToken("oldself", null));
				}

				List<PPattern> plist = new LinkedList<PPattern>();
				plist.add(pattern);
				tmBind2.setPlist(plist);
				r.add(tmBind2);

			}

			return r;

		}

		@Override
		public String getName()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isScopeBoundary()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void noteType(PExp exp, PType type)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public PType checkType(PExp exp)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getGUIContext()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getIsabelleContext()
		{
			// TODO Auto-generated method stub
			return null;
		}

		//FIXME implement ast based Context


	}