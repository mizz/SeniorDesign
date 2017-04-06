require('dotenv').config();
var braintree = require("braintree");
var gcm = require('node-gcm');

var gateway = braintree.connect({
  environment: braintree.Environment.Sandbox,
  merchantId: process.env.BRAINTREE_MERCHANT_ID,
  publicKey: process.env.BRAINTREE_PUBLIC_KEY,
  privateKey: process.env.BRAINTREE_PRIVATE_KEY
});

// Module dependencies
var express = require('express');
// create instance
var app = express();

var bodyParser = require('body-parser');
var mongoose = require('mongoose');

app.use(bodyParser.json());

Category = require('./models/category');
Item = require('./models/item');
Review = require('./models/review');
Claim = require('./models/claim');
Tag = require('./models/tag');
User = require('./models/user')
Rental = require('./models/rental');

//Connect to Mongoose
var connection_options = {
	user: process.env.MONGO_DB_USER,
	pass: process.env.MONGO_DB_PASS
};
mongoose.connect(process.env.MONGO_DB_URL,
					connection_options);
//mongoose.connect('mongodb://localhost/rent_it');
//mongoose.connect('mongodb://localhost/bookstore');
var db = mongoose.connection;

/*app.get('/', function(req,res){
	res.send('Hello World! Please user /api/books or /api/genres');
});*/

//get all categories
app.get('/api/categories',function(req,res){
	console.log("reached the server");
	Category.getCategories(function(err,categories){
		if(err){
			throw err;
		}
		res.json(categories);
	});
});

//get all tags -no
app.get('/api/tags',function(req,res){
	Tag.getTags(function(err,tags){
		if(err){
			throw err;
		}
		res.json(tags);
	});
});

/*app.post('/api/categories',function(req,res){
	var genre = req.body;
	Genre.addGenre(genre,function(err,genre){
		if(err){
			throw err;
		}
		res.json(genre);
	});
});*/

/*app.put('/api/categories/:_id',function(req,res){
	var id = req.params._id;
	var category = req.body;
	Category.updateCategory(id, category, {}, function(err,category){
		if(err){
			throw err;
		}
		res.json(category);
	});
});*/

/*app.delete('/api/genres/:_id',function(req,res){
	var id = req.params._id;
	var genre = req.body;
	Genre.removeGenre(id, function(err,genre){
		if(err){
			throw err;
		}
		res.json(genre);
	});
});*/

/*Item.find({}, function(err,items){
	if (err) throw err;

	console.log(items);
});*/
//get all items
app.get('/api/items',function(req,res){
	console.log("reached the server");
	Item.getItems(function(err, items){
		if(err){
			throw err;
		}
		res.json(items);
	});
});
//get tags
app.get('/api/item/tags', function(req, res) {
    var array = [];
	Item.getTags(function(err, items){
		if(err){
			throw err;
		}
		//console.log(items);
		for(var i = 0, len = items.length; i < len; i++){
			console.log(items[i]);
		}
		res.json(items);
	});
});
//get item by tag
app.get('/api/items/tag/:tag',function(req,res){
	Item.getItemsByTag(req.params.tag,function(err,items){
		if(err){
			throw err;
		}
		res.json(items);
	})
});

//get item by id
app.get('/api/item/:_id',function(req,res){
	Item.getItemById(req.params._id,function(err,item){
		if(err){
			throw err;
		}
		res.json(item);
	})
});
//get item by category
app.get('/api/items/category/:category',function(req,res){
	Item.getItemsByCategoryId(req.params.category,function(err,items){
		if(err){
			throw err;
		}
		res.json(items);
	})
});
//get one item by uid
app.get('/api/items/user/:uid',function(req,res){
	Item.getItemsByUid(req.params.uid,function(err,items){
		if(err){
			throw err;
		}
		res.json(items);
	})
});
//add new item
app.post('/api/items',function(req,res){
	var item = req.body;
	Item.addItem(item,function(err,item){
		if(err){
			throw err;
		}
		res.json(item);
	});
});

//update item
/*app.put('/api/item/:_id',function(req,res){
	var id = req.params._id;
	var item = req.body;
	Item.updateItem(id, item, {}, function(err,item){
		if(err){
			throw err;
		}
	});
});*/

/*app.put('/api/item/:_id',function(req,res){
	var id = req.param.id;
	Item.findOne({_id:id}, function(err,foundItem) {
		if(err) {
			console.log(err);
			res.status(500).send();
		} else {
			if(!foundItem) {
				res.status(404).send();
			} else {
				if(req.body.value) {
					foundItem.value = req.body.value;
				}

				foundItem.save(function(err, updatedItem){
					if(err) {
						console.log(err);
						res.status(500).send();
					} else {
						res.send(updatedItem);
					}
				});
			}
		}
	});
});*/
app.put('/api/item/:_id',function(req,res){
	var id = req.params._id;
	var item = req.body;
	Item.updateItem(id, item, {}, function(err,item){
		if(err){
			throw err;
		}
		res.json(item);
	});
});


/*
app.put('/api/books/:_id',function(req,res){
	var id = req.params._id;
	var book = req.body;
	Book.updateBook(id, book, {}, function(err,book){
		if(err){
			throw err;
		}
		res.json(book);
	});
});
*/
//Delete Item
/*app.delete('/api/items/:_id',function(req,res){
	var id = req.params._id;
	var item = req.body;
	Item.removeItem(id, function(err,item){
		if(err){
			throw err;
		}
		res.json(item);
	});
});*/
//get all claims
app.get('/api/claims',function(req,res){
	console.log("reached the server");
	Claim.getClaims(function(err, claims){
		if(err){
			throw err;
		}
		res.json(claims);
	});
});

//add new claim
app.post('/api/claims',function(req,res){
	var claim = req.body;
	Claim.addClaim(claim,function(err,claim){
		if(err){
			throw err;
		}
		res.json(claim);
	});
});

//get all reviews
app.get('/api/reviews',function(req,res){
	console.log("reached the server");
	Review.getReviews(function(err, reviews){
		if(err){
			throw err;
		}
		res.json(reviews);
	});
});

//get latest review by item id
app.get('/api/review/item/:item',function(req,res){
	Review.getLatestReviewByItemId(req.params.item,function(err,review){
		if(err){
			throw err;
		}
		if(!review){
			//(review == null) works too
			console.log("review is null 1");
			review =[];
			console.log("review is now " + review);
			//res.json(review);
		}
		res.json(review);
		//else{
			/*console.log(review);
			User.getUserByUid(review.reviewer, function(err, user){
				if(err){
					throw err;
				}else{
					console.log(user);
					//review.reviewer = user.display_name;
					console.log(review);
				}*/
				//res.json(review);
			//});
		//}
	})
});

//get review by item id
app.get('/api/reviews/item/:item',function(req,res){
	Review.getReviewsByItemId(req.params.item,function(err,reviews){
		if(err){
			throw err;
		}
		if (reviews){
			console.log(reviews);

			//console.log("reviewer is "+reviews[0].reviewer);

			//console.log("reviewer is "+reviews[0].reviewer);
			/*User.getUserByUid(reviews[1].reviewer, function(err, user){
				if(err){
					throw err;
				}else{
					console.log("user is " + user);
					
					console.log("review is "+reviews[1]);
					console.log("reviewer is "+reviews[1].reviewer);
					console.log("display_name is "+user.display_name);
					reviews[1].reviewer = user.display_name;
					console.log(reviews);
				}
			
			});*/

			/*for(var i = 0, len = reviews.length; i < len; i++){
				//console.log("reviewer is "+reviews[i].reviewer);
				User.getUserByUid(reviews[i].reviewer, function(err, user){
					if(err){
						throw err;
					}else{
						console.log("user is " + user);
						console.log("review is "+reviews[i]);
						console.log("reviewer is "+reviews[i].reviewer);
						reviews[i].reviewer = user.display_name;
					}
				});
			}*/
			
		}
		res.json(reviews);
	})
});

//get review by owner id
app.get('/api/reviews/owner/:owner',function(req,res){
	Review.getReviewsByOwnerId(req.params.owner,function(err,reviews){
		if(err){
			throw err;
		}
		res.json(reviews);
	})
});

//add new review
app.post('/api/reviews',function(req,res){
	var review = req.body;
	Review.addReview(review,function(err,review){
		if(err){
			throw err;
		}
		res.json(review);
	});
});

//User Model
//get all reviews
app.get('/api/users',function(req,res){
	User.getUsers(function(err, users){
		if(err){
			throw err;
		}
		res.json(users);
	});
});
//create a new user
app.post('/api/users', function(req,res){
	var user = req.body;
	User.createUser(user,function(err,user){
		if(err){
			throw err;
		}
		res.json(user);
	});
});
//get user by uid
app.get('/api/user/:uid',function(req,res){
	User.getUserByUid(req.params.uid,function(err,user){
		if(err){
			throw err;
		}
		res.json(user);
	})
});

//update user
app.put('/api/user/:uid',function(req,res){
	var uid = req.params.uid;
	var user = req.body;
	User.updateUser(uid, user, {}, function(err,user){
		if(err){
			throw err;
		}
		res.json(user);
	});
});



//Rental Model
//get all rentals
app.get('/api/rentals',function(req,res){
	Rental.getRentals(function(err, rentals){
		if(err){
			throw err;
		}
		res.json(rentals);
	});
});

//create a new rental
app.post('/api/rentals', function(req,res){
	var rental = req.body;
	Rental.addRental(rental,function(err,rental){
		if(err){
			throw err;
		}
		res.json(rental);
	});
});

//get contacted rentals by renter
app.get('/api/rentals/contacted/renter/:renter',function(req,res){
	Rental.getContactedRentalsItems(req.params.renter, function(err,rentalsItems){
		if(err){
			throw err;
		}else{
			res.json(rentalsItems);
		}
	});
	/*Rental.getContactedRentalsItemIDs(req.params.renter,function(err,itemIDs){
		if(err){
			throw err;
		}else{
			Item.getItemsByItemIDs(itemIDs, function(err, items){
				if(err){
					throw err;
				}else{
					res.json(items);
				}
			});
		}
		//res.json(rentals);
	})*/
});

//get active rentals by renter
app.get('/api/rentals/active/renter/:renter',function(req,res){
	Rental.getActiveRentalsItems(req.params.renter, function(err,rentalsItems){
		if(err){
			throw err;
		}else{
			res.json(rentalsItems);
		}
	});
});

//get rentals by rental id
app.get('/api/rental/:rental_id',function(req,res){
	Rental.getRentalWithItemByRentalId(req.params.rental_id, function(err,rentalsItems){
		if(err){
			throw err;
		}else{
			res.json(rentalsItems);
		}
	});
});



//retrieve client token if given a customer id
//retrieve client token
app.get('/api/bt/client_token/:user_id', function(req,res){
	//find the user based on user_id
	console.log('/api/bt/client_token/:user_id');

	User.getUserByUid(req.params.user_id, function(err,user){
		if(err){
			throw err;
		}
		console.log(user);

		//if no customer id found, generate one for the user
		gateway.customer.find(user.braintree_customer_id, function(err, customer){
			if (err) {
			    console.log(err.type); // "notFoundError"
			    console.log(err.name); // "notFoundError"
			    console.log(err.message); // "Not Found"

			    gateway.customer.create({
			    	id: user.braintree_customer_id
			    }, function(err, result){
			    	if(err){
		    			console.log(err.type);
				    	console.log(err.name); 
				    	console.log(err.message); 
			    	}else{
		    			console.log(result);

		    			//if they have a customer_id, use it
						//generate client token and return it
						gateway.clientToken.generate({
							customerId: user.braintree_customer_id
						}, function (err, response) {
							if(err){
								throw err;
							}else{
								console.log(response.clientToken);
								var clientToken = response.clientToken;
				  				res.send(response.clientToken);
				  			}
						});
			    	}
			    });
			  } else {
			    console.log(customer);

			    //if they have a customer_id, use it
				//generate client token and return it
				gateway.clientToken.generate({
					customerId: user.braintree_customer_id
				}, function (err, response) {
					if(err){
						throw err;
					}else{
						console.log(response.clientToken);
						var clientToken = response.clientToken;
		  				res.send(response.clientToken);
		  			}
				});
			  }		
		});

		

	});
});
//send cancel rental request
app.post('/api/rental/cancel/:rental_id', function(req,res){
	var rental_id = req.params.rental_id;
	Rental.getRentalWithItemByRentalId(rental_id, function(err, rental){
		if(err){
			throw err;
		}else{
			console.log('rental:'+rental);
			User.getUserByUid(rental.renter, function(err, renter){
				if(err){
					throw err;
				}else{
					console.log('renter:'+renter);
					User.getUserByUid(rental.owner, function(err, lender){
						if(err){
							throw err;
						}else{
							// We now have the item (rental.item), renter, and lender info
							console.log('lender:'+lender);
							sendCancelNotification(rental, renter, lender, function(err, response){
								res.json(response);
							});
						}
					});
				}
			});
		}
	});

	//sendFCM();
	
});

//update rental - when sending rental request
app.put('/api/rental/request/:rental_id',function(req,res){
	var rental_id = req.params.rental_id;
	var rental = req.body;
	//update rental info
	Rental.updateRental(rental_id, rental, {}, function(err,rental){
		if(err){
			throw err;
		}
		//res.json(rental);
	});
	//send notification
	Rental.getRentalWithItemByRentalId(rental_id, function(err, rental){
		if(err){
			throw err;
		}else{
			//console.log('rental:'+rental);
			User.getUserByUid(rental.renter, function(err, renter){
				if(err){
					throw err;
				}else{
					//console.log('renter:'+renter);
					User.getUserByUid(rental.owner, function(err, lender){
						if(err){
							throw err;
						}else{
							// We now have the item (rental.item), renter, and lender info
							//console.log('lender:'+lender);
							sendRentalRequest(rental, renter, lender, function(err, response){
								console.log('notification: '+JSON.stringify(response));
								//res.json(response);
							});
						}
					});
				}
			});
		}
		res.json(rental);
	});
});

//update rental - when sending return request
app.put('/api/rental/return/:rental_id',function(req,res){
	var rental_id = req.params.rental_id;
	var rental = req.body;
	//update rental info
	Rental.updateRental(rental_id, rental, {}, function(err,rental){
		if(err){
			throw err;
		}
		//res.json(rental);
	});
	//send notification
	Rental.getRentalWithItemByRentalId(rental_id, function(err, rental){
		if(err){
			throw err;
		}else{
			//console.log('rental:'+rental);
			User.getUserByUid(rental.renter, function(err, renter){
				if(err){
					throw err;
				}else{
					//console.log('renter:'+renter);
					User.getUserByUid(rental.owner, function(err, lender){
						if(err){
							throw err;
						}else{
							// We now have the item (rental.item), renter, and lender info
							//console.log('lender:'+lender);
							sendReturnRequest(rental, renter, lender, function(err, response){
								console.log('notification: '+JSON.stringify(response));
								//res.json(response);
							});
						}
					});
				}
			});
		}
		res.json(rental);
	});
});

//update rental - when accepting request and starting rental
app.put('/api/rental/start/:rental_id',function(req,res){
	var rental_id = req.params.rental_id;
	var rental = req.body;
	//update rental info
	Rental.updateRental(rental_id, rental, {}, function(err,rental){
		if(err){
			throw err;
		}
		//res.json(rental);
	});
	//send notification
	Rental.getRentalWithItemByRentalId(rental_id, function(err, rental){
		if(err){
			throw err;
		}else{
			//console.log('rental:'+rental);
			User.getUserByUid(rental.renter, function(err, renter){
				if(err){
					throw err;
				}else{
					//console.log('renter:'+renter);
					User.getUserByUid(rental.owner, function(err, lender){
						if(err){
							throw err;
						}else{
							// We now have the item (rental.item), renter, and lender info
							//console.log('lender:'+lender);
							sendRentalConfirmation(rental, renter, lender, function(err, response){
								console.log('notification: '+JSON.stringify(response));
								//res.json(response);
							});
						}
					});
				}
			});
		}
		res.json(rental);
	});
});

//update rental - when confirming return and end rental
app.put('/api/rental/end/:rental_id',function(req,res){
	var rental_id = req.params.rental_id;
	var rental = req.body;
	//update rental info
	Rental.updateRental(rental_id, rental, {}, function(err,rental){
		if(err){
			throw err;
		}
		//res.json(rental);
	});
	//send notification
	Rental.getRentalWithItemByRentalId(rental_id, function(err, rental){
		if(err){
			throw err;
		}else{
			//console.log('rental:'+rental);
			User.getUserByUid(rental.renter, function(err, renter){
				if(err){
					throw err;
				}else{
					//console.log('renter:'+renter);
					User.getUserByUid(rental.owner, function(err, lender){
						if(err){
							throw err;
						}else{
							// We now have the item (rental.item), renter, and lender info
							//console.log('lender:'+lender);
							sendReturnConfirmation(rental, renter, lender, function(err, response){
								console.log('notification: '+JSON.stringify(response));
								//res.json(response);
							});
						}
					});
				}
			});
		}
		res.json(rental);
	});
});

/*function sendFCM(rental_id){
	var lenderID = 'onBNW00rlNg9S1CmBWDHTOu0j3Z2';	// gotten from a DB call via item

	User.getUserByUid(lenderID, function(err, lender){
		if(err){
			console.log(err);
		} else{
			console.log(lender);

			var renter = 'Fred';	// replace this with either a DB lookup here
									// or the parameter of renter name should be
									// passed into the function
			var item = 'Speedboat';	// replace this with either a DB lookup here
									// or the parameter of renter name should be
									// passed into the function
			var rental_request = 	renter +
									' would like to rent your ' +
									item +
									'!';

			var message = new gcm.Message({
			    collapseKey: 'demo',
			    priority: 'high',
			    contentAvailable: true,
			    data: {
			        rentalId: rental_id
			    },
			    notification: {
			        title: 'Rental Request!',
			        icon: 'ic_launcher',
			        body: rental_request
			    }
			});

			console.log(lender.uid);
			console.log(lender.fcm_token);

			// Set up the sender with you API key, prepare your recipients' registration tokens. 
			var sender = new gcm.Sender(process.env.FCM_API_KEY);
			var regTokens = [lender.fcm_token];
			 
			sender.send(message, { registrationTokens: regTokens }, function (err, response) {
			    if(err) {
			    	console.error(err);
			    }else {
			    	console.log(response);
			    }
			});
		}

		
	});

	
}*/


//send rental request
function sendRentalRequest(rental, renter, lender, callback){

	var renter_name = renter.display_name;
	var owner_name = lender.display_name;
	var item_name = rental.item.title;
	var estimated_profit = rental.estimated_profit;
	var return_date = rental.booked_end_date;
	var rental_request = 	renter_name +
							' would like to rent your ' +
							item_name +
							'!';

	var message = new gcm.Message({
	    collapseKey: 'demo',
	    priority: 'high',
	    contentAvailable: true,
	    data: {
	    	notificationType:'rental_request',
	        rentalId: rental.rental_id,
	        renter: renter_name,
	        itemName: item_name,
	        returnDate: return_date,
	        estimatedProfit:estimated_profit
	    },
	    notification: {
	        title: 'Rental Request!',
	        icon: 'ic_launcher',
	        body: rental_request,
	        click_action:'RENTAL_REQUEST'
	    }
	});

	console.log(lender.uid);
	console.log(lender.fcm_token);

	// Set up the sender with you API key, prepare your recipients' registration tokens. 
	var sender = new gcm.Sender(process.env.FCM_API_KEY);
	var regTokens = [lender.fcm_token];
	 
	sender.send(message, { registrationTokens: regTokens }, callback);
}

//send return request
function sendReturnRequest(rental, renter, lender, callback){

	var renter_name = renter.display_name;
	var owner_name = lender.display_name;
	var item_name = rental.item.title;
	var estimated_profit = rental.estimated_profit;
	var return_date = rental.booked_end_date;
	var rental_request = 	renter_name +
							' would like to return your ' +
							item_name +
							'!';

	var message = new gcm.Message({
	    collapseKey: 'demo',
	    priority: 'high',
	    contentAvailable: true,
	    data: {
	    	notificationType:'return_request',
	        rentalId: rental.rental_id,
	        renter: renter_name,
	        itemName: item_name,
	        returnDate: return_date,
	        estimatedProfit:estimated_profit
	    },
	    notification: {
	        title: 'Return Initiated!',
	        icon: 'ic_launcher',
	        body: rental_request,
	        click_action:'RETURN_REQUEST'
	    }
	});

	console.log(lender.uid);
	console.log(lender.fcm_token);

	// Set up the sender with you API key, prepare your recipients' registration tokens. 
	var sender = new gcm.Sender(process.env.FCM_API_KEY);
	var regTokens = [lender.fcm_token];
	 
	sender.send(message, { registrationTokens: regTokens }, callback);
}

//cancel rental request
function sendCancelNotification(rental, renter, lender, callback){

	var renter_name = renter.display_name;
	var owner_name = lender.display_name;
	var item_name = rental.item.title;
	var estimated_profit = rental.estimated_profit;
	var return_date = rental.booked_end_date;
	var rental_request = 	'Your rental request for ' +
							item_name +
							'was canceled by '+ owner_name;

	var message = new gcm.Message({
	    collapseKey: 'demo',
	    priority: 'high',
	    contentAvailable: true,
	    data: {
	    	notificationType:'cancel_rental',
	        rentalId: rental.rental_id,
	        renter: renter_name,
	        itemName: item_name,
	        returnDate: return_date,
	        estimatedProfit:estimated_profit
	    },
	    notification: {
	        title: 'Rental Canceled!',
	        icon: 'ic_launcher',
	        body: rental_request,
	        click_action:'CANCEL_REQUEST'
	    }
	});

	console.log(renter.uid);
	console.log(renter.fcm_token);

	// Set up the sender with you API key, prepare your recipients' registration tokens. 
	var sender = new gcm.Sender(process.env.FCM_API_KEY);
	var regTokens = [renter.fcm_token];
	 
	sender.send(message, { registrationTokens: regTokens }, callback);
}

//send rental confirmation - trade started
function sendRentalConfirmation(rental, renter, lender, callback){

	var renter_name = renter.display_name;
	var owner_name = lender.display_name;
	var item_name = rental.item.title;
	var estimated_profit = rental.estimated_profit;
	var return_date = rental.booked_end_date;
	var rental_request = 	'Your rental request for '+item_name
							' was accepted by' + owner_name + 
							'. Your rental clock has started!';

	var message = new gcm.Message({
	    collapseKey: 'demo',
	    priority: 'high',
	    contentAvailable: true,
	    data: {
	    	notificationType:'start_rental',
	        rentalId: rental.rental_id,
	        renter: renter_name,
	        itemName: item_name,
	        returnDate: return_date,
	        estimatedProfit:estimated_profit
	    },
	    notification: {
	        title: 'Rental Started!',
	        icon: 'ic_launcher',
	        body: rental_request,
	        click_action:'START_RENTAL'
	    }
	});

	console.log(renter.uid);
	console.log(renter.fcm_token);

	// Set up the sender with you API key, prepare your recipients' registration tokens. 
	var sender = new gcm.Sender(process.env.FCM_API_KEY);
	var regTokens = [renter.fcm_token];
	 
	sender.send(message, { registrationTokens: regTokens }, callback);
}

//send rental confirmation - trade started
function sendReturnConfirmation(rental, renter, lender, callback){

	var renter_name = renter.display_name;
	var owner_name = lender.display_name;
	var item_name = rental.item.title;
	var estimated_profit = rental.estimated_profit;
	var return_date = rental.booked_end_date;
	var rental_request = 	'Your return of '+item_name
							' was confirmed by' + owner_name + 
							'. You are all set!';

	var message = new gcm.Message({
	    collapseKey: 'demo',
	    priority: 'high',
	    contentAvailable: true,
	    data: {
	    	notificationType:'confirm_return',
	        rentalId: rental.rental_id,
	        renter: renter_name,
	        itemName: item_name,
	        returnDate: return_date,
	        estimatedProfit:estimated_profit
	    },
	    notification: {
	        title: 'Rental Started!',
	        icon: 'ic_launcher',
	        body: rental_request,
	        click_action:'RETURN_CONFIRM'
	    }
	});

	console.log(renter.uid);
	console.log(renter.fcm_token);

	// Set up the sender with you API key, prepare your recipients' registration tokens. 
	var sender = new gcm.Sender(process.env.FCM_API_KEY);
	var regTokens = [renter.fcm_token];
	 
	sender.send(message, { registrationTokens: regTokens }, callback);
}


//sendFCM('15b2888c-837d-4fd5-ae1d-093aac5ec5f4');

app.listen(process.env.PORT_NO);
console.log('Running on port 3000...');

// Router
/*var router = require('./routes/router.js')
app.use('/', router);*/

// Setup Server configuration
/*var port = process.env.PORT || 3000;
app.listen(port);
console.log('Node is running on port ' + port);*/

