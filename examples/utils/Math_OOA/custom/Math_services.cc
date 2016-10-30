//
// File: NativeStubs.cc
//
// UK Crown Copyright (c) 2012. All Rights Reserved
//
#include <stdint.h>
#include "Math_OOA/__Math_services.hh"
#include "Math_OOA/__Math_types.hh"
#include <math.h>

namespace masld_Math
{
  double masls_nan ( ){ return NAN; }

  double masls_infinity ( ){ return INFINITY; }

  double masls_pi ( ){ return M_PI; }

  double masls_pi2 ( ){ return M_PI_2; }

  double masls_pi4 ( ){ return M_PI_4; }

  double masls_m1pi ( ){ return M_1_PI; }

  double masls_m2pi ( ){ return M_2_PI; }

  double masls_m2sqrtPi ( ){ return M_2_SQRTPI; }

  double masls_sqrt2 ( ){ return M_SQRT2; }

  double masls_sqrt12 ( ){ return M_SQRT1_2; }

  double masls_e ( ){ return M_E; }

  double masls_log2e ( ){ return M_LOG2E; }

  double masls_log10e ( ){ return M_LOG10E; }

  double masls_ln2 ( ){ return M_LN2; }

  double masls_ln10 ( ){ return M_LN10; }

 int masls_isinf ( double maslp_v ) { return isinf( maslp_v ); }
 bool masls_isnan ( double maslp_v ) { return isnan( maslp_v ); }
 double masls_acos ( double maslp_v ) { return acos( maslp_v ); }
 double masls_acosh ( double maslp_v ) { return acosh( maslp_v ); }
 double masls_asin ( double maslp_v ) { return asin( maslp_v ); }
 double masls_asinh ( double maslp_v ) { return asinh( maslp_v ); }
 double masls_atan ( double maslp_v ) { return atan( maslp_v ); }
 double masls_atan2 ( double maslp_v1, double maslp_v2 ) { return atan2( maslp_v1, maslp_v2); }
 double masls_atanh ( double maslp_v ) { return atanh( maslp_v ); }
 double masls_cbrt ( double maslp_v ) { return cbrt( maslp_v ); }
 double masls_ceil ( double maslp_v ) { return ceil( maslp_v ); }
 double masls_copysign ( double maslp_v1, double maslp_v2 ) { return copysign( maslp_v1, maslp_v2); }
 double masls_cos ( double maslp_v ) { return cos( maslp_v ); }
 double masls_cosh ( double maslp_v ) { return cosh( maslp_v ); }
 double masls_erf ( double maslp_v ) { return erf( maslp_v ); }
 double masls_erfc ( double maslp_v ) { return erfc( maslp_v ); }
 double masls_exp ( double maslp_v ) { return exp( maslp_v ); }
 double masls_exp2 ( double maslp_v ) { return exp2( maslp_v ); }
 double masls_expm1 ( double maslp_v ) { return expm1( maslp_v ); }
 double masls_fabs ( double maslp_v ) { return fabs( maslp_v ); }
 double masls_fdim ( double maslp_v1, double maslp_v2 ) { return fdim( maslp_v1, maslp_v2); }
 double masls_floor ( double maslp_v ) { return floor( maslp_v ); }
 double masls_fma ( double maslp_v1, double maslp_v2, double maslp_v3 ) { return fma( maslp_v1, maslp_v2, maslp_v3); }
 double masls_fmax ( double maslp_v1, double maslp_v2 ) { return fmax( maslp_v1, maslp_v2); }
 double masls_fmin ( double maslp_v1, double maslp_v2 ) { return fmin( maslp_v1, maslp_v2); }
 double masls_fmod ( double maslp_v1, double maslp_v2 ) { return fmod( maslp_v1, maslp_v2); }
 double masls_frexp ( double maslp_v, int32_t& maslp_exponent ) { return frexp( maslp_v, &maslp_exponent); }
 double masls_hypot ( double maslp_v1, double maslp_v2 ) { return hypot( maslp_v1, maslp_v2); }
 int32_t masls_ilogb ( double maslp_v ) { return ilogb(maslp_v); }
 double masls_j0 ( double maslp_v ) { return j0( maslp_v ); }
 double masls_j1 ( double maslp_v ) { return j1( maslp_v ); }
 double masls_jn ( int32_t maslp_v1, double maslp_v2 ) { return jn( maslp_v1, maslp_v2); }
 double masls_ldexp ( double maslp_v1, int32_t maslp_v2 ) { return ldexp( maslp_v1, maslp_v2); }
 double masls_lgamma ( double maslp_v ) { return lgamma( maslp_v ); }
 double masls_log ( double maslp_v ) { return log( maslp_v ); }
 double masls_log10 ( double maslp_v ) { return log10( maslp_v ); }
 double masls_log1p ( double maslp_v ) { return log1p( maslp_v ); }
 double masls_log2 ( double maslp_v ) { return log2( maslp_v ); }
 double masls_logb ( double maslp_v ) { return logb( maslp_v ); }
 int64_t masls_lrint ( double maslp_v ) { return llrint(maslp_v); }
 int64_t masls_lround ( double maslp_v ) { return llround(maslp_v); }
 double masls_modf ( double maslp_v, double& maslp_integral ) { return modf( maslp_v, &maslp_integral); }
 double masls_nearbyint ( double maslp_v ) { return nearbyint( maslp_v ); }
 double masls_nextafter ( double maslp_v1, double maslp_v2 ) { return nextafter( maslp_v1, maslp_v2); }
 double masls_pow ( double maslp_v1, double maslp_v2 ) { return pow( maslp_v1, maslp_v2); }
 double masls_remainder ( double maslp_v1, double maslp_v2 ) { return remainder( maslp_v1, maslp_v2); }
 double masls_remquo ( double maslp_v1, double maslp_v2, int32_t& maslp_quo ) { return remquo( maslp_v1, maslp_v2, &maslp_quo); }
 double masls_rint ( double maslp_v ) { return rint( maslp_v ); }
 double masls_round ( double maslp_v ) { return round( maslp_v ); }
 double masls_scalb ( double maslp_v1, double maslp_v2 ) { return scalb( maslp_v1, maslp_v2); }
 double masls_scalbln ( double maslp_v1, int64_t maslp_v2 ) { return scalbln( maslp_v1, maslp_v2); }
 double masls_scalbn ( double maslp_v1, int32_t maslp_v2 ) { return scalbn( maslp_v1, maslp_v2); }
 double masls_sin ( double maslp_v ) { return sin( maslp_v ); }
 double masls_sinh ( double maslp_v ) { return sinh( maslp_v ); }
 double masls_sqrt ( double maslp_v ) { return sqrt( maslp_v ); }
 double masls_tan ( double maslp_v ) { return tan( maslp_v ); }
 double masls_tanh ( double maslp_v ) { return tanh( maslp_v ); }
 double masls_tgamma ( double maslp_v ) { return tgamma( maslp_v ); }
 double masls_trunc ( double maslp_v ) { return trunc( maslp_v ); }
 double masls_y0 ( double maslp_v ) { return y0( maslp_v ); }
 double masls_y1 ( double maslp_v ) { return y1( maslp_v ); }
 double masls_yn ( int32_t maslp_v1, double maslp_v2 ) { return yn( maslp_v1, maslp_v2); }


}
