package admin_flexguaraje.back_end.Repositorio;

import admin_flexguaraje.back_end.Modelo.Cuenta;
import admin_flexguaraje.back_end.Modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaRepositorio extends JpaRepository<Cuenta, Long> {
    List<Cuenta> findTop20ByOrderByIdCuentaDesc();
    boolean existsByEmail(String email);
    Optional<Cuenta> findByUsuario(Usuario usuario);
    boolean existsByUsuarioDni(String dni);
    Optional<Cuenta> findByUsuarioDni(String dni);
}
