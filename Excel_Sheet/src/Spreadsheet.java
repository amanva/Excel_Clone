import java.util.*;
/**
 * This class has the functionality related to a spreadsheet
 *
 *  @version 03/07/2022
 *  @author Aman Vahora, Trevor Tomlin, Phuoc Le, and Bohdan Ivanovich Ivchenko.
 */
public class Spreadsheet {

	private int WIDTH;
	private int HEIGHT;

	public Cell sheet[][] = new Cell[HEIGHT][WIDTH];

	private HashMap<Cell, LinkedList<Cell>> graph = new HashMap();

	
	/**
	 * Constructor for Spreadsheet class.
	 * @param s is size for the spreadsheet.
	 * @throws Overflow
	 * @throws Underflow
	 */
	Spreadsheet(int s) throws Overflow, Underflow {

        HEIGHT = s;
        WIDTH = s;

        sheet = new Cell[HEIGHT][WIDTH];

        // Populate sheet with CellTokens
        for (int row = 0; row < HEIGHT; row++) {

            for (int col = 0; col < WIDTH; col++) {

                sheet[row][col] = new Cell("");
                sheet[row][col].value = 0;

            }

        }

        buildGraph();

    }
	

	/**
	 * printGraph is to print out graph.
	 */
	public void printGraph() {

		Iterator dgIt = graph.entrySet().iterator();

		// Puts all tokens with no dependencies in tokensNoEdges
		while (dgIt.hasNext()) {

			Map.Entry entry = (Map.Entry) dgIt.next();

			Cell key = (Cell) entry.getKey();
			LinkedList<Cell> dependencies = (LinkedList<Cell>) entry.getValue();

			System.out.print("(" + key + ") : ");
			for (int i = 0; i < dependencies.size(); i++) {
				System.out.print(dependencies.get(i));
				if (i < dependencies.size() - 1)
					System.out.print("-> ");
			}
			System.out.println();

		}

	}

	
	/**
	 * buildGraph is to build graph.
	 */
	public void buildGraph() {

		for (int row = 0; row < HEIGHT; row++) {

			for (int col = 0; col < WIDTH; col++) {

				Cell currentCell = sheet[row][col];

				LinkedList<Cell> dependencies = new LinkedList<>();

				if (!currentCell.formula.equals("")) {

					int i = 0;

					while (i < currentCell.formula.length()) {

						CellToken cur = new CellToken();
						i = CellToken.getCellToken(currentCell.formula, i, cur);

						if (cur.getRow() != -1 && cur.getColumn() != -1) {

							dependencies.add(sheet[cur.getRow()][cur.getColumn()]);
						} else
							i++;

					}

				}

				graph.put(currentCell, dependencies);

			}

		}

	}

	
	/**
	 * printCellFormula is to print out formula for given celltoken.
	 * @param cellToken given celltoken.
	 */
	public void printCellFormula(CellToken cellToken) {

		Cell currentCell = sheet[cellToken.getRow()][cellToken.getColumn()];

		System.out.println(currentCell.formula);

	}

	/**
	 * isOperator is to check if given character is either +, -, *, /
	 * @param ch the char
	 * @return
	 */
	public static boolean isOperator(char ch) {
		return ((ch == '+') || (ch == '-') || (ch == '*') || (ch == '/'));
	}

	/**
	 * changeCellFormulaAndRecalculate is to check if given inputFormula is valid, if it is valid, then calculate cell's value based on that formula. If its not valid, do not do anything.
	 * @param cellToken is given cellToken.
	 * @param expTreeStack is given expTreeStack.
	 * @param inputFormula is given inputFormula
	 * @return 
	 */
	public boolean changeCellFormulaAndRecalculate(CellToken cellToken, Stack expTreeStack, String inputFormula) {
		boolean formulaLegit = true;
		boolean leftParen = false; // need left paren( and right paren)
		boolean rightParen = false;

		for (int i = 0; i < inputFormula.length(); i++) {

			if (Character.isLowerCase(inputFormula.charAt(i))) { // if formula has lowercase, invalid
				formulaLegit = false;
				break;
			}

			if (leftParen == true && rightParen == true) { // when we have pair (), we reset it
				leftParen = false;
				rightParen = false;
			}

			if (inputFormula.charAt(i) == ')') { // when we have ) without (, invalid
				if (leftParen == false) {
					formulaLegit = false;
					break;
				} else if (leftParen == true) { // if we have ( already, ) is valid
					rightParen = true;
				}
			}

			if (inputFormula.charAt(i) == '(') { // invalid formula if ( is at the ennd
				if (i == inputFormula.length() - 1) {
					formulaLegit = false;
					break;
				} else {
					leftParen = true;
				}

			}
			if (leftParen == true) { // invalid formula has ( but no )
				if (i == inputFormula.length() - 1) {
					if (inputFormula.charAt(i) != ')') {
						formulaLegit = false;
						break;
					}
				}

			}

			if (i + 1 < inputFormula.length()) { // Check for adjacent operator
				if ((isOperator(inputFormula.charAt(i)) && isOperator(inputFormula.charAt(i + 1)))) {
					formulaLegit = false;
					break;
				}

			}

		}
		if (formulaLegit == true) {

			Cell currentCell = sheet[cellToken.getRow()][cellToken.getColumn()];

			String currentFormula = currentCell.formula;
			int currentValue = currentCell.getValue();
			LinkedList<Cell> currentDependencies = graph.get(currentCell);

			currentCell.formula = inputFormula;

			ExpressionTree t = new ExpressionTree();
			t.BuildExpressionTree(expTreeStack);

			currentCell.setExpressionTree(t);

			buildGraph();

			LinkedList<Cell> sorted = topSort();

			// topSort had a cycle
			if (sorted == null) {
				currentCell.formula = currentFormula;
				currentCell.value = currentValue;
				graph.put(currentCell, currentDependencies);
				System.out.println("Cycle detected.");
				return false;
			} else {

				for (int i = 0; i < sorted.size(); i++) {

					sorted.get(i).Evaluate(this);

				}

			}

		}
		return true;

	}

	/**
	 * printAllFormulas is to print out formulas for all cells in the spreadsheet.
	 */
	public void printAllFormulas() {

		for (Cell rows[] : sheet) {

			for (Cell cell : rows) {

				System.out.print(cell.formula + " | ");

			}

			System.out.println();

		}

	}

	/**
	 * getNumRows is to get row.
	 */
	public int getNumRows() {

		return HEIGHT;

	}

	/**
	 * getNumColumns is to get columns.
	 */
	public int getNumColumns() {

		return WIDTH;

	}

	/**
	 * printValues is to print out values of all cells in the spreadsheet.
	 */
	public void printValues() {

		for (Cell rows[] : sheet) {

			for (Cell cell : rows) {

				System.out.print(cell.value + " | ");

			}

			System.out.println();

		}

	}
	
	/**
	 * topSort is the topological sort
	 * @return a sorted list based on number of indegree
	 */
	public LinkedList<Cell> topSort() {

		// CellToken depends on LL of CellTokens
		HashMap<Cell, LinkedList<Cell>> dependencyGraph = graph;

		// All Cells with no incoming edges
		Queue<Cell> tokensNoEdges = new LinkedList<Cell>();

		// Sorted List
		LinkedList<Cell> sortedOrder = new LinkedList<Cell>();

		Iterator dgIt = dependencyGraph.entrySet().iterator();

		// Puts all tokens with no dependencies in tokensNoEdges
		while (dgIt.hasNext()) {

			Map.Entry entry = (Map.Entry) dgIt.next();

			Cell key = (Cell) entry.getKey();
			LinkedList<Cell> dependencies = (LinkedList<Cell>) entry.getValue();

			if (dependencies.isEmpty()) {
				dgIt.remove();
				tokensNoEdges.add(key);
			}

		}

		// Kahn's algorithm
		// https://en.wikipedia.org/wiki/Topological_sorting

		// sortedOrder <- Empty list that will contain the sorted elements
		// tokensNoEdges <- Set of all nodes with no incoming edge

		while (!tokensNoEdges.isEmpty()) {

			Cell current = tokensNoEdges.poll();

			// System.out.println(tokensNoEdges.size());

			sortedOrder.add(current);

			dgIt = dependencyGraph.entrySet().iterator();

			// Loop through the HashTable and remove current from any of the dependencies.
			while (dgIt.hasNext()) {

				Map.Entry entry = (Map.Entry) dgIt.next();

				Cell key = (Cell) entry.getKey();
				LinkedList<Cell> dependencies = (LinkedList<Cell>) entry.getValue();

				if (dependencies.contains(current))
					dependencies.remove(current);

				if (dependencies.isEmpty()) {

					dgIt.remove();
					tokensNoEdges.add(key);

				}

			}

		}

		dgIt = dependencyGraph.entrySet().iterator();

		// if graph has edges then return error (graph has at least one cycle)
		while (dgIt.hasNext()) {

			Map.Entry entry = (Map.Entry) dgIt.next();
			LinkedList<Cell> edges = (LinkedList<Cell>) entry.getValue();

			if (edges.size() > 0) {

				buildGraph();
				return null;

			}

		}

		buildGraph();

		return sortedOrder;

	}

	/**
	 * getSheet returns 2 dimensional array of the spreadsheet.
	 * @return returns 2 dimensional array.
	 */
	public Cell[][] getSheet() {
		return sheet;
	}

	public static void main(String[] args) throws Overflow, Underflow {
//        Spreadsheet s = new Spreadsheet(2);
//        s.sheet[0][0] = new Cell("5");
//        s.sheet[0][1] = new Cell("3");
//        s.sheet[1][0] = new Cell("A0++5");
//        s.sheet[1][1] = new Cell("B1");
//
//        s.sheet[0][1].Evaluate(s);
//        s.sheet[1][0].Evaluate(s);
//        System.out.println(s.sheet[1][0].getValue());
//
//        s.printAllFormulas();
	}
}