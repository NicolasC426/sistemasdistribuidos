package uptc.edu.co.demo.Exceptions;

public class InvalidOperation  extends RuntimeException {

    private final String number1;
    private final String number2;
    private final String operation;

    public InvalidOperation(String number1, String number2, String operation) {
        super("OPERACION INVALIDA"); this.number1 = number1;
        this.number2 = number2; this.operation = operation;
    }

    public String getNumber1() {
        return number1;
    }

    public String getNumber2() {
        return number2;
    }

    public String getOperacion() {
        return operation;
    }

    
}
