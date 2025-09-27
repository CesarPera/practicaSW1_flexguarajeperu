package admin_flexguaraje.back_end.Repositorio;

import admin_flexguaraje.back_end.Modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
    public Cliente findByDni(String dni);
    List<Cliente> findByNombreAndApellidoPaternoAndApellidoMaterno(String nombre, String apellidoPaterno, String apellidoMaterno);
    public boolean existsByDni(String dni);
    public boolean existsByEmail(String email);
}
