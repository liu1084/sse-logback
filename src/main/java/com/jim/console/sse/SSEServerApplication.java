package com.jim.console.sse;

import com.jim.console.config.AppConfigBean;
import com.jim.console.config.SSEServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console.sse
 * @author: Administrator
 * @date: 2020-12-25 12:45
 * @description：TODO
 */

//@Component
@Order(2)
@Slf4j
public class SSEServerApplication implements ApplicationRunner {
	@Autowired
	private SSEServerConfig sseServerConfig;
	private SSEServer sseServer;
	@Override
	public void run(ApplicationArguments args) {
		sseServer = new SSEServer(sseServerConfig.getHost(),sseServerConfig.getPort(), sseServerConfig.getReplayBufferSize());
		sseServer.start();
		Runtime.getRuntime().addShutdownHook(new SSEServerShutdown());
	}


	private class SSEServerShutdown extends Thread {
		@Override
		public void run() {
			if (sseServer != null) {
				sseServer.stop();
				log.debug("SSE server shutdown.");
			}
		}
	}
}
