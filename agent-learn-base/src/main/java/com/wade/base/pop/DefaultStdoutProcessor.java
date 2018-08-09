package com.wade.base.pop;

import com.wade.base.PerfStats;
import com.wade.base.PerfStatsProcessor;
import com.wade.base.meta.ResultDataBean;
import com.wade.base.transport.NettySendMessage;
import com.wade.base.transport.bean.RpcRequest;

import java.util.List;

/**
 * @author :lwy
 * @date 2018/7/29 17:49
 * 默认的输出
 */
public class DefaultStdoutProcessor implements PerfStatsProcessor {
    @Override
    public void process(List<PerfStats> perfStatsList, int injectMethodCount, long startMillis, long stopMillis) {
        List<ResultDataBean> list = DefaultFormatter.getFormatStr(perfStatsList, injectMethodCount, startMillis, stopMillis);
        for (ResultDataBean bean : list) {
            System.out.println(bean);

            NettySendMessage message=new NettySendMessage("localhost",6000);
            RpcRequest rpcRequest=new RpcRequest();
            rpcRequest.setServiceVersion(bean.toString());
            message.send(rpcRequest);
        }

        System.out.println();
    }
}
