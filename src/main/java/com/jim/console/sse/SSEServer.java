package com.jim.console.sse;

import com.jim.console.config.SSEServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console
 * @author: Administrator
 * @date: 2020-12-24 15:36
 * @description：TODO
 */

@Slf4j
public class SSEServer {
	private EventLoopGroup boss;
	private EventLoopGroup workers;
	private Channel serverChannel;

	private String host;
	private int port;
	private int bufferSize;

	private ChannelGroup channels;


	public SSEServer(String host, int port, int bufferSize) {
		this.host = host;
		this.port = port;
		this.bufferSize = bufferSize;
	}

	public ChannelOutputStream start() {
		channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		ChannelOutputStream channelOutputStream = new ChannelOutputStream(channels, bufferSize);
		try {
			boss = new NioEventLoopGroup();
			workers = new NioEventLoopGroup();
			ServerBootstrap serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(boss, workers)
					.channel(NioServerSocketChannel.class)
					.childOption(ChannelOption.SO_KEEPALIVE, true)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline pipeline = ch.pipeline();
							pipeline.addLast(new IdleStateHandler(30, 30, 30, TimeUnit.SECONDS));
							pipeline.addLast(new HttpRequestDecoder());
							pipeline.addLast(new HttpResponseEncoder());
							pipeline.addLast(new ServerLogEventHandler(channelOutputStream, host, port));
						}
					});
			serverChannel = serverBootstrap.bind(host, port).sync().channel();
			log.debug("SSE server started on host {}, port {}", host, port);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return channelOutputStream;
	}

	public void stop() {
		if (serverChannel != null) {
			try {
				serverChannel.close().sync();
				channels.close().sync();
				workers.shutdownGracefully();
				boss.shutdownGracefully();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
