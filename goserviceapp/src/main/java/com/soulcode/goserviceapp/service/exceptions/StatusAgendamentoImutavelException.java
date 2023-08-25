package com.soulcode.goserviceapp.service.exceptions;

public class StatusAgendamentoImutavelException extends RuntimeException{
    public StatusAgendamentoImutavelException() {
        super("Agendamento imutável");
    }

    public StatusAgendamentoImutavelException(String message) {
        super(message);
    }
}
