var mongoose = require('mongoose');

var rentalSchema = mongoose.Schema({
	rental_id:{
		type:String,
		required:true
	},
	renter:{
		type:String
	},
	owner:{
		type:String
	},
	item:{
		//type:String
		type:String,
		ref: 'Item'
	},
	rental_status:{
		type:Number
	},
	booked_start_date:{
		type:Date
	},
	booked_end_date:{
		type:Date
	},
	booked_period:{
		type:Number
	},
	estimated_profit:{
		type:Number
	},
	notes:{
		type:String
	},
	rental_started_date:{
		type:Date
	},
	rental_end_date:{
		type:Date
	},
	return_confirmed_date:{
		type:Date
	},
	payment_status:{
		type:Number
	},
	daily_rate:{
		type:Number
	},
	rental_period:{
		type:Number
	},
	rental_fee:{
		type:Number
	},
	service_fee:{
		type:Number
	},
	tax:{
		type:Number
	},
	total:{
		type:Number
	},
	created_date:{
		type: Date,
		default: Date.now
	}
});

var Rental =  module.exports = mongoose.model('Rental',rentalSchema);


//Get All Rentals
module.exports.getRentals = function(callback, limit){
	Rental.find(callback).limit(limit);
}

//Get item IDs of rentals for trade list - not using
/*module.exports.getContactedRentalsItemIDs = function(renter, callback){
	var query = {renter:renter,rental_status: 1};
	Rental.find(query)
		.sort({created_date:-1})
		.select('-_id item')
		.exec(function(err, items){
			if(err){
				throw err;
			}else{
				// extract the item ids from the items
				var itemIDs=[];
				for(var i=0;i<items.length;i++){
				   itemIDs.push(items[i].item);
				}
				callback(err, itemIDs);
			}
			
		});
}*/
//using - Get item IDs of rentals for trade list 
module.exports.getContactedRentalsItems = function(renter, callback){
	var query = {renter:renter,rental_status: 1};
	Rental.find(query)
		  .sort({created_date:-1})
		  .populate('item')
		  .exec(callback);
}
//
module.exports.getActiveRentalsItems = function(renter, callback){
	var query = {renter:renter,rental_status: 2};
	Rental.find(query)
		  .sort({created_date:-1})
		  .populate('item')
		  .exec(callback);
}

//get by rental id
module.exports.getRentalWithItemByRentalId = function(rentalid, callback){
	var query = {rental_id:rentalid};
	Rental.findOne(query)
		  .populate('item')
		  .exec(callback);
}

//Add Rental
module.exports.addRental = function(rental, callback){
	Rental.create(rental, callback);
}

//Update Rental
module.exports.updateRental = function(rentalid, rental, options, callback){
	var query = {rental_id: rentalid};
	var update = {
		rental_status: rental.rental_status,
		booked_start_date:rental.booked_start_date,
		booked_end_date:rental.booked_end_date,
		booked_period:rental.booked_period,
		estimated_profit:rental.estimated_profit,
		notes:rental.notes,
		rental_started_date: rental.rental_started_date,
		rental_end_date: rental.rental_end_date,
		return_confirmed_date: rental.return_confirmed_date,
		payment_status:rental.payment_status,
		daily_rate: rental.daily_rate,
		rental_period: rental.rental_period,
		rental_fee:rental.rental_fee,
		service_fee:rental.service_fee,
		tax:rental.service_fee,
		total:rental.total
	}
	Rental.findOneAndUpdate(query, update, options, callback);
}






