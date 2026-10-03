package uptc.edu.co.demo.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uptc.edu.co.demo.Entities.PersonEntity;
import uptc.edu.co.demo.Repository.PersonRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class PersonDbController {

    @Autowired
    private PersonRepository personaRepository;

    private static final int PAGE_SIZE = 50;

    // Endpoint 1: mostrar personas paginadas (desde PostgreSQL)
    @GetMapping("/personas-db")
    public Map<String, Object> listar(@RequestParam(defaultValue = "0") int page) {
        String maquina = System.getenv().getOrDefault("MAQUINA", "desconocida");
        String contenedor;
        try {
            contenedor = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            contenedor = "desconocido";
        }

        int desde = page * PAGE_SIZE + 1;
        int hasta = desde + PAGE_SIZE;
        List<PersonEntity> data = personaRepository
                .findByIdGreaterThanEqualAndIdLessThanOrderByIdAsc(desde, hasta);

        return Map.of(
                "page", page,
                "size", PAGE_SIZE,
                "count", data.size(),
                "data", data,
                "maquina", maquina,
                "contenedor", contenedor
        );
    }

    // Endpoint 2: modificar uno o varios campos de una persona
    @PutMapping("/personas-db/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody Map<String, String> cambios) {
        Optional<PersonEntity> opt = personaRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        PersonEntity p = opt.get();
        cambios.forEach((campo, valor) -> {
            switch (campo) {
                case "primerNombre" -> p.setPrimerNombre(valor);
                case "segundoNombre" -> p.setSegundoNombre(valor);
                case "primerApellido" -> p.setPrimerApellido(valor);
                case "segundoApellido" -> p.setSegundoApellido(valor);
                case "ciudad" -> p.setCiudad(valor);
            }
        });
        personaRepository.save(p);
        return ResponseEntity.ok(Map.of("mensaje", "Persona " + id + " actualizada", "data", p));
    }
}