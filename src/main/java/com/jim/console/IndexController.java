package com.jim.console;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @project: green26-web-console
 * @packageName: com.jim.console
 * @author: Administrator
 * @date: 2020-12-24 13:21
 * @description：TODO
 */
@Controller
@CrossOrigin("*")
@Slf4j
public class IndexController {

	@GetMapping
	public String index() {
		return "index";
	}
}
