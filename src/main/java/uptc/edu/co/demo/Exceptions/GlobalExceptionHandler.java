package uptc.edu.co.demo.Exceptions;


import jakarta.servlet.http.HttpServletRequest;
import uptc.edu.co.demo.Dto.DtoError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;



@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DivisionByZero.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DtoError handleDivisionByZero(DivisionByZero d) {

        logger.warn("DIVISION ZERO" + d.getNumber1() + " / " + d.getNumber2() + " = " + d.getMessage());
        return new DtoError(d.getNumber1(), d.getNumber2(), d.getOperacion(), d.getMessage());
    }

    @ExceptionHandler(InvalidOperation.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DtoError handleInvalidOperation(InvalidOperation i) {

            logger.warn("NO ES VALIDO: {}", i.getOperacion());
        return new DtoError(i.getNumber1(), i.getNumber2(), i.getOperacion(), i.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DtoError handleTypeMismatch(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {

        String number1 = request.getParameter("number1");
        String number2 = request.getParameter("number2");
        String operation = request.getParameter("operation");

        logger.warn("ENTEROS INVALIDOS:", e.getName(), e.getValue());

        return new DtoError( number1, number2, operation, "DEBEN SER VALORES ENTEROS");
    }
}

