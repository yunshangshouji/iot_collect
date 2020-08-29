package zhuboss.gateway.tx.netty;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.*;
import zhuboss.gateway.tx.netty.handler.log.LogUpstreamByteBufDecoder;
import zhuboss.gateway.tx.netty.handler.*;
import zhuboss.gateway.tx.netty.handler.log.LogDownByteBufEncoder;
import zhuboss.gateway.tx.meter.modbus.ModbusRequestEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.stereotype.Component;

/**
 * 参考：http://www.zicheng.net/article/84.htm
 * @author Administrator
 *
 */
@Component
@Slf4j
public class Server4Client implements InitializingBean,DisposableBean {
	@Value("${listen.port}")
	String ports;
	
	@Value("${debugTcp}")
	Boolean debugTcp;

	EventLoopGroup connectionManagerGroup = new NioEventLoopGroup();//接收进来的连接
	EventLoopGroup workerGroup = new NioEventLoopGroup(100); //处理已经被接收的连接

	@Override
	public void afterPropertiesSet() throws Exception {
			 /**
			  * ServerBootstrap 是一个启动NIO服务的辅助启动类
			  */
			 ServerBootstrap b = new ServerBootstrap();
			 b = b.group(connectionManagerGroup, workerGroup);
			 //ServerSocketChannel以NIO的selector为基础进行实现的，用来接收新的连接
			 b = b.channel(NioServerSocketChannel.class);
			 b.childOption(ChannelOption.SO_RCVBUF, 1024*1024);
		b = b.option(ChannelOption.TCP_NODELAY,true).option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024, 1024, 1024*1024));
		b = b.childHandler(new ChannelInitializer<SocketChannel>() {
	
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					 //upstream<read> 从上到下
					ch.pipeline().addLast(UpstreamDecoderChooser.class.getName(),new UpstreamDecoderChooser(debugTcp)); //1. ByteBuf->TransPackage、ByteBuf(HttpResponse)
					 ch.pipeline().addLast(BaseUpstreamHandler.class.getName(),new BaseUpstreamHandler()); //2. dispatch TransPackage & write ByteBuf to server4Www channel
					 
//					ch.pipeline().addLast("Dsc2DTURegisterAckMessageEncoder",new HongdianRegisterAckMessageEncoder());

//					ch.pipeline().addLast("Dlt1997RequestEncoder",new Dlt1997RequestEncoder());
				}
				 
			 });
			 b = b.option(ChannelOption.SO_BACKLOG, 128);
			 b = b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000); //Netty的处理是在调用建连之后马上启动一个延时任务，该任务在timeout时间过后执行，任务中执行关闭操作
			 b = b.childOption(ChannelOption.SO_KEEPALIVE, true);
			 List<ChannelFuture> channelFutureList = new ArrayList<>();
			 String[] groups = ports.split(",");
			 for(String group : groups){
                 int beginPort = Integer.parseInt(group.split("-")[0]);
                 int endPort = Integer.parseInt(group.split("-")[1]);
                 for(int port = beginPort;port <=endPort; port ++){
                     channelFutureList.add(b.bind(port));
                     log.info("监听端口：" + port);
                 }
             }

//			 for(ChannelFuture channelFuture : channelFutureList){
//				 channelFuture.channel().closeFuture().sync();
//				 log.info("监听端口：" + (channelFuture.channel().localAddress()));
//			 }
		
			 
	}
	@Override
	public void destroy() throws Exception {
		log.info("Server4Client destroy...");
		workerGroup.shutdownGracefully();
		 connectionManagerGroup.shutdownGracefully();
		
	}

	
}