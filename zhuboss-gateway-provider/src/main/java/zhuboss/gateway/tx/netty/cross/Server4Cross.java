package zhuboss.gateway.tx.netty.cross;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import zhuboss.gateway.tx.netty.handler.BaseUpstreamHandler;
import zhuboss.gateway.tx.netty.handler.UpstreamDecoderChooser;

import java.util.ArrayList;
import java.util.List;

/**
 * 参考：http://www.zicheng.net/article/84.htm
 * @author Administrator
 *
 */
@Component
@Slf4j
public class Server4Cross implements InitializingBean,DisposableBean {
	@Value("${debug.port}")
	Integer port;

	EventLoopGroup connectionManagerGroup = new NioEventLoopGroup();//接收进来的连接
	EventLoopGroup workerGroup = new NioEventLoopGroup(100); //处理已经被接收的连接

	ServerBootstrap b = new ServerBootstrap();
	@Override
	public void afterPropertiesSet() throws Exception {
			 b = b.group(connectionManagerGroup, workerGroup);
			 //ServerSocketChannel以NIO的selector为基础进行实现的，用来接收新的连接
			 b = b.channel(NioServerSocketChannel.class);
			 b.childOption(ChannelOption.SO_RCVBUF, 1024*1024);
			b = b.option(ChannelOption.TCP_NODELAY,true).option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(1024, 1024, 1024*1024));
			b = b.childHandler(new ChannelInitializer<SocketChannel>() {
	
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					 //upstream<read> 从上到下
					ch.pipeline().addLast(CrossDecoderChooser.class.getName(),new CrossDecoderChooser());

				}
				 
			 });
			 b = b.option(ChannelOption.SO_BACKLOG, 128);
			 b = b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000); //Netty的处理是在调用建连之后马上启动一个延时任务，该任务在timeout时间过后执行，任务中执行关闭操作
			 b = b.childOption(ChannelOption.SO_KEEPALIVE, true);
			 ChannelFuture channelFuture = b.bind(port);

	}
	@Override
	public void destroy() throws Exception {
		log.info("Server4Client destroy...");
		workerGroup.shutdownGracefully();
		 connectionManagerGroup.shutdownGracefully();
		
	}

	
}