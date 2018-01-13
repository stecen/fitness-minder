var http = require("http");
var url = require("url");

exports = module.exports;
 // handle = redirector preset
function start(route, handle) {
	function onRequest(request, response) {
		var posData = "";
		var pathname = url.parse(request.url).pathname;
		console.log("\nRequest for " + pathname + " received.");

		request.setEncoding("utf8");

		// LISTENERS -- when /update is called
		var postData = "";
		request.addListener("data", function(postDataChunk) { // every post chunk
			postData += postDataChunk;
			// postDataChunk = postDataChunk.replace('"', "").replace("'", "").replace(";", ""); // prevention against SQL injection
			console.log("Received POST data chunk '" + postDataChunk + "'.");
		})

		request.addListener("end", function() { // when the message finishes
			route(handle, pathname, response, postData); // let the route handle the request :)
		})

	}

	http.createServer(onRequest).listen(9999);
	console.log("Server has started");
}

exports.start = start;