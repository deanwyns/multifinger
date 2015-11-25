package com.realdolmen.multifinger.connection;

import java.io.Serializable;

public class NetworkCommand implements Serializable {
    private Connection.Commands command;
    private Transferable dto;

    public Connection.Commands getCommand() {
        return command;
    }

    public void setCommand(Connection.Commands command) {
        this.command = command;
    }

    public Transferable getDto() {
        return dto;
    }

    public void setDto(Transferable dto) {
        this.dto = dto;
    }
}
