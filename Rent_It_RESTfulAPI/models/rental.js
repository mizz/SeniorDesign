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
		type:String
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
	estimated_total:{
		type:Number
	},
	image:{
		type:Number
	},
	rental_started_date:{
		type:Date
	},
	rental_end_date:{
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
	service_fee:{
		type:Number
	},
	tax:{
		type:Number
	},
	total:{
		type:Number
	}
});

var Rental =  module.exports = mongoose.model('Rental',rentalSchema);


//Get All Rentals
module.exports.getRentals = function(callback, limit){
	Rental.find(callback).limit(limit);
}

//Get Item by Category Name
/*module.exports.getItemsByCategoryId = function(category, callback){
	Item.find()
		.where('category').equals(category)
		.exec(callback);
}*/


//Add Rental
module.exports.addRental = function(rental, callback){
	Rental.create(rental, callback);
}

//Update Item
/*module.exports.updateItem = function(id, item, options, callback){
	var query = {_id: id};
	var update = {
		//category: item.category,
		city:item.city,
		condition:item.condition,
		description:item.description,
		//image:item.image,
		title: item.title,
		value: item.value,
		zipcode:item.zipcode,
		tags: item.tags,
		visible: item.visible,
		rate:item.rate
	}
	Item.findOneAndUpdate(query, update, options, callback);
}*/






