package uptc.edu.co.demo.Controllers;

import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uptc.edu.co.demo.Dto.Person;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class PersonController {

    private static final String CSV_PATH = "/data/personas.csv";
    private static final String INDEX_PATH = "/data/personas.idx";
    private static final int PAGE_SIZE = 50;

    private long[] offsets;

    @PostConstruct
    public void construirIndice() throws IOException {
        File csv = new File(CSV_PATH);
        File indexFile = new File(INDEX_PATH);

        if (!csv.exists()) {
            System.err.println("ADVERTENCIA: no se encontró el CSV en " + CSV_PATH);
            offsets = new long[0];
            return;
        }

        if (indexFile.exists() && indexFile.lastModified() >= csv.lastModified()) {
            cargarIndiceDesdeDisco(indexFile);
            System.out.println("Índice cargado desde disco: " + offsets.length + " páginas.");
            return;
        }

        System.out.println("Construyendo índice nuevo (puede tardar varios minutos la primera vez)...");
        long inicio = System.currentTimeMillis();
        List<Long> lista = new ArrayList<>();

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(csv), 1 << 20)) {
            long pos = 0;
            int b;
            while ((b = in.read()) != -1) {
                pos++;
                if (b == '\n') break;
            }
            lista.add(pos);
            long lineasEnPagina = 0;
            while ((b = in.read()) != -1) {
                pos++;
                if (b == '\n') {
                    lineasEnPagina++;
                    if (lineasEnPagina == PAGE_SIZE) {
                        lista.add(pos);
                        lineasEnPagina = 0;
                    }
                }
            }
        }

        offsets = new long[lista.size()];
        for (int i = 0; i < lista.size(); i++) offsets[i] = lista.get(i);

        guardarIndiceEnDisco(indexFile);
        long ms = System.currentTimeMillis() - inicio;
        System.out.println("Índice construido en " + ms + " ms, " + offsets.length + " páginas.");
    }

    private void guardarIndiceEnDisco(File indexFile) {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(indexFile)))) {
            out.writeInt(offsets.length);
            for (long o : offsets) out.writeLong(o);
        } catch (IOException e) {
            System.err.println("No se pudo guardar el índice en disco: " + e.getMessage());
        }
    }

    private void cargarIndiceDesdeDisco(File indexFile) throws IOException {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(indexFile)))) {
            int n = in.readInt();
            offsets = new long[n];
            for (int i = 0; i < n; i++) offsets[i] = in.readLong();
        }
    }

    @GetMapping("/personas")
    public Map<String, Object> listarPersonas(@RequestParam(defaultValue = "0") int page) throws Exception {
        String maquina = System.getenv().getOrDefault("MAQUINA", "desconocida");
        String contenedor;
        try {
            contenedor = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            contenedor = "desconocido";
        }

        if (offsets == null || page < 0 || page >= offsets.length) {
            return Map.of(
                    "page", page,
                    "count", 0,
                    "data", List.of(),
                    "maquina", maquina,
                    "contenedor", contenedor
            );
        }

        List<Person> personas = new ArrayList<>(PAGE_SIZE);
        try (RandomAccessFile raf = new RandomAccessFile(CSV_PATH, "r")) {
            raf.seek(offsets[page]);
            String linea;
            int leidas = 0;
            while (leidas < PAGE_SIZE && (linea = raf.readLine()) != null) {
                personas.add(parseLinea(linea));
                leidas++;
            }
        }

        return Map.of(
                "page", page,
                "size", PAGE_SIZE,
                "count", personas.size(),
                "data", personas,
                "maquina", maquina,
                "contenedor", contenedor
        );
    }

    private Person parseLinea(String linea) {
        String[] partes = linea.split(",");
        return new Person(
                Integer.parseInt(partes[0].trim()),
                partes[1].trim(),
                partes[2].trim(),
                partes[3].trim(),
                partes[4].trim(),
                partes[5].trim()
        );
    }
}