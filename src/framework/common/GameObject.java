package framework.common;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import framework.components.Component;

/**
 * The Class GameObject.
 * parent class of all objects in a game,
 * can be seen as a "component-holder"
 * 
 * @author priyangkar ghosh
 */
public class GameObject {
	/** The transform. */
	// transform of the objects
	public Transform transform = new Transform();

	/** The components a game object has attached. */
	private ArrayList<Component> components = new ArrayList<>();

	/**
	 * constructor for default game object.
	 */
	public GameObject() { }

	/**
	 * Instantiates a new game object.
	 *
	 * @param {Transform} transform - the transform
	 * @summary game object with specified transform
	 */
	public GameObject(Transform transform) {
		this.transform.position = transform.position;
		this.transform.rotation = transform.rotation;
		this.transform.scale = transform.scale;
	}

	/**
	 * Adds the component in its sorted position.
	 *
	 * @param {Component} c - the c
	 */
	public void addComponent(Component c) {
		int index;
		// finds where to add the component
		for (index = 0; index < this.components.size(); index++)
			if (this.components.get(index).type.ordinal() > c.type.ordinal()) break;
		// adds the component to this game object, and components set
		this.components.add(index, c);
		Component.components.add(c);
	}

	/**
	 * Removes the component.
	 *
	 * @param {Component} c - the c
	 * @summary remove specified component from game object
	 */
	public void removeComponent(Component c) {
		this.components.remove(c);
		Component.components.remove(c);
	}

	/**
	 * Gets the component using binary search.
	 *
	 * @param {Component.Type} type - the type
	 * @return the component, or null if the component is not found
	 */
	public Component getComponent(Component.Type type) {
		// inits the low and high
		int low = 0, high = this.components.size() - 1;
		
		// iterative binary search implementation
		while (high - low > 1) {
			// finds the middle
			int mid = (high + low) / 2;
			
			// checks if this index is lower or higher than where we're looking for
			// then updates accordingly
			if (this.components.get(mid).type.ordinal() < type.ordinal())
				low = mid + 1;
			else
				high = mid;
		}
		
		// returns the component if found
		Component temp;
		if ((temp = this.components.get(low)).type == type)
			return temp;
		else if ((temp = this.components.get(high)).type == type)
			return temp;
		return null;
	}

	/**
	 * Gets a list of all the components.
	 *
	 * @return {ArrayList<Component>} the components
	 */
	public ArrayList<Component> getComponents() {
		return this.components;
	}

	/**
	 * Checks for component.
	 *
	 * @param {Component.Type} type - the type
	 * @return returns true if the game object has this component, false otherwise
	 */
	public boolean hasComponent(Component.Type type) {
		return getComponent(type) != null;
	}

	/**
	 * Destroys this game object by removing all components.
	 */
	public void destroy() {		
		while (true) {
			try { for (Component c : this.components) c.close(); break; }
			catch (ConcurrentModificationException e) { continue; }
		}
		
		this.transform = null;
		this.components.clear();
	}
}