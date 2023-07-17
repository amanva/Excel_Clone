/**
 * This class creates the node for the expression tree
 *  @version 03/07/2022
 *  @author Aman Vahora, Trevor Tomlin, Phuoc Le, and Bohdan Ivanovich Ivchenko.
 */
public class ExpressionTreeNode {
	private Token token;
	ExpressionTreeNode left;
	ExpressionTreeNode right;
	
	/**
	 * constructor for ExpressionTreeNode.
	 * @param theToken input token. 
	 * @param theLeft input node for left. 
	 * @param theRight input node for right. 
	 */
	public ExpressionTreeNode(Token theToken, ExpressionTreeNode theLeft, ExpressionTreeNode theRight) {
		token= theToken;
		left = theLeft;
		right = theRight;
	}
	
	/**
	 * GetToken is to get token
	 * @return token.
	 */
	public Token GetToken() {
		return token;
	}
	
}
