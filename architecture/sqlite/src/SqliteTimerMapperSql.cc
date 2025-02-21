/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "sqlite/BlobData.hh"
#include "swa/Process.hh"
#include "swa/Timestamp.hh"
#include <string>

#include "sql/Schema.hh"
#include "sql/TimerMapperSqlFactory.hh"
#include "sql/Util.hh"

#include "sqlite/Database.hh"
#include "sqlite/EventParameterCodecs.hh"
#include "sqlite/Exception.hh"
#include "sqlite/Resultset.hh"
#include "sqlite/SqliteTimerMapperSql.hh"

#include <memory>
#include "boost/tuple/tuple.hpp"

namespace SQLITE {

// *****************************************************************
// *****************************************************************
namespace {

const ::std::string TIMER_TABLE_NAME("S_TIMER_TABLE");
const ::std::string createTimerTableStatment() {
    ::std::string createTableStatement;
    createTableStatement += " CREATE TABLE " + TIMER_TABLE_NAME + " (";
    createTableStatement += " timer_id        INTEGER ,"; /*  1 */
    createTableStatement += " scheduled       INTEGER ,"; /*  2 */
    createTableStatement += " expired         INTEGER ,"; /*  3 */
    createTableStatement += " expiry_time     INTEGER ,"; /*  4 */
    createTableStatement += " period          INTEGER ,"; /*  5 */
    createTableStatement += " missed          INTEGER ,"; /*  6 */
    createTableStatement += " domain_name     TEXT    ,"; /*  7 */
    createTableStatement += " src_object_id   INTEGER ,"; /*  8 */
    createTableStatement += " dst_object_id   INTEGER ,"; /*  9 */
    createTableStatement += " event_id        INTEGER ,"; /* 10 */
    createTableStatement += " src_instance_id INTEGER ,"; /* 11 */
    createTableStatement += " dst_instance_id INTEGER ,"; /* 12 */
    createTableStatement += " parameters      BLOB    ,"; /* 13 */
    createTableStatement += " PRIMARY KEY (timer_id) );\n";
    return createTableStatement;
}

bool registerSchema = ::SQL::Schema::singleton().registerTable(
    TIMER_TABLE_NAME, createTimerTableStatment());
bool registerSQLImpl = ::SQL::TimerMapperSqlFactory::singleton().registerImpl(
    std::shared_ptr<SqliteTimerMapperSql>(new SqliteTimerMapperSql));

// *****************************************************************
// *****************************************************************
class SqliteTimerEvent : public ::SWA::Event {
  public:
    SqliteTimerEvent() : domainId(0), destObjectId(0), eventId(0) {}
    virtual ~SqliteTimerEvent() {}

    void setDomainId(const int id) { domainId = id; }
    void setObjectId(const int id) { destObjectId = id; }
    void setEventId(const int id) { eventId = id; }

    virtual int getDomainId() const { return domainId; }
    virtual int getObjectId() const { return destObjectId; }
    virtual int getEventId() const { return eventId; }

    virtual void invoke() const {
        throw SWA::ProgramError("SqliteTimerEvent::invoke() should not be "
                                "called, is only a place holder");
    }

  private:
    int domainId;
    int destObjectId;
    int eventId;
};

} // namespace

// *****************************************************************
// *****************************************************************
SqliteTimerMapperSql::SqliteTimerMapperSql()
    : createStatement(
          std::string("INSERT INTO ") + TIMER_TABLE_NAME + " ( timer_id, " +
          " scheduled      ," + " expired        ," + " expiry_time    ," +
          " period         ," + " missed         ," + " domain_name    ," +
          " src_object_id  ," + " dst_object_id  ," + " event_id       ," +
          " src_instance_id," + " dst_instance_id " +
          " ) VALUES(:1,0,0,0,0,0,\"\",-1,-1,-1,-1,-1);\n"),

      deleteStatement(std::string("DELETE FROM ") + TIMER_TABLE_NAME +
                      " WHERE timer_id = :1;\n"),

      updateStatement(std::string("UPDATE      ") + TIMER_TABLE_NAME + " SET " +
                      "scheduled       = :2, " + "expired         = :3, " +
                      "expiry_time     = :4, " + "period          = :5, " +
                      "missed          = :6, " + "domain_name     = :7, " +
                      "src_object_id   = :8, " + "dst_object_id   = :9, " +
                      "event_id        = :10, " + "src_instance_id = :11, " +
                      "dst_instance_id = :12, " + "parameters      = :13 " +
                      " WHERE timer_id = :1;\n") {}

// *****************************************************************
// *****************************************************************
SqliteTimerMapperSql::~SqliteTimerMapperSql() {}

// *****************************************************************
// *****************************************************************
void SqliteTimerMapperSql::initialise(SQL::TimerMapper &mapper) {
    createStatement.prepare();
    deleteStatement.prepare();
    updateStatement.prepare();

    std::string timerQuery = std::string("SELECT ") +
                             " timer_id       ," + /*  0 */
                             " scheduled      ," + /*  1 */
                             " expired        ," + /*  2 */
                             " expiry_time    ," + /*  3 */
                             " period         ," + /*  4 */
                             " missed         ," + /*  5 */
                             " domain_name    ," + /*  6 */
                             " src_object_id  ," + /*  7 */
                             " dst_object_id  ," + /*  8 */
                             " event_id       ," + /*  9 */
                             " src_instance_id," + /* 10 */
                             " dst_instance_id," + /* 11 */
                             " parameters      " + /* 12 */
                             " FROM " + TIMER_TABLE_NAME + ";";

    Database &database = Database::singleton();
    ResultSet timerResults;
    if (database.executeQuery(timerQuery, timerResults)) {
        for (ResultSet::RowType rowIndex = 0; rowIndex < timerResults.getRows();
             ++rowIndex) {
            std::shared_ptr<SqliteTimerEvent> realisedEvent(
                new SqliteTimerEvent);
            const ResultSet::EntryContainerType &currentRow =
                timerResults.getRow(rowIndex);

            SQL::TimerMapper::EventTimerData data;

            data.id = ::SQL::stringToValue<uint32_t>(currentRow.at(0));
            data.scheduled = ::SQL::stringToValue<int>(currentRow.at(1));
            data.expired = ::SQL::stringToValue<int>(currentRow.at(2));
            data.expiryTime =
                ::SQL::stringToValue<SWA::Timestamp>(currentRow.at(3));
            data.period = ::SQL::stringToValue<SWA::Duration>(currentRow.at(4));
            data.missed = ::SQL::stringToValue<int>(currentRow.at(5));

            if (data.scheduled) {
                std::string domainName = currentRow.at(6);
                int domainId = domainName.size() ? SWA::Process::getInstance()
                                                       .getDomain(domainName)
                                                       .getId()
                                                 : 0;

                int objectId = ::SQL::stringToValue<int>(currentRow.at(8));
                int eventId = ::SQL::stringToValue<int>(currentRow.at(9));

                BlobData params(currentRow.at(12));
                data.event = EventParameterCodecs::getInstance().decode(
                    domainId, objectId, eventId, params);

                SWA::IdType srcInstanceId(
                    ::SQL::stringToValue<int>(currentRow.at(10)));
                if (srcInstanceId != -1) {
                    data.event->setSource(
                        ::SQL::stringToValue<int>(currentRow.at(7)),
                        srcInstanceId);
                }

                SWA::IdType dstInstanceId(
                    ::SQL::stringToValue<int>(currentRow.at(11)));
                if (dstInstanceId != -1) {
                    data.event->setDest(dstInstanceId);
                }
            }

            mapper.restoreTimer(data);
        }
    } else {
        throw SqliteException(::boost::make_tuple(
            "Failed to initialise SQLite stored timers[", timerQuery,
            "] : ", database.getCurrentError()));
    }
}

// *****************************************************************
// *****************************************************************
uint32_t SqliteTimerMapperSql::getRowCount() {
    const std::string query(std::string("SELECT count(*) FROM ") +
                            TIMER_TABLE_NAME);
    uint32_t rowCount = getFunctionValue(query);
    ;
    return rowCount;
}
// *****************************************************************
// *****************************************************************
SqliteTimerMapperSql::TimerIdType SqliteTimerMapperSql::getMaxTimerId() {
    const std::string query(std::string("SELECT max(timer_id) FROM ") +
                            TIMER_TABLE_NAME);
    TimerIdType maxId = getFunctionValue(query);
    return maxId;
}

// *****************************************************************
// *****************************************************************
void SqliteTimerMapperSql::executeCreate(const TimerIdType timerId) {
    // A timer will always be created before it is used, therefore just
    // fill the timer columns with default data.
    createStatement.execute(::boost::make_tuple(timerId));
}

// *****************************************************************
// *****************************************************************
void SqliteTimerMapperSql::executeDelete(const TimerIdType timerId) {
    deleteStatement.execute(timerId);
}

// *****************************************************************
// *****************************************************************
void SqliteTimerMapperSql::executeUpdate(
    const SQL::TimerMapper::EventTimerData &data) {
    BlobData params;

    if (data.event) {
        EventParameterCodecs::getInstance().encode(data.event, params);
    }

    updateStatement.execute(
        /*  1 */ ::boost::make_tuple(
            data.id,
            /*  2 */ data.scheduled,
            /*  3 */ data.expired,
            /*  4 */ data.expiryTime,
            /*  5 */ data.period,
            /*  6 */ data.missed,
            /*  7 */ !data.event ? std::string()
                                 : SWA::Process::getInstance()
                                       .getDomain(data.event->getDomainId())
                                       .getName(),
            /*  8 */ !data.event || !data.event->getHasSource()
                ? -1
                : data.event->getSourceObjectId(),
            /*  9 */ !data.event ? -1 : data.event->getObjectId(),
            /* 10 */ !data.event ? -1 : data.event->getEventId()),
        /* 11 */ ::boost::make_tuple(!data.event || !data.event->getHasSource()
                                         ? -1
                                         : data.event->getSourceInstanceId(),
                                     /* 12 */ !data.event ||
                                             !data.event->getHasDest()
                                         ? -1
                                         : data.event->getDestInstanceId(),
                                     /* 13 */ boost::ref(params)));
}

// *****************************************************************
// *****************************************************************
SqliteTimerMapperSql::TimerIdType
SqliteTimerMapperSql::getFunctionValue(const std::string &functionQuery) {
    TimerIdType cellvalue = 0;
    ResultSet result;
    if (Database::singleton().executeQuery(functionQuery, result) == true) {
        if (result.getRows() == 1 && result.getColumns() == 1) {
            const std::string cellValue = result.getRow(0).at(0);
            if (cellValue != "NULL") {
                cellvalue = ::SQL::stringToValue<TimerIdType>(cellValue);
            }
        } else {
            throw SqliteException(::boost::make_tuple(
                "Sqlite function query failed [", functionQuery,
                "] : incorrect row/column count in result set ",
                result.getRows(), "/", result.getColumns()));
        }
    } else {
        throw SqliteException(::boost::make_tuple(
            "Sqlite function query failed [", functionQuery,
            "] : ", Database::singleton().getCurrentError()));
    }
    return cellvalue;
}

} // end namespace SQLITE
