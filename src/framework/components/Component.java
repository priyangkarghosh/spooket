package framework.components;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;

import framework.common.GameObject;
import game.Game;

/**
 * The abstract Component parent class.
 * 
 * @author priyangkar ghosh
 */
public abstract class Component {
	/** The components present in this scene. */
	public static HashSet<Component> components = new HashSet<>();
	
	/**
	 * Updates each component in the components array.
	 */
	public static void refresh() {
		try {
			for (Component c : components)  {
				try { c.update(); }
				catch (NullPointerException e) { }
			}
		}

		catch (ConcurrentModificationException e) { }
	}

	/**
	 * Resets the component, by removing all components.
	 */
	public synchronized static void reset() {
		// uses a while loop here so the loop is continued even if 
		// there's a concurrent modification exception
		while (true) {
			try { 
				// loops using an iterator so concurrent modification exception
				// isn't thrown while removing from the list
				for (Iterator<Component> i = components.iterator(); i.hasNext();) {
				    Component component = i.next();
				    component.close();
				    i.remove();
				}
				break;
			}
			catch (ConcurrentModificationException e) { 
				continue;
			}
		}		
	}

	/**
	 * The different types of Components.
	 */
	public static enum Type {
		/** The rigidbody component. */
		RIGIDBODY,

		/** The character controller component. */
		CHARACTER_CONTROLLER,

		/** The renderer component. */
		RENDERER,

		/** The trigger controller component. */
		TRIGGER_COLLIDER, 
	}

	/** The type of the component. */
	public Type type;

	/** The host. */
	protected GameObject host;

	/**
	 * Instantiates a new component.
	 *
	 * @param {GameObject} host - the host
	 * @param {Type} type - the type
	 */
	public Component(GameObject host, Type type) {
		this.type = type;
		this.host = host;
		
		// adds component to host, if it isn't a prefab
		if (host != Game.getPrefabObject())
			host.addComponent(this);
	}
	
	/**
	 * Abstract update method.
	 */
	public abstract void update();
	
	/**
	 * Closes a component.
	 */
	public synchronized void close() {
		Component.components.remove(this);
	}

	/**
	 * Gets the host.
	 *
	 * @return {GameObject} the host
	 */
	public GameObject getHost() {
		return this.host;
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return Objects.hash(host, type);
	}

	/**
	 * Equals.
	 *
	 * @param {Object} obj - the obj
	 * @return true, if successful
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Component)) return false;
		Component other = (Component) obj;
		return Objects.equals(this.host, other.host) && this.type == other.type;
	}
}
