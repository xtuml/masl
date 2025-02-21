#include <cstddef>
#include <concepts>
#include <unordered_set>
#include <print>
#include <tuple>
#include <string>
#include <optional>

// Experimental... not used


namespace Hash {


class fnv1a {
private:
    std::size_t h = 14695981039346656037u;
public:
    using result_type = std::size_t;

    void operator()(void const * key, std::size_t len) noexcept
    {
        unsigned char const * p = static_cast<unsigned char const*>(key);
        unsigned char const * const e = p + len;
        for (; p < e; ++p) {
            h = (h ^ *p) * 1099511628211u;
        }
    }

    explicit operator result_type() noexcept {
        return h;
    }
};

template<typename Algorithm>
void hash_append(Algorithm& hasher, const std::string& v) {
    hasher(v.data(),v.size());
}

template<typename Algorithm,typename T>
requires std::is_enum<T> || std::is_arithmetic<T>
void hash_append(Algorithm& hasher, const T& v)  {
    hasher(&v,sizeof(v));
}
template<typename Algorithm,std::floating_point T>
void hash_append(Algorithm& hasher, const T& v) {
    hasher(&v,sizeof(v));
}

template<typename Algorithm,typename T1, typename T2>
void hash_append(Algorithm& hasher, const std::pair<T1,T2>& v) {
    hash_append(hasher,v.first);
    hash_append(hasher,v.second);
}

template<typename Algorithm,typename T>
void hash_append(Algorithm& hasher, const std::optional<T>& v) {
    if (v) {
        hash_append(hasher,v.value());
    }
}


template<typename Algorithm,typename... T>
void hash_append(Algorithm& hasher, const std::tuple<T...>& v) {
   std::apply([&hasher](const auto&... vs){ (hash_append(hasher,vs), ...); }, v);
}

template<typename Algorithm>
struct GenericHash {
    using result_type = typename Algorithm::result_type;
    template<typename T>
    result_type operator()(const T&t) const noexcept {
        Algorithm hasher;
        hash_append(hasher,t);
        return static_cast<result_type>(hasher);
    }
};

using FNV1A = GenericHash<fnv1a>;

}

struct C {
    int a{1};
    int b{2};

    template<typename Algorithm>
    friend void hash_append(Algorithm& hasher, const C& c) {
        hash_append(hasher,c.a);
        hash_append(hasher,c.b);
    }
};

int tst() {

    std::unordered_set<int,Hash::FNV1A> myset;
    std::unordered_set<std::pair<int,int>,Hash::FNV1A> myset2;

    myset2.emplace(1,1);

    Hash::FNV1A hasher;
    std::println("{}",hasher(1));
    std::println("{}",hasher(2));
    std::println("{}",hasher(3));
    std::println("{}",hasher(4));
    std::println("{}",hasher(5));
    std::println("{}",hasher(std::make_pair(1,2)));
    std::println("{}",hasher(std::make_tuple(1,2)));
    std::println("{}",hasher(std::string("hello")));
    std::println("{}",hasher(C{}));
}

