// ----------------------------------------------------------------------------
// (c) 2024 - CROWN OWNED COPYRIGHT. All rights reserved.
// The copyright of this Software is vested in the Crown
// and the Software is the property of the Crown.
// ----------------------------------------------------------------------------
// Classification: UK OFFICIAL
// ----------------------------------------------------------------------------

#include "Hash_OOA/__Hash_services.hh"
#include "Hash_OOA/__Hash_types.hh"
#include "Hash_OOA/__Hash__HashException.hh"
#include "swa/Sequence.hh"
#include "swa/String.hh"
#include <openssl/err.h>
#include <openssl/evp.h>

#include <xxhash.h>

namespace masld_Hash {


    std::shared_ptr <EVP_MD> get_digest(maslt_Algorithm algorithm) {

        static const std::array<std::shared_ptr < EVP_MD>, maslt_Algorithm::index_masle_SHA3_512 + 1 > evp_digests = {
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "MD5", nullptr), EVP_MD_free},
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "SHA1", nullptr), EVP_MD_free},
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "SHA224", nullptr), EVP_MD_free},
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "SHA256", nullptr), EVP_MD_free},
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "SHA384", nullptr), EVP_MD_free},
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "SHA512", nullptr), EVP_MD_free},
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "SHA3-224", nullptr), EVP_MD_free},
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "SHA3-256", nullptr), EVP_MD_free},
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "SHA3-384", nullptr), EVP_MD_free},
                std::shared_ptr < EVP_MD > {EVP_MD_fetch(nullptr, "SHA3-512", nullptr), EVP_MD_free}};

        return evp_digests[algorithm.getIndex()];
    }

    void checkErrors(int status) {
        if (status != 1) {
            std::string message;
            ERR_print_errors_cb([](const char *str, std::size_t len, void *ctx) {
                static_cast<std::string *>(ctx)->append(str, len);
                return 1;
            }, &message);
            throw masld_Hash::maslex_HashException(message);
        }
    }


    class Hasher {

    private:
        std::shared_ptr <EVP_MD_CTX> context{EVP_MD_CTX_new(), EVP_MD_CTX_free};
        std::shared_ptr <EVP_MD> digest;
    public:
        Hasher(maslt_Algorithm algorithm) : digest(get_digest(algorithm)) {
            checkErrors(EVP_DigestInit_ex(context.get(), digest.get(), nullptr));

        }

        template<typename Buffer>
        requires(std::ranges::contiguous_range<Buffer>
        && std::ranges::sized_range <Buffer>)

        void update(const Buffer &input) {
            checkErrors(EVP_DigestUpdate(context.get(), std::ranges::cdata(input),
                                         std::ranges::size(input) * sizeof(std::ranges::range_value_t < Buffer > )));

        }

        std::vector<unsigned char> hash() {
            std::vector<unsigned char> result(EVP_MD_CTX_size(context.get()));
            checkErrors(EVP_DigestFinal_ex(context.get(), result.data(), nullptr));
            return result;
        }
    };

    class HMACHasher {

    private:
        std::shared_ptr <EVP_MD_CTX> context{EVP_MD_CTX_new(), EVP_MD_CTX_free};
        std::shared_ptr <EVP_MD> digest;
        std::shared_ptr <EVP_PKEY> key;
    public:
        template<typename Key>
        requires (std::ranges::contiguous_range<Key>
        &&
        std::ranges::sized_range <Key> &&requires{
                sizeof(std::ranges::range_value_t < Key > ) == 1;
        }
        )

        HMACHasher(maslt_Algorithm algorithm, const Key &key) :
                digest(get_digest(algorithm)),
                key(EVP_PKEY_new_mac_key(
                            EVP_PKEY_HMAC,
                            nullptr,
                            reinterpret_cast<const unsigned char *>(std::ranges::data(key)),
                            std::ranges::size(key)),
                    EVP_PKEY_free) {
            checkErrors(EVP_DigestSignInit(context.get(), nullptr, digest.get(), nullptr, this->key.get()));

        }

        template<typename Buffer>
        requires std::ranges::contiguous_range<Buffer>
        &&

        std::ranges::sized_range <Buffer>
        void update(const Buffer &input) {
            checkErrors(EVP_DigestSignUpdate(context.get(), std::ranges::cdata(input),
                                             std::ranges::size(input) *
                                             sizeof(std::ranges::range_value_t < Buffer > )));

        }

        std::vector<unsigned char> hash() {
            std::size_t size = EVP_MD_CTX_size(context.get());
            std::vector<unsigned char> result(size);
            checkErrors(EVP_DigestSignFinal(context.get(), result.data(), &size));
            return result;
        }
    };

    template<class Function>
    void process_stream(std::istream &input, Function function) {
        static const std::size_t maxReadSize = 4 * 1024 * 1024;

        std::ios::pos_type cur_pos = input.tellg();
        input.seekg(0, std::ios::end);
        std::ios::pos_type end_pos = input.tellg();
        input.seekg(cur_pos);

        std::size_t remaining = end_pos - cur_pos;
        std::vector<char> buffer;
        buffer.reserve(std::min(maxReadSize, remaining));

        while (input && remaining) {
            std::size_t to_read = std::min(buffer.capacity(), remaining);
            buffer.resize(to_read);
            input.read(buffer.data(), to_read);
            remaining -= to_read;

            function(buffer);

        }

    }


    maslt_Hash masls_hash(const maslt_Algorithm &maslp_algorithm,
                          const ::SWA::Sequence <uint8_t> &maslp_input) {
        Hasher hasher{maslp_algorithm};
        hasher.update(maslp_input.getData());
        return hasher.hash();
    }

    maslt_Hash masls_overload1_hash(const maslt_Algorithm &maslp_algorithm,
                                    const ::SWA::String &maslp_input) {
        Hasher hasher{maslp_algorithm};
        hasher.update(maslp_input.s_str());
        return hasher.hash();
    }

    maslt_Hash masls_overload2_hash(const maslt_Algorithm &maslp_algorithm,
                               const ::SWA::Device &maslp_input) {
        Hasher hasher{maslp_algorithm};
        process_stream(*maslp_input.getInputStream(), [&hasher](const std::vector<char> &buffer) {
            hasher.update(buffer);
        });
        return hasher.hash();

    }

    maslt_Hash masls_hmac(const maslt_Algorithm &maslp_algorithm,
                          const ::SWA::Sequence <uint8_t> &maslp_key,
                          const ::SWA::Sequence <uint8_t> &maslp_input) {
        HMACHasher hasher{maslp_algorithm, maslp_key.getData()};
        hasher.
                update(maslp_input.getData());
        return hasher.

                hash();
    }

    maslt_Hash masls_overload1_hmac(const maslt_Algorithm &maslp_algorithm,
                                    const ::SWA::Sequence <uint8_t> &maslp_key,
                                    const ::SWA::String &maslp_input) {
        HMACHasher hasher{maslp_algorithm, maslp_key.getData()};
        hasher.update(maslp_input.s_str());
        return hasher.hash();
    }

    maslt_Hash masls_overload2_hmac(const maslt_Algorithm &maslp_algorithm,
                               const ::SWA::Sequence <uint8_t> &maslp_key,
                               const ::SWA::Device &maslp_input) {
        HMACHasher hasher{maslp_algorithm, maslp_key.getData()};
        process_stream(*maslp_input.getInputStream(), [&hasher](const std::vector<char> &buffer) {
            hasher.update(buffer);
        });
        return hasher.hash();

    }
    int64_t masls_hash_xxh3 ( const ::SWA::Sequence<uint8_t>& maslp_input ) {
        return std::bit_cast<int64_t,uint64_t>(XXH3_64bits(maslp_input.getData().data(), maslp_input.size()));
    }

    int64_t masls_overload1_hash_xxh3 ( const ::SWA::String& maslp_input ) {
        return std::bit_cast<int64_t,uint64_t>(XXH3_64bits(maslp_input.s_str().data(), maslp_input.size()));
    }


}
