#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include "DocumentStore.hh"

using namespace std::literals;

class DocumentStore : public testing::Test {
protected:
    std::string id1 = "https://www.example.com/test1";
    std::string id2 = "https://www.example.com/test2";
    nlohmann::json noIdDoc = R"( { "a" : 1, "b" : 2 } )"_json;
    nlohmann::json id1Doc = R"( { "$id" : "https://www.example.com/test1", "a" : 1, "b" : 2 } )"_json;
    nlohmann::json id2Doc = R"( { "$id" : "https://www.example.com/test2", "a" : 1, "b" : 2 } )"_json;
    masld_JSON::DocumentStore store;
};


TEST_F(DocumentStore,noId) {

    auto id = store.register_document(noIdDoc);

    EXPECT_TRUE(store.has_document(id));
    EXPECT_THAT(store.list_documents(),
        testing::UnorderedElementsAre(id));
    EXPECT_EQ(store.get_document(id),noIdDoc);
    store.deregister_document(id);

    EXPECT_FALSE(store.has_document(id));
    EXPECT_THAT(store.list_documents(),testing::IsEmpty());

}

TEST_F(DocumentStore,suppliedId) {

    store.register_document(id1,noIdDoc);

    EXPECT_TRUE(store.has_document(id1));
    EXPECT_THAT(store.list_documents(),
        testing::UnorderedElementsAre(id1));
    EXPECT_EQ(store.get_document(id1),noIdDoc);
    store.deregister_document(id1);

    EXPECT_FALSE(store.has_document(id1));
    EXPECT_THAT(store.list_documents(),testing::IsEmpty());


}

TEST_F(DocumentStore,documentId) {

    auto id = store.register_document(id1Doc);
    EXPECT_EQ(id,id1);

    EXPECT_TRUE(store.has_document(id1));
    EXPECT_THAT(store.list_documents(),
            testing::UnorderedElementsAre(id1));
    EXPECT_EQ(store.get_document(id1),id1Doc);
    store.deregister_document(id1);

    EXPECT_FALSE(store.has_document(id1));
    EXPECT_THAT(store.list_documents(),testing::IsEmpty());
}

TEST_F(DocumentStore,suppliedAndDocumentId) {

    store.register_document(id1,id2Doc);

    EXPECT_TRUE(store.has_document(id1));
    EXPECT_TRUE(store.has_document(id2));
    EXPECT_THAT(store.list_documents(),
            testing::UnorderedElementsAre(id1,id2));
    EXPECT_EQ(store.get_document(id1),id2Doc);
    EXPECT_EQ(store.get_document(id2),id2Doc);

    store.deregister_document(id1);

    EXPECT_FALSE(store.has_document(id1));
    EXPECT_FALSE(store.has_document(id2));
    EXPECT_THAT(store.list_documents(),testing::IsEmpty());

    store.register_document(id1,id2Doc);
    store.deregister_document(id2);

    EXPECT_FALSE(store.has_document(id1));
    EXPECT_FALSE(store.has_document(id2));
    EXPECT_THAT(store.list_documents(),testing::IsEmpty());

}

TEST_F(DocumentStore,suppliedAndDocumentIdSame) {

    store.register_document(id1,id1Doc);

    EXPECT_TRUE(store.has_document(id1));
    EXPECT_THAT(store.list_documents(),
            testing::UnorderedElementsAre(id1));
    EXPECT_EQ(store.get_document(id1),id1Doc);
    store.deregister_document(id1);

    EXPECT_FALSE(store.has_document(id1));
    EXPECT_THAT(store.list_documents(),testing::IsEmpty());
}
