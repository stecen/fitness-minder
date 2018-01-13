var exec = require("child_process").exec;
var querystring = require("querystring"); // helps query the string
var mysql = require("mysql");

var pool = mysql.createPool ({ // mysql pooling database for more efficient connections
	connectionLimit: 100,
	host: "localhost",
	user:"root",
	password:"steven",
	database:"fitnessApp"
});

// Sends to client 'N' if user already exists, and 'Y' if user was successfully created with password
function createUser(response, postData) {
	var username = querystring.parse(postData).username;
	var password = querystring.parse(postData).password;

	// Make sure that the username doesn't already exist

	// Insert username/password into users table

	pool.getConnection(function (err, connection) {
		if (err) {
			connection.release();
			console.log(err);
			response.end();
			return;
		}

		console.log("connected as thread id " + connection.threadId);
		// console.log("UPDATE users SET password = '" + password +"' WHERE username = '" + username + "'");


		connection.query('SELECT * FROM users WHERE username = "' + username + '"',
			function (err, rows) {
				if (err) {
					console.log(err);
					response.end();
				}
				else {
					connection.release();

					// if the user doesn't exist
					if (rows[0] == undefined) { // this username does not exist
						//generate salt and hash NO LOL removed
						// Create record with username and password
						var statement = "INSERT INTO users(username, password, workouts) value('" + username + "', '" +
							password + "', NULL)";
						console.log(statement);
						connection.query(statement, function(err, results) {
							if (err) console.log(err);
							else {
								console.log("Successfully created user " + username);

								response.writeHead(200); // OK response code
								response.write("Y");  // YES
								response.end();
							}
							// return;
						});
					} else {

						// this user already exists, can't use this username
						// tell client sorry
						response.writeHead(200); // OK response code
						response.write("N");  // NO
						response.end();
					}
				}

			}
		);
	});

}

// POSTS Y or N to indicate success of signing in
function signIn(response, postData) {
	var username = querystring.parse(postData).username;
	var password = querystring.parse(postData).password;

	pool.getConnection(function (err, connection) {
		if (err) {
			connection.release();
			console.log(err);
			response.end();
			return;
		}

		console.log("connected as thread id " + connection.threadId);

		connection.query('SELECT * FROM users WHERE username = "' + username + '"',
			function (err, rows) {
				if (err) {
					console.log(err);
					response.end();
				}
				else {
					connection.release();

					// if the user doesn't exist
					if (rows[0] == undefined) { // this username does not exist
						response.writeHead(200); // OK response code
						response.write("N"); // TODO
						response.end();
						return;
					}

					//the username now must exist, but check if the password is correct
					if (rows[0].password == password) {
						console.log("SIGNED IN SUCCESSFULLY!");
						response.writeHead(200); // OK response code
						response.write("Y"); // you successfully signed in
						response.end();
					} else {
						// incorrect password
						console.log("Incorrect password");
						response.writeHead(200); // OK response code
						response.write("N"); // you successfully signed in
						response.end();
					}

				}

			}
		);

	});
}

// receives user and workouts
function uploadWorkouts(response, postData) {
	var username = querystring.parse(postData).username;
	var workouts = querystring.parse(postData).workouts;

	pool.getConnection(function (err, connection) {
		if (err) {
			connection.release();
			console.log(err);

			response.write("N");
			response.end();
			return;
		}

		console.log("connected as thread id " + connection.threadId);

		connection.query("UPDATE users SET workouts = '" + workouts + "' WHERE username = '" + username + "'",
			function (err, result) {
				connection.release();

				if (err) {
					console.log(err);
					response.write("N");
					response.end();
				}
				else {
					// successfully
					console.log("Successfully updated workouts for " + username);
					response.write("Y");
					response.end();
				}

			}
		);
	});

}

// send the workouts json of the said username to the client
function downloadWorkouts(response, postData) {
	var username = querystring.parse(postData).username;
	
	pool.getConnection(function (err, connection) {
		if (err) {
			connection.release();
			console.log(err);
			response.end();
			return;
		}

		console.log("connected as thread id " + connection.threadId);

		connection.query('SELECT * FROM users WHERE username = "' + username + '"',
			function (err, rows) {
				connection.release();
				if (err) {
					console.log(err);
					response.end();
					return;
				}
				else {

					// the user should be guaranteed to exist because this download function is only accessible
					// after the user successfully signs in


					if (rows[0].workouts == null) {
						console.log("rows[0].workouts is null");
						response.end();
						return;
					}

					//the username now must exist, but check if the password is correct
					response.write(rows[0].workouts, {"Content-Type": "application/json"}); // write the workouts blob
					response.end();


				}

			}
		);
	});
}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function start(response, postData) { // they want to view the "start" webpage
	console.log("Request handler 'start' was called.");

	var body = 
	'<html>'+
		'<head>'+
			'<meta http-equiv="Content-Type" content="text/html; '+
			'charset=UTF-8" />'+
		'</head>'+

		'<body>'+
			'<form action="/upload" method="post">'+
				'<textarea name="text" rows="20" cols="60"></textarea>'+
				'<input type="submit" value="Submit text" />'+
		'	</form>'+
		'</body>'+
	'</html>';

 //    var connection = mysql.createConnection({
	// 	host:"localhost",
	// 	user:"steven",
	// 	password:"hello",
	// 	database:"gps"
	// });
	pool.getConnection(function (err,connection) {
		if (err) {
			connection.release();
			console.log(err);
			return;
		}

		console.log("connected as thread id " + connection.threadId);

		connection.query("SELECT * FROM users", 
			function (err, result) {
				connection.release();
				if (err) console.log(err);
				else {
					console.log("Successful MySQL query");
				}

			}
		);
	
	});

   
	// function (error, stdout, stderr) { // heavy process w/ callback 
	// 	// including server reponse :) injected all the way from server.js --> router --> requestHandler
	response.writeHead(200, {"Content-Type": "text/html"});
	response.write(body);
	response.end();
}

function upload(response, postData) {
	console.log("Request handler 'upload' was called.");

	response.writeHead(200, {"Content-Type": "text/plain"});
	response.write("You've sent: " + querystring.parse(postData).text);
	response.end();

}

exports.start = start;
exports.upload = upload;

exports.createUser = createUser;
exports.signIn = signIn;
exports.uploadWorkouts = uploadWorkouts;
exports.downloadWorkouts = downloadWorkouts;
