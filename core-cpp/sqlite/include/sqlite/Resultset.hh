//============================================================================//
// UK Crown Copyright (c) 2007. All rights reserved.
//
// File:   Resultset.hh
//
//============================================================================//

#ifndef Sqlite_ResultSet_HH
#define Sqlite_ResultSet_HH

#include <string>
#include <vector>

namespace SQLITE {

// *****************************************************************
//! @brief A container to hold SQL query result
//!     The ResultSet class provides a container that holds the 
//! information returned by an SQL query.
//!
// *****************************************************************
class ResultSet
{
    public:
        typedef unsigned int RowType;
        typedef unsigned int ColumnType;
        typedef std::string EntryType;
        typedef std::vector<EntryType>                 EntryContainerType;
        typedef std::vector<EntryType>::iterator       EntryItrType;
        typedef std::vector<EntryType>::const_iterator EntryConstItrType;
	typedef std::pair<bool,int>                    ColumnPositionType;

    public:
       ResultSet();
      ~ResultSet();
             
       // *****************************************************************
       //! Configure the Resultset to hold the required number of data
       //! columns for the expected SQL query data set. This method helps
       //! to size the ResultSet before its use. This method cannot be used
       //! to alter the column size after data has been added to the ResultSet.
       //!
       //! throws SqliteException on error
       //! 
       //! @param iColumns the number of columns the data set should hold.
       // *****************************************************************
       void setColumns(const ColumnType iColumns);
       
       // *****************************************************************
       //! Add a name to the specified indexed column. Will throw an exception
       //! if the index is out of range.
       //!
       //! throws SqliteException on error
       //! 
       //! @param iIndex       the column index.
       //! @param iColumnName  the column name to applied to the index.
       // *****************************************************************
       void addColumnName (const ColumnType iIndex, const EntryType& iColumnName);

       // *****************************************************************
       //! Add a row to the end of the current list of row data. Throw an
       //! exception if the number of elements in the row does not match
       //! the column count.
       //!
       //! throws SqliteException on error
       //! 
       //! @param iRow  the row to add.
       // *****************************************************************
       void appendRow (const EntryContainerType& iRow);
       
       // *****************************************************************
       //! @return The number of rows.
       // *****************************************************************
       const RowType getRows() const { return matrix_.size(); }

       // *****************************************************************
       //! @return The number of columns.
       // *****************************************************************
       const ColumnType getColumns() const { return columns_; }

       // *****************************************************************
       //! @return The first row index.
       // *****************************************************************
       const RowType  firstRow() const { return 0; }

       // *****************************************************************
       //! It is an error to call this method when the resultset is empty
       //! as the firstRow and lastRow methods will both return the same 
       //! result.
       //! @return The last row index. 
       // *****************************************************************
       const ColumnType lastRow () const { return matrix_.size() == 0 ? 0 : matrix_.size()-1; }
                    
       // *****************************************************************
       //! @return The list of column names defined for this result set.
       // *****************************************************************
       const EntryContainerType& getColumnNames() const { return columnNames_; }
      
       // *****************************************************************
       //! @param  the index of the required row.
       //! @return The row at the required index.
       // *****************************************************************
       const EntryContainerType& getRow  (const RowType iRowIndex) const;
              
       // *****************************************************************
       //! Empty the ResultSet object
       // *****************************************************************
       void clearAll();

       // *****************************************************************
       //! Remove all the rows from the ResultSet object, but keep the size
       //! of columns the same along with any associated column name data.
       // *****************************************************************
       void clearRows();
       
       // *****************************************************************
       //! Produce a seperated tabular display of the contents, including the
       //! column names if any.
       // *****************************************************************
       void display() const;

       // *****************************************************************
       //! The objects must be an exact match, including the column names.
       // *****************************************************************
       bool operator == (const ResultSet& iRhs) const;

       bool operator != (const ResultSet& iRhs) const;
	        
    private:
	void  convertToLowerCase(const std::string& iSource, std::string& iDestination) const;
	void  checkRowIndex     (const RowType    iIndex)   const;
	void  checkColumnIndex  (const ColumnType iIndex)   const;
	void  checkColumnSize   (const ColumnType iColumns) const;
	

    private:
       RowType    rows_;
       ColumnType columns_;
       
       EntryContainerType columnNames_;
       std::vector<EntryContainerType> matrix_;
};

} // end namespace DB_LAYER

#endif
