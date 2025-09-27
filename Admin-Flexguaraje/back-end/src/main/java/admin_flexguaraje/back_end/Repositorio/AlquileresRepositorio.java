package admin_flexguaraje.back_end.Repositorio;

import admin_flexguaraje.back_end.Modelo.Alquileres;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AlquileresRepositorio extends JpaRepository<Alquileres, Long> {
    List<Alquileres> findByEstado(Alquileres.estadoAlquiler estado);
    @Query("SELECT a FROM Alquileres a WHERE a.espacio.idEspacio = :idEspacio AND a.estado = :estado")
    Optional<Alquileres> findByEspacio_IdEspacioAndEstado(@Param("idEspacio") Long idEspacio, @Param("estado") Alquileres.estadoAlquiler estado);
    public List<Alquileres> findAlquileresActivosByClienteDni(String dni);
    List<Alquileres> findByClienteDni(String dni); // Buscar alquileres por el DNI del cliente
}

