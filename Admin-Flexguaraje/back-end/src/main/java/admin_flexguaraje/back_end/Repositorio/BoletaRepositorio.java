package admin_flexguaraje.back_end.Repositorio;

import admin_flexguaraje.back_end.Modelo.Alquileres;
import admin_flexguaraje.back_end.Modelo.Boleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BoletaRepositorio extends JpaRepository<Boleta, Long> {
    Optional<Boleta> findByCodigoBoleta(String codigoBoleta);
    boolean existsByCodigoBoleta(String codigoBoleta);
    boolean existsByAlquileres(Alquileres alquileres);
    Boleta findByAlquileresClienteDniAndAlquileresEspacioCodigoEspacio(String dni, String codigoEspacio);
}

