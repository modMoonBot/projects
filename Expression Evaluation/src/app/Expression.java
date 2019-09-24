package app;

import java.io.*;
import java.util.*;

public class Expression {

	public static String delims = " \t*+-/()[]";

	/**
	 * Populates the vars list with simple variables, and arrays lists with arrays
	 * in the expression. For every variable (simple or array), a SINGLE instance is
	 * created and stored, even if it appears more than once in the expression. At
	 * this time, values for all variables and all array items are set to zero -
	 * they will be loaded from a file in the loadVariableValues method.
	 *
	 * @param expr   The expression
	 * @param vars   The variables array list - already created by the caller
	 * @param arrays The arrays array list - already created by the caller
	 */
	public static void makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

		// remove spaces from this expression, first (See helper method, below)
		String newExpr = removeSpaces(expr);
		char[] tempCharArray = newExpr.toCharArray();

		// parse through expr and find vars and arrays by finding letters
		for (int i = 0; i < tempCharArray.length; i++) {

			String tempName = "";

			if (Character.isLetter(tempCharArray[i])) {

				// when a letter is found, we keep parsing to find consecutive letters and add
				// to tempName

				for (int j = i; j < tempCharArray.length && Character.isLetter(tempCharArray[j]); j++) {
					tempName = tempName + Character.toString(tempCharArray[j]);
					i = j;
					System.out.println(tempName);
				}

				// exits when no more letters found

				// depending on whether next character is left bracket or not, search
				// vars/arrays and add to appropriate arraylist
				if (i + 1 < tempCharArray.length && tempCharArray[i + 1] == '[') {

					if (searchArrays(arrays, tempName) == null) {
						Array temp = new Array(tempName);
						arrays.add(temp);
					}
				} else if (i < tempCharArray.length && tempCharArray[i] != '[') {
					if (!searchVars(vars, tempName)) {
						Variable temp = new Variable(tempName);
						vars.add(temp);
					}
				}

			}
		}
	}

	// search function to check if variable/array is already in the arraylist
	// returns true when finding match. false if finding nothing
	private static boolean searchVars(ArrayList<Variable> curr, String check) {
		for (int i = 0; i < curr.size(); i++) {
			if (curr.get(i).name.equals(check)) {
				return true;
			}
		}
		return false;
	}

	private static Array searchArrays(ArrayList<Array> curr, String check) {
		for (int i = 0; i < curr.size(); i++) {
			if (curr.get(i).name.equals(check)) {
				return curr.get(i);
			}
		}
		return null;
	}

	/**
	 * Loads values for variables and arrays in the expression
	 *
	 * @param sc     Scanner for values input
	 * @param vars   The variables array list, previously populated by
	 *               makeVariableLists
	 * @param arrays The arrays array list - previously populated by
	 *               makeVariableLists
	 * @throws IOException If there is a problem with the input
	 */
	// OPENS FILE AND GOES OVER LINE BY LINE AND GIVES VALUE TO VARS
	public static void loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays)
			throws IOException {
		while (sc.hasNextLine()) {
			StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
			int numTokens = st.countTokens();
			String tok = st.nextToken();
			Variable var = new Variable(tok);
			Array arr = new Array(tok);
			int vari = vars.indexOf(var);
			int arri = arrays.indexOf(arr);
			if (vari == -1 && arri == -1) {
				continue;
			}
			int num = Integer.parseInt(st.nextToken());
			if (numTokens == 2) { // scalar symbol
				vars.get(vari).value = num;
			} else { // array symbol
				arr = arrays.get(arri);
				arr.values = new int[num];
				// following are (index,val) pairs
				while (st.hasMoreTokens()) {
					tok = st.nextToken();
					StringTokenizer stt = new StringTokenizer(tok, " (,)");
					int index = Integer.parseInt(stt.nextToken());
					int val = Integer.parseInt(stt.nextToken());
					arr.values[index] = val;
				}
			}
		}
	}

	// helper method to detect numbers in a string and cast it to a float
	// given that we know at the character at index i is a digit
	// this method is called only when we know we hit a digit and want to find the
	// whole number
	private static float detectNextNum(char[] charArray, int index) {
		String stringNum = "";
		for (int i = index; i < charArray.length && Character.isDigit(charArray[i]); i++) {
			stringNum = stringNum + charArray[i];
		}
		float detected = Float.parseFloat(stringNum);
		return detected;
	}

	// This helper method detects the value of a variable, given that the first
	// index given is a letter
	private static float detectValue(ArrayList<Variable> vars, char[] aCharArray, int count) {

		String varName = "";
		int index = 0;

		for (int i = count; i < aCharArray.length && Character.isLetter(aCharArray[i]); i++) {
			varName = varName + aCharArray[i];
		}
		if (searchVars(vars, varName)) {
			for (int j = 0; j < vars.size(); j++) {
				if (vars.get(j).name.equals(varName)) {
					index = j;
				}
			}
		}

		return vars.get(index).value;
	}

	// this helper method removes spaces from the string
	private static String removeSpaces(String expr) {
		String newExpr = "";
		for (int i = 0; i < expr.length(); i++) {
			if (expr.charAt(i) == ' ') {
				// copy nothing to the next string
			} else {
				newExpr = newExpr + expr.charAt(i);
				// any character with something populated besides space is concatenated to
				// output string
			}
		}

		return newExpr;
	}

	// this helper method simply detects the entire name of variable or array
	private static String detectNames(char[] charArray, int index) {
		String varName = "";

		for (int i = index; i < charArray.length && Character.isLetter(charArray[i]); i++) {
			varName = varName + charArray[i];
		}

		return varName;
	}

	// helper method that counts left parentheses
	private static int leftCount(char[] charArray, SearchType searchType, int leftIndex, int rightIndex) {
		int searchCount = 0;
		for (int i = leftIndex; i <= rightIndex; i++) {
			if (searchType.equals(SearchType.BRACKET) && charArray[i] == '[') {
				searchCount++;
			} else if (searchType.equals(SearchType.PAREN) && charArray[i] == '(') {
				searchCount++;
			}
		}
		return searchCount;
	}

	// helper method that counts right parentheses
	private static int rightCount(char[] charArray, SearchType searchType, int leftIndex, int rightIndex) {
		int searchCount = 0;
		for (int i = leftIndex; i <= rightIndex; i++) {
			if (searchType.equals(SearchType.BRACKET) && charArray[i] == ']') {
				searchCount++;
			} else if (searchType.equals(SearchType.PAREN) && charArray[i] == ')') {
				searchCount++;
			}
		}
		return searchCount;
	}

	/**
	 * Evaluates the expression.
	 *
	 * @param vars   The variables array list, with values for all variables in the
	 *               expression
	 * @param arrays The arrays array list, with values for all array items
	 * @return Result of evaluation
	 */
	public static float evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {

		// remove spaces from the expression first
		String newExpr = removeSpaces(expr);
		char[] tempCharList = newExpr.toCharArray();

		Stack<Float> numbers = new Stack<Float>();
		float total = 0;

		// FIRST, READ THROUGH EXPRESSION, STORE NUMBERS AND OPERATORS IN RESPECTIVE
		// STACKS
		// for as long as the expression is, parse through
		// stack numbers adds everything at the end of reading the entire expression

		for (int i = 0; i < tempCharList.length; i++) {
			int numDig = 0;
			String varName = "";

			// check if number, if so, add to stack
			if (Character.isDigit(tempCharList[i])) {
				float num = detectNextNum(tempCharList, i);
				numbers.push(num);
				numDig = String.valueOf((int) (num)).length();
				i = i + numDig - 1;

			}
			// check if operator, if +, add to stack, if -, add + to stack and negative
			// version of next number to stack
			// if * or /, execute and continue parsing through expression
			else if (tempCharList[i] == '*' || tempCharList[i] == '/' || tempCharList[i] == '+'
					|| tempCharList[i] == '-') {

				// if minus sign is detected, store next number as negative float
				int nextIndex = i + 1;
				if (tempCharList[i] == '-') {
					if (Character.isDigit(tempCharList[nextIndex])) { // if succeeded by numbers
						float negNum = detectNextNum(tempCharList, nextIndex);
						numDig = String.valueOf((int) (negNum)).length();
						negNum = negNum * -1.0f;
						numbers.push(negNum);
						i = i + numDig;
					} else if (Character.isLetter(tempCharList[nextIndex])
							&& tempCharList[i + (detectNames(tempCharList, i).length() + 1)] != '[') { // if succeeded
						// by variables
						varName = detectNames(tempCharList, nextIndex);
						float negNum = detectValue(vars, tempCharList, nextIndex);
						numDig = String.valueOf((int) (negNum)).length();
						negNum = negNum * -1.0f;
						numbers.push(negNum);
						i = i + numDig;
					} else if (tempCharList[nextIndex] == '(') { // if succeeded by parentheses
						InnerExpressionResult innerResult = evaluateInnerExpression(nextIndex, SearchType.PAREN,
								tempCharList, vars, arrays);
						i = innerResult.getIndex();
						float negNum = innerResult.getResult() * -1;
						numbers.push(negNum);
					} else if (Character.isLetter(tempCharList[i])
							&& tempCharList[i + (detectNames(tempCharList, i).length())] == '[') { // IS ARRAY
						InnerExpressionResult innerResult = evaluateArrayExpression(i, tempCharList, vars, arrays);
						float negNum = innerResult.getResult() * -1;
						numbers.push(negNum);
						i = innerResult.getIndex();
					}
					// if divisor, then find next value and push the subtotal to stack
					// ERROR: PUSHES ON STACK BEFORE AND AFTER "/", DOES NOT POP TO DIVIDE
				} else if (tempCharList[i] == '/') {
					if (Character.isDigit(tempCharList[nextIndex])) {
						float nextNum = detectNextNum(tempCharList, nextIndex);
						numDig = String.valueOf((int) (nextNum)).length();
						float last = numbers.pop();
						float subtotal = last / nextNum;
						numbers.push(subtotal);
						i = i + numDig;
					} else if (Character.isLetter(tempCharList[nextIndex])
							&& tempCharList[i + (detectNames(tempCharList, i).length() + 1)] != '[') {
						varName = detectNames(tempCharList, nextIndex);
						float nextNum = detectValue(vars, tempCharList, nextIndex);
						float subtotal = numbers.pop() / nextNum;
						numbers.push(subtotal);
						i = i + varName.length();
					} else if (tempCharList[nextIndex] == '(') { // if succeeded by parentheses
						InnerExpressionResult innerResult = evaluateInnerExpression(nextIndex, SearchType.PAREN,
								tempCharList, vars, arrays);
						i = innerResult.getIndex();
						float sub = numbers.pop() / innerResult.getResult();
						numbers.push(sub);
					} else if (Character.isLetter(tempCharList[i])
							&& tempCharList[i + (detectNames(tempCharList, i).length())] == '[') { // IS ARRAY
						InnerExpressionResult innerResult = evaluateArrayExpression(i, tempCharList, vars, arrays);
						float sub = numbers.pop() / innerResult.getResult();
						numbers.push(sub);
						i = innerResult.getIndex();
					}
				} else if (tempCharList[i] == '*') {
					if (Character.isDigit(tempCharList[nextIndex])) {
						System.out.println(i);
						float nextNum = detectNextNum(tempCharList, nextIndex);
						System.out.print(numbers.size());
						float subtotal = nextNum * numbers.pop();
						numDig = String.valueOf((int) (nextNum)).length();
						numbers.push(subtotal);
						i = i + numDig;

					} else if (Character.isLetter(tempCharList[nextIndex])
							&& tempCharList[i + (detectNames(tempCharList, i).length() + 1)] != '[') {
						varName = detectNames(tempCharList, nextIndex);
						float nextNum = detectValue(vars, tempCharList, nextIndex);
						float subtotal = numbers.pop() * nextNum;
						numbers.push(subtotal);
						i = i + varName.length();
						// System.out.println("subtotal"+ subtotal);
					} else if (tempCharList[nextIndex] == '(') { // if succeeded by parentheses
						InnerExpressionResult innerResult = evaluateInnerExpression(nextIndex, SearchType.PAREN,
								tempCharList, vars, arrays);
						i = innerResult.getIndex();
						float sub = numbers.pop() * innerResult.getResult();
						numbers.push(sub);
					} else if (Character.isLetter(tempCharList[i])
							&& tempCharList[i + (detectNames(tempCharList, i).length())] == '[') { // IS ARRAY
						InnerExpressionResult innerResult = evaluateArrayExpression(i, tempCharList, vars, arrays);
						float sub = numbers.pop() * innerResult.getResult();
						numbers.push(sub);
						i = innerResult.getIndex();
					}
				}
				// if multiplier, find next value and push subtotal to stack
			} else if (Character.isLetter(tempCharList[i])
					&& tempCharList[i + (detectNames(tempCharList, i).length())] != '[') { // IT'S A VARIABLE
				varName = detectNames(tempCharList, i);
				float varNum = detectValue(vars, tempCharList, i);
				numbers.push(varNum);
				i = i + varName.length() - 1;
			} else if (tempCharList[i] == '(') { // if succeeded by parentheses
				InnerExpressionResult innerResult = evaluateInnerExpression(i, SearchType.PAREN, tempCharList, vars,
						arrays);
				numbers.push(innerResult.getResult());
				i = innerResult.getIndex();
			} else if (Character.isLetter(tempCharList[i])
					&& tempCharList[i + (detectNames(tempCharList, i).length())] == '[') { // IS ARRAY
				InnerExpressionResult innerResult = evaluateArrayExpression(i, tempCharList, vars, arrays);
				numbers.push(innerResult.getResult());
				i = innerResult.getIndex();
			}
		}
		while (!numbers.isEmpty()) {
			System.out.print("stack: " + numbers.peek());
			total = total + numbers.pop();

		}
		System.out.println("  total" + total);
		return total;
	}
	// end of class

	private static InnerExpressionResult evaluateInnerExpression(int i, SearchType searchType, char[] tempCharList,
			ArrayList<Variable> vars, ArrayList<Array> arrays) {
		String inside = "";
		String subExpression = new String(tempCharList);
		System.out.println("Expression: " + subExpression);
		Float result = 0f;
		Integer returnIndex = null;
		for (int index = i + 1; index < tempCharList.length; index++) {
			int left = leftCount(tempCharList, searchType, i, index);
			int right = rightCount(tempCharList, searchType, i, index);

			if (left == right) {
				System.out.println("INSIDE:  " + inside);
				result = evaluate(inside, vars, arrays);
				System.out.println("RESULT:  " + result);
				returnIndex = index;
				break;
			}
			inside = inside + tempCharList[index];
		}
		return new Expression.InnerExpressionResult(result, returnIndex);
	}

	private static InnerExpressionResult evaluateArrayExpression(int i, char[] tempCharList, ArrayList<Variable> vars,
			ArrayList<Array> arrays) {
		String arrayName = detectNames(tempCharList, i);
		int newIndex = i + arrayName.length();
		InnerExpressionResult result = evaluateInnerExpression(newIndex, SearchType.BRACKET, tempCharList, vars,
				arrays);
		Array found = searchArrays(arrays, arrayName);
		return new InnerExpressionResult((float) found.values[result.getResult().intValue()], result.getIndex());
	}

	public static class InnerExpressionResult {
		private Float result;
		private Integer index;

		public InnerExpressionResult(Float result, Integer index) {
			this.result = result;
			this.index = index;
		}

		public Float getResult() {
			return result;
		}

		public Integer getIndex() {
			return index;
		}
	}

	public enum SearchType {
		PAREN, BRACKET
	}

}
