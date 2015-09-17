#import "Curve25519.h"
#import "curve25519-donna.c"
#import <Cordova/CDV.h>
#import <Cordova/CDVPluginResult.h>

@interface Curve25519 ()

@property (strong, nonatomic) NSString *_callbackId;

@end

@implementation Curve25519

- (void)curve25519:(CDVInvokedUrlCommand*)command {
	//Parsing args
	//secret_key, public_key|base_point
	NSString *secretKeyHex = [command.arguments objectAtIndex: 0];
	if ([secretKeyHex length] != 64){
		//Write to error callback, complaining about bad private key size
		CDVPluginResult *rslt = [CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR messageAsString: @"INVALID_PRIVATE_KEY"];
		[self.commandDelegate sendPluginResult: rslt callbackId: command.callbackId];
		return;
	}

	//Transform secretKeyHex to secretKey buffer
	secretKeyHex = [secretKeyHex stringByReplacingOccurrencesOfString: @" " withString: @""];
	NSMutableData *secretKeyMut = [[NSMutableData alloc] init];
	unsigned char whole_byte;
	char byte_chars[3] = {'\0', '\0', '\0'};
	for (int i = 0; i < [secretKeyHex length] / 2; i++){
		byte_chars[0] = [secretKeyHex characterAtIndex: 2*i];
		byte_chars[1] = [secretKeyHex characterAtIndex: 2*i+1];
		whole_byte = strtol(byte_chars, NULL, 16);
		[secretKeyMut appendBytes: &whole_byte length: 1];
	}
	const uint8_t* sec = (const uint8_t*) [secretKeyMut bytes];
	uint8_t* shared[32];

	uint8_t* baseToUse = 0;

	if ([command.arguments count] == 1){
		//Use base_point = {9}
		baseToUse = base_point;
	} else {
		//Parse the point. Convert from hex to uint8_t
		NSString *publicKeyHex = [command.arguments objectAtIndex: 1];
		if ([publicKeyHex length] != 64){
			//Write to error callback, complaining about bad private key size
			CDVPluginResult *rslt = [CDVPluginResult resultWithStatus: CDVCommandStatus_ERROR messageAsString: @"INVALID_PUBLIC_KEY"];
			[self.commandDelegate sendPluginResult: rslt callbackId: command.callbackId];
			return;
		}

		publicKeyHex = [publicKeyHex stringByReplacingOccurrencesOfString: @" " withString: @""];
		NSMutableData *publicKeyMut = [[NSMutableData alloc] init];
		for (int i = 0; i < [publicKeyHex length] / 2; i++){
			byte_chars[0] = [publicKeyHex characterAtIndex: 2*i];
			byte_chars[1] = [publicKeyHex characterAtIndex: 2*i+1];
			whole_byte = strtol(byte_chars, NULL, 16);
			[publicKeyMut appendBytes: &whole_byte length: 1];
		}

		baseToUse = (uint8_t*) [publicKeyMut bytes];

	}

	curve25519_donna(shared, sec, baseToUse);

	NSMutableString *sharedHex = [[NSMutableString alloc] init];
	for (int i = 0; i < 32; i++){
		[sharedHex appendFormat:@"%02x", shared[i]];
	}

	CDVPlugin *rslt = [CDVPluginResult resultWithStatus: CDVCommandStatus_OK messageAsString: [sharedHex lowercaseString]];
	[self.commandDelegate sendPluginResult: rslt callbackId: command.callbackId];
}

@end
