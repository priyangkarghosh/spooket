package adt;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;

import framework.math.AABB;
import framework.math.Vector;

/**
 * Dynamic AABB tree, used for fast collision detection.
 * based off of article by AzureFromTheTrenches:
 * https://www.azurefromthetrenches.com/introductory-guide-to-aabb-tree-collision-detection/
 * 
 * @author priyangkar ghosh
 */
public class AABBTree {

	/**  Additional "fat"/margin in aabb. */
	private Vector aabbFattener;

	/** The root. */
	private TreeNode<AABB> root = null;

	/** The leaves. */
	private HashMap<AABB, TreeNode<AABB>> leaves = new HashMap<>();

	/**
	 * Instantiates a new AABB tree.
	 *
	 * @param {Vector} fat - the fat
	 */
	public AABBTree(Vector fat) {
		this.aabbFattener = fat;
	}

	/**
	 * Insert leaf.
	 *
	 * @param {AABB} aabb - the aabb
	 */
	public void insertLeaf(AABB aabb) {
		// if the root is null, this aabb should become the root
		if (this.root == null) {
			this.root = new TreeNode<>(aabb);
			this.leaves.put(aabb, this.root);
			return;
		}

		// loops to find sibling node
		TreeNode<AABB> temp = this.root;
		while (!temp.isLeaf()) {
			// holds requires "unions" of the aabbs
			AABB[] unions = new AABB[] {
					temp.left.value.union(aabb),
					temp.right.value.union(aabb),
					temp.value.union(aabb)
			};

			// areas of the current aabbs and unions
			double[] areas = new double[] {
					temp.value.area(),
					temp.left.value.area(),
					temp.right.value.area(),
					unions[0].area(),
					unions[1].area(),
					unions[2].area(),
			};

			// calculates the "costs" of creating a
			// a) new branch, b) traversing left, or, c) traversing right
			// the cost is essentially the additional surface area added
			// by adding the aabb to the current branch
			double branchCost = 2 * areas[5];
			double minPushCost = 2 * (areas[5] - areas[0]);

			double leftCost = minPushCost;
			if (temp.left.isLeaf()) leftCost += areas[3];
			else leftCost += areas[3] - areas[1];

			double rightCost = minPushCost;
			if (temp.left.isLeaf()) rightCost += areas[4];
			else rightCost += areas[4] - areas[2];

			// creates a new node here if it has the lowest cost
			if (branchCost < leftCost && branchCost < rightCost) break;

			// traverses left or right depending on which has a lower cost
			if (leftCost < rightCost) temp = temp.left;
			else temp = temp.right;
		}

		TreeNode<AABB> sibling = temp;
		TreeNode<AABB> oldParent = sibling.parent;
		TreeNode<AABB> leaf = new TreeNode<>(aabb);

		// the new parents aabb will be the union of the leaf and sibling
		TreeNode<AABB> newParent =
				new TreeNode<>(
						leaf.value.union(sibling.value), oldParent
		);

		// the new parents children will be the sibling and leaf
		newParent.left = sibling;
		newParent.right = leaf;

		// the sibling and leaf's parent will be the new parent
		sibling.parent = newParent;
		leaf.parent = newParent;

		// if there is no old parent, it must be the root
		if (oldParent == null)
			this.root = newParent;

		// replaces where the sibling used to be with the new parent
		else if (oldParent.left == sibling)
			oldParent.left = newParent;
		else
			oldParent.right = newParent;

		// adds the aabb to the hash map of leaves
		this.leaves.put(aabb, leaf);

		// fixes the tree
		fixTree(leaf.parent);
	}

	/**
	 * Removes an aabb from the tree.
	 *
	 * @param {AABB} leaf - the leaf
	 */
	public void removeLeaf(AABB leaf) {
		// gets the corresponding node and removes it
		this.removeLeaf(leaves.get(leaf));
	}

	/**
	 * Removes the leaf using node.
	 *
	 * @param {TreeNode<AABB>} leaf - the leaf
	 */
	private void removeLeaf(TreeNode<AABB> leaf) {
		// should not be possible but there just in case
		if (!leaf.isLeaf()) return;

		// removes the leaf from the hash map
		this.leaves.remove(leaf.value);

		// if the leaf is the root, then set the root to null
		if (leaf == this.root) {
			this.root = null;
			return;
		}
		
		if (leaf.parent == null) return;

		// finds the leafs parent, grandparent, and sibling
		TreeNode<AABB> parent = leaf.parent;
		TreeNode<AABB> grandparent = parent.parent;
		TreeNode<AABB> sibling = parent.left == leaf ? parent.right : parent.left;

		if (grandparent != null) {
			// sets the grandparents corresponding child to the sibling
			if (grandparent.left == parent)
				grandparent.left = sibling;
			else
				grandparent.right = sibling;

			// sets the siblings parent to the grandparent
			sibling.parent = grandparent;

			// fixes the tree
			fixTree(grandparent);
		}

		else {
			// otherwise the root turns into the sibling so the parent must be null
			this.root = sibling;
			sibling.parent = null;
		}

		leaf.parent = null;
	}

	/**
	 * Updates a leaf.
	 *
	 * @param {TreeNode<AABB>} leaf - the leaf
	 */
	private void updateLeaf(TreeNode<AABB> leaf) {
		// if the leaf is the root, or its parent contains it, then leaf remains unchanged
		if ((leaf.parent == null) || leaf.parent.value.contains(leaf.value, this.aabbFattener))
			return;

		// otherwise removes the leaf, and reinserts it
		removeLeaf(leaf);
		insertLeaf(leaf.value);
	}

	/**
	 * updates the tree using bfs
	 * it cant just loop through the leaves map because of
	 * concurrent modification exception.
	 */
	public void updateTree() {
		Stack<TreeNode<AABB>> stack = new Stack<>();

		// adds the root the stack
		stack.push(root);

		// loops while stack is not empty
		while (!stack.empty()) {
			TreeNode<AABB> subtree = stack.pop();
			if (subtree == null) continue;

			// updates the leaf if the subtree is a leaf
			if (subtree.isLeaf())
				updateLeaf(subtree);
			else {
				// otherwise adds children to the stack
				// the node must have a left and right child if its not a leaf
				stack.push(subtree.left);
				stack.push(subtree.right);
			}
		}
	}

	/**
	 * gets the aabb's in the tree which overlaps with the given aabb.
	 *
	 * @param {AABB} aabb - the aabb
	 * @return {ArrayList} return the overlapping aabbs
	 */
	public ArrayList<AABB> overlaps(AABB aabb) {
		ArrayList<AABB> pairs = new ArrayList<>();
		Stack<TreeNode<AABB>> stack = new Stack<>();

		stack.push(this.root);
		while (!stack.empty()) {
			TreeNode<AABB> subtree = stack.pop();
			if (subtree == null || subtree.value == aabb)
				continue;

			if (subtree.value.overlap(aabb)) {
				if (subtree.isLeaf())
					pairs.add(subtree.value);

				else {
					if (subtree.left != null)
						stack.push(subtree.left);
					if (subtree.right != null)
						stack.push(subtree.right);
				}
			}
		}

		return pairs;
	}

	/**
	 * Fixes tree by making sure each node contains its children.
	 *
	 * @param {TreeNode<AABB>} node - the node
	 */
	private void fixTree(TreeNode<AABB> node) {
		while (node != null) {
			node.value = node.left.value.union(node.right.value);
			node = node.parent;
		}
	}

	/**
	 * Renders the tree.
	 *
	 * @param {Graphics2D} g2d - the graphics object
	 */
	public void render(Graphics2D g2d) {
		Stack<TreeNode<AABB>> stack = new Stack<>();
		g2d.setColor(new Color(125, 5, 12, 25));

		stack.push(root);
		while (!stack.empty()) {
			TreeNode<AABB> subtree = stack.pop();
			if (subtree == null) continue;

			subtree.value.render(g2d);

			if (!subtree.isLeaf()) {
				stack.push(subtree.left);
				stack.push(subtree.right);
			}
		}
	}
	
	/**
	 * Deletes the tree.
	 */
	public void clear() {
		this.root = null;
	}
}
