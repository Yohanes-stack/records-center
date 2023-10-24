package test.srpc.protocol.seriallzation;

import java.io.IOException;

public interface RpcSerialization {

    <T> byte[] serialize(T obj) throws IOException;

    <T> T deserialize(byte[] data ,Class<T> clz) throws IOException;
}
