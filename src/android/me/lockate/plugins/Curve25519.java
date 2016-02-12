package me.lockate.plugins;

import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Curve25519 extends CordovaPlugin {
	private static final String LOGTAG = "Curve25519";
	private static char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	private static byte[] BASE_POINT = {9};

	static {
		System.loadLibrary("curve25519_lockateme");
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (action.equals("curve25519")){

			String privateKeyHex = args.getString(0);
			byte[] privateKey = fromHex(privateKeyHex);
			byte[] basePointToUse;

			if (args.Length() > 1){
				String publicKeyHex = args.getString(1);
				basePointToUse = fromHex(publicKeyHex)
			} else {
				basePointToUse = BASE_POINT;
			}

			cordova.getThreadPool().execute(new Runnable(){
				public void run(){
					try {
						byte[] sharedSecret = c25519donna(privateKey, basePointToUse);
						JSONArray jsonResult = new JSONArray();
						for (int i = 0; i < sharedSecret.length; i++){
							jsonResult.put((int) sharedSecret[i] & 0x000000ff);
						}
						callbackContext.success(jsonResult);
					} catch (Exception e){
						callbackContext.error(e.getMessage());
					}
				}
			});

			return true;
		} else {
			callbackContext.error("Invalid method");
			return false;
		}
	}

	private static String dumpHex(byte[] data){ //To hex. No spacing between bytes
		final int n = data.length;
		final StringBuilder sb = new StringBuilder(n * 2);
		for (int i = 0; i < n; i++){
			sb.append(HEX_CHARS[(data[i] >> 4) & 0x0f]);
			sb.append(HEX_CHARS[data[i] & 0x0f]);
		}
		return sb.toString();
	}

	private static byte[] fromHex(String h){
		int hLength = h.length();
		if (hLength % 2 != 0) return null;
		char[] original = new char[(int) hLength / 2];

		for (int i = 0; i < hLength; i += 2){
			char lChar = h[i], rChar = h[i + 1];
			byte l, r;

			for (int j = 0; j < HEX_CHARS.length; j++){
				if (lChar == HEX_CHARS[j]) l = j;
				if (rChar == HEX_CHARS[j]) r = (j << 4);
			}
			byte currentByte = (byte) l * 16 + r;
			original[(int) i / 2] = currentByte;
		}

		return original;
	}
}
