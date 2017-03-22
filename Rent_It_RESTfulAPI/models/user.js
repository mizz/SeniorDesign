var mongoose = require('mongoose');

var userSchema = mongoose.Schema({
	uid:{
		type:String,
		required:true
	},
	email:{
		type:String,
		required:true
	},
	display_name:{
		type:String
	},
	first_name:{
		type:String
	},
	last_name:{
		type:String
	},
	braintree_customer_id:{
		type:String
	},
	fcm_token:{
		type:String
	}
});

var User = module.exports = mongoose.model('User',userSchema);

//Get All User
module.exports.getUsers = function(callback, limit){
	User.find(callback).limit(limit);
}

//Add User
module.exports.createUser = function(user, callback){
	User.create(user, callback);
}

//Get User by UID
module.exports.getUserByUid = function(uid, callback){
	User.find()
		.where('uid').equals(uid)
		.exec(callback);
}