package com.jim.console.sse;

import ch.qos.logback.classic.Level;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.springframework.http.HttpHeaders.*;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console.sse
 * @author: Administrator
 * @date: 2020-12-24 17:02
 * @description：TODO
 */
@Slf4j
public class ServerLogEventHandler extends SimpleChannelInboundHandler<Object> {

	private final String welcomeMessage;
	private ChannelRegistry channels;
	private String streamPath = "/stream";

	public ServerLogEventHandler(ChannelRegistry allChannels, String host, int port) {
		this.channels = allChannels;
		this.welcomeMessage = "Connected successfully on LOG stream from " + host + ":" + port;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object message) throws Exception {
		//message match
		if (message instanceof HttpRequest) {
			HttpRequest req = (HttpRequest) message;
			String uri = req.uri();
			if (uri.matches(streamPath)) {
				log.info("new stream request");
				Channel channel = channelHandlerContext.channel();
				//channel.write(response);
				ServerLogEvent event = new ServerLogEvent(Level.INFO, welcomeMessage);
				ByteBuf buffer = Unpooled.copiedBuffer(event.toString(), Charset.defaultCharset());
				HttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, buffer);

				response.headers().set(CONTENT_TYPE, "text/event-stream; charset=UTF-8");
				response.headers().set(CACHE_CONTROL, "no-cache");
				response.headers().set(CONNECTION, "keep-alive");
				response.headers().set(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
				response.headers().add(HttpHeaderNames.CONTENT_LENGTH, buffer.readableBytes());
				channelHandlerContext.writeAndFlush(response).sync();
				channels.addChannel(channel);
			}
		}
	}

	private void sendHttpResponse(ChannelHandlerContext channelHandlerContext, HttpRequest req, DefaultFullHttpResponse resp) {
		// 如果response不为200
		if (resp.status() != OK) {
			ByteBuf content = Unpooled.copiedBuffer(resp.status().codeAsText(), StandardCharsets.UTF_8);
			resp.content().writeBytes(content);
			content.release();
			HttpUtil.setContentLength(resp, content.readableBytes());
		}

		ChannelFuture channelFuture = channelHandlerContext.channel().writeAndFlush(resp);
		if (!HttpUtil.isKeepAlive(req) || resp.status().code() != 200) {
			channelFuture.addListener(ChannelFutureListener.CLOSE);
		}
	}
}
