package org.genericsystem.cdi;

import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AppliWebTest extends AbstractTest {

	public void technicalTest() {
		assert expressions.evaluateValueExpression("#{getCacheWithMemoryEngine}") != null;
		assert expressions.evaluateValueExpression("#{getCacheWithPersistentEngine}") != null;
		Type vehicle = expressions.evaluateMethodExpression("#{getCacheWithMemoryEngine.newType('Vehicle')}", Type.class);
		Type vehicle2 = expressions.evaluateMethodExpression("#{getCacheWithMemoryEngine.newType('Vehicle')}", Type.class);
		assert vehicle == vehicle2;
	}

}
