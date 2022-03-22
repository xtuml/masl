#include "Math_OOA/__Math_services.hh"
#include "__Math_JNI.hh"

/*
 * Class:     org_xtuml_masl_util_Math
 * Method:    sqrt
 * Signature: (D)D
 */
JNIEXPORT jdouble JNICALL Java_org_xtuml_masl_util_Math_sqrt(JNIEnv * env, jobject thisObject, jdouble v)
{
  return masld_Math::masls_sqrt(v);
}
