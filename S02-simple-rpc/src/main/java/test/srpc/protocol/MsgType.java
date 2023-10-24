package test.srpc.protocol;

public enum MsgType {
    REQUEST,
    RESPONSE,
    HEARTBEAT;

    public static MsgType findByType(int type) {
        return MsgType.values()[type];
    }
}
