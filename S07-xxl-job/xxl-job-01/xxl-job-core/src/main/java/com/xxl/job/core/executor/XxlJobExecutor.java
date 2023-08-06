package com.xxl.job.core.executor;

import com.xxl.job.core.biz.AdminBiz;
import com.xxl.job.core.biz.client.AdminBizClient;
import com.xxl.job.core.server.EmbedServer;
import com.xxl.job.core.util.IpUtil;
import com.xxl.job.core.util.NetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class XxlJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobExecutor.class);
    //服务器的地址，也就是调度中心的地址
    private String adminAddress;
    //token令牌，这个令牌要和调度中心的令牌一致，否则调度中心校验报错
    private String accessToken;
    //执行器名称
    private String appName;
    //执行器的地址，如果为空，使用默认地址 。 格式: ip+port
    private String address;
    //注册器的ip
    private String ip;
    //端口号
    private int port;
    //执行器的日志收集地址
    private String logPath;
    //执行器日志的保留天数，一般为30天
    private int logRetentionDays;


    public void start() {
        //初始化地址存储集合
        //在集群环境下，会有多个调度中心，所以，执行器要把自己分别注册到这些调度中心里
        initAdminBizList(adminAddress, accessToken);

        initEmbedServer(address, ip, port, appName, accessToken);
    }

    private void initEmbedServer(String address, String ip, int port, String appName, String accessToken) {
        //
        port = port > 0 ? port : NetUtil.findAvailablePort(9999);
        //
        ip = ip != null && ip.trim().length() > 0 ? ip : IpUtil.getIp();
        //判断地址是否为null
        if (address == null || address.trim().length() == 0) {
            //如果为空说明没有配置，就把刚刚得到的ip和地址拼接起来
            String ip_port_address = IpUtil.getIpPort(ip, port);
            address = "http://{ip_port}/".replace("{ip_port}", ip_port_address);
        }
        //校验token
        if (accessToken == null || accessToken.trim().length() == 0) {
            logger.warn(">>>>>>>>>>> xxl-job accessToken is empty. To ensure system security, please set the accessToken.");
        }
        //创建执行器短的Netty服务器
        embedServer = new EmbedServer();
        embedServer.start(address,port,appName,accessToken);
    }

    private void initAdminBizList(String adminAddress, String accessToken) {
        if (adminAddress != null && adminAddress.trim().length() > 0) {
            for (String address : adminAddress.trim().split(",")) {
                AdminBiz adminBiz = new AdminBizClient(address.trim(), accessToken);
                if (adminBizList == null) {
                    adminBizList = new ArrayList<AdminBiz>();
                }
                adminBizList.add(adminBiz);
            }
        }
    }

    //该成员变量是用来存放AdminBizClient对象的，而该对象是用来向调度中心发送注册信息的
    private static List<AdminBiz> adminBizList;
    //服务器独享
    private EmbedServer embedServer = null;

    public String getAdminAddress() {
        return adminAddress;
    }

    public XxlJobExecutor setAdminAddress(String adminAddress) {
        this.adminAddress = adminAddress;
        return this;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public XxlJobExecutor setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public XxlJobExecutor setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public XxlJobExecutor setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public XxlJobExecutor setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return port;
    }

    public XxlJobExecutor setPort(int port) {
        this.port = port;
        return this;
    }

    public String getLogPath() {
        return logPath;
    }

    public XxlJobExecutor setLogPath(String logPath) {
        this.logPath = logPath;
        return this;
    }

    public int getLogRetentionDays() {
        return logRetentionDays;
    }

    public XxlJobExecutor setLogRetentionDays(int logRetentionDays) {
        this.logRetentionDays = logRetentionDays;
        return this;
    }
}
