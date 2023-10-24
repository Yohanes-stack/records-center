package test.srpc.provider;

import test.srpc.annotation.RpcService;
import test.srpc.common.RpcServiceNameBuilder;
import test.srpc.common.ServiceMeta;
import test.srpc.configuration.RpcProperties;
import test.srpc.protocol.codec.RpcDecoder;
import test.srpc.protocol.codec.RpcEncoder;
import test.srpc.protocol.handler.RpcRequestHandler;
import test.srpc.registry.RegistryFactory;
import test.srpc.registry.RegistryService;
import test.srpc.registry.RegistryType;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

public class ProviderPostProcess implements InitializingBean, BeanPostProcessor, ApplicationContextAware {

    private RpcProperties properties;

    private Logger logger = LoggerFactory.getLogger(ProviderPostProcess.class);


    private final Map<String, Object> rpcServiceMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(()->{
            startRpcServer();
        });

        thread.setDaemon(true);
        thread.start();
    }

    private void startRpcServer() {
        int serverPort = properties.getProtocolPort();
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new RpcRequestHandler(rpcServiceMap));
                    }
                })
                .childOption(ChannelOption.SO_KEEPALIVE,true);
        ChannelFuture channelFuture = bootstrap.bind(this.properties.getRegisterAddr(),serverPort);
        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService annotation = beanClass.getAnnotation(RpcService.class);
        if(annotation != null){
            String interfaceName = beanClass.getInterfaces()[0].getName();
            String version = annotation.serviceVersion();
            try{
                RegistryService registryService = RegistryFactory.getInstance(RegistryType.valueOf(properties.getRegisterType()));
                ServiceMeta serviceMeta = new ServiceMeta();
                serviceMeta.setServiceAddr(properties.getRegisterAddr());
                serviceMeta.setServiceVersion(version);
                serviceMeta.setServicePort(properties.getProtocolPort());
                serviceMeta.setServiceName(interfaceName);
                registryService.register(serviceMeta);

                rpcServiceMap.put(RpcServiceNameBuilder.buildServiceKey(serviceMeta.getServiceName(),serviceMeta.getServiceVersion()), bean);
                logger.info("register server {} version {}",interfaceName,version);
            }catch (Exception e){

            }
        }
        return BeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        properties = applicationContext.getBean(RpcProperties.class);
    }
}
