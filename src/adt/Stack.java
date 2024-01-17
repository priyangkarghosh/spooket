package adt;

import java.util.Objects;

/**
 * Generic stack implementation.
 *
 * @param <T> the generic type
 * @author priyangkar ghosh
 */
public class Stack<T> {

	/** Top of the stack. */
	private Node<T> top;

	/** The size of the stack. */
	private int size = 0;

	/**
	 * Instantiates a new stack.
	 */
	public Stack() {
		this.top = null;
	}

	/**
	 * Empty.
	 *
	 * @return {boolean} true, if empty
	 */
	public boolean empty() {
		return top == null;
	}

	/**
	 * Peek.
	 *
	 * @return {T} the value of the top of the stack
	 */
	public T peek() {
		return top.value;
	}

	/**
	 * Pop value from stack.
	 *
	 * @return {T} the value popped
	 */
	public T pop() {
		// if the size is 0, then nothing can be popped
		if (size == 0)
			return null;

		else {
			size--;

			// sets the top value to what came before it
			T temp = this.top.value;
			this.top = this.top.link;
			return temp;
		}
	}

	/**
	 * Push value on stack.
	 *
	 * @param {T} value - the value
	 */
	public void push(T value) {
		// adds a new node which points to the previous "top"
		this.top = new Node<>(value, top);
		size++;
	}

	/**
	 * Size of the stack.
	 *
	 * @return {int} size of stack
	 */
	public int size() {
		return size;
	}

	/**
	 * Empties the stack.
	 *
	 */
	public void clear() {
		this.top = null;
		this.size = 0;
	}

	/**
	 * Hash code.
	 *
	 * @return {int} the hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(size, top);
	}

	/**
	 * Equals.
	 *
	 * @param {Object} obj - the obj
	 * @return {boolean} true, if equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Stack))
			return false;
		Stack<?> other = (Stack<?>) obj;
		return this.size == other.size && Objects.equals(this.top, other.top);
	}
}
