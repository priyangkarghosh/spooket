package adt;

import java.util.Objects;

/**
 * Generic queue.
 *
 * @param <T> the generic type
 * @author priyangkar ghosh
 */
public class Queue<T> {

	/** Pointers to the front and back of the queue. */
	public Node<T> front, back;

	/** The size of the queue. */
	private int size = 0;

	/**
	 * Instantiates a new queue.
	 */
	public Queue() {
		this.front = this.back = null;
	}

	/**
	 * Dequeues a node.
	 *
	 * @return {T} the value of the dequeued node
	 */
	public T dequeue() {
		if (size == 0) return null;

		else {
			// decreases the size and sets the link to the next one
			size--;
			Node<T> temp = this.front;
			this.front = this.front.link;
			return temp.value;
		}
	}

	/**
	 * checks if the queue is empty.
	 *
	 * @return {boolean} true, if the queue is empty
	 */
	public boolean empty() {
		return front == back;
	}

	/**
	 * Peek.
	 *
	 * @return {T} the value of the front of the queue
	 */
	public T peek() {
		return front.value;
	}

	/**
	 * Queue another value.
	 *
	 * @param {T} value - the value
	 */
	public void queue(T value) {
		// if this is the first value being added, set front and back
		if (size == 0)
			this.front = this.back = new Node<>(value);
		else {
			// otherwise the back should point to this node
			// and this node should become the new back
			Node<T> temp = new Node<>(value);
			this.back.link = temp;
			this.back = temp;
		}

		// increments the size by 1
		size++;
	}

	/**
	 * Size of the queue.
	 *
	 * @return {int} the size of the queue
	 */
	public int size() {
		return size;
	}

	/**
	 * Empties the queue.
	 *
	 */
	public void clear() {
		this.front = this.back = null;
		this.size = 0;
	}

	/**
	 * Hash code.
	 *
	 * @return {int} the hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(back, front, size);
	}

	/**
	 * Equals.
	 *
	 * @param {Object} obj - the obj
	 * @return {boolean} true, if they are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Queue))
			return false;
		Queue<?> other = (Queue<?>) obj;
		return Objects.equals(this.back, other.back) && Objects.equals(this.front, other.front)
				&& this.size == other.size;
	}
}
