package admin_flexguaraje.back_end.Repositorio;

import admin_flexguaraje.back_end.Modelo.Permisos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermisoRepositorio extends JpaRepository<Permisos, Long> {
    Optional<Permisos> findByIdPermiso(Long idPermiso);
    Optional<Permisos> findByNombrePermiso(String nombrePermiso);
}
