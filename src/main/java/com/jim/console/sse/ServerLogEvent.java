package com.jim.console.sse;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.Serializable;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console.sse
 * @author: Administrator
 * @date: 2020-12-24 17:09
 * @description：TODO
 */
@Data
public class ServerLogEvent extends LoggingEvent implements Serializable {
	private static final long serialVersionUID = -8654122649596294772L;
	private ObjectMapper mapper = new ObjectMapper();
	private Level level;
	private String data;

	public ServerLogEvent(Level level, String data) {
		this.level = level;
		this.data = data;
	}

	@Override
	public String toString() {
		try {
			LogEventDTO dto = new LogEventDTO();
			dto.data = this.data;
			dto.level = this.level.toInt();
			return mapper.writeValueAsString(dto);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "";
	}

	@Data
	public static class LogEventDTO {
		private int level;
		private String data;
	}
}
