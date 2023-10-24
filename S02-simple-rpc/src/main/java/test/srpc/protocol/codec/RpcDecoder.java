package test.srpc.protocol.codec;

import test.srpc.common.RpcRequest;
import test.srpc.common.RpcResponse;
import test.srpc.protocol.MsgHeader;
import test.srpc.protocol.MsgType;
import test.srpc.protocol.ProtocolConstants;
import test.srpc.protocol.RpcProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import test.srpc.protocol.seriallzation.RpcSerialization;
import test.srpc.protocol.seriallzation.SerializationFactory;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < ProtocolConstants.HEADER_TOTAL_LEN) {
            return;
        }

        in.markReaderIndex();

        short magic = in.readShort();

        if (magic != ProtocolConstants.MAGIC) {
            throw new IllegalArgumentException("magic number is illegal, " + magic);
        }

        byte version = in.readByte();

        byte serializeType = in.readByte();

        byte msgType = in.readByte();

        byte status = in.readByte();

        long requestId = in.readLong();

        int dataLength = in.readInt();
        //如果可读字节小于消息体长度，说明还没有接受完整消息体，回退并返回
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];

        in.readBytes(data);

        MsgType msgTypeEnum = MsgType.findByType(msgType);

        if (msgTypeEnum == null) {
            return;
        }
        MsgHeader header = new MsgHeader.Builder()
                .setMagic(magic)
                .setVersion(version)
                .setSerialization(serializeType)
                .setStatus(status)
                .setMsgType(msgType)
                .setMsgLen(dataLength)
                .build();
        //序列化器
        RpcSerialization rpcSerialization = SerializationFactory.getRpcSerialization(header.getSerialization());
        switch (msgTypeEnum){
            //请求消息
            case REQUEST:
                RpcRequest request = rpcSerialization.deserialize(data,RpcRequest.class);
                if(request != null){
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
                break;
            case RESPONSE:
                RpcResponse response = rpcSerialization.deserialize(data, RpcResponse.class);
                if (response != null) {
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
                break;
        }

    }
}
