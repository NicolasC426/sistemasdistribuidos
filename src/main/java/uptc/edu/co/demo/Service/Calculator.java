package uptc.edu.co.demo.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import uptc.edu.co.demo.Dto.DtoResult;
import uptc.edu.co.demo.Exceptions.InvalidOperation;


@Service
public class Calculator {

    private static final Logger logger = LoggerFactory.getLogger(Calculator.class);
    private final Operations operaciones = new Operations();

    public DtoResult realizarOperacion(int number1, int number2, String operacion) {

        logger.info("Realizando operación: {} {} {}", number1, operacion, number2);
        int resultado;

        switch (operacion) {
            case "sum":
                resultado = operaciones.addition(number1, number2);
                break;
            case "sub":
                resultado = operaciones.substraction(number1, number2);
                break;
            case "mul":
                resultado = operaciones.multiplication(number1, number2);
                break;
            case "div":
                resultado = operaciones.division(number1, number2);
                break;
            default:
                throw new InvalidOperation(String.valueOf(number1), String.valueOf(number2), operacion);
        }

        String maquina = System.getenv().getOrDefault("MAQUINA", "desconocida");
        String contenedor;
        try {
            contenedor = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            contenedor = "desconocido";
        }

        logger.info("Resultado de la operación: {}", resultado);

        return new DtoResult(
                number1,
                number2,
                operacion,
                resultado,
                maquina,
                contenedor
        );
    }
    
}