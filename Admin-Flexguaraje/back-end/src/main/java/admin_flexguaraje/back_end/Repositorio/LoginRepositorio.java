package admin_flexguaraje.back_end.Repositorio;

import admin_flexguaraje.back_end.Modelo.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepositorio extends JpaRepository<Cuenta, Long> {
    Optional<Cuenta> findByEmailAndPassword(String email, String password);
    Optional<Cuenta> findByEmail(String email);
}
