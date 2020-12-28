package com.jim.console.sse;

import ch.qos.logback.classic.Level;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console.sse
 * @author: Administrator
 * @date: 2020-12-24 17:01
 * @description：TODO
 */
public interface ChannelRegistry {
	void addChannel(Channel channel);
	List<ServerLogEvent>  getServerLogEvents();
	void setCurrentLevel(Level level);
}
