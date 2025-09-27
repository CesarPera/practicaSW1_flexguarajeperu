package admin_flexguaraje.back_end.Repositorio;


import admin_flexguaraje.back_end.Modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario,Long> {
    List<Usuario> findTop20ByOrderByIdUsuarioDesc(); // Consulta automática
    Optional<Usuario> findByDni(String dni);

}
