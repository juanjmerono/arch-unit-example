package dev.example.domain.model;

public class Usuario {

    private Usuario() {}

    public static Usuario of () {
        return new Usuario();
    }

}
