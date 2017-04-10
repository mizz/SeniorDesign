var mongoose = require('mongoose');

var reviewSchema = mongoose.Schema({
	item:{
		type:String
	},
	rental_id:{
		type:String
	},
	owner:{
		type: String
	},
	item_rating:{
		type: Number
	},
	title:{
		type: String
	},
	item_comment:{
		type: String
	},
	owner_rating:{
		type: Number
	},
	owner_comment:{
		type: String
	},
	renter:{
		type: String
	},
	renter_rating:{
		type: Number
	},
	renter_comment:{
		type: String
	},
	date_created:{
		type: Date,
		default: Date.now
	},
}, { toJSON: { virtuals: true } });

reviewSchema.virtual('reviewer_info', {
	ref: 'User', 			// The model to use
	localField: 'renter', // Find users where `localField`
	foreignField: 'uid',	// is equal to the `foreignField`
	justOne: true
});

var Review = module.exports = mongoose.model('Review',reviewSchema);

//Get All Reviews
module.exports.getReviews = function(callback, limit){
	Review.find(callback).limit(limit);
}

//Get a latest Review
module.exports.getLatestReviewByItemId = function(item, callback){
	Review.findOne({'item': item})
		.sort({date_created: -1})
		.populate('reviewer_info')
		.exec(callback);
}

//Get Reviews by Item Id
module.exports.getReviewsByItemId = function(item, callback){
	Review.find()
		.where('item').equals(item)
		.populate('reviewer_info')
		.exec(callback);
}

//Get Reviews by Owner Id
module.exports.getReviewsByOwnerId = function(owner, callback){
	Review.find()
		.where('owner').equals(owner)
		.populate('reviewer_info')
		.exec(callback);
}

//Get Review by Rental Id
module.exports.getReviewsByRentalId = function(rental_id, callback){
	var query = {rental_id:rental_id};
	Review.findOne(query)
		.exec(callback);
}

//get by rental id
/*module.exports.getRentalWithItemByRentalId = function(rentalid, callback){
	var query = {rental_id:rentalid};
	Rental.findOne(query)
		  .populate('item')
		  .exec(callback);
}*/

//Add Review
module.exports.addReview = function(review, callback){
	Review.create(review, callback);
}

//Update Review
module.exports.updateReview = function(rentalid, review, options, callback){
	var query = {rental_id: rentalid};
	var update = {
		item_rating: review.item_rating,
		title:review.title,
		item_comment:review.item_comment,
		owner_rating:review.owner_rating,
		owner_comment:review.owner_comment,
		notes:review.renter_rating,
		renter_comment: review.renter_comment
	}
	Review.findOneAndUpdate(query, update, options, callback);
}
