package container.handler;

import container.service.SSHService;
import container.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

@Component
public class SSHHandler implements WebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SSHHandler.class);

    @Autowired
    private SSHService sshService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOG.info("用户:{},连接WebSSH", session.getAttributes().get(Constant.USER_UUID_KEY));
        sshService.initConnection(session);
        LOG.info("afterConnectionEstablished " + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        LOG.info("handleMessage");
        if (message instanceof TextMessage){
            LOG.info("TextMessage");
            sshService.recvHandle(((TextMessage) message).getPayload(), session);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        LOG.info("handleTransportError");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOG.info("afterConnectionClosed");
    }

    @Override
    public boolean supportsPartialMessages() {
        LOG.info("supportsPartialMessages");
        return false;
    }
}
