package admin_flexguaraje.back_end.Controlador;

import admin_flexguaraje.back_end.Modelo.Roles;
import admin_flexguaraje.back_end.Negocio.RolesNegocio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/roles")
public class RolesControlador {
    @Autowired
    private RolesNegocio rolesNegocio;

    @GetMapping("/listar_roles")
    public ResponseEntity<List<Roles>> listarRoles() {
        List<Roles> roles = rolesNegocio.listarRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/crear_rol")
    public ResponseEntity<String> crearRol(@RequestBody Map<String, Object> body) {
        String nombreRol = (String) body.get("nombreRol");

        // Convertir a mayúsculas
        nombreRol = nombreRol != null ? nombreRol.toUpperCase() : null;

        // Validación para el nombreRol (solo letras y espacios)
        if (!Pattern.matches("^[A-ZÁÉÍÓÚ\\s]+$", nombreRol)) {
            return ResponseEntity.badRequest().body("El nombre del rol solo puede contener letras y espacios.");
        }

        // Validación de existencia del rol
        try {
            rolesNegocio.crearRol(nombreRol);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok("Rol creado exitosamente.");
    }

    @PutMapping("/actualizar_nombre_rol")
    public ResponseEntity<String> actualizarRol(@RequestBody Map<String, Object> body) {
        String idRolString = (String) body.get("idRol");
        String nombreRol = (String) body.get("nombreRol");

        // Validación para idRol (solo numérico)
        if (idRolString == null || !idRolString.matches("[0-9]+")) {
            return ResponseEntity.badRequest().body("El idRol debe ser un número válido.");
        }
        Long idRol = Long.valueOf(idRolString);

        // Convertir a mayúsculas
        nombreRol = nombreRol != null ? nombreRol.toUpperCase() : null;

        // Validación para nombreRol (solo letras y espacios)
        if (nombreRol == null || nombreRol.isEmpty() || !Pattern.matches("^[A-ZÁÉÍÓÚ\\s]+$", nombreRol)) {
            return ResponseEntity.badRequest().body("El nombre del rol solo puede contener letras y espacios.");
        }

        // Verificar si el rol existe
        if (!rolesNegocio.existsById(idRol)) {
            return ResponseEntity.badRequest().body("El rol con el ID " + idRol + " no existe.");
        }

        // Verificar si el estado del rol es ACTIVO
        Roles rolActual = rolesNegocio.obtenerRolPorId(idRol);
        if (rolActual.getEstado() != Roles.estadoRoles.Activo) {
            return ResponseEntity.badRequest().body("El rol con el ID " + idRol + " no está activo, no se puede actualizar.");
        }


        // Validación de existencia del rol con el mismo nombre
        try {
            Roles rolActualizado = rolesNegocio.actualizarRol(idRol, nombreRol);
            return ResponseEntity.ok("Rol actualizado exitosamente: " + rolActualizado.getNombreRol());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PutMapping("/actualizar_estado_rol")
    public ResponseEntity<String> actualizarEstadoRol(@RequestBody Map<String, Object> body) {
        String idRolString = (String) body.get("idRol");

        // Validación para idRol (solo numérico)
        if (idRolString == null || !idRolString.matches("[0-9]+")) {
            return ResponseEntity.badRequest().body("El idRol debe ser un número válido.");
        }
        Long idRol = Long.valueOf(idRolString);

        // Verificar si el rol existe
        if (!rolesNegocio.existsById(idRol)) {
            return ResponseEntity.badRequest().body("El rol con el ID " + idRol + " no existe.");
        }

        // Intentar actualizar el estado del rol
        try {
            Roles rolActualizado = rolesNegocio.actualizarEstadoRol(idRol);
            return ResponseEntity.ok("Estado del rol actualizado exitosamente: " + rolActualizado.getEstado());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar_rol")
    public ResponseEntity<String> eliminarRol(@RequestBody Map<String, Object> body) {
        // Extraemos el idRol desde el cuerpo de la solicitud
        String idRolStr = String.valueOf(body.get("idRol"));

        // Validación para asegurarse de que idRol es numérico
        if (idRolStr == null || !idRolStr.matches("[0-9]+")) {
            return ResponseEntity.badRequest().body("El idRol debe ser un número válido.");
        }

        Long idRol = Long.valueOf(idRolStr);

        // Llamamos al negocio para eliminar el rol
        String result = rolesNegocio.eliminarRol(idRol);

        if (result.contains("no existe")) {
            return ResponseEntity.badRequest().body("El rol con el ID " + idRol + " no existe.");
        }

        return ResponseEntity.ok(result);
    }
}
