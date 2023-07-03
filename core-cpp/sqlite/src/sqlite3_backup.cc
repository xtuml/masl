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

#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <time.h>
#include "sqlite3.h"

int busy_handler ( void* , int count )
{
  // Arbitrary 1ms delay. Seems to take about 1ms to try the 
  // lock anyway (making the retry interval about 2ms), so not 
  // much point going shorter and cpu usage is almost zero 
  // with a 1ms delay. 
  static const timespec delay = {0,1000000};

  // Print message first time in
  if ( !count ) std::cout << "Waiting for database lock...\n" << std::flush;

  // wait a bit before trying again
  nanosleep(&delay,0);

  // retry indefinitely
  return 1;             
}


int main( int argc, char** argv )
{
  std::vector<std::string> args(argv+1,argv+argc);

  if ( args.size() != 2 )
  {
    std::cerr << "Usage: " << argv[0] << "<source> <dest>\n" << std::flush;
    return 1;
  }

  std::string sourceName = args[0];
  std::string destName = args[1];

  std::ifstream source(sourceName.c_str(), std::ios::in | std::ios::binary);

  if ( !source )
  {
    std::cerr << "Error: Could not open file: " << sourceName << "\n" << std::flush;
    return 1;
  }

  std::ofstream dest(destName.c_str(), std::ios::trunc | std::ios::binary);

  if ( !dest )
  {
    std::cerr << "Error: Could not write to file: " << destName << "\n" << std::flush;
    return 1;
  }


  sqlite3* database;

  if ( sqlite3_open(sourceName.c_str(),&database) != SQLITE_OK )
  {
    std::cerr << "Error: Could not open database: " << sourceName << "\n" << std::flush;
    return 1;
  }
 
  sqlite3_busy_handler(database,&busy_handler,0);

  if ( sqlite3_exec(database,"BEGIN TRANSACTION;",0,0,0) != SQLITE_OK )
  {
    std::cerr << "Error: Could not lock database: " << sourceName << "\n" << std::flush;
    return 1;
  }

  // Lock is not actually taken out until a read is done, so 
  // just read one value from the master table, which is the 
  // only thing we can guarantee will be there! 
  if ( sqlite3_exec(database,"SELECT name FROM sqlite_master LIMIT 1;",0,0,0) != SQLITE_OK )
  {
    std::cerr << "Error: Could read from database: " << sourceName << "\n" << std::flush;
    return 1;
  }

  std::cout << "Copying database "  << sourceName << " to " << destName << "..." << std::endl;
  dest << source.rdbuf() << std::flush;
  dest.close();

  // Don't close source until we've shut down the database, as 
  // the kernel will release the sqlite's lock on the file 
  // when our close is actioned (yes, really!). Not sure what 
  // would happen if we did the rollback and close if someone 
  // managed to get a write lock out before we do it, so play 
  // safe and let sqlite close down the db properly before the 
  // lock is released. 
  
  if ( sqlite3_exec(database,"ROLLBACK;",0,0,0) != SQLITE_OK)
  {
    std::cerr << "Error: Could not rollback database: " << sourceName << "\n" << std::flush;
    return 1;
  }
  
  if ( sqlite3_close(database) != SQLITE_OK )
  {
    std::cerr << "Error: Could close database: " << sourceName << "\n" << std::flush;
    return 1;
  }
  std::cout << "Copied database "  << sourceName << " to " << destName << "." << std::endl;
  
}
