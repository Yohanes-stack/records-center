package com.rc.netty.protocol;

public interface Command {
    //定义登陆请求指令和响应指令为 1 和2 ，其他指令同理
    Byte LOGIN_REQUEST = 1;
    Byte LOGIN_RESPONSE = 2;
}
