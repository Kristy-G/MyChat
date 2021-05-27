package org.example.chat_client.network;

public interface ChatMessageService {
    void send(String msg);
    void receive(String msg);
    void connect();
}
