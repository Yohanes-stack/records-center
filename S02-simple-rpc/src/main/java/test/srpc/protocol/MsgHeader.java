package test.srpc.protocol;

import java.io.Serializable;

/**
 * 消息头
 */
public class MsgHeader implements Serializable {
    //魔数
    private short magic;
    //协议版本号
    private byte version;
    //序列化算法
    private byte serialization;
    //数据类型
    private byte msgType;
    //状态
    private byte status;
    //请求ID
    private long requestId;
    //数据长度
    private int msgLen;

    private MsgHeader(Builder builder) {
        this.magic = builder.magic;
        this.version = builder.version;
        this.serialization = builder.serialization;
        this.msgType = builder.msgType;
        this.status = builder.status;
        this.requestId = builder.requestId;
        this.msgLen = builder.msgLen;
    }


    public short getMagic() {
        return magic;
    }

    public void setMagic(short magic) {
        this.magic = magic;
    }

    public byte getVersion() {
        return version;
    }

    public void setVersion(byte version) {
        this.version = version;
    }

    public byte getSerialization() {
        return serialization;
    }

    public void setSerialization(byte serialization) {
        this.serialization = serialization;
    }

    public byte getMsgType() {
        return msgType;
    }

    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public int getMsgLen() {
        return msgLen;
    }

    public void setMsgLen(int msgLen) {
        this.msgLen = msgLen;
    }
    public static class Builder {
        private short magic;
        private byte version;
        private byte serialization;
        private byte msgType;
        private byte status;
        private long requestId;
        private int msgLen;

        public Builder() {
        }

        public Builder setMagic(short magic) {
            this.magic = magic;
            return this;
        }

        public Builder setVersion(byte version) {
            this.version = version;
            return this;
        }

        public Builder setSerialization(byte serialization) {
            this.serialization = serialization;
            return this;
        }

        public Builder setMsgType(byte msgType) {
            this.msgType = msgType;
            return this;
        }

        public Builder setStatus(byte status) {
            this.status = status;
            return this;
        }

        public Builder setRequestId(long requestId) {
            this.requestId = requestId;
            return this;
        }

        public Builder setMsgLen(int msgLen) {
            this.msgLen = msgLen;
            return this;
        }

        public MsgHeader build() {
            return new MsgHeader(this);
        }
    }
}
