package zhuboss.gateway.tx.meter.dlt645;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import zhuboss.framework.spring.SpringContextUtils;
import zhuboss.framework.utils.JavaUtil;
import zhuboss.gateway.dict.ProtocolEnum;
import zhuboss.gateway.service.Dlt645Service;
import zhuboss.gateway.tx.gateway.IResponseDecoder;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
public class Dlt645ResponseDecoder extends IResponseDecoder {

    enum Part{
        START, //0x68H (1b)
        ADDR, //表号 (12b)
        START2, //0x68H (1b)
        CONTROL, //控制码 (1b)
        DATA_LEN, //数据长度 (1b)
        DATA, //数据(DATA_LEN byte)
        CRC,
        END //0x16H (1b)
    }

    ProtocolEnum protocolEnum;
    String addr;
    int dataLen;
    int dataId;
    BigDecimal value;
    byte[] data;
    byte csB; //CRC检验码
    boolean ok;

    Part nextPart = Part.START;

    private Map<Integer,Integer> scale;
    public Dlt645ResponseDecoder(ProtocolEnum protocolEnum){
        this.protocolEnum = protocolEnum;
        scale = SpringContextUtils.getApplicationContext().getBean(Dlt645Service.class).getScaleMap(protocolEnum);
    }
    @Override
    public void readData(ByteBuf buf) throws Exception {

        if(buf.readableBytes() < 1 ) return;

        if(nextPart == Part.START){
            byte b = buf.readByte();
            if(b == Dlt645Constants.FE){
                nextPart = Part.START;
            }else if(b == Dlt645Constants.START){
                nextPart = Part.ADDR;
                csB = b;
            }else{
                throw new RuntimeException("START must be 0x68 or 0xfe,but:" +JavaUtil.bytesToHexString(new byte[]{b}));
            }
        }else if(nextPart == Part.ADDR){
            byte[] addrBytes = new byte[6];
            for(int i=5;i>-1;i--){
                addrBytes[i] = buf.readByte();
                csB += addrBytes[i];
            }
            this.addr = BcdUtil.bcd2Str(addrBytes).replaceAll("^[0]+","");
            nextPart = Part.START2;
        }else if(nextPart == Part.START2){
            byte b = buf.readByte();
            if(b != 0x68){
                throw new RuntimeException("START != 0x68");
            }
            nextPart = Part.CONTROL;
            csB += b;
        }else if(nextPart == Part.CONTROL){
            byte b = buf.readByte();
            if( (b&0x80) == 0x80){ //从站正确应答
                this.ok = true;
            }else{
                log.error("控制码：从站应答异常");
                this.ok = false;
            }
            nextPart = Part.DATA_LEN;
            csB += b;
        }else if(nextPart == Part.DATA_LEN){
            //部分电表长度、功能码，混乱,84012818368,fefefefefe6834131600000068810433337534c116
            byte b = buf.readByte(); //这个长度包含DATA_ID数据标识，2位
//            dataLen = (int) b - 2;
            dataLen = b&0xff;
            csB += b;
            nextPart = Part.DATA;
        }else if(nextPart == Part.DATA){
            data = new byte[dataLen];
            buf.readBytes(data,0,dataLen);
            nextPart = Part.CRC;
            for(int i=0;i<dataLen;i++){
                csB += data[i];
                data[i]= (byte) (data[i] - 0x33);
            }
            if(this.ok){
                byte[] dtaIdBytes = protocolEnum.equals(ProtocolEnum.DLT2007)? (new byte[]{data[3], data[2], data[1], data[0]}) : new byte[]{data[1], data[0]};
                dataId = Integer.parseInt(JavaUtil.bytesToHexString(dtaIdBytes),16);
                byte[] valueBytes = new byte[dataLen - (protocolEnum.equals(ProtocolEnum.DLT2007)?4:2)];
                for(int x=dataLen-1;x>(protocolEnum.equals(ProtocolEnum.DLT2007)?3:1);x--){
                    valueBytes[dataLen-1-x] = data[x];  //{data[7],data[6],data[5],data[4]};
                }
                this.value = new BigDecimal(BcdUtil.bcd2Str(valueBytes));
                for(int i=0;i<scale.get(dataId);i++){
                    this.value = this.value.divide(new BigDecimal(10));
                }
                this.value.setScale(scale.get(dataId));
            }
            log.debug(JavaUtil.bytesToHexString(data));
        }else if(nextPart == Part.CRC){
            byte b = buf.readByte();
            if(b != csB){
                throw new RuntimeException("CS校验码错误");
            }
            nextPart = Part.END;
        }else if(nextPart == Part.END){
            byte b = buf.readByte();
            if(b != 0x16){
                throw new RuntimeException("START != 0x16");
            }
            //触发消息接收成功处理
            Dlt645Message dlt645Message = new Dlt645Message(protocolEnum,Long.parseLong(addr),dataId,this.value,ok);
            SpringContextUtils.getBean(Dlt645ReceiveHandler.class).handle(this.getChannel(), dlt645Message);

            nextPart = Part.START;
        }
        readData(buf);
    }

    @Override
    public void reset() {

    }

    @Override
    public boolean hasParsing() {
        return !nextPart.equals(Part.START);
    }

}
