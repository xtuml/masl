#include <gtest/gtest.h>
#include <gmock/gmock.h>
#include "Logger_OOA/__Logger_types.hh"
#include "Logger_OOA/__Logger_services.hh"
#include <log4cplus/log4cplus.h>
#include <swa/Stack.hh>
#include <swa/Process.hh>

class MockAppender : public log4cplus::Appender {
public:
    void close() override {}

    void append(const log4cplus::spi::InternalLoggingEvent &event) override {
        append(event.getFile(),
        event.getFunction(),
        event.getLine(),
        event.getLogLevel(),
        event.getLoggerName(),
        event.getMessage());
    }

    MOCK_METHOD(void,append,(
            const std::string& file,
            const std::string& function,
            int line,
            log4cplus::LogLevel level,
            const std::string& logger,
            const std::string& message),());

    ~MockAppender() override {
        destructorImpl();
    }
};


using namespace masld_Logger;
using testing::_;


struct Logger : public testing::Test {
    Logger() {
        log4cplus::Logger::getRoot().setLogLevel(log4cplus::TRACE_LOG_LEVEL);
        log4cplus::Logger::getRoot().addAppender(appender_ptr);
        SWA::Stack::getInstance().push(SWA::StackFrame{SWA::StackFrame::DomainService,SWA::Process::getInstance().registerDomain("LogTest").getId(),2});
        SWA::Stack::getInstance().top().setLine(3);
    }

    log4cplus::SharedAppenderPtr appender_ptr{new MockAppender()};
    MockAppender& appender() { return dynamic_cast<MockAppender&>(*appender_ptr); }

    ~Logger() {
        log4cplus::Logger::getRoot().removeAppender(appender_ptr);
        SWA::Stack::getInstance().pop();
    }
};


TEST_F(Logger,logTrace) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::TRACE_LOG_LEVEL,"mylogger","message"));
        masls_log(masld_Logger::maslt_Priority::masle_Trace,"mylogger","message");
}

TEST_F(Logger,logDebug) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::DEBUG_LOG_LEVEL,"mylogger","message"));
     masls_log(masld_Logger::maslt_Priority::masle_Debug,"mylogger","message");
}

TEST_F(Logger,logInfo) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::INFO_LOG_LEVEL,"mylogger","message"));
    masls_log(masld_Logger::maslt_Priority::masle_Information, "mylogger","message");
}

TEST_F(Logger,logWarn) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::WARN_LOG_LEVEL,"mylogger","message"));
    masls_log(masld_Logger::maslt_Priority::masle_Warning, "mylogger","message");
}

TEST_F(Logger,logError) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::ERROR_LOG_LEVEL,"mylogger","message"));
    masls_log(masld_Logger::maslt_Priority::masle_Error, "mylogger","message");
}

TEST_F(Logger,logFatal) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::FATAL_LOG_LEVEL,"mylogger","message"));
    masls_log(masld_Logger::maslt_Priority::masle_Fatal, "mylogger","message");
}

TEST_F(Logger,trace) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::TRACE_LOG_LEVEL,"mylogger","message"));
    masls_trace("mylogger","message");
}

TEST_F(Logger,debug) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::DEBUG_LOG_LEVEL,"mylogger","message"));
    masls_debug("mylogger","message");
}

TEST_F(Logger,info) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::INFO_LOG_LEVEL,"mylogger","message"));
    masls_information("mylogger","message");
}

TEST_F(Logger,warn) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::WARN_LOG_LEVEL,"mylogger","message"));
    masls_warning("mylogger","message");
}

TEST_F(Logger,error) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::ERROR_LOG_LEVEL,"mylogger","message"));
    masls_error("mylogger","message");
}

TEST_F(Logger,fatal) {
    EXPECT_CALL(appender(),append(_,_,_,log4cplus::FATAL_LOG_LEVEL,"mylogger","message"));
    masls_fatal("mylogger","message");
}

TEST_F(Logger,traceEnabled) {
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::TRACE_LOG_LEVEL);
    EXPECT_TRUE(masls_traceEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::DEBUG_LOG_LEVEL);
    EXPECT_FALSE(masls_traceEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::INFO_LOG_LEVEL);
    EXPECT_FALSE(masls_traceEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::WARN_LOG_LEVEL);
    EXPECT_FALSE(masls_traceEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::ERROR_LOG_LEVEL);
    EXPECT_FALSE(masls_traceEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::FATAL_LOG_LEVEL);
    EXPECT_FALSE(masls_traceEnabled("mylogger"));
}

TEST_F(Logger,debugEnabled) {
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::TRACE_LOG_LEVEL);
    EXPECT_TRUE(masls_debugEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::DEBUG_LOG_LEVEL);
    EXPECT_TRUE(masls_debugEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::INFO_LOG_LEVEL);
    EXPECT_FALSE(masls_debugEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::WARN_LOG_LEVEL);
    EXPECT_FALSE(masls_debugEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::ERROR_LOG_LEVEL);
    EXPECT_FALSE(masls_debugEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::FATAL_LOG_LEVEL);
    EXPECT_FALSE(masls_debugEnabled("mylogger"));
}

TEST_F(Logger,informationEnabled) {
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::TRACE_LOG_LEVEL);
    EXPECT_TRUE(masls_informationEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::DEBUG_LOG_LEVEL);
    EXPECT_TRUE(masls_informationEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::INFO_LOG_LEVEL);
    EXPECT_TRUE(masls_informationEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::WARN_LOG_LEVEL);
    EXPECT_FALSE(masls_informationEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::ERROR_LOG_LEVEL);
    EXPECT_FALSE(masls_informationEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::FATAL_LOG_LEVEL);
    EXPECT_FALSE(masls_informationEnabled("mylogger"));
}

TEST_F(Logger,warningEnabled) {
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::TRACE_LOG_LEVEL);
    EXPECT_TRUE(masls_warningEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::DEBUG_LOG_LEVEL);
    EXPECT_TRUE(masls_warningEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::INFO_LOG_LEVEL);
    EXPECT_TRUE(masls_warningEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::WARN_LOG_LEVEL);
    EXPECT_TRUE(masls_warningEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::ERROR_LOG_LEVEL);
    EXPECT_FALSE(masls_warningEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::FATAL_LOG_LEVEL);
    EXPECT_FALSE(masls_warningEnabled("mylogger"));
}

TEST_F(Logger,errorEnabled) {
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::TRACE_LOG_LEVEL);
    EXPECT_TRUE(masls_errorEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::DEBUG_LOG_LEVEL);
    EXPECT_TRUE(masls_errorEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::INFO_LOG_LEVEL);
    EXPECT_TRUE(masls_errorEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::WARN_LOG_LEVEL);
    EXPECT_TRUE(masls_errorEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::ERROR_LOG_LEVEL);
    EXPECT_TRUE(masls_errorEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::FATAL_LOG_LEVEL);
    EXPECT_FALSE(masls_errorEnabled("mylogger"));
}

TEST_F(Logger,fatalEnabled) {
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::TRACE_LOG_LEVEL);
    EXPECT_TRUE(masls_fatalEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::DEBUG_LOG_LEVEL);
    EXPECT_TRUE(masls_fatalEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::INFO_LOG_LEVEL);
    EXPECT_TRUE(masls_fatalEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::WARN_LOG_LEVEL);
    EXPECT_TRUE(masls_fatalEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::ERROR_LOG_LEVEL);
    EXPECT_TRUE(masls_fatalEnabled("mylogger"));
    log4cplus::Logger::getInstance("mylogger").setLogLevel(log4cplus::FATAL_LOG_LEVEL);
    EXPECT_TRUE(masls_fatalEnabled("mylogger"));
}

TEST_F(Logger,setLogLevel) {
    masls_setLogLevel("mylogger",masld_Logger::maslt_Priority::masle_Debug);
    EXPECT_EQ(log4cplus::Logger::getRoot().getLogLevel(),log4cplus::TRACE_LOG_LEVEL);
    EXPECT_EQ(log4cplus::Logger::getInstance("mylogger").getLogLevel(),log4cplus::DEBUG_LOG_LEVEL);
}

TEST_F(Logger,filename) {
    EXPECT_CALL(appender(),append("<unknown>",_,_,_,_,_));
    masls_debug("mylogger","message");
}

TEST_F(Logger,lineNo) {
    EXPECT_CALL(appender(),append(_,_,42,_,_,_));
    SWA::Stack::getInstance().top().setLine(42);
    masls_debug("mylogger","message");
}

TEST_F(Logger,domainServiceName) {
    EXPECT_CALL(appender(),append(_,"LogTest::Service_2",_,_,_,_));
    SWA::Stack::EnteringDomainService raii(SWA::Process::getInstance().getDomain("LogTest").getId(),2);
    masls_debug("mylogger","message");
}

TEST_F(Logger,objectServiceName) {
    EXPECT_CALL(appender(),append(_,"LogTest::Object_2.Service_3",_,_,_,_));
    SWA::Stack::EnteringObjectService raii(SWA::Process::getInstance().getDomain("LogTest").getId(),2,3);
    masls_debug("mylogger","message");
}

TEST_F(Logger,terminatorServiceName) {
    EXPECT_CALL(appender(),append(_,"LogTest::Terminator_2~>Service_3",_,_,_,_));
    SWA::Stack::EnteringTerminatorService raii(SWA::Process::getInstance().getDomain("LogTest").getId(),2,3);
    masls_debug("mylogger","message");
}

TEST_F(Logger,stateActionName) {
    EXPECT_CALL(appender(),append(_,"LogTest::Object_2.State_3",_,_,_,_));
    SWA::Stack::EnteringState raii(SWA::Process::getInstance().getDomain("LogTest").getId(),2,3);
    masls_debug("mylogger","message");
}