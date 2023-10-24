package test.srpc.protocol.codec;

import test.srpc.protocol.MsgHeader;
import test.srpc.protocol.RpcProtocol;
import test.srpc.protocol.seriallzation.RpcSerialization;
import test.srpc.protocol.seriallzation.SerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {

    @Override
    protected void encode(ChannelHandlerContext chc, RpcProtocol<Object> data, ByteBuf byteBuf) throws Exception {
        MsgHeader header = data.getHeader();

        byteBuf.writeShort(header.getMagic());

        byteBuf.writeByte(header.getVersion());

        byteBuf.writeByte(header.getSerialization());

        byteBuf.writeByte(header.getMsgType());

        byteBuf.writeByte(header.getStatus());

        byteBuf.writeLong(header.getRequestId());

        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(header.getSerialization());
        byte[] serializeBody = rpcSerialization.serialize(data.getBody());
        byteBuf.writeInt(serializeBody.length);
        byteBuf.writeBytes(serializeBody);
    }
}
