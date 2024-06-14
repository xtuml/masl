#ifndef Kafka_BufferedIO_HH
#define Kafka_BufferedIO_HH

#include "swa/Dictionary.hh"
#include "swa/Duration.hh"
#include "swa/ObjectPtr.hh"
#include "swa/Set.hh"
#include "swa/String.hh"
#include "swa/Timestamp.hh"

namespace Kafka {

class BufferedOutputStream {
private:
  std::vector<unsigned char> buffer;

  template <class T> void writeRaw(const T &bytes);

  template <class InputIterator>
  void writeRange(InputIterator first, InputIterator last);

public:
  template <class T> BufferedOutputStream &operator<<(T &val);

  template <class T> void write(const T &val);

  template <class T1, class T2> void write(const std::pair<T1, T2> &val);

  template <class T> void write(const std::vector<T> &val);

  template <class T> void write(const SWA::Set<T> &val);

  template <class K, class V> void write(const SWA::Dictionary<K, V> &val);

  template <class T> void write(const SWA::Bag<T> &val);

  template <class T> void write(const SWA::Sequence<T> &val);

  template <class T> void write(const SWA::ObjectPtr<T> &val);

  void write(const SWA::ObjectPtr<void> &val);

  void write(const char *val);

  // Provide non-template versions for fundamental types for efficiency
  void write(uint64_t val);
  void write(int64_t val);
  void write(uint32_t val);
  void write(int32_t val);
  void write(double val);
  void write(char val);
  void write(unsigned char val);
  void write(signed char val);
  void write(bool val);

  // Writes the collection to the file, including a size integer at the start.
  template <class InputIterator>
  void write(InputIterator first, InputIterator last);

  using const_iterator = const unsigned char *;

  std::vector<unsigned char>::const_iterator begin() const {
    return buffer.begin();
  }
  std::vector<unsigned char>::const_iterator end() const {
    return buffer.end();
  }
};

template <class T>
BufferedOutputStream &BufferedOutputStream::operator<<(T &val) {
  write(val);
  return *this;
}

template <class T1, class T2>
void BufferedOutputStream::write(const std::pair<T1, T2> &val) {
  write(val.first);
  write(val.second);
}

template <class T> void BufferedOutputStream::write(const std::vector<T> &val) {
  write(static_cast<int>(val.size()));
  writeRange(val.begin(), val.end());
}

template <class T> void BufferedOutputStream::write(const SWA::Set<T> &val) {
  write(static_cast<int>(val.first()));
  write(static_cast<int>(val.last()));
  writeRange(val.begin(), val.end());
}

template <class T> void BufferedOutputStream::write(const SWA::Bag<T> &val) {
  write(static_cast<int>(val.first()));
  write(static_cast<int>(val.last()));
  writeRange(val.begin(), val.end());
}

template <class T>
void BufferedOutputStream::write(const SWA::Sequence<T> &val) {
  write(static_cast<int>(val.first()));
  write(static_cast<int>(val.last()));
  writeRange(val.begin(), val.end());
}

template <class K, class V>
void BufferedOutputStream::write(const SWA::Dictionary<K, V> &val) {
  write(static_cast<int>(val.size()));
  writeRange(val.begin(), val.end());
}

template <class T>
void BufferedOutputStream::write(const SWA::ObjectPtr<T> &val) {
  bool valid = val;
  write(valid);
  if (valid) {
    // Do not access ptr through 'operator->'as object
    // may have been deleted in the current frame.
    write(val.getChecked()->getArchitectureId());
  }
}

template <class InputIterator>
void BufferedOutputStream::write(InputIterator first, InputIterator last) {
  write(static_cast<int>(std::distance(first, last)));
  writeRange(first, last);
}

template <class InputIterator>
void BufferedOutputStream::writeRange(InputIterator first, InputIterator last) {
  while (first != last)
    write(*first++);
}

class BufferedInputStream {
private:
  std::vector<unsigned char>::iterator iter;
  std::vector<unsigned char>::iterator end;

  // Reads raw bytes into val
  template <class T> void readRaw(T &val);

  template <class Container> void readCollection(Container &val);

  // Reads 'length' values into the output iterator
  template <class Container>
  void read_container(std::back_insert_iterator<Container> it, int length);

  // Reads 'length' values into the output iterator
  template <class Container>
  void read_container(std::insert_iterator<Container> it, int length);

public:
  BufferedInputStream(std::vector<unsigned char>::iterator iter,
                      std::vector<unsigned char>::iterator end)
      : iter(iter), end(end) {}

  template <class T> BufferedInputStream &operator>>(T &val);

  // read operations for different data types
  template <class T> void read(T &val);
  template <class T> void read(SWA::Set<T> &val);
  template <class T> void read(SWA::Bag<T> &val);
  template <class T> void read(SWA::Sequence<T> &val);
  template <class K, class V> void read(SWA::Dictionary<K, V> &val);
  template <class T1, class T2> void read(std::pair<T1, T2> &val);
  template <class T> void read(std::vector<T> &val);
  template <class T> void read(std::set<T> &val);

  // Provide non-template versions for fundamental types for efficiency
  void read(uint64_t &val);
  void read(int64_t &val);
  void read(uint32_t &val);
  void read(int32_t &val);
  void read(double &val);
  void read(char &val);
  void read(unsigned char &val);
  void read(signed char &val);
  void read(bool &val);
  void read(SWA::ObjectPtr<void> &val) {}
};

template <class T>
BufferedInputStream &BufferedInputStream::operator>>(T &val) {
  read(val);
  return *this;
}

template <class T> void BufferedInputStream::read(SWA::Sequence<T> &val) {
  int start = 0;
  int end = 0;
  read(start);
  read(end);
  val = SWA::Sequence<T>();
  read_container(std::back_inserter(val), end - start + 1);
}

template <class K, class V>
void BufferedInputStream::read(SWA::Dictionary<K, V> &dict) {
  int size = 0;
  read(size);
  dict = SWA::Dictionary<K, V>();
  K key;
  V val;
  while (size--) {
    read(key);
    read(val);
    dict.setValue(key) = val;
  }
}

template <class T> void BufferedInputStream::read(SWA::Set<T> &val) {
  int start = 0;
  int end = 0;
  read(start);
  read(end);
  val = SWA::Set<T>();
  read_container(val.inserter(), end - start + 1);
}

template <class T> void BufferedInputStream::read(SWA::Bag<T> &val) {
  int start = 0;
  int end = 0;
  read(start);
  read(end);
  val = SWA::Bag<T>();
  read_container(val.inserter(), end - start + 1);
}

template <class T1, class T2>
void BufferedInputStream::read(std::pair<T1, T2> &val) {
  read(val.first);
  read(val.second);
}

template <class T> void BufferedInputStream::read(std::vector<T> &val) {
  int size = 0;
  read(size);
  val.clear();
  val.reserve(size);
  read_container(back_inserter(val), size);
}

template <class T> void BufferedInputStream::read(std::set<T> &val) {
  int size = 0;
  read(size);
  val.clear();
  val.reserve(size);
  read_container(inserter(val), size);
}

// Reads 'length' values into the container
template <class Container>
void BufferedInputStream::read_container(
    std::back_insert_iterator<Container> it, int length) {
  typename Container::value_type val;
  while (length--) {
    read(val);
    *it++ = val;
  }
}

// Reads 'length' values into the container
template <class Container>
void BufferedInputStream::read_container(std::insert_iterator<Container> it,
                                         int length) {
  typename Container::value_type val;
  while (length--) {
    read(val);
    *it++ = val;
  }
}

} // namespace Kafka
#endif
