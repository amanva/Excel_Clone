/**
 * This class contains the literal token object which is a constant
 *
 *  @version 03/07/2022
 *  @author Aman Vahora, Trevor Tomlin, Phuoc Le, and Bohdan Ivanovich Ivchenko.
 */
public class LiteralToken extends Token {
    private int value;

    /**
     * The constructor for LiteralToken
     *
     * @param value
     */
    public LiteralToken(int value){
        this.value = value;
    }


    /**
     * setValue sets the value for the Literal
     *
     * @param value
     */
    public void setValue(int value) {
        this.value = value;
    }


    /**
     * getValue gets the literal value.
     *
     * @return value
     */
    int getValue() {
        return value;
    }
    @Override
    public String toString() {

        return Integer.toString(value);
    }
}