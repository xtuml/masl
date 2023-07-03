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

#include <vector>
#include <iterator>
#include <algorithm>
#include <iostream>

#include "sql/Util.hh"
#include "sqlite/Resultset.hh"
#include "sqlite/Exception.hh"

namespace SQLITE {


//********************************************************
//********************************************************
ResultSet::ResultSet():
   rows_(0),
   columns_(0)
{

}

//********************************************************
//********************************************************
ResultSet::~ResultSet()
{    
}

//********************************************************
//********************************************************
void ResultSet::clearAll()     
{ 
  columns_ = 0; 
  columnNames_.clear();  
  clearRows(); 
}

//********************************************************
//********************************************************
void ResultSet::clearRows()    
{ 
  rows_ = 0; matrix_.clear(); 
}

//********************************************************
//********************************************************
void ResultSet::setColumns(const ColumnType iColumns)  
{ 
  // If the matrix has data and we are attempting to change
  // the size of the matrix then report an error.
  if (matrix_.empty() == false && columns_ != iColumns){
     throw SqliteException("ResultSet::setColumns - operation failed : ResultSet already contains data");  
  }
  columns_ = iColumns; 
  columnNames_.resize(iColumns);
}

//********************************************************
//********************************************************
void ResultSet::addColumnName (const ColumnType iIndex, const EntryType& iColumnName)
{
  checkColumnIndex(iIndex);
  std::string lowerCaseName;
  convertToLowerCase(iColumnName,lowerCaseName);
  columnNames_[iIndex] = lowerCaseName;
}


//********************************************************
//********************************************************
void ResultSet::appendRow (const EntryContainerType& iRow)
{
  checkColumnSize(iRow.size());
  matrix_.push_back(iRow);
  ++rows_;
}

//********************************************************
//********************************************************
const ResultSet::EntryContainerType& ResultSet::getRow(const RowType iRowIndex) const 
{  
  checkRowIndex(iRowIndex);
  return matrix_[iRowIndex];
}


//********************************************************
//********************************************************
void ResultSet::display() const
{
   std::string delimiter;
   columnNames_.size() == 1 ? delimiter += "" : delimiter += " | ";
   
   std::copy(columnNames_.begin(), columnNames_.end(), 
             std::ostream_iterator<std::string>(std::cout,delimiter.c_str()));
   std::cout << std::endl;
   
   std::vector<EntryContainerType>::const_iterator rowItr = matrix_.begin();
   std::vector<EntryContainerType>::const_iterator rowEnd = matrix_.end();
   for(;rowItr != rowEnd; ++rowItr){
       EntryContainerType currentRow = (*rowItr);
       std::copy(currentRow.begin(), currentRow.end(), std::ostream_iterator<std::string>(std::cout,delimiter.c_str()));
       std::cout << std::endl;
   }
}

//********************************************************
//********************************************************
bool ResultSet::operator == (const ResultSet& iRhs) const 
{ 
  return    rows_        == iRhs.rows_        &&
            columns_     == iRhs.columns_     &&
            columnNames_ == iRhs.columnNames_ &&
            matrix_      == iRhs.matrix_; 
}

//********************************************************
//********************************************************
bool ResultSet::operator != (const ResultSet& iRhs) const 
{ 
  return !(*this == iRhs); 
}

//********************************************************
//********************************************************
void  ResultSet::convertToLowerCase(const std::string& iSource, std::string& iDestination) const
{
   iDestination.clear();
   for(unsigned int x = 0; x < iSource.size(); ++x){
      iDestination += tolower(iSource[x]);
   }
}

//********************************************************
//********************************************************
void  ResultSet::checkColumnIndex (const ColumnType iIndex) const
{
  if (columns_ == 0 || iIndex > columns_-1) {
      throw SqliteException("ResultSet::checkColumnIndex invalid index");
  }
}

//********************************************************
//********************************************************
void  ResultSet::checkRowIndex (const RowType iIndex) const
{
  if (rows_ == 0 || iIndex > rows_-1) {
      throw SqliteException("ResultSet::checkRowIndex invalid index");
  }
}
//********************************************************
//********************************************************
void  ResultSet::checkColumnSize (const ColumnType iColumns) const
{
  if (iColumns != columns_) {
      throw SqliteException("ResultSet::checkColumnSize : column size mismatch");
  }
}


} // end MBUS namespace
