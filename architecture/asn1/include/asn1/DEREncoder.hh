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
#ifndef ASN1_DER_Encoder_HH
#define ASN1_DER_Encoder_HH

#include "UniversalTag.hh"

#include <boost/operators.hpp>
#include <stack>
#include <stdint.h>
#include <string>
#include <vector>

namespace ASN1 {
    namespace DER {
        typedef uint8_t Octet;
        typedef std::string Buffer;

        class Encoder {
          public:
            typedef std::vector<Encoder> Children;

            Encoder();

            Encoder(UniversalTag tag);

            Encoder(UniversalTag tag, const Buffer &value);

            Encoder(UniversalTag tag, const Children &children);

            void setValue(Buffer value);
            void addChild(const Encoder &child);

            class iterator
                : public boost::input_iterator_helper<iterator, Octet, std::ptrdiff_t, const Octet *, const Octet &> {
              public:
                struct Position {
                    Position(const Encoder *encoder)
                        : encoder(encoder), child(0), it(encoder->header.begin()) {}

                    bool operator==(const Position &rhs) const {
                        return encoder == rhs.encoder && child == rhs.child && it == rhs.it;
                    }

                    const Encoder *encoder;
                    size_t child;
                    Buffer::const_iterator it;
                };

                iterator();
                iterator(const Encoder *encoder);

                Octet operator*() const {
                    return static_cast<Octet>(*stack.top().it);
                }
                iterator &operator++();
                bool operator==(const iterator &rhs) const {
                    return stack == rhs.stack;
                }

              private:
                std::stack<Position, std::vector<Position>> stack;
            };

            typedef iterator const_iterator;

            friend class iterator;
            friend class iterator::Position;

            size_t size() const;
            void reserve(size_t noChildren) {
                children.reserve(noChildren);
            }

            iterator begin() const;
            const iterator &end() const;

          private:
            enum TagClass { UNIVERSAL = 0x00, APPLICATION = 0x40, CONTEXT = 0x80, PRIVATE = 0xC0 };

            enum ValueForm { PRIMITIVE = 0x00, CONSTRUCTED = 0x20 };

          private:
            uint64_t tag;
            TagClass tagClass;
            bool constructed;

            Buffer value;
            Children children;

          private:
            void cacheBuffers() const;
            void cacheTag() const;
            void cacheLength() const;

            mutable bool stale;
            mutable size_t length;
            mutable Buffer header;
        };

        inline Encoder::iterator &Encoder::iterator::operator++() {
            Position *frame = &stack.top();
            ++frame->it;

            if (frame->it == frame->encoder->header.end()) {
                frame->it = frame->encoder->value.begin();
            }

            while (frame && frame->it == frame->encoder->value.end()) {
                if (frame->child < frame->encoder->children.size()) {
                    stack.push(&frame->encoder->children[frame->child++]);
                } else {
                    stack.pop();
                }
                frame = stack.empty() ? 0 : &stack.top();
            }

            return *this;
        }

    } // namespace DER
} // namespace ASN1

#endif
