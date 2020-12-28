package com.jim.console.config;

import com.jim.console.sse.ChannelOutputStream;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console.config
 * @author: Administrator
 * @date: 2020-12-24 16:19
 * @description：TODO
 */

@Configuration
public class AppConfigBean {

	@Autowired
	private SSEServerConfig sseServerConfig;

	@Bean
	public ChannelGroup channels() {
		return new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	}

	@Bean
	public ChannelOutputStream channelOutputStream () {
		return new ChannelOutputStream(channels(), sseServerConfig.getReplayBufferSize());
	}
}
