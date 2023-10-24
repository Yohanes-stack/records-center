package test.srpc.protocol.seriallzation;


public class SerializationFactory {
    public static RpcSerialization getRpcSerialization(byte serializationType) {
        SerializationTypeEnum typeEnum = SerializationTypeEnum.findByType(serializationType);

        switch (typeEnum) {
            case HESSIAN:
                return new HessianSerialization();
            default:
                throw new IllegalArgumentException("serialization type is illegal, " + serializationType);
        }
    }
}
