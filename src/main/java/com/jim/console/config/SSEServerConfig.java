package com.jim.console.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console.config
 * @author: Administrator
 * @date: 2020-12-24 15:39
 * @description：TODO
 */
@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "sse.server")
public class SSEServerConfig {
	private String host;
	private int port;
	private int replayBufferSize;
}
