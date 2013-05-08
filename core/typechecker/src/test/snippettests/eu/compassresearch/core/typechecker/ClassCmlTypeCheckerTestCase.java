package eu.compassresearch.core.typechecker;

import java.util.Collection;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test regarding testing classes are placed here.
 * 
 * Snippet tests are for small CML-fragments that approximates units of 
 * the type checker.
 * 
 * @author rwl
 *
 */

@RunWith(value = Parameterized.class)
public class ClassCmlTypeCheckerTestCase extends AbstractTypeCheckerTestCase{
	
	static {
		//0//
		add("class A = begin end class B = begin end class C is subclass of A = begin end");
	}
	
	
	
	
	
	

	@Parameters
	public static Collection<Object[]> parameter() {
		return testData.get(ClassCmlTypeCheckerTestCase.class);
	}
	
	public ClassCmlTypeCheckerTestCase(String cmlSource, boolean parsesOk,
			boolean typesOk, String[] errorMessages) {
		super(cmlSource, parsesOk, typesOk, errorMessages);
	}


}
