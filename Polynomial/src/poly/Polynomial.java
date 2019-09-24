package poly;

import java.io.IOException;
import java.util.Scanner;

/**
 * This class implements evaluate, add and multiply for polynomials.
 * 
 * @author runb-cs112
 *
 */
public class Polynomial {

	/**
	 * Reads a polynomial from an input stream (file or keyboard). The storage
	 * format of the polynomial is:
	 * 
	 * <pre>
	 *     <coeff> <degree>
	 *     <coeff> <degree>
	 *     ...
	 *     <coeff> <degree>
	 * </pre>
	 * 
	 * with the guarantee that degrees will be in descending order. For example:
	 * 
	 * <pre>
	 *      4 5
	 *     -2 3
	 *      2 1
	 *      3 0
	 * </pre>
	 * 
	 * which represents the polynomial:
	 * 
	 * <pre>
	 * 4 * x ^ 5 - 2 * x ^ 3 + 2 * x + 3
	 * </pre>
	 * 
	 * @param sc Scanner from which a polynomial is to be read
	 * @throws IOException If there is any input error in reading the polynomial
	 * @return The polynomial linked list (front node) constructed from coefficients
	 *         and degrees read from scanner
	 */
	public static Node read(Scanner sc) throws IOException {
		Node poly = null;
		while (sc.hasNextLine()) {
			Scanner scLine = new Scanner(sc.nextLine());
			poly = new Node(scLine.nextFloat(), scLine.nextInt(), poly);
			scLine.close();
		}
		return poly;
	}

	/**
	 * Returns the sum of two polynomials - DOES NOT change either of the input
	 * polynomials. The returned polynomial MUST have all new nodes. In other words,
	 * none of the nodes of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list
	 * @return A new polynomial which is the sum of the input polynomials - the
	 *         returned node is the front of the result polynomial
	 */
	public static Node add(Node poly1, Node poly2) {
		
		// first get the size of the polynomials 1 and 2
		int poly1size = 0;
		int poly2size = 0;
		for (Node n = poly1; n.next != null; n = n.next) {
			poly1size++;
		}
		for (Node n = poly2; n.next != null; n = n.next) {
			poly2size++;
		}
		
		//initialize iterators to traverse poly 1 and 2
		int i = 0;
		int j = 0;
		
		// 1st node and case for head
		Node head = null;
		Node sumTerms = null;
		if (poly1.term.degree == poly2.term.degree) {
			// add the terms
			head = new Node(poly1.term.coeff + poly2.term.coeff, poly1.term.degree, null);
			// advance to next node
			poly1 = poly1.next;
			i++;
			poly2 = poly2.next;
			j++;
		} 
		else if (poly1.term.degree > poly2.term.degree) {
			head = poly2;
			poly2 = poly2.next;
			j++;
		} 
		else {
			head = poly1;
			poly1 = poly1.next;
			i++;
		}
		sumTerms = head;
		// case for rest of linked list
		//while loop using iterators to prevent null pointer exceptions.  break the while loop when an iterator matches the size of the linked list.
		while ( i<=poly1size && j<=poly2size) {
		
			//case for poly degrees being equivalent
			if (poly1.term.degree == poly2.term.degree) {
				// add the terms
				sumTerms.next = new Node(poly1.term.coeff + poly2.term.coeff, poly1.term.degree, null);
				// advance to next node, increment iterators where needed.  This avoids null pointer exceptions.
				poly1 = poly1.next;
				i++;
				poly2 = poly2.next;
				j++;
				
			} 
			//case for poly1 having degree less than poly 2.  only include poly 1's node and move to next node.
			else if (poly1.term.degree < poly2.term.degree) {
				sumTerms.next = new Node(poly1.term.coeff,poly1.term.degree,null);
				poly1 = poly1.next;
				i++;
			
			} 
			//case for poly2 having degree less than poly 1.  only include poly 2's node and move to next node.
			else {
				sumTerms.next = new Node(poly2.term.coeff,poly2.term.degree,null); 
				poly2 = poly2.next;
				j++;
				

			} 
			sumTerms = sumTerms.next;
				
		}
		//extra while loop for the cases that poly 1 has degrees larger than poly 2.  This while loop will include the rest of poly 1 in the linked list.
		while ( i <= poly1size) {
			sumTerms.next = new Node(poly1.term.coeff,poly1.term.degree,null);
			poly1 = poly1.next;
			i++;
	
			sumTerms = sumTerms.next;
		}
		//extra while loop for the cases that poly 2 has degrees larger than poly 1.  This while loop will include the rest of poly 2 in the linked list.
		while ( j <= poly2size) {
			sumTerms.next = new Node(poly2.term.coeff,poly2.term.degree,null); 
			poly2 = poly2.next;
			j++;
			
			sumTerms = sumTerms.next;
		}
		
		return head;
	}

	/**
	 * Returns the product of two polynomials - DOES NOT change either of the input
	 * polynomials. The returned polynomial MUST have all new nodes. In other words,
	 * none of the nodes of the input polynomials can be in the result.
	 * 
	 * @param poly1 First input polynomial (front of polynomial linked list)
	 * @param poly2 Second input polynomial (front of polynomial linked list)
	 * @return A new polynomial which is the product of the input polynomials - the
	 *         returned node is the front of the result polynomial
	 */
	public static Node multiply(Node poly1, Node poly2) {
		
		//create temp variables to point to poly1 poly2
		Node poly1tmp = poly1;
		Node poly2tmp = poly2;
		
		//multiplying first node in each as head of list, store as "answer"
		Node answer = new Node(poly1tmp.term.coeff*poly2tmp.term.coeff,poly1tmp.term.degree+poly2tmp.term.degree,null);
		
		poly2tmp = poly2tmp.next;
		
	
	
		//loops thru first poly, then loops through second polynomial, multiplying every term of 2nd to each of 1st. adding every product to "answer" 
		//at end of each outer loop iteration, reset temp poly2 pointer to beginning of poly2 and repeat for next term of 1st polynomial
		while(poly1tmp != null) {
			while(poly2tmp != null) {
				answer = add(answer,new Node(poly1tmp.term.coeff*poly2tmp.term.coeff,poly1tmp.term.degree+poly2tmp.term.degree,null));
				poly2tmp = poly2tmp.next;
			}	
			poly1tmp = poly1tmp.next;
			poly2tmp = poly2;
		}


		return answer;
	
		
		
	}

	/**
	 * Evaluates a polynomial at a given value.
	 * 
	 * @param poly Polynomial (front of linked list) to be evaluated
	 * @param x    Value at which evaluation is to be done
	 * @return Value of polynomial p at x
	 */
	public static float evaluate(Node poly, float x) {

		//initialize variable to return
		float sum = 0;
		
		//evaluate each node and add result to "sum"
		while (poly != null) {
			float temp = (float) (Math.pow(x, poly.term.degree)) * poly.term.coeff;
			sum = temp + sum;
			poly = poly.next;
		}
		return sum;
	}

	/**
	 * Returns string representation of a polynomial
	 * 
	 * @param poly Polynomial (front of linked list)
	 * @return String representation, in descending order of degrees
	 */
	public static String toString(Node poly) {
		if (poly == null) {
			return "0";
		}

		String retval = poly.term.toString();
		for (Node current = poly.next; current != null; current = current.next) {
			retval = current.term.toString() + " + " + retval;
		}
		return retval;
	}
}
