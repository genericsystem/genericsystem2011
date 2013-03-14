package org.genericsystem.cdi;

import org.genericsystem.core.Generic;
import org.genericsystem.core.Snapshot;
import org.genericsystem.core.Snapshot.Filter;
import org.genericsystem.generic.Attribute;
import org.genericsystem.generic.Link;
import org.genericsystem.generic.Relation;
import org.genericsystem.generic.Type;
import org.testng.annotations.Test;

@Test
public class AppliWebTest extends AbstractTest {

	@SuppressWarnings("unchecked")
	public void testAttribute() {
		expressions.evaluateMethodExpression("#{beanGS.newType('Car')}");
		assert expressions.evaluateMethodExpression("#{beanGS.getType('Car')}", Type.class).getValue().equals("Car");
		expressions.evaluateMethodExpression("#{beanGS.setAttribute('Car', 'Wheel')}");
		assert expressions.evaluateMethodExpression("#{beanGS.getAttribute('Car', 'Wheel')}", Attribute.class).getValue().equals("Wheel");
		assert ((Snapshot<Generic>) expressions.evaluateMethodExpression("#{beanGS.getAttributes('Car')}", Snapshot.class)).filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue().equals("Wheel");
			}
		}).size() == 1;
		expressions.evaluateMethodExpression("#{beanGS.newInstance('Car', 'myCar')}");
		assert expressions.evaluateMethodExpression("#{beanGS.getAttribute('myCar', 'Wheel')}", Attribute.class).getValue().equals("Wheel");
		assert ((Snapshot<Generic>) expressions.evaluateMethodExpression("#{beanGS.getAttributes('myCar')}", Snapshot.class)).filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue().equals("Wheel");
			}
		}).size() == 1;
		expressions.evaluateMethodExpression("#{beanGS.setValue('myCar', 'Wheel', 4)}");
		assert expressions.evaluateMethodExpression("#{beanGS.getValue('myCar', 'Wheel')}", Long.class) == 4;
	}

	@SuppressWarnings("unchecked")
	public void testRelation() {
		expressions.evaluateMethodExpression("#{beanGS.newType('Car')}");
		expressions.evaluateMethodExpression("#{beanGS.newType('Color')}");
		expressions.evaluateMethodExpression("#{beanGS.setRelation('Car', 'CarColor', 'Color')}");
		assert expressions.evaluateMethodExpression("#{beanGS.getRelation('Car', 'CarColor')}", Relation.class).getValue().equals("CarColor");
		assert expressions.evaluateMethodExpression("#{beanGS.getRelation('Color', 'CarColor')}", Relation.class).getValue().equals("CarColor");
		assert ((Snapshot<Generic>) expressions.evaluateMethodExpression("#{beanGS.getRelations('Car')}", Snapshot.class)).filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue().equals("CarColor");
			}
		}).size() == 1;
		assert ((Snapshot<Generic>) expressions.evaluateMethodExpression("#{beanGS.getRelations('Color')}", Snapshot.class)).filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue().equals("CarColor");
			}
		}).size() == 1;
		expressions.evaluateMethodExpression("#{beanGS.newInstance('Car', 'myCar')}");
		expressions.evaluateMethodExpression("#{beanGS.newInstance('Color', 'red')}");
		assert expressions.evaluateMethodExpression("#{beanGS.getRelation('myCar', 'CarColor')}", Relation.class).getValue().equals("CarColor");
		assert expressions.evaluateMethodExpression("#{beanGS.getRelation('red', 'CarColor')}", Relation.class).getValue().equals("CarColor");
		assert ((Snapshot<Generic>) expressions.evaluateMethodExpression("#{beanGS.getRelations('myCar')}", Snapshot.class)).filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue().equals("CarColor");
			}
		}).size() == 1;
		assert ((Snapshot<Generic>) expressions.evaluateMethodExpression("#{beanGS.getRelations('red')}", Snapshot.class)).filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue().equals("CarColor");
			}
		}).size() == 1;
		expressions.evaluateMethodExpression("#{beanGS.setLink('myCar', 'CarColor', 'myCarRed', 'red')}");
		assert expressions.evaluateMethodExpression("#{beanGS.getLink('myCar', 'CarColor')}", Link.class).getValue().equals("myCarRed");
		assert expressions.evaluateMethodExpression("#{beanGS.getLinks('red', 'CarColor')}", Link.class).getValue().equals("myCarRed");
		assert ((Snapshot<Generic>) expressions.evaluateMethodExpression("#{beanGS.getLinks('myCar')}", Snapshot.class)).filter(new Filter<Generic>() {

			@Override
			public boolean isSelected(Generic element) {
				return element.getValue().equals("CarColor");
			}
		}).size() == 1;
	}

}
