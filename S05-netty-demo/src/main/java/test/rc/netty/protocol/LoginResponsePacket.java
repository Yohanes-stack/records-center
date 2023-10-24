package test.rc.netty.protocol;

public class LoginResponsePacket extends Packet{
    //定义用户信息
    private boolean success;
    //如果失败，返回的消息
    private String reason;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public Byte getCommand() {
        return Command.LOGIN_RESPONSE;
    }
}
