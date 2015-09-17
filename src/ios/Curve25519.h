#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

static const uint8_t base_point[32] = {9};

@interface Curve25519 : CDVPlugin

- (void)curve25519_donna:(CDVInvokedUrlCommand*)command;

@end
