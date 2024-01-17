package adt;

import java.util.Objects;

/**
 * Generic binary tree node.
 *
 * @param <T> the generic type
 * @author priyangkar ghosh
 */
public class TreeNode<T> {

	/** Value of node. */
	public T value = null;

	/** Pointer to left node. */
	public volatile TreeNode<T> left = null;

	/** Pointer to right node. */
	public volatile TreeNode<T> right = null;

	/** Pointer to parent node. */
	public volatile TreeNode<T> parent = null;

	/**
	 * Instantiates a new tree node.
	 */
	public TreeNode() { }

	/**
	 * Instantiates a new tree node.
	 *
	 * @param {T} value - the value
	 */
	public TreeNode(T value) {
		this.value = value;
	}

	/**
	 * Instantiates a new tree node.
	 *
	 * @param {T} value - the value
	 * @param {TreeNode<T>} parent - the parent
	 */
	public TreeNode(T value, TreeNode<T> parent) {
		this.value = value;
		this.parent = parent;
	}

	/**
	 * Instantiates a new tree node.
	 *
	 * @param {TreeNode<T>} node - the node
	 * @param {TreeNode<T>} parent - the parent
	 */
	public TreeNode(TreeNode<T> node, TreeNode<T> parent) {
		this.value = node.value;
		this.left = node.left;
		this.right = node.right;

		this.parent = parent;
	}

	/**
	 * Checks if this node is a leaf.
	 *
	 * @return true, if both the left and right nodes are null
	 */
	public boolean isLeaf() {
		return this.left == null && this.right == null;
	}

	/**
	 * Hash code.
	 *
	 * @return {int} the hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(value.getClass());
	}

	/**
	 * Equals.
	 *
	 * @param {Object} obj - the obj
	 * @return {boolean} true, if the two are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof TreeNode)) return false;

		TreeNode<T> other = (TreeNode<T>) obj;
		return Objects.equals(value, other.value);
	}
}