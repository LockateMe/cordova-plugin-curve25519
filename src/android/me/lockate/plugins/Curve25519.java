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

	private static byte[] BASE_POINT = {9, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; //32 bytes long. Beginning with 9

	static {
		System.loadLibrary("curve25519-lockateme");
	}

	public native byte[] c25519donna(byte[] privateKey, byte[] basePoint);

	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		if (action.equals("curve25519")){

			String privateKeyHex = args.getString(0);
			final byte[] privateKey = fromHex(privateKeyHex);
			byte[] basePointToUse;

			if (args.length() > 1){
				String publicKeyHex = args.getString(1);
				basePointToUse = fromHex(publicKeyHex);
			} else {
				basePointToUse = BASE_POINT;
			}

			final byte[] basePointToUseFinal = basePointToUse;

			cordova.getThreadPool().execute(new Runnable(){
				public void run(){
					try {
						byte[] sharedSecret = c25519donna(privateKey, basePointToUseFinal);
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
			callbackContext.error("Invalid method: " + action);
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
		byte[] original = new byte[(int) hLength / 2];

		for (int i = 0; i < hLength; i += 2){
			char lChar = h.charAt(i), rChar = h.charAt(i + 1);
			byte l = 0, r = 0;

			for (int j = 0; j < HEX_CHARS.length; j++){
				if (lChar == HEX_CHARS[j]) l = (byte) j;
				if (rChar == HEX_CHARS[j]) r = (byte) j;
			}
			byte currentByte = (byte) (l * 16 + r);
			original[(int) i / 2] = currentByte;
		}

		return original;
	}
}
