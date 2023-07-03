# ----------------------------------------------------------------------------
# (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
# The copyright of this Software is vested in the Crown
# and the Software is the property of the Crown.
# ----------------------------------------------------------------------------
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ----------------------------------------------------------------------------
# Classification: UK OFFICIAL
# ----------------------------------------------------------------------------

find_package(Boost CONFIG REQUIRED)

simple_add_shared_library (
  NAME      asn1
  SOURCES
            DEREncode.cc
            DEREncoder.cc
  LINKS
            swa-headers
            m
            Boost::boost
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

if(CMAKE_CXX_COMPILER_VERSION VERSION_GREATER_EQUAL 12.0 AND CMAKE_CXX_COMPILER_VERSION VERSION_LESS 12.3 )
    # Compiler bug... see https://stackoverflow.com/questions/74677951
    # also https://godbolt.org/z/Pe5aE59xG - Should be fixed in gcc 12.3
    target_compile_options(asn1Decoder PRIVATE -Wno-restrict)
endif()

