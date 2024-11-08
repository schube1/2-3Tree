public class Tree {

	public Node root;

	public Tree() {
		root = null;
	}

	public boolean insert(int key) {  
		if (root == null) {
			root = new Node(key);
			return true;
		}
		if (hasKey(key)) { // double found
			return false;
		}
		Node newNode = root.insert(key);
		if (newNode != null) {
			root = newNode;

		} 
		return true;

	}

	public boolean hasKey(int key) {	
		if (root == null) {
			return false;
		}

		return root.hasKey(key);

	}

	public int size() {					
		if (root == null) {
			return 0;
		}
		return root.size();

	}

	public int size(int startKey) {		
		if (root == null) {
			return 0;
		}
		Node node = root.findNode(startKey);
		if (node == null) {
			return 0;
		}
		return node.size();



	}

	public int get(int index) { // CCHECK
		// take care of edge cases 1. if root is null
		if (root == null) {
			throw new IndexOutOfBoundsException("the tree is empty -- can't get index"); // will now throw instead of zero for more clarity
		}
		if (index < 0 || index >= size()) { // 2. if the index given is negative or outtabounds
			throw new IndexOutOfBoundsException("the given index is out of bounds"); // maybe throw instead for more accuracy
		}
		// implement this foo
		return root.get(index);
	}

	class Node {

		Node[] children;
		int[] keys;
		int keyCount;
		int childCount;
		int size;

		public Node(int key) {
			children = new Node[4];
			keys = new int[3];
			keys[0] = key;
			childCount = 0;
			keyCount = 1;
			size = 1;
		}

		public Node() {
			children = new Node[4];
			keys = new int[3];
			childCount = 0;
			keyCount = 0;
		}

		public Node insert(int key) {
			int i = 0;
			while (i < keyCount && key > keys[i]) {  // find where in the array to insert
				i++;
			}
			if (i < keyCount && key == keys[i]) { // see if that key already exists
				return null;
			}

			if (isLeaf()) {
				keyInsertLoc(key, i);
				size++;

				if (keyCount <= 2) {
					return null;
				}
				return splitNode();

			}


			Node newNode = children[i].insert(key);
			if (newNode != null) {
				// insert newNode into this // need to make space for the new added key
				for(int j = keyCount -1; j>= i; j-- ) {
					keys[j+1] = keys[j];
				}
				keys[i] = newNode.keys[0];
				keyCount++;
				//also need to make more space for the new child added
				for(int j = childCount -1; j>= i; j-- ) {
					children[j+1] = children[j];
				}
				children[i] = newNode.children[0];
				children[i+1] = newNode.children[1];
				childCount++;

				if (keyCount <= 2) {
					sizeUpdate();
					return null;
				}
				return splitNode(); // 3 keys require a split

			}
			sizeUpdate();
			return null;

		}

		public void sizeUpdate() {
			size = keyCount;
			for(int i =0; i < childCount;i++) {
				size += children[i].size();
			}

		}

		private void keyInsertLoc(int key, int loc) { // if the node u insert into is a leaf simply use i from insert as the location to insert
			for (int i = keyCount - 1; i >= loc; i--) {

				keys[i + 1] = keys[i];
			}
			keys[loc] = key;
			keyCount++;
		}

		private Node splitNode() { // leaf nodes where the midVal will move up);
			int mid = 1; 
			int midVal = keys[mid]; //index 1 will always be the key to split if kept in order
			//midVal will either move up to parent or become new root if its in the currRoot else 
			Node rNode = new Node();
			rNode.keys[0] = keys[2];
			rNode.keyCount = 1;

			if (!isLeaf()) { //only way nonLeaf is split is thru cascade (therefore reassign) - last two og children to become the left and right of Right Node
				rNode.children[0] = children[2];
				rNode.children[1] = children[3];
				rNode.childCount = 2;
			}
			
			
			keyCount = 1;

			if (!isLeaf()) { // now that weve removed the child2 , child3 set to null adjust count
				childCount = 2;

			}
			children[2] = null;
			children[3] = null;
			keys[1] =0;
			keys[2]=0;
			
			sizeUpdate();
			rNode.sizeUpdate();

			if (this == root) {
 
				Node newNode = new Node(midVal);
				newNode.children[0] = this;
				newNode.children[1] = rNode;
				newNode.childCount = 2;
				newNode.keyCount = 1;
				newNode.sizeUpdate();
				return newNode;
			} // need to work on this**

			Node nNode = new Node();

			nNode.keys[0] = midVal;
			nNode.keyCount =1;
			nNode.children[0] = this;
			nNode.children[1] = rNode; // alllocate the right node as the child on the right [1]
			nNode.childCount =2;
			rNode.sizeUpdate();
			return nNode;			

		}

		public boolean hasKey(int key) {
			int i = 0;

			while (i < keyCount && key > keys[i]) {
				i++;
			}
			if (i < keyCount && key == keys[i]) {
				return true;
			}
			if (isLeaf()) {
				return false;
			}

			return children[i].hasKey(key);

		}

		public Node findNode(int key) {
			int i = 0;

			while (i < keyCount && key > keys[i]) {
				i++;
			}
			if (i < keyCount && key == keys[i]) {
				return this;
			}
			if (isLeaf()) {
				return null;
			}
			return children[i].findNode(key);

		}

		public int size() { // should have the root node passed in , set tot to numOfKeys then recursively
			// call all the children and continue adding their numOfKeys to tot
			int tot = keyCount; 
			if (!isLeaf()) {
				for (int i = 0; i < childCount; i++) {
					tot += children[i].size(); 
				}
			}

			return tot;
		}





		public int get(int index) {
			int left =0;
			for(int i = 0; i < keyCount; i++) {
				if(children[i] != null) {
					
					int sizeOfChild = children[i].size();
					if(index < left + sizeOfChild) {
						return children[i].get(index - left);
					}
					left += sizeOfChild;
				} 
				if(index == left) {
					return keys[i];
				} 

				left++;
			}


			if(children[keyCount] != null) {
				int sizeOfChild = children[keyCount].size();
				if(index < sizeOfChild + left) {
					return children[keyCount].get(index - left); 
				}
			}

			throw new IndexOutOfBoundsException("out of the bounds");




		}

		public boolean isLeaf() {
			return childCount == 0;
		}

	}

}

