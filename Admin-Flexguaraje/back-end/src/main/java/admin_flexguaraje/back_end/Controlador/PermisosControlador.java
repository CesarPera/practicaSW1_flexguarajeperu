package admin_flexguaraje.back_end.Controlador;

import admin_flexguaraje.back_end.Modelo.Permisos;
import admin_flexguaraje.back_end.Modelo.Roles;
import admin_flexguaraje.back_end.Negocio.PermisosNegocio;
import admin_flexguaraje.back_end.Negocio.RolesNegocio;
import admin_flexguaraje.back_end.Repositorio.RolesRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/permisos")
public class PermisosControlador {

    @Autowired
    private PermisosNegocio permisosNegocio ;

    @Autowired
    private RolesNegocio rolesNegocio;

    // Listar permisos
    @GetMapping("/listar_permisos")
    public ResponseEntity<?> listarPermisos() {
        return ResponseEntity.ok(permisosNegocio.listarPermisos());
    }

    @GetMapping("/roles_activos")
    public ResponseEntity<List<Roles>> obtenerRolesActivos() {
        List<Roles> rolesActivos = permisosNegocio.obtenerRolesActivos();
        return ResponseEntity.ok(rolesActivos);
    }

    @PostMapping("/crear_permisos")
    public ResponseEntity<String> crearPermiso(@RequestBody Map<String, Object> body) {
        String nombreRol = (String) body.get("nombreRol");
        String nombrePermiso = (String) body.get("nombrePermiso");

        // Convertir los valores a mayúsculas
        nombreRol = nombreRol != null ? nombreRol.toUpperCase() : null;
        nombrePermiso = nombrePermiso != null ? nombrePermiso.toUpperCase() : null;

        // Validación para el nombreRol (solo letras y espacios)
        if (!Pattern.matches("^[A-ZÁÉÍÓÚ\\s]+$", nombreRol)) {
            return ResponseEntity.badRequest().body("El nombre del rol solo puede contener letras y espacios.");
        }

        // Validación para nombrePermiso (solo letras y espacios)
        if (!Pattern.matches("^[A-ZÁÉÍÓÚ\\s]+$", nombrePermiso)) {
            return ResponseEntity.badRequest().body("El nombre del permiso solo puede contener letras y espacios.");
        }

        // Verificar si el rol existe
        if (!rolesNegocio.existeRolConNombre(nombreRol)) {
            return ResponseEntity.badRequest().body("El rol con el nombre " + nombreRol + " no existe.");
        }

        // Verificar si el permiso ya existe
        if (permisosNegocio.existePermisoConNombre(nombrePermiso)) {
            return ResponseEntity.badRequest().body("Ya existe un permiso con el nombre " + nombrePermiso + ".");
        }

        try {
            permisosNegocio.crearPermiso(nombreRol, nombrePermiso);
            return ResponseEntity.ok("Permiso creado exitosamente.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al crear el permiso: " + e.getMessage());
        }
    }

    // Actualizar nombre de permiso
    @PutMapping("/actualizar_nombre_permiso")
    public ResponseEntity<String> actualizarNombrePermiso(@RequestBody Map<String, Object> body) {
        String idPermisoStr = String.valueOf(body.get("idPermiso"));
        String nuevoNombre = (String) body.get("nuevoNombre");

        // Validación para idPermiso (solo numérico)
        if (idPermisoStr == null || !idPermisoStr.matches("[0-9]+")) {
            return ResponseEntity.badRequest().body("El idPermiso debe ser un número válido.");
        }

        Long idPermiso = Long.valueOf(idPermisoStr);

        // Convertir el nuevo nombre a mayúsculas
        nuevoNombre = nuevoNombre != null ? nuevoNombre.toUpperCase() : null;

        // Validación para nuevoNombre (solo letras y espacios)
        if (nuevoNombre == null || nuevoNombre.isEmpty() || !Pattern.matches("^[A-ZÁÉÍÓÚ\\s]+$", nuevoNombre)) {
            return ResponseEntity.badRequest().body("El nuevo nombre del permiso solo puede contener letras y espacios.");
        }

        // Verificar si el permiso existe
        Permisos permisoActual = permisosNegocio.obtenerPermisoPorId(idPermiso);
        if (permisoActual == null) {
            return ResponseEntity.badRequest().body("El permiso con el ID " + idPermiso + " no existe.");
        }

        // Verificar si el estado del permiso es ACTIVO
        if (permisoActual.getEstado() != Permisos.estadoPermisos.Activo) {
            return ResponseEntity.badRequest().body("El permiso con el ID " + idPermiso + " no está activo, no se puede actualizar.");
        }

        // Verificar si ya existe un permiso con el nuevo nombre
        if (permisosNegocio.existePermisoConNombre(nuevoNombre)) {
            return ResponseEntity.badRequest().body("Ya existe un permiso con el nombre " + nuevoNombre + ".");
        }

        try {
            Permisos permisoActualizado = permisosNegocio.actualizarNombrePermiso(idPermiso, nuevoNombre);
            return ResponseEntity.ok("Permiso actualizado exitosamente: " + permisoActualizado.getNombrePermiso());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el permiso: " + e.getMessage());
        }
    }


    // Actualizar estado de permiso
    @PutMapping("/actualizar_estado_permiso")
    public ResponseEntity<String> actualizarEstadoPermiso(@RequestBody Map<String, Object> body) {
        String idPermisoStr = String.valueOf(body.get("idPermiso"));

        // Validación para idPermiso (solo numérico)
        if (idPermisoStr == null || !idPermisoStr.matches("[0-9]+")) {
            return ResponseEntity.badRequest().body("El idPermiso debe ser un número válido.");
        }

        Long idPermiso = Long.valueOf(idPermisoStr);

        // Verificar si el permiso existe
        if (!permisosNegocio.existePermiso(idPermiso)) {
            return ResponseEntity.badRequest().body("El permiso con el ID " + idPermiso + " no existe.");
        }

        try {
            Permisos permisoActualizado = permisosNegocio.actualizarEstadoPermiso(idPermiso);
            return ResponseEntity.ok("Estado del permiso actualizado exitosamente: " + permisoActualizado.getEstado());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar el estado del permiso: " + e.getMessage());
        }
    }

    @DeleteMapping("/eliminar_permiso")
    public ResponseEntity<String> eliminarPermiso(@RequestBody Map<String, Object> body) {
        // Extraemos el idPermiso desde el cuerpo de la solicitud
        String idPermisoStr = String.valueOf(body.get("idPermiso"));

        // Validación para asegurarse de que idPermiso es numérico
        if (idPermisoStr == null || !idPermisoStr.matches("[0-9]+")) {
            return ResponseEntity.badRequest().body("El idPermiso debe ser un número válido.");
        }

        Long idPermiso = Long.valueOf(idPermisoStr);

        // Llamamos al negocio para eliminar el permiso
        String result = permisosNegocio.eliminarPermiso(idPermiso);

        if (result.contains("no existe")) {
            return ResponseEntity.badRequest().body("El permiso con el ID " + idPermiso + " no existe.");
        }

        return ResponseEntity.ok(result);
    }

}
