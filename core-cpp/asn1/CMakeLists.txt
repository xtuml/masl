#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
simple_add_shared_library (
  NAME      asn1
  SOURCES
            DEREncode.cc
            DEREncoder.cc
  LINKS   
            swa-headers 
            m
  EXPORT    MaslCore
  INCLUDES  asn1/BERDecode.hh
            asn1/BERDecoder.hh
            asn1/DEREncode.hh
            asn1/DEREncoder.hh
            asn1/UniversalTag.hh
            asn1/Encoding.hh
  )

simple_add_executable (
  NAME    asn1Decoder
  SOURCES
          asn1Decoder.cc
  LINKS
          asn1
  INSTALL
  )
