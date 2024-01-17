package adt;

/**
 * Generic tree class.
 *
 * @param <T> the generic type
 * @author priyangkar ghosh
 */
public class Tree<T extends Comparable<T>> {
	/** The root node. */
	private TreeNode<T> root;

	/**
	 * Insert new node.
	 *
	 * @param {T} value - the value
	 */
	public void insert(T value) {
		this.root = insert(value, this.root);
	}

	/**
	 * Recursively inserts new node.
	 *
	 * @param {T} value - the value
	 * @param {TreeNode<T>} current - the current
	 * @return {TreeNode<T>} the updated subtree
	 */
	private TreeNode<T> insert(T value, TreeNode<T> current) {
		if (current == null)
			return new TreeNode<>(value);
		
		// decides which subtree to traverse
		if (value.compareTo(current.value) < 0)
			current.left = insert(value, current.left);
		else
			current.right = insert(value, current.right);

		return current;
	}

	/**
	 * Removes a specified node.
	 *
	 * @param {T} node - the node
	 */
	public void remove(T node) {
		this.root = remove(node, this.root);
	}

	/**
	 * Recursively removes node.
	 *
	 * @param {T} node - the node
	 * @param {TreeNode<T>} current - the current
	 * @return {TreeNode<T>} the updated subtree
	 */
	private TreeNode<T> remove(T node, TreeNode<T> current) {
		if (current == null) return null;
		
		// compares the node to the current node
		int branch = node.compareTo(current.value);
		
		// recurses corresponding tree
		if (branch < 0) current.left = remove(node, current.left);
		else if (branch > 0) current.right = remove(node, current.right);
		
		// if the two values are equal, then it removes the node
		else {
			if (current.left == null) return current.right;
			else if (current.right == null) return current.left;

			current.value = getMin(current.right);
			current.right = remove(current.value, current.right);
		}

		return current;
	}

	/**
	 * Checks if the tree contains a node.
	 *
	 * @param {T} value - the value
	 * @return {boolean} true, if the tree contains the value
	 */
	public boolean contains(T value) {
		Stack<TreeNode<T>> stack = new Stack<>();
		
		// uses bfs to search through the tree
		stack.push(this.root);
		while (!stack.empty()) {
			TreeNode<T> subtree = stack.pop();
			if (subtree == null) continue;
			
			// returns if the value is equal to the search value
			if (subtree.value.equals(value)) return true;
			else if (value.compareTo(subtree.value) < 0) stack.push(subtree.left);
			else stack.push(subtree.right);
		}
		return false;
	}
	
	/**
	 * Finds the minimum value in the whole tree.
	 *
	 * @return {T} the minimum value
	 */
	public T getMin() {
		if (this.root == null) return null;	
		return getMin(this.root);
	}
	
	/**
	 * Finds the minimum value, starting at specific node.
	 *
	 * @param {TreeNode<T>} start - the start
	 * @return {T} the minimum value
	 */
	private T getMin(TreeNode<T> start) {
		if (start == null) return null;

		while (start.left != null)
			start = start.left;
		return start.value;
	}
	
	/**
	 * Finds the maximum value in the whole tree.
	 *
	 * @return {T} the maximum value
	 */
	public T getMax() {
		if (this.root == null) return null;	
		return getMax(this.root);
	}

	/**
	 * Finds the maximum value, starting at specific node.
	 *
	 * @param {TreeNode<T>} start - the start
	 * @return {T} the maximum value
	 */
	public T getMax(TreeNode<T> start) {
		if (this.root == null) return null;

		while (start.right != null)
			start = start.right;
		return start.value;
	}

	/**
	 * Empties the tree.
	 */
	public void clear() {
		this.root = null;
	}

	/**
	 * Gets the root of the tree.
	 *
	 * @return {TreeNode<T>} the root
	 */
	public TreeNode<T> getRoot() {
		return this.root;
	}
}
