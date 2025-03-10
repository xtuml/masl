/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "sql/ObjectSqlRepository.hh"
#include "sql/Exception.hh"
#include "sql/ObjectSql.hh"

namespace SQL {

    // ***********************************************************************
    // ***********************************************************************
    ObjectSqlRepository &ObjectSqlRepository::getInstance() {
        static ObjectSqlRepository instance;
        return instance;
    }

    // ***********************************************************************
    // ***********************************************************************
    ObjectSqlRepository::ObjectSqlRepository() {}

    // ***********************************************************************
    // ***********************************************************************
    ObjectSqlRepository::~ObjectSqlRepository() {}

    // ***********************************************************************
    // ***********************************************************************
    bool ObjectSqlRepository::registerObjectSql(const ObjectSql *const sql) {
        const std::string objectKey(sql->getDomainName() + "::" + sql->getObjectName());
        repository.insert(std::make_pair(objectKey, sql));
        return true;
    }

    // ***********************************************************************
    // ***********************************************************************
    void ObjectSqlRepository::deregisterObjectSql(const ObjectSql *const sql) {
        // Cannot remove the registered components as cannot be sure the
        // order that this singleton will be destroyed on shutdown. Therefore
        // do not remove any registered components.
    }

    // ***********************************************************************
    // ***********************************************************************
    const ObjectSql &
    ObjectSqlRepository::getObjectSql(const std::string &domainName, const std::string &objectName) const {
        const std::string objectKey(domainName + "::" + objectName);
        std::map<std::string, const ObjectSql *const>::const_iterator objSqlItr = repository.find(objectKey);
        if (objSqlItr == repository.end()) {
            throw SqlException(
                std::string("ObjectSqlRepository::getObjectSql : Failed to find "
                            "SQL details for object ") +
                objectKey
            );
        }
        return *(objSqlItr->second);
    }

} // end namespace SQL
