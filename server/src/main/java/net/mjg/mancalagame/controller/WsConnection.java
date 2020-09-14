package net.mjg.mancalagame.controller;

import net.mjg.mancalagame.messages.Message;

import java.io.IOException;

public interface WsConnection {
    void sendMessage(Message message) throws IOException;
}

