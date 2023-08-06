package com.xxl.job.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;

public class NetUtil {
    private static Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public static int findAvailablePort(int defaultPort) {
        int portTmp = defaultPort;
        while (defaultPort < 65535) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp++;
            }
        }
        portTmp = defaultPort--;
        while (portTmp > 0) {
            if (!isPortUsed(portTmp)) {
                return portTmp;
            } else {
                portTmp--;
            }
        }
        throw new RuntimeException("no available port.");
    }

    private static boolean isPortUsed(int port) {
        boolean used = false;

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            used = false;
        } catch (IOException e) {
            logger.info(">>>>>>>>>>> xxl-job, port[{}] is in use.", port);
            used = true;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    logger.warn(">>>>>>>>>>> xxl-job,server socket  close failed");
                }
            }
        }
        return used;
    }

}
