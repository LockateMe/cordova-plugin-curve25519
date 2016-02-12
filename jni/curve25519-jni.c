#include <errno.h>
#include <stdint.h>
#include <string.h>
#include <jni.h>
#include <android/log.h>

#include "curve25519-donna.h"

#define LOGTAG "curve25519-jni"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,LOGTAG,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,LOGTAG,__VA_ARGS__)
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE,LOGTAG,__VA_ARGS__)

static void throwException(JNIEnv* env, char *msg);

JNIEXPORT jbyteArray JNICALL Java_me_lockate_plugins_Curve25519_c25519donna
(
	JNIEnv* env,
	jobject thiz,
	jbyteArray privateKey,
	jbyteArray basePoint
)
{
	jbyte *_privateKey = (*env)->GetByteArrayElements(env, privateKey, NULL);
	if ((*env)->ExceptionOccurred(env)){
		LOGE("Cannot retrieve private key");
		goto END;
	}

	jbyte *_basePoint = (*env)->GetByteArrayElements(env, basePoint, NULL);
	if ((*env)->ExceptionOccurred(env)){
		LOGE("Cannot retrieve base point / public key");
		goto END;
	}

	u8 *sharedSecret = malloc(sizeof(u8) * 32);
	if (sharedSecret == NULL){
		LOGE("Cannot allocate shared secret buffer");
		throwException(env, "Cannot allocate shared secret buffer");
		goto END;
	}

	curve25519_donna(sharedSecret, _privateKey, _basePoint);

	jbyteArray result = (*env)->NewByteArray(env, 32);
	if ((*env)->ExceptionOccurred(env)){
		LOGE("Failed to allocate result buffer");
		throwException(env, "Failed to allocate result buffer");
		goto END;
	}
	(*env)->SetByteArrayRegion(env, result, 0, 32, (jbyte*) sharedSecret);
	if ((*env)->ExceptionOccurred(env)){
		LOGE("Failed to copy shared secret to result buffer");
		throwException(env, "Failed to copy shared secret to result buffer");
		goto END;
	}

	END:
		if (_privateKey) (*env)->ReleaseByteArrayElements(env, _privateKey, privateKey, JNI_ABORT);
		if (_basePoint) (*env)->ReleaseByteArrayElements(env, _basePoint, basePoint, JNI_ABORT);
		if (sharedSecret) free(sharedSecret);

	return result;
}

static void throwException(JNIEnv* env, char* message){
	jclass JC_Exception = (*env)->FindClass(env, "java/lang/Exception");
	(*env)->ThrowNew(env, JC_Exception, message);
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* aReserved){
	JNIEnv* env;

	if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_6) != JNI_OK){
		LOGE("Failed to get environment");
		return JNI_ERR;
	}

	return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnLoad(JavaVM* vm, void* aReserved){
	JNIEnv* env;
	//What do here since we didn't special Java classes?
}
