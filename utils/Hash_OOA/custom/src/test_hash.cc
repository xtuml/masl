#include "gtest/gtest.h"
#include "Hash_OOA/__Hash_types.hh"
#include "Hash_OOA/__Hash_services.hh"
#include <boost/algorithm/hex.hpp>
#include <boost/make_shared.hpp>
#include <sstream>

using namespace masld_Hash;

SWA::Sequence<uint8_t> to_bytes(const std::string& in) {
    return SWA::Sequence<uint8_t>(in.begin(),in.end());
}

SWA::Sequence<uint8_t> from_hex(const std::string& in) {
    SWA::Sequence<uint8_t> result;
    boost::algorithm::unhex(in.begin(),in.end(),std::back_inserter(result));
    return result;
}

struct Params {
    maslt_Algorithm algorithm;
    std::string hash;
    std::string hmac;
};

struct TestHash : public testing::TestWithParam<Params> {
    std::string input = "Hello World!";
    std::string key = "Our Little Secret";
};

TEST_P(TestHash,Bytes) {
    EXPECT_EQ(masls_hash(GetParam().algorithm,to_bytes(input)),from_hex(GetParam().hash));

}

TEST_P(TestHash,String) {
    EXPECT_EQ(masls_overload1_hash(GetParam().algorithm,input),from_hex(GetParam().hash));
}

TEST_P(TestHash,File) {
    SWA::Device device{boost::make_shared<std::ostringstream>(input)};
    EXPECT_EQ(masls_overload2_hash(GetParam().algorithm,device),from_hex(GetParam().hash));

}

TEST_P(TestHash,HMACBytes) {
    EXPECT_EQ(masls_hmac(GetParam().algorithm,to_bytes(key),to_bytes(input)),from_hex(GetParam().hmac));

}

TEST_P(TestHash,HMACString) {
    EXPECT_EQ(masls_overload1_hmac(GetParam().algorithm,to_bytes(key),input),from_hex(GetParam().hmac));

}

TEST_P(TestHash,HMACFile) {
    SWA::Device device{boost::make_shared<std::ostringstream>(input)};
    EXPECT_EQ(masls_overload2_hmac(GetParam().algorithm,to_bytes(key),device),from_hex(GetParam().hmac));
}

INSTANTIATE_TEST_SUITE_P(All,TestHash,testing::Values(
        Params{ maslt_Algorithm::masle_MD5,         "ed076287532e86365e841e92bfc50d8c",
                                                    "10f085910d99e2f9b3a6f4a580a09df6"},
        Params{ maslt_Algorithm::masle_SHA1,        "2ef7bde608ce5404e97d5f042f95f89f1c232871",
                                                    "d530a54086842943b06be086230f191ab6be4f3a"},
        Params{ maslt_Algorithm::masle_SHA224,      "4575bb4ec129df6380cedde6d71217fe0536f8ffc4e18bca530a7a1b",
                                                    "4ab2b234ee483d255b08950be52a0c578147427bd896ecd8e6485aa1"},
        Params{ maslt_Algorithm::masle_SHA256,      "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
                                                    "ff2c55fae276cfe9aaeee2b83317e8279d29f7efa5085f1aa4616c66cdb55b71"},
        Params{ maslt_Algorithm::masle_SHA384,      "bfd76c0ebbd006fee583410547c1887b0292be76d582d96c242d2a792723e3fd6fd061f9d5cfd13b8f961358e6adba4a",
                                                    "8dee995a393ff9629ccfb14a930133c212532e676a9a6b16f56921b512f08e634c42d53684be5fa72310b953596213b1"},
        Params{ maslt_Algorithm::masle_SHA512,      "861844d6704e8573fec34d967e20bcfef3d424cf48be04e6dc08f2bd58c729743371015ead891cc3cf1c9d34b49264b510751b1ff9e537937bc46b5d6ff4ecc8",
                                                    "4b18e8665d3ea3c11b50670a8c73288989dee6ac323802c3a715561b846c90d49ce00a08064fc5972356bfd3b46cf9cc11de38061e3f9b138e2bd4c8047cd2c2"},
        Params{ maslt_Algorithm::masle_SHA3_224,    "716596afadfa17cd1cb35133829a02b03e4eed398ce029ce78a2161d",
                                                    "0e777b55289a3a647e24e2fb313206155e04c55dc93b469f71c82afb"},
        Params{ maslt_Algorithm::masle_SHA3_256,    "d0e47486bbf4c16acac26f8b653592973c1362909f90262877089f9c8a4536af",
                                                    "87dccf535cd4ab355d5bbf022b6f4ddc8e06c33b5e55d370d6e5f0190b283c89"},
        Params{ maslt_Algorithm::masle_SHA3_384,    "f324cbd421326a2abaedf6f395d1a51e189d4a71c755f531289e519f079b224664961e385afcc37da348bd859f34fd1c",
                                                    "df4ea20cd7bb50fea7a1fb63c160cc66956ec3ebcf45730f0f1e999c516e2058eae2a8e73e53bc9406547141628e0b52"},
        Params{ maslt_Algorithm::masle_SHA3_512,    "32400b5e89822de254e8d5d94252c52bdcb27a3562ca593e980364d9848b8041b98eabe16c1a6797484941d2376864a1b0e248b0f7af8b1555a778c336a5bf48",
                                                    "dbc93992fc4359f85b89f308dd0d852c577bc045cfc2e3dc4be76bef7cb98cfd4aba4c693b13c98ff2af5c9f899aee3802a5a6589632741907b4df8bb7c82ba5"}
        ),[](const testing::TestParamInfo<Params>& info){
            return info.param.algorithm.getText();
        });

TEST(TestXXH3,String) {
    EXPECT_EQ(masls_overload1_hash_xxh3("Hello World!"), 0x673e3c493921a2d5);
}

TEST(TestXXH3,Bytes) {
    EXPECT_EQ(masls_hash_xxh3(to_bytes("Hello World!")), 0x673e3c493921a2d5);
}
