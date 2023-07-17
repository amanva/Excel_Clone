
/**
 * This class has the expression tree which calculates the cell
 *  @version 03/07/2022
 *  @author Aman Vahora, Trevor Tomlin, Phuoc Le, and Bohdan Ivanovich Ivchenko.
 */
public class ExpressionTree {
	private ExpressionTreeNode root;

	/**
	 * makeEmpty() is to make the ExpressionTree empty with root is null.
	 */
	public void makeEmpty() {
		root = null;
	}

	/**
	 * Constructor for ExpressionTree
	 */
	public ExpressionTree(ExpressionTreeNode theRoot) {
		root = theRoot;
	}
	
	/**
	 * Constructor for ExpressionTree
	 */

	public ExpressionTree() {
		root = null;
	}
	
	/**
	 * getRoot() just get root
	 */
	
	public ExpressionTreeNode getRoot() {
		return root;
	}

	/**
	 * printTree is to print of tree.
	 * @param theRoot input root to print.
	 */
	public void printTree(ExpressionTreeNode theRoot) {
		if (theRoot == null)
			return;

		printTree(theRoot.left);
		System.out.print(theRoot.GetToken() + " "); // in order
		printTree(theRoot.right);

	}
	
	


	// Build an expression tree from a stack of ExpressionTreeTokens
	void BuildExpressionTree(Stack s) {
		root = GetExpressionTree(s);
		if (!s.isEmpty()) {
			System.out.println("Error in BuildExpressionTree.");
		}
	}

	ExpressionTreeNode GetExpressionTree(Stack s) {
		ExpressionTreeNode returnTree = null;
		Token token;
		if (s.isEmpty())
			return null;
		
		token =  (Token) s.topAndPop(); // need to handle stack underflow. NEED TO CAST TO ITS OWN TOKEN
		
		if ((token instanceof LiteralToken) || (token instanceof CellToken)) {
			// Literals and Cells are leaves in the expression tree
			returnTree = new ExpressionTreeNode(token, null, null);
			return returnTree;
		} else if (token instanceof OperatorToken) {
			// Continue finding tokens that will form the
			// right subtree and left subtree.
			ExpressionTreeNode rightSubtree = GetExpressionTree(s);
			ExpressionTreeNode leftSubtree = GetExpressionTree(s);
			returnTree = new ExpressionTreeNode(token, leftSubtree, rightSubtree);
			return returnTree;
		}
		return returnTree;
	}
	

	/**
	 *  Evaluate is to calculate the value of cells
	 * @param spreadsheet
	 * @return value of cell
	 */
	public int Evaluate(Spreadsheet spreadsheet) {
		return EvaluateHelper(root, spreadsheet);
	}

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
