import java.util.LinkedList;

/**
 * This class contains the functionality related to the cell
 *
 * @version 03/07/2022
 * @author Aman Vahora, Trevor Tomlin, Phuoc Le, and Bohdan Ivanovich Ivchenko.
 */
public class Cell {
	String formula;
	int value;
	// the expression tree below represents the formula
	private ExpressionTree expressionTree;


	/**
	 * Constructor for the cell
	 * @param theFormula is input formula for cell which is used to build  expression tree.
	 * @throws Overflow
	 * @throws Underflow
	 */
	public Cell(String theFormula) throws Overflow, Underflow {
		formula = theFormula;
		value = 0; // default value is 0
		expressionTree = new ExpressionTree(null);

		Stack stackTree = Token.getFormula(formula); // use util class method to get stack from a formula.
		expressionTree.BuildExpressionTree(stackTree);


	}
	
	/**
	 * getFormula is to get formula of cell
	 * @return formula 
	 */
	public String getFormula() {
		return formula;
	}

	/**
	 * getValue is to get value of cell
	 * @return value 
	 */
	public int getValue() {
		return value;
	}

	/**
	 * getExpressionTree is to get expression tree
	 * @return expression tree 
	 */
	public ExpressionTree getExpressionTree() {
		return expressionTree;
	}

	/**
	 * setExpressionTree is to set setExpressionTree to input one.
	 * @param _expressionTree is input tree
	 */
	public void setExpressionTree(ExpressionTree _expressionTree) {
		expressionTree = _expressionTree;
	}

	
	/**
	 *  Evaluate is to calculate the value of cells
	 * @param spreadsheet the spreadsheet
	 * @return value of cell
	 */
    public void Evaluate(Spreadsheet spreadsheet) {
        if (expressionTree == null) {
            value = 0; // when there is nothing give value = 0
            return;

        } else { // if there is something in the tree, calculate its value
            value = EvaluateHelper(expressionTree.getRoot(), spreadsheet);

        }
    }

	/**
	 * A helper method for the evaluate method which calculates the value.
	 *
	 * @param node the node.
	 * @param spreadsheet the spreadsheet.
	 * @return the value.
	 */
    public int EvaluateHelper(ExpressionTreeNode node, Spreadsheet spreadsheet) {
        if (node == null) {
            return 0;

        } else if (node.GetToken() instanceof OperatorToken) {
        	if (((OperatorToken)node.GetToken()).getOperatorToken() == '+') {
        		return EvaluateHelper(node.left, spreadsheet) + EvaluateHelper(node.right, spreadsheet);
        	} else if (((OperatorToken)node.GetToken()).getOperatorToken() == '-') {
        		return EvaluateHelper(node.left, spreadsheet) - EvaluateHelper(node.right, spreadsheet);
        	}if (((OperatorToken)node.GetToken()).getOperatorToken() == '*') {
        		return EvaluateHelper(node.left, spreadsheet) * EvaluateHelper(node.right, spreadsheet);
        	}if (((OperatorToken)node.GetToken()).getOperatorToken() == '/') {
        		return EvaluateHelper(node.left, spreadsheet) / EvaluateHelper(node.right, spreadsheet);
        	}
            
        } else if (node.GetToken() instanceof LiteralToken) {
            return ((LiteralToken) node.GetToken()).getValue();
        } else if (node.GetToken() instanceof CellToken) {
            return spreadsheet.sheet[((CellToken) node.GetToken()).getRow()][((CellToken) node.GetToken()).getColumn()]
                    .getValue();

        }
        return 0;// it'd never make it here.(only if input is a different class)
    }

}