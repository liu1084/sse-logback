/**
 * @project: green26-web-console
 * @packageName:
 * @author: Administrator
 * @date: 2020-12-24 13:17
 * @description：TODO
 */

(function() {
	if (typeof (EventSource) !== "undefined") {
		let streamUrl = "http://172.16.0.126:8765/stream";
		let source = new EventSource(streamUrl);
		let eventLevel = "DEBUG";
		source.onmessage = function (event) {
			console.log(event.data);
		};

		source.onerror = function(err) {
			console.log(err);
		};

		source.addEventListener("DEBUG", function (event) {
			console.debug(event.data);
		});
		source.addEventListener("INFO", function (event) {
			console.info(event.data);
		});

		source.addEventListener("WARN", function (event) {
			console.warn(event.data);
		});
		source.addEventListener("ERROR", function (event) {
			console.error(event.data);
		});
	} else {
		alert("Your browser does not support SSE, hence web-logback will not work properly");
	}
})();