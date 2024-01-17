package adt;

/**
 * Generic node class for queues and stacks.
 *
 * @param <T> the generic type
 * @author priyangkar ghosh
 */
public class Node<T> {
	/** The value of the node. */
	public T value;

	/** The node this node points to. */
	public volatile Node<T> link;

	/**
	 * Instantiates a new node.
	 *
	 * @param {T} value - the value
	 */
	public Node(T value) {
		this.value = value;
	}

	/**
	 * Instantiates a new node.
	 *
	 * @param {T} value - the value
	 * @param {Node<T>} link - the link
	 */
	public Node(T value, Node<T> link) {
		this.value = value;
		this.link = link;
	}
}