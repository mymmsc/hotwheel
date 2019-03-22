package org.hotwheel.spring.helper;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Created by pasta on 15/9/22.
 */
public class SFTPHelper {

    private static final Logger log = LoggerFactory.getLogger(SFTPHelper.class);

    private static final String separator = File.separator;

    private String host;

    private int port;

    private String userName;

    private String passWord;

    private String remotePath;

    private Session sshSession;

    private ChannelSftp channelSftp;

    public SFTPHelper(String host, int port, String userName, String passWord, String remotePath) {
        this.host = host;
        this.port = port;
        this.userName = userName;
        this.passWord = passWord;
        this.remotePath = remotePath;
    }

    private ChannelSftp connect() {
        try {
            JSch jsch = new JSch();
            if (port == -1) {
                sshSession = jsch.getSession(userName, host);
            } else {
                sshSession = jsch.getSession(userName, host, port);
            }
            sshSession.setPassword(passWord);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
        } catch (Exception e) {
            log.error("occurs exception：", e);
        }
        return channelSftp;
    }

    //上传文件夹 不含文件夹内文件夹递归
    public void upload(File uploadDir, String listFiles) {
        try {
            if (!this.connect().isConnected()) {
                throw new Exception("建立连接失败！");
            }
            channelSftp.cd(remotePath);
            String uploadDirName = uploadDir.getName();
            try {
                channelSftp.mkdir(uploadDirName);
            } catch (Exception e1) {
                //
            }
            channelSftp.cd(remotePath + separator + uploadDirName);
            //String[] files = uploadDir.list();
            String[] files = listFiles.split(",");
            for (int i = 0; i < files.length; i++) {
                File uploadFile = new File(uploadDir.getPath() + separator + files[i]);
                if (!uploadFile.isFile()) {
                    log.info("{} is not a normal file, sftp ignore.", uploadFile.getName());
                } else {
                    log.info("{}, sftp upload...", uploadFile.getName());
                    channelSftp.put(new FileInputStream(uploadFile), uploadFile.getName());
                    log.info("{}, sftp upload...ok", uploadFile.getName());
                }
            }
        } catch (Exception e) {
            log.error("occurs exception：", e);
        } finally {
            this.channelSftp.disconnect();
        }
    }

    public void disconnect() {
        this.sshSession.disconnect();
    }

}
