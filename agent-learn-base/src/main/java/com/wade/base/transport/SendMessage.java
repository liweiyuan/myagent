package com.wade.base.transport;

import com.wade.base.meta.ResultDataBean;
import com.wade.base.transport.bean.RpcRequest;
import com.wade.base.transport.bean.RpcResponse;

/**
 * @author :lwy
 * @date 2018/8/9 14:29
 */
public interface SendMessage {

    RpcResponse send(RpcRequest request);
}
