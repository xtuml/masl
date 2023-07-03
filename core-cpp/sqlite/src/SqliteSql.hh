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

#ifndef Sqlite_Sql_HH
#define Sqlite_Sql_HH

namespace SQLITE {

  namespace {
     const std::string SPACE              = " ";
     const std::string SQL_TERMINATOR     = ";";
     const std::string NEW_LINE           = "\n";
     const std::string ALL_COLUMNS        = "*";

     const std::string SELECT             = "SELECT";
     const std::string DELETE             = "DELETE";
     const std::string WHERE              = "WHERE";
     const std::string FROM               = "FROM";
     const std::string DROP_TABLE         = "DROP TABLE";
     const std::string CREATE_DATABASE    = "CREATE DATABASE";
     const std::string DROP_DATABASE      = "DROP DATABASE";

     const std::string START_TRANSACTION  = "BEGIN    TRANSACTION";
     const std::string COMMIT_TRANSACTION = "COMMIT   TRANSACTION";
     const std::string ABORT_TRANSACTION  = "ROLLBACK TRANSACTION";
  }

}
#endif
