package container.service;

import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface SSHService {
    void initConnection(WebSocketSession session);
    void recvHandle(String buffer, WebSocketSession session);
    void sendMessage(WebSocketSession session, byte[] buffer) throws IOException;
    void close(WebSocketSession session);
}
