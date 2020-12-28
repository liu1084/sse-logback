package com.jim.console.sse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.Encoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.SubstituteLoggerFactory;

import java.io.IOException;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console.sse
 * @author: Administrator
 * @date: 2020-12-28 13:32
 * @description：TODO
 */
@Slf4j
@Data
public class BrowserAppender<E> extends OutputStreamAppender<ILoggingEvent> {

	protected String host = "172.16.0.126";
	protected int port = 8765;
	protected int buffer = 1024;
	private SSEServer webServer;
	private ChannelOutputStream stream;

	@Override
	public void start() {
		if (encoder == null) {
			addError("No encoder was configured. Use <encoder> to specify the fully qualified class name of the encoder to use");
			return;
		}

		if (StringUtils.isBlank(host)) {
			addWarn("No host was configured.");
			host = "localhost";
		}

		if (port == 0) {
			addWarn("No port was configured.");
			port = 8765;
		}

		if (buffer == 0) {
			addWarn("No buffer was configured.");
			buffer = 1024;
		}

		encoder.start();

		new Thread(() -> {
			waitForSlf4jInitialization();
			if (webServer == null) {
				webServer = new SSEServer(host, port, buffer);
				stream = webServer.start();
				setOutputStream(stream);
			}
			BrowserAppender.super.start();
		}).start();
	}

	private void waitForSlf4jInitialization() {
		try {
			Integer lock = new Integer(0);
			synchronized (lock) {
				while (isSlf4jUninitialized()) {
					lock.wait(100);
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isSlf4jUninitialized() {
		ILoggerFactory factory = LoggerFactory.getILoggerFactory();
		return factory instanceof SubstituteLoggerFactory;
	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	protected void append(ILoggingEvent eventObject) {
		try {
			byte[] bytes = eventObject.getFormattedMessage().getBytes();
			stream.write(bytes, 0, bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
