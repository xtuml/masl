/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef Sqlite_BlobData_HH
#define Sqlite_BlobData_HH

#include <string>

namespace SQLITE {
class BlobData {
  public:
    typedef std::string::const_iterator const_iterator;

    BlobData() : blob() {}

    template <class It> BlobData(It begin, It end) : blob(begin, end) {}

    explicit BlobData(const std::string &str) : blob(str) {}

    void append(const char *data, size_t size) { blob.append(data, size); }

    template <class It> void assign(It begin, It end) {
        blob.assign(begin, end);
    }

    const char *const data() const { return blob.data(); }
    size_t size() const { return blob.size(); }

    const_iterator begin() const { return blob.begin(); }
    const_iterator end() const { return blob.end(); }

  private:
    std::string blob;
};

} // namespace SQLITE
#endif
