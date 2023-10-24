package test.rc.netty.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf requestByteBuf = (ByteBuf) msg;
        // 解码
        Packet packet = PacketCodeC.INSTANCE.decode(requestByteBuf);
        if(packet instanceof  LoginRequestPacket){
            LoginRequestPacket loginRequestPacket = (LoginRequestPacket) packet;
            LoginResponsePacket loginResponsePacket = new LoginResponsePacket();
            loginResponsePacket.setVersion(packet.getVersion());
            // 登录校验
            if (valid(loginRequestPacket)) {
                // 校验成功
                loginResponsePacket.setSuccess(true);
                System.out.println("客户端登录成功！");
            } else {
                // 校验失败
                loginResponsePacket.setReason("账号或密码错误");
                loginResponsePacket.setSuccess(false);
                System.out.println("客户端登录失败！");
            }
            // 编码，结果发送给客户端
            ByteBuf responseByteBuf = PacketCodeC.INSTANCE.encode(ctx.alloc().buffer(), loginResponsePacket);
            ctx.channel().writeAndFlush(responseByteBuf);
        }
    }
    private boolean valid(LoginRequestPacket loginRequestPacket) {
        //这里可以查询数据库，验证用户的账号密码是否正确
        return true;
    }
}
