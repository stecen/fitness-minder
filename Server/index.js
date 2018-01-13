var server = require("./server");
var router = require("./router");
var requestHandlers = require("./requestHandlers");

var handle = {}; // what function should be used to handle the client's request?
handle["/"] = requestHandlers.start; // functions
handle["/start"] = requestHandlers.start; // loads the website (html)
handle["/upload"] = requestHandlers.upload; // comes with POST request, displays what the user input

handle["/createUser"] = requestHandlers.createUser;
handle["/signIn"] = requestHandlers.signIn;
handle["/uploadWorkouts"] = requestHandlers.uploadWorkouts;
handle["/downloadWorkouts"] = requestHandlers.downloadWorkouts;

server.start(router.route, handle); // dependency injections

