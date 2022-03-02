#include "Environment_OOA/__Environment_services.hh"
#include "Environment_OOA/__Environment_types.hh"
#include "__Environment_JNI.hh"

/*
 * Class:     Environment
 * Method:    setenv
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_Environment_setenv(JNIEnv * env, jobject thisObject, jstring p_name, jstring p_value)
{
  char * name = env->GetStringUTFChars(p_name, NULL);
  char * value = env->GetStringUTFChars(p_value, NULL);
  masld_Environment::masls_setenv(name, value);
  env->ReleaseStringUTFChars(p_name, name);
  env->ReleaseStringUTFChars(p_value, value);
}

/*
 * Class:     Environment
 * Method:    unsetenv
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_Environment_unsetenv(JNIEnv * env, jobject thisObject, jstring p_name)
{
  char * name = env->GetStringUTFChars(p_name, NULL);
  masld_Environment::masls_unsetenv(name);
  env->ReleaseStringUTFChars(p_name, name);
}

/*
 * Class:     Environment
 * Method:    getenv
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_Environment_getenv(JNIEnv * env, jobject thisObject, jstring p_name)
{
  char * name = env->GetStringUTFChars(p_name, NULL);
  char * value = masld_Environment::masls_getenv(name).c_str();
  env->ReleaseStringUTFChars(p_name, name);
  return env->NewStringUTF(value);
}

/*
 * Class:     Environment
 * Method:    isset
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_Environment_isset(JNIEnv * env, jobject thisObject, jstring p_name)
{
  char * name = env->GetStringUTFChars(p_name, NULL);
  bool isset = masld_Environment::masls_isset(name);
  env->ReleaseStringUTFChars(p_name, name);
  return isset;
}
