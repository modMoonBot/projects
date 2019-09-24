package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {

	/**
	 * Root node
	 */
	TagNode root=null;

	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;

	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}

	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public String tagToString(String input){
		return input.substring(1,input.length()-1);
	}

	public String endTagToString(String input){
		return input.substring(2,input.length()-1);
	}

	public void build() {
		/** COMPLETE THIS METHOD **/

		TagNode first = new TagNode (tagToString(sc.nextLine()), null, null);
		root = first;
		Stack<TagNode> parents= new Stack<TagNode>();
		parents.push(root);

		while(sc.hasNextLine()){
			String temp = sc.nextLine();
			if (temp.charAt(0)== '<'&& temp.charAt(1)!='/'){ //tag case
				temp = tagToString(temp);
				TagNode tempNode = new TagNode(temp, null, null);
				if (!parents.isEmpty()){
					if (parents.peek().firstChild == null){
						parents.peek().firstChild = tempNode;
					}else {
						TagNode currSib = parents.peek().firstChild;
						while(currSib.sibling != null){
							currSib = currSib.sibling;
						}
						currSib.sibling = tempNode;
					}
				}
				parents.push(tempNode);

			} else if (temp.charAt(0) == '<' && temp.charAt(1)== '/') { //tag end case
				parents.pop();

			} else if (Character.isLetter(temp.charAt(0))){ //string case
				TagNode tempNode = new TagNode(temp, null, null);
				if (!parents.isEmpty()){
					if (parents.peek().firstChild == null){
						parents.peek().firstChild = tempNode;
					}else {
						TagNode currSib = parents.peek().firstChild;
						while(currSib.sibling != null){
							currSib = currSib.sibling;
						}
						currSib.sibling = tempNode;
					}
				}
			}
		}
	}

	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		replaceHelp(oldTag, newTag, root);
	}

	private void replaceHelp (String oldTag, String newTag, TagNode root) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) { //traverse tree 
			if (ptr.tag.equals(oldTag)) {
				ptr.tag = newTag;
			}
			if (ptr.firstChild != null) {
				replaceHelp(oldTag, newTag, ptr.firstChild);
			} 
		}
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		boldRowHelp(root, row);
	}

	private void boldRowHelp(TagNode root, int row) {
		if (root == null) {
			return;
		}
		for (TagNode ptr = root; ptr != null; ptr = ptr.sibling) {
			System.out.println(ptr.tag);
			if (ptr.tag.equals("tr")) {
				System.out.println(ptr);
				for(int i = 1; i < row; i++) {
					ptr = ptr.sibling;
					System.out.println(i);
				}
				if (ptr.firstChild != null) {
					for(TagNode curr = ptr.firstChild; curr != null; curr = curr.sibling) {
						TagNode bold = new TagNode("b", curr.firstChild, curr.firstChild.sibling);
						curr.firstChild = bold;
					}return;
				}
			} 
			if (ptr.firstChild != null) {
				boldRowHelp(ptr.firstChild, row);
			}
		}
	}
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		removeHelp(tag, root.firstChild, root);

	}

	private void removeHelp(String tag, TagNode root, TagNode prev) { //RIGHT NOW, NOT DELETING THE TAG IN QUESTION 

		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) { //traverse tree 
			if (ptr.tag.equals(tag)) {
				System.out.println(ptr.tag);
				System.out.println(prev.tag);
				if (prev.firstChild == ptr){
					if (ptr.firstChild != null) {
						prev.firstChild = ptr.firstChild;
						TagNode follow = ptr.firstChild; 
						while(follow.sibling != null ){
							if (follow.tag.equals("li")) {
								follow.tag = "p";
								System.out.println(follow);
							}
							follow = follow.sibling;

						}
						if (follow.tag.equals("li")) {
							follow.tag = "p";
						}
						follow.sibling = ptr.sibling;
						ptr = ptr.firstChild;				
					}
				} else if (prev.sibling == ptr) {
					prev.sibling = ptr.firstChild;
					TagNode follow = ptr.firstChild; 
					while(follow.sibling != null ){
						if (follow.tag.equals("li")) {
							follow.tag = "p";
							System.out.println(follow);
						}
						follow = follow.sibling;
					}
					if (follow.tag.equals("li")) {
						follow.tag = "p";
						System.out.println(follow);
					}
					follow.sibling = ptr.sibling;					
				}
			}
			if (ptr.firstChild != null) {
				removeHelp(tag, ptr.firstChild, ptr);
			} 			
			prev = ptr;
		}

	}

	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		addTagHelp(word, tag, root, root.firstChild);
	}

	private void addTagHelp(String word, String tag, TagNode prev, TagNode root) {	
		//contain all legal variations of word to search for in arraylist 
		word = word.toLowerCase();
		ArrayList<String> wordTypes = new ArrayList<String>();
		wordTypes.add(word+" ");
		wordTypes.add(word + ".");
		wordTypes.add(word + ",");
		wordTypes.add(word + "?");
		wordTypes.add(word + "!");
		wordTypes.add(word + ":");
		wordTypes.add(word + ";");

		//traverse through array and find instances 
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			//base case
			if (root == null) {
				return;
			}
			//case that word stands alone
			if (wordTypes.contains(ptr.tag)||ptr.tag.equals(word)) {
				if (prev.firstChild == ptr) {
					prev.firstChild = new TagNode(tag, ptr, ptr.sibling);
					ptr.sibling = null;
				}
				if (prev.sibling == ptr) {
					prev.sibling = new TagNode(tag, ptr, ptr.sibling);
					ptr.sibling = null;
				}
			}
			//case that we are encountering line of text 
			else if (ptr.firstChild == null && !ptr.tag.equals(word)) {
				String textLine = ptr.tag;
				textLine = textLine.toLowerCase();
				//recognize properly 
				String isThisTag = "";
				for (int i=0; i<textLine.length()-word.length()-1; i++) {
						
					if(textLine.charAt(i)==word.charAt(0)) {			
						isThisTag = textLine.substring(i, i + word.length()+1);				
					}
				
					
					
				}
				
				//create the nodes
				if (prev.firstChild == ptr) {
					prev.firstChild = new TagNode(tag, ptr, ptr.sibling);
					ptr.sibling = null;
				}
				if (prev.sibling == ptr) {
					prev.sibling = new TagNode(tag, ptr, ptr.sibling);
					ptr.sibling = null;
				}
			}
			if(ptr.firstChild != null) {
				addTagHelp(word, tag, ptr, ptr.firstChild);
			}
			//have prev follow ptr
			prev = ptr;
		}
	}

	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}

	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}

	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}

	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}

