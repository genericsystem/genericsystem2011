package org.genericsystem.generic;


/**
 * A Relation <br/>
 * Link any Type
 * 
 * @author Nicolas Feybesse
 * @author Michael Ory
 */
public interface Relation extends Attribute, Link {

	/**
	 * Enable cascade remove for the component position.
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Relation> T enableCascadeRemove(int componentPos);

	/**
	 * Disable cascade remove for the component position
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return Return this.
	 */
	<T extends Relation> T disableCascadeRemove(int componentPos);

	/**
	 * Returns true if the cascade remove enabled for the component position
	 * 
	 * @param componentPos
	 *            The component position implicated by the constraint.
	 * @return true if the cascade remove enabled for the component position
	 */
	boolean isCascadeRemove(int componentPos);

}
