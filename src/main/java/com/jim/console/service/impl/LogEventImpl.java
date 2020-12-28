package com.jim.console.service.impl;

import ch.qos.logback.classic.Level;
import com.jim.console.service.ILogEvent;
import com.jim.console.sse.ServerLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console.service.impl
 * @author: Administrator
 * @date: 2020-12-25 15:50
 * @description：TODO
 */
@Service
@Slf4j
@EnableScheduling
public class LogEventImpl implements ILogEvent {
	ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());;

	@Scheduled(cron = "0/5 * * * * ?")
	public void log() {
		executorService.submit(() -> {
			ServerLogEvent serverLogEvent = new ServerLogEvent(Level.INFO, "cron execute per 5 seconds.");
			log.info("{}", serverLogEvent);
		} );
	}
}
