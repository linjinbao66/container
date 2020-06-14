package container.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.*;

/**
 * @author linjinbao66@gmail.com
 * @date 2020/6/7
 * 处理文件socket
 */
public class BinaryHandler extends BinaryWebSocketHandler {
    private static final Logger LOG = LoggerFactory.getLogger(BinaryHandler.class);
    private static String filePath = "E:\\tmp";
    private static String fileOut;      //输出的文件名称
    private static File file;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        LOG.info("handleTextMessage");
        fileOut = message.getPayload();
        file = new File(filePath, fileOut);
        LOG.info("文件已经创建");
        LOG.info("文件的路径为：" + file.getAbsolutePath());
        try {
            session.sendMessage(new TextMessage("开始传输文件："+ file.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws IOException {
        LOG.info("handleBinaryMessage");
        int payloadLength = message.getPayloadLength();
        try {
            session.sendMessage(new TextMessage("progress" + payloadLength));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fstream = null;
        byte[] b = message.getPayload().array();
        try {
            fstream = new FileOutputStream(file, true);
            fstream.write(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            fstream.close();
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> webSocketMessage) throws Exception {
        if (webSocketMessage instanceof TextMessage){
            handleTextMessage(session, (TextMessage)webSocketMessage);
        }else if (webSocketMessage instanceof BinaryMessage){
            handleBinaryMessage(session, (BinaryMessage)webSocketMessage);
        }else if (webSocketMessage instanceof PongMessage) {
            handlePongMessage(session, (PongMessage) webSocketMessage);
        }else {
            throw new IllegalStateException("Unexpected WebSocket message type: " + webSocketMessage);
        }
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        LOG.info("handlePongMessage");
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        LOG.info("handleTransportError");
    }

    @Override
    public boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }

    public BinaryHandler() {
        super();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        LOG.info("连接建立，开始通信");
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LOG.info("连接关闭，，，");
    }
}
