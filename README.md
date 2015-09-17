# Curve25519 plugin for Cordova/Phonegap - iOS only

----------------------------------

Curve25519 plugin for Cordova/Phonegap, for iOS only. It turns out that the Curve25519 implementation in [libsodium.js](https://github.com/jedisct1/libsodium.js) is around 30x slower on iOS than it is on Android-Crosswalk.

The Curve25519 implementation in this plugin is [Adam Langley's curve25519-donna](https://github.com/agl/curve25519-donna).

## Usage

The module is available in `window.plugins.Curve25519`

Input must be hexadecimal strings. Output is also hexadecimal strings.

Examples :

```js
//Computing your public key
window.plugins.curve25519(secretKey, undefined, function(err, publicKey){

});

//Computing a shared secret
window.plugins.curve25519(secretKey, counterpartPublicKey, function(err, sharedSecret){

});
```

## License

MIT license
