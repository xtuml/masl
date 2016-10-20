//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#ifndef ASN1_UniversalTag_HH
#define ASN1_UniversalTag_HH

namespace ASN1
{

  enum UniversalTag
  {
    BOOLEAN           =  1,
    INTEGER           =  2,
    BIT_STRING        =  3,
    OCTET_STRING      =  4,
    ASN_NULL          =  5,
    OBJECT_IDENTIFIER =  6,
    ObjectDescriptor  =  7,
    INSTANCE_OF       =  8,
    REAL              =  9,
    ENUMERATED        = 10,
    EMBEDDED_PDV      = 11,
    UTF8String        = 12,
    RELATIVE_OID      = 13,
    SEQUENCE          = 16,
    SET               = 17,
    NumericString     = 18,
    PrintableString   = 19,
    TeletexString     = 20,
    VideotexString    = 21,
    IA5String         = 22,
    UTCTime           = 23,
    GeneralizedTime   = 24,
    GraphicString     = 25,
    VisibleString     = 26,
    GeneralString     = 27,
    UniversalString   = 28,
    CHARACTER_STRING  = 29,
    BMPString         = 30

  };

}

#endif
