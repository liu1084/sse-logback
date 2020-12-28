package com.jim.console.sse;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console.sse
 * @author: Administrator
 * @date: 2020-12-24 17:06
 * @description：TODO
 */
@Data
@Slf4j
public class ChannelOutputStream extends OutputStream implements ChannelRegistry {

	private ChannelGroup channels;
	private Level level;
	private int bufferSize;
	private final List<ServerLogEvent> serverLogEvents;
	private ObjectMapper mapper = new ObjectMapper();

	public ChannelOutputStream(ChannelGroup channels, int replayBufferSize) {
		this.channels = channels;
		this.bufferSize = replayBufferSize;
		this.level = Level.OFF;
		serverLogEvents = new ArrayList<>();
	}

	@Override
	public void addChannel(Channel channel) {
		channels.add(channel);
		synchronized (serverLogEvents) {
			for (ServerLogEvent event : serverLogEvents) {
				ByteBuf buffer = Unpooled.copiedBuffer(event.toString(), StandardCharsets.UTF_8);
				HttpContent content = new DefaultHttpContent(buffer);
				channel.write(content);
			}
			channel.flush();
		}
	}

	@Override
	public void setCurrentLevel(Level level) {
		this.level = level;
	}

	@Override
	public void write(int b) throws IOException {
		//not be called
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		String logStub = new String(b, off, len);
		ServerLogEvent logEvent = new ServerLogEvent(level, logStub);
		log.debug(logStub);
		synchronized (serverLogEvents) {
			if (serverLogEvents.size() > bufferSize) {
				serverLogEvents.remove(0);
			}
			serverLogEvents.add(logEvent);
		}
		String log = logEvent.toString();
		ByteBuf buffer = Unpooled.copiedBuffer(log, StandardCharsets.UTF_8);
		//ByteBuf buffer = Unpooled.wrappedBuffer(b);
		HttpContent httpContent = new DefaultHttpContent(buffer);
		if (channels.size() > 0) {
			channels.write(httpContent);
		}
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		channels.flush();
	}

}
