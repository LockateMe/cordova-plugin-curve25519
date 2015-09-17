/*
* Test vectors harvested from libsodium's test cases for Curve25519
* namely scalarmult, scalarmult2, scalarmult5, scalarmult6
*/

var testCases = [
	{
		privateKey: '77076d0a7318a57d3c16c17251b26645df4c2f87ebc0992ab177fba51db92c2a',
		result: '8520f0098930a754748b7ddcb43ef75a0dbf3a0d26381af4eba4a98eaa9b4e6a'
	},
	{
		privateKey: '5dab087e624a8a4b79e17f8b83800ee66f3bb1292618b6fd1c2f8b27ff88e0eb',
		result: 'de9edb7d7b7dc1b4d35b61c2ece435373f8343c85b78674dadfc7e146f882b4f'
	},
	{
		privateKey: '77076d0a7318a57d3c16c17251b26645df4c2f87ebc0992ab177fba51db92c2a',
		counterpartPublicKey: 'de9edb7d7b7dc1b4d35b61c2ece435373f8343c85b78674dadfc7e146f882b4f',
		result: '4a5d9d5ba4ce2de1728e3bf480350f25e07e21c947d19e3376f09b3c1e161742'
	},
	{
		privateKey: '5dab087e624a8a4b79e17f8b83800ee66f3bb1292618b6fd1c2f8b27ff88e0eb',
		counterpartPublicKey: '8520f0098930a754748b7ddcb43ef75a0dbf3a0d26381af4eba4a98eaa9b4e6a',
		result: '4a5d9d5ba4ce2de1728e3bf480350f25e07e21c947d19e3376f09b3c1e161742'
	}
];

exports.defineAutoTests = function(){

	describe('me.lockate.plugins.curve25519', function(){

		it('should be defined', function(){
			expect(window.plugins.curve25519).toBeDefined();
		});

		it('generate keypairs and shared secrets', function(done){
			var count = 0;
			var fail = jasmine.createSpy('fail');
			testCases.forEach(function(testCase){
				window.plugins.curve25519(testCase.privateKey, testCase.counterpartPublicKey, function(err, _result){
					if (err){
						fail(err);
						count++;
						expect(fail).not.toHaveBeenCalled();
						if (count == testCases.length) done();
						return;
					}
					expect(_result).toEqual(testCase.result);
					if (count == testCases.length) done();
				})
			});
		});

	});

}
