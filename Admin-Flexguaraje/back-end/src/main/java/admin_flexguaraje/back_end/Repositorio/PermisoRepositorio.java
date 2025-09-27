package admin_flexguaraje.back_end.Repositorio;

import admin_flexguaraje.back_end.Modelo.Permisos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermisoRepositorio extends JpaRepository<Permisos, Long> {
    Optional<Permisos> findByIdPermiso(Long idPermiso);
    Optional<Permisos> findByNombrePermiso(String nombrePermiso);
    void deleteByIdPermiso(Long idPermiso);

}
