package org.genericsystem.cdi;

import org.testng.annotations.Test;

@Test
@SuppressWarnings("unchecked")
public class AppliWebTest extends AbstractTest {

	// public void testAttribute() {
	// // CREATE THE TYPE CAR
	// expressions.evaluateMethodExpression("#{beanGS.newType('Car')}");
	// assert expressions.evaluateMethodExpression("#{beanGS.getType('Car')}", Type.class).getValue().equals("Car");
	//
	// // EDIT THE TYPE CAR
	// expressions.evaluateMethodExpression("#{beanGS.setEditType('Car')}");
	// expressions.evaluateMethodExpression("#{beanGS.setAttribute('Wheel')}");
	// assert expressions.evaluateMethodExpression("#{beanGS.getAttribute('Wheel')}", Attribute.class).getValue().equals("Wheel");
	// assert expressions.evaluateMethodExpression("#{beanGS.getAttributes()}", Snapshot.class).filter(new Filter<Generic>() {
	//
	// @Override
	// public boolean isSelected(Generic element) {
	// return element.getValue().equals("Wheel");
	// }
	// }).size() == 1;
	//
	// // CREATE A INSTANCE OF CAR
	// expressions.evaluateMethodExpression("#{beanGS.newInstance('myCar')}");
	//
	// // EDIT THE INSTANCE
	// expressions.evaluateMethodExpression("#{beanGS.setEditInstance('myCar')}");
	// expressions.evaluateMethodExpression("#{beanGS.setValue('Wheel', 4)}");
	// assert expressions.evaluateMethodExpression("#{beanGS.getValue('Wheel')}", Long.class) == 4;
	// }
	//
	// public void testRelation() {
	// expressions.evaluateMethodExpression("#{beanGS.newType('Car')}");
	// expressions.evaluateMethodExpression("#{beanGS.newType('Color')}");
	//
	// // EDIT THE TYPE CAR
	// expressions.evaluateMethodExpression("#{beanGS.setEditType('Car')}");
	// expressions.evaluateMethodExpression("#{beanGS.setRelation('CarColor', 'Color')}");
	// assert expressions.evaluateMethodExpression("#{beanGS.getRelation('CarColor')}", Relation.class).getValue().equals("CarColor");
	// assert expressions.evaluateMethodExpression("#{beanGS.getRelations()}", Snapshot.class).filter(new Filter<Generic>() {
	//
	// @Override
	// public boolean isSelected(Generic element) {
	// return element.getValue().equals("CarColor");
	// }
	// }).size() == 1;
	// expressions.evaluateMethodExpression("#{beanGS.newInstance('myCar')}");
	//
	// // REQUEST THE TYPE COLOR
	// expressions.evaluateMethodExpression("#{beanGS.setEditType('Color')}");
	// assert expressions.evaluateMethodExpression("#{beanGS.getRelation('CarColor')}", Relation.class).getValue().equals("CarColor");
	// assert expressions.evaluateMethodExpression("#{beanGS.getRelations()}", Snapshot.class).filter(new Filter<Generic>() {
	//
	// @Override
	// public boolean isSelected(Generic element) {
	// return element.getValue().equals("CarColor");
	// }
	// }).size() == 1;
	// expressions.evaluateMethodExpression("#{beanGS.newInstance('red')}");
	//
	// // EDIT THE INSTANCE OF CAR
	// expressions.evaluateMethodExpression("#{beanGS.setEditType('Car')}");
	// expressions.evaluateMethodExpression("#{beanGS.setEditInstance('myCar')}");
	// expressions.evaluateMethodExpression("#{beanGS.setLink('CarColor', 'myCarRed', 'red')}");
	// assert expressions.evaluateMethodExpression("#{beanGS.getLink('CarColor')}", Link.class).getValue().equals("myCarRed");
	// assert expressions.evaluateMethodExpression("#{beanGS.getLinks('CarColor')}", Snapshot.class).filter(new Filter<Generic>() {
	//
	// @Override
	// public boolean isSelected(Generic element) {
	// return element.getValue().equals("myCarRed");
	// }
	// }).size() == 1;
	//
	// // REQUEST THE INSTANCE OF COLOR
	// expressions.evaluateMethodExpression("#{beanGS.setEditType('Color')}");
	// expressions.evaluateMethodExpression("#{beanGS.setEditInstance('red')}");
	// assert expressions.evaluateMethodExpression("#{beanGS.getLink('CarColor')}", Link.class).getValue().equals("myCarRed");
	// assert expressions.evaluateMethodExpression("#{beanGS.getLinks('CarColor')}", Snapshot.class).filter(new Filter<Generic>() {
	//
	// @Override
	// public boolean isSelected(Generic element) {
	// return element.getValue().equals("myCarRed");
	// }
	// }).size() == 1;
	// }

}
