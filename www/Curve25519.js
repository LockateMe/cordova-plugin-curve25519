"use strict";
var exec = require('cordova/exec');

function Curve25519(privateKey, publicKey, callback){
	if (typeof callback != 'function') throw new TypeError('callback must be a function');
	if (!(is_hex(privateKey) && privateKey.length == 64)){
		callback('INVALID_PRIVATE_KEY');
		return;
	}
	if (publicKey && !(is_hex(publicKey) && publicKey.length == 64)){
		callback('INVALID_PUBLIC_KEY');
		return;
	}

	var paramsArray = [privateKey];
	if (publicKey) paramsArray.push(publicKey);

	cordova.exec(resultHandler, callback, 'Curve25519', 'curve25519', paramsArray);

	function resultHandler(result){
		callback(undefined, result);
	}

	function is_hex(s){
		return typeof s == 'string' && s.length % 2 == 0 && /^([a-f]|[0-9])+$/ig.test(s);
	}
}

module.exports = Curve25519;
