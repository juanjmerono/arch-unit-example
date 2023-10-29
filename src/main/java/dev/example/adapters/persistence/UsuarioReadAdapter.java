package dev.example.adapters.persistence;

import org.springframework.stereotype.Repository;

import dev.example.application.port.UsuarioReadRepository;
import dev.example.domain.model.Usuario;

@Repository
public class UsuarioReadAdapter implements UsuarioReadRepository {

    @Override
    public Usuario read() {
        return Usuario.of();
    }
    
    

}
