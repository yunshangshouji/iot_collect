package zhuboss.gateway.tx.gateway.raw;

import io.netty.channel.Channel;
import io.netty.handler.codec.ByteToMessageDecoder;
import zhuboss.gateway.tx.gateway.IResponseDecoder;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractRawMessageDecoder extends ByteToMessageDecoder {

//    private Map<Class<?extends IResponseDecoder> ,IResponseDecoder> responseDecoderSet = new HashMap<>();

//    IResponseDecoder responseDecoder;
//    /**
//     * 切换接收数据解码器
//     * @param cls
//     */
//    public   void switchResponseDecoder(Class<? extends IResponseDecoder> cls, Channel channel){
//        if(responseDecoderSet.containsKey(cls)){
//            responseDecoder = responseDecoderSet.get(cls);
//            responseDecoder.reset();
//        }else {
//            try {
//                responseDecoder = cls.newInstance();
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        responseDecoder.setChannel(channel);
//    }
//
//    public IResponseDecoder getResponseDecoderClass() {
//        return responseDecoder;
//    }
}
