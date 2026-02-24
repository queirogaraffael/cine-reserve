package com.example.cinema.api.domain.room.exception;

public class NumeroDeQuartoJaCadastradoException extends RuntimeException{
    public NumeroDeQuartoJaCadastradoException() {
    }

    public NumeroDeQuartoJaCadastradoException(String message) {
        super(message);
    }
}
