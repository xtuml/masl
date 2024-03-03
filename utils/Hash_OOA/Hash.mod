domain Hash is

    exception HashException;

    public type Algorithm is enum(
        MD5, SHA1,
        SHA224,   SHA256,   SHA384,   SHA512,
        SHA3_224, SHA3_256, SHA3_384, SHA3_512 );

    public type Hash is sequence of byte;
    public type Key is sequence of byte;

    //! Returns the cryptographic hash of the input bytes using the supplied algorithm
    public service hash (
        algorithm : in Algorithm,
        input     : in anonymous sequence of anonymous byte ) return Hash;

    //! Returns the cryptographic hash of the input string using the supplied algorithm
    public service hash (
        algorithm : in Algorithm,
        input     : in anonymous string ) return Hash;

    //! Returns the cryptographic hash of the input file using the supplied algorithm
    public service hash (
        algorithm : in Algorithm,
        input     : in anonymous device ) return Hash;

    //! Returns the HMAC code of the input bytes using the supplied key and algorithm
    public service hmac (
        algorithm : in Algorithm,
        key       : in Key,
        input     : in anonymous sequence of anonymous byte ) return Hash;

    //! Returns the HMAC code of the input string using the supplied key and algorithm
    public service hmac (
        algorithm : in Algorithm,
        key       : in Key,
        input     : in anonymous string ) return Hash;

    //! Returns the HMAC code of the input file using the supplied key and algorithm
    public service hmac (
        algorithm : in Algorithm,
        key       : in Key,
        input     : in anonymous device ) return Hash;

    //! Returns a non-cryptographic hash of the input bytes using the XXH3 algorithm
    //! See http://fastcompression.blogspot.com/2019/03/presenting-xxh3.html
    public service hash_xxh3 ( input : in anonymous sequence of anonymous byte ) return long_integer;

    //! Returns a non-cryptographic hash of the input string using the XXH3 algorithm
    //! See http://fastcompression.blogspot.com/2019/03/presenting-xxh3.html
    public service hash_xxh3 ( input : in anonymous string ) return long_integer;

end domain;