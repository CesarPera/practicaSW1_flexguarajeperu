package admin_flexguaraje.back_end.Controlador;

import admin_flexguaraje.back_end.Modelo.Alquileres;
import admin_flexguaraje.back_end.Modelo.Espacio;
import admin_flexguaraje.back_end.Negocio.AlquileresNegocio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/alquileres")
public class AlquileresControlador {

    @Autowired
    private final AlquileresNegocio alquileresNegocio;

    @Autowired
    public AlquileresControlador(AlquileresNegocio alquileresNegocio) {
        this.alquileresNegocio = alquileresNegocio;
    }

    // Endpoint para listar todos los alquileres generales
    @GetMapping("/listar_alquileres_general")
    public ResponseEntity<List<Alquileres>> listarAlquileres() {
        List<Alquileres> listaAlquileres = alquileresNegocio.listarAlquileres();
        return ResponseEntity.ok(listaAlquileres);
    }

    // LISTAR ALQUILERES CON SOLO ESTADO "NO IGNORAR"
    @GetMapping("/listar_alquileres")
    public List<Alquileres> obtenerAlquileresNoIgnorar() {
        return alquileresNegocio.obtenerAlquileresNoIgnorar();
    }

    @PostMapping("/crear_alquiler")
    public ResponseEntity<Object> agregarClienteAlEspacio(@RequestBody Map<String, Object> body) {
        try {
            // Verificar si los valores son nulos o vacíos
            if (body.get("dni") == null || body.get("dni").toString().isEmpty() ||
                    body.get("codigoEspacio") == null || body.get("codigoEspacio").toString().isEmpty() ||
                    body.get("fechaFin") == null || body.get("fechaFin").toString().isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Faltan parámetros en la solicitud. Complete todos los campos."), HttpStatus.BAD_REQUEST);
            }

            String dni = body.get("dni").toString();
            String codigoEspacio = body.get("codigoEspacio").toString();
            String fechaFinStr = body.get("fechaFin").toString();

            // Validar DNI: verificar longitud y formato
            if (dni.length() != 8) {
                return ResponseEntity.badRequest().body("El DNI debe tener exactamente 8 caracteres.");
            }
            if (!dni.matches("\\d+")) { // Validar que el DNI solo contenga números
                return ResponseEntity.badRequest().body("El DNI solo debe contener números.");
            }
            if (!alquileresNegocio.existeDni(dni)) {
                return new ResponseEntity<>(Map.of("message", "CLIENTE CON DNI " + dni + " NO EXISTE."), HttpStatus.NOT_FOUND);
            }

            // Validar formato de fecha
            LocalDate fechaFin;
            try {
                fechaFin = LocalDate.parse(fechaFinStr);
            } catch (Exception e) {
                return new ResponseEntity<>(Map.of("message", "Formato de fecha inválido. Use el formato YYYY-MM-DD."), HttpStatus.BAD_REQUEST);
            }

            // Validar que la fecha de fin sea posterior o igual a la fecha actual
            if (fechaFin.isBefore(LocalDate.now())) {
                return new ResponseEntity<>(Map.of("message", "La fecha de fin debe ser igual o posterior a la fecha actual."), HttpStatus.BAD_REQUEST);
            }

            // Validar existencia del código de espacio
            Long idEspacio = alquileresNegocio.obtenerIdPorCodigoEspacio(codigoEspacio);
            if (idEspacio == null) {
                return new ResponseEntity<>(Map.of("message", "El código del espacio ingresado no existe."), HttpStatus.BAD_REQUEST);
            }

            // Verificar si el espacio ya tiene un alquiler activo
            if (alquileresNegocio.espacioTieneAlquiler(codigoEspacio)) {
                return new ResponseEntity<>(Map.of("message", "El espacio ya tiene un alquiler activo."), HttpStatus.CONFLICT);
            }

            // Crear el alquiler
            Alquileres nuevoAlquiler = alquileresNegocio.agregarClienteAlEspacio(dni, idEspacio, fechaFin);
            return new ResponseEntity<>(nuevoAlquiler, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/actualizar_estado")
    public ResponseEntity<Object> actualizarEstadoEspacio(@RequestBody Map<String, Object> body) {
        try {
            // Verificar campos requeridos
            if (body.get("codigoEspacio") == null || body.get("codigoEspacio").toString().isEmpty() ||
                    body.get("nuevoEstado") == null || body.get("nuevoEstado").toString().isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Faltan parámetros en la solicitud. Complete todos los campos."), HttpStatus.BAD_REQUEST);
            }

            String codigoEspacio = body.get("codigoEspacio").toString();
            String nuevoEstado = body.get("nuevoEstado").toString();

            // Validar existencia del espacio
            if (!alquileresNegocio.existeCodigoEspacio(codigoEspacio)) {
                return new ResponseEntity<>(Map.of("message", "El código del espacio ingresado no existe."), HttpStatus.BAD_REQUEST);
            }

            // Validar estado válido
            try {
                Espacio.EstadoEspacio estado = Espacio.EstadoEspacio.valueOf(nuevoEstado);
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>(Map.of("message", "El estado ingresado no es válido."), HttpStatus.BAD_REQUEST);
            }

            Espacio espacioActualizado = alquileresNegocio.actualizarEstadoPorCodigo(codigoEspacio, Espacio.EstadoEspacio.valueOf(nuevoEstado));
            return new ResponseEntity<>(espacioActualizado, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Ocurrió un error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/actualizar_alquiler")
    public ResponseEntity<Object> actualizarClienteEnAlquiler(@RequestBody Map<String, Object> body) {
        try {
            // Verificar campos requeridos
            if (body.get("codigoEspacio") == null || body.get("codigoEspacio").toString().isEmpty() ||
                    body.get("nuevoDniCliente") == null || body.get("nuevoDniCliente").toString().isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Faltan parámetros en la solicitud. Complete todos los campos."), HttpStatus.BAD_REQUEST);
            }

            String codigoEspacio = body.get("codigoEspacio").toString();
            String nuevoDni = body.get("nuevoDniCliente").toString();

            // Validar DNI
            if (nuevoDni.length() != 8) {
                return ResponseEntity.badRequest().body("El DNI debe tener exactamente 8 caracteres.");
            }
            if (!nuevoDni.matches("\\d+")) { // Validar que el DNI solo contenga números
                return ResponseEntity.badRequest().body("El DNI solo debe contener números.");
            }
            if (!alquileresNegocio.existeDni(nuevoDni)) {
                return new ResponseEntity<>(Map.of("message", "CLIENTE CON DNI " + nuevoDni + " NO EXISTE."), HttpStatus.NOT_FOUND);

            }


            // Validar existencia del espacio
            if (!alquileresNegocio.existeCodigoEspacio(codigoEspacio)) {
                return new ResponseEntity<>(Map.of("message", "El código del espacio ingresado no existe."), HttpStatus.BAD_REQUEST);
            }

            Alquileres alquilerActualizado = alquileresNegocio.actualizarClienteEnAlquiler(codigoEspacio, nuevoDni);
            return new ResponseEntity<>(alquilerActualizado, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    // LITERAL SE ESTARIA ELIMINANDO ALQUILER PERO SOLO ACTUALIZA
    @PutMapping("/eliminar_alquiler")
    public ResponseEntity<Object> actualizarEstadoAlquiler(@RequestBody Map<String, String> body) {
        try {
            if (body.get("codigoEspacio") == null || body.get("codigoEspacio").isEmpty()) {
                return new ResponseEntity<>(Map.of("message", "Faltan parámetros en la solicitud. Complete todos los campos."), HttpStatus.BAD_REQUEST);
            }

            String codigoEspacio = body.get("codigoEspacio");
            if (!alquileresNegocio.existeCodigoEspacio(codigoEspacio)) {
                return new ResponseEntity<>(Map.of("message", "El código del espacio ingresado no existe."), HttpStatus.BAD_REQUEST);
            }

            alquileresNegocio.actualizarEstadoAlquilerparaeliminar(codigoEspacio);
            return new ResponseEntity<>(Map.of("message", "Estado del alquiler actualizado a IGNORAR correctamente"), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Ocurrió un error: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}