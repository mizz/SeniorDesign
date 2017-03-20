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
		type:String,
		required:true
	},
	customer_id:{
		type:String,
		required:true
	},
});

var User = module.exports = mongoose.model('User',userSchema);

//Add Item
module.exports.createUser = function(user, callback){
	User.create(user, callback);
}

//Get User by UID
module.exports.getUserbyUID = function(uid, callback){
	User.find()
		.where('uid').equals(uid)
		.exec(callback);
}