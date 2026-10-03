package uptc.edu.co.demo.Controllers;

import uptc.edu.co.demo.Dto.DtoResult;
import uptc.edu.co.demo.Service.Calculator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CalculadoraController {

    private final Calculator calculatorService;

    public CalculadoraController(Calculator calculatorService) {
        this.calculatorService = calculatorService;
    }

    @GetMapping("/operation")
    public DtoResult makeOperation( @RequestParam int number1, @RequestParam int number2,@RequestParam String operation) {
        return calculatorService.realizarOperacion( number1, number2, operation);
    }
}