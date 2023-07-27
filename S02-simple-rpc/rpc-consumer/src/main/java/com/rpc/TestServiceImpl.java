package com.rpc;

import com.srpc.annotation.RpcService;

@RpcService
public class TestServiceImpl implements TestService{

    @Override
    public String getMsg(String msg) {
        return null;
    }
}
