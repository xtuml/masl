/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#include "asn1/BERDecode.hh"
#include "asn1/BERDecoder.hh"
#include "asn1/BERPrettyPrint.hh"

#include <fstream>
#include <iostream>
#include <sstream>

void decode(const std::istream &stream) {
    std::ostringstream input;
    input << stream.rdbuf();
    std::string str = input.str();

    std::string::const_iterator start = str.begin();
    std::string::const_iterator end = str.end();

    while (start != end) {
        ASN1::BER::Decoder<std::string::const_iterator> decoder(start);
        std::cout << decoder << std::flush;
        start = decoder.end();
        // assume separator char between asn1 encodings
        if (start != end)
            ++start;
        std::cout << "-------------------------------------------------------------------------------" << std::endl;
    }
}

int main(int argc, char **argv) {
    std::vector<std::string> args(argv + 1, argv + argc);

    std::ifstream infile;

    if (args.size() == 0) {
        decode(std::cin);
    }

    for (std::vector<std::string>::const_iterator it = args.begin(); it != args.end(); ++it) {
        if (*it == "-")
            decode(std::cin);
        else {
            std::ifstream file(it->c_str());
            if (!file) {
                std::cerr << *argv << ": File not found:  " << *it << std::endl;
                return 1;
            }
            decode(file);
        }
    }
}
