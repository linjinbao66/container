package container.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import container.pojo.SSHConnectInfo;
import container.pojo.WebSSHData;
import container.service.SSHService;
import container.util.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SSHServiceImpl implements SSHService {
    private static Map<String, Object> sshMap = new ConcurrentHashMap<>();
    private Logger LOG = LoggerFactory.getLogger(SSHServiceImpl.class);
    private ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 初始化
     * @param session
     */
    @Override
    public void initConnection(WebSocketSession session) {
        JSch jSch = new JSch();
        SSHConnectInfo sshConnectInfo = new SSHConnectInfo();
        sshConnectInfo.setjSch(jSch);
        sshConnectInfo.setWebSocketSession(session);
        String uuid = String.valueOf(session.getAttributes().get(Constant.USER_UUID_KEY));
        //将这个ssh连接信息放入map中
        sshMap.put(uuid, sshConnectInfo);
    }

    @Override
    public void recvHandle(String buffer, WebSocketSession session) {
        ObjectMapper objectMapper = new ObjectMapper();
        WebSSHData webSSHData = null;
        try {
            webSSHData = objectMapper.readValue(buffer, WebSSHData.class);
        } catch (IOException e) {
            LOG.error("Json转换异常");
            LOG.error("异常信息:{}", e.getMessage());
            return;
        }

        String userId = String.valueOf(session.getAttributes().get(Constant.USER_UUID_KEY));

        if (Constant.WEBSSH_OPERATE_CONNECT.equals(webSSHData.getOperate())){
            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
            //启动线程异步处理
            WebSSHData finalWebSSHData = webSSHData;

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        connectToSSH(sshConnectInfo, finalWebSSHData, session);
                    } catch (JSchException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        LOG.error("webssh连接异常");
                        LOG.error("异常信息:{}", e.getMessage());
                        close(session);
                    }
                }
            });
        }else if (Constant.WEBSSH_OPERATE_COMMAND.equals(webSSHData.getOperate())){
            String command = webSSHData.getCommand();
            SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
            if (sshConnectInfo != null) {
                try {
                    transToSSH(sshConnectInfo.getChannel(), command);
                } catch (IOException e) {
                    LOG.error("webssh连接异常");
                    LOG.error("异常信息:{}", e.getMessage());
                    close(session);
                }
            }
        }else {
            LOG.error("不支持的操作");
            close(session);
        }

    }
    @Override
    public void close(WebSocketSession session) {
        String userId = String.valueOf(session.getAttributes().get(Constant.USER_UUID_KEY));
        SSHConnectInfo sshConnectInfo = (SSHConnectInfo) sshMap.get(userId);
        if (sshConnectInfo != null) {
            if (sshConnectInfo.getChannel() != null) sshConnectInfo.getChannel().disconnect();
            sshMap.remove(userId);
        }
    }

    private void connectToSSH(SSHConnectInfo sshConnectInfo, WebSSHData webSSHData, WebSocketSession webSocketSession) throws JSchException, IOException {
        Session session = null;
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session = sshConnectInfo.getjSch().getSession(webSSHData.getUsername(), webSSHData.getHost(), webSSHData.getPort());
        session.setConfig(config);
        session.setPassword(webSSHData.getPassword());
        session.connect(30000);
        Channel channel = session.openChannel("shell");
        channel.connect(3000);
        sshConnectInfo.setChannel(channel);
        transToSSH(channel, "\r");
        InputStream inputStream = channel.getInputStream();
        try {
            //循环读取
            byte[] buffer = new byte[1024];
            int i = 0;
            //如果没有数据来，线程会一直阻塞在这个地方等待数据。
            while ((i = inputStream.read(buffer)) != -1) {
                sendMessage(webSocketSession, Arrays.copyOfRange(buffer, 0, i));
            }

        } finally {
            session.disconnect();
            channel.disconnect();
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    @Override
    public void sendMessage(WebSocketSession session, byte[] buffer) throws IOException {
        session.sendMessage(new TextMessage(buffer));
    }

    private void transToSSH(Channel channel, String command) throws IOException {
        if (channel != null) {
            OutputStream outputStream = channel.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
        }
    }
}
