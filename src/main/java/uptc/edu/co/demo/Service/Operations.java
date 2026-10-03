package uptc.edu.co.demo.Service;

import uptc.edu.co.demo.Exceptions.DivisionByZero;

public class Operations {
    
    public int addition(int number1, int number2) {
        return number1 + number2;
    }

    public int substraction(int number1, int number2) {
        return number1 - number2;
    }

    public int multiplication(int number1, int number2){
        return number1 * number2;
    }

    public int division(int number1, int number2){
        if (number2 == 0) {
            throw new DivisionByZero(String.valueOf(number1), String.valueOf(number2), "division");
        }
        return number1 / number2;
    }
}
