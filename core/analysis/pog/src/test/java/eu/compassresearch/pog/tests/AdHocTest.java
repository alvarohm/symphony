package eu.compassresearch.pog.tests;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;

import eu.compassresearch.core.analysis.pog.obligations.CmlProofObligation;
import eu.compassresearch.core.analysis.pog.obligations.CmlProofObligationList;
import eu.compassresearch.core.analysis.pog.utility.PogPubUtil;
import eu.compassresearch.pog.tests.utils.TestInputHelper;
/**
 * Simple test class to play around with the POG without having
 * to waste time running the IDE tool.
 * @author ldc
 *
 */
public class AdHocTest {

	/**
	 * Main test method. Runs a test on the adhoc/test.cml file. Place whatever 
	 * model you wish to test there. That file is treated as a sandbox so
	 * do not expect anything you place there to remain there.
	 * @throws AnalysisException
	 * @throws IOException
	 */
	@Test
	public void testCmlPog() throws AnalysisException, IOException {
		String file = "src/test/resources/adhoc/adhoc.cml";
		System.out.println("Processing " + file);
		IProofObligationList poList = buildPosFromFile(file);

		System.out.println("\n------------------------");
		System.out.println("POS FOR: "+file);
				
		for (IProofObligation po : poList) {
			System.out.println("------------------------");	
			StringBuilder pretty = new StringBuilder();
			pretty.append("Full Name: " + po.getUniqueName());			
			pretty.append("\nIsabelle Name: " + po.getIsaName());
			pretty.append("\nPO: " + getPreamble(po));
			pretty.append(po.getValueTree().toString());
			System.out.println(pretty.toString());

		}
		System.out.println("--------DONE-----------");

	}

	private String getPreamble(IProofObligation po)
	
	{
		StringBuilder sb = new StringBuilder();
		if (po instanceof CmlProofObligation)
		{
			sb.append(((CmlProofObligation) po).cmltype.toString());
		}
		else {
			sb.append(po.getKind().toString());
		}
		
		sb.append("\n");
		return sb.toString();
	}

	private IProofObligationList buildPosFromFile(String file) throws IOException,
			AnalysisException {

		IProofObligationList poList = new CmlProofObligationList();
		List<INode> ast = TestInputHelper.getAstFromName(file);
		poList = PogPubUtil.generateProofObligations(ast);
		return poList;
	}


}
