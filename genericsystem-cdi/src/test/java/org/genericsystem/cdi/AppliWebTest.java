package org.genericsystem.cdi;

import org.genericsystem.generic.Relation;
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

	public void testRelation() {
		Relation carColor = expressions.evaluateMethodExpression("#{getCacheWithMemoryEngine.newType('Car').setRelation(getCacheWithMemoryEngine, 'carColor', getCacheWithMemoryEngine.newType('Color'))}", Relation.class);
		// expressions.evaluateMethodExpression(
		// "#{getCacheWithMemoryEngine.newType('Car').newInstance(getCacheWithMemoryEngine, 'myBmw').setLink(getCacheWithMemoryEngine, carColor, 'myBmwRed', getCacheWithMemoryEngine.newType('Color').newInstance(getCacheWithMemoryEngine, 'red'))}",
		// Link.class);
	}

	// public void testRelation() {
	// ELContext elContext = expressions.getELContext();
	// ExpressionFactory expressionFactory = expressions.getExpressionFactory();
	//
	// Cache cache = expressions.evaluateValueExpression("#{getCacheWithMemoryEngine}", Cache.class);
	// ValueExpression valueExpression = expressionFactory.createValueExpression(elContext, "#{cache}", Cache.class);
	// System.out.println("getExpressionString : " + valueExpression.getExpressionString());
	// System.out.println("isReadOnly : " + valueExpression.isReadOnly(elContext));
	// // System.out.println("getValue : " + valueExpression.getValue(elContext));
	// valueExpression.setValue(elContext, cache);
	// // ValueExpression valueExpression = expressionFactory.createValueExpression(cache, Cache.class);
	// // Type vehicle = expressions.evaluateMethodExpression("#{" + valueExpression.getExpressionString() + ".newType('Vehicle')}", Type.class);
	// //
	// // Type color = expressions.evaluateMethodExpression("#{getCacheWithMemoryEngine.newType('Color')}", Type.class);
	// // MethodExpression vehicleExpression = expressions.getExpressionFactory().createMethodExpression(expressions.getELContext(), "#{getCacheWithMemoryEngine.newType('Vehicle')}", Type.class, new Class[] { String.class });
	// // MethodExpression colorExpression = expressions.getExpressionFactory().createMethodExpression(expressions.getELContext(), "#{getCacheWithMemoryEngine.newType('Color')}", Type.class, new Class[] { String.class });
	//
	// }
}
