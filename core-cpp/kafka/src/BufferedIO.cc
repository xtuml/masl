#include "kafka/BufferedIO.hh"

#include <byteswap.h>

namespace Kafka {

union doublecast {
  double dval;
  uint64_t ival;
};

template <class T> void BufferedOutputStream::writeRaw(const T &bytes) {
  unsigned const char *src = reinterpret_cast<unsigned const char *>(&bytes);
  copy(src, src + sizeof(T), back_inserter(buffer));
}

template <>
void BufferedOutputStream::write<std::string>(const std::string &val) {
  write(static_cast<int>(val.size()));
  writeRange(val.begin(), val.end());
}

template <>
void BufferedOutputStream::write<SWA::String>(const SWA::String &val) {
  write(static_cast<int>(val.size()));
  writeRange(val.begin(), val.end());
}

void BufferedOutputStream::write(const SWA::ObjectPtr<void> &val) {
  bool valid = val;
  write(valid);
}

void BufferedOutputStream::write(const char *val) { write(std::string(val)); }

template <>
void BufferedOutputStream::write<SWA::Timestamp>(const SWA::Timestamp &val) {
  write(val.nanosSinceEpoch());
}

template <>
void BufferedOutputStream::write<SWA::Duration>(const SWA::Duration &val) {
  write(val.nanos());
}

// Provide templated versions for fundamental types, just
// in case someone writes "write<double>(x)" or similar
template <> void BufferedOutputStream::write<uint64_t>(const uint64_t &val) {
  write(val);
}

template <> void BufferedOutputStream::write<int64_t>(const int64_t &val) {
  write(val);
}

template <> void BufferedOutputStream::write<uint32_t>(const uint32_t &val) {
  write(val);
}

template <> void BufferedOutputStream::write<int32_t>(const int32_t &val) {
  write(val);
}

template <> void BufferedOutputStream::write<double>(const double &val) {
  write(val);
}

template <> void BufferedOutputStream::write<char>(const char &val) {
  write(val);
}

template <>
void BufferedOutputStream::write<unsigned char>(const unsigned char &val) {
  write(val);
}

template <>
void BufferedOutputStream::write<signed char>(const signed char &val) {
  write(val);
}

template <> void BufferedOutputStream::write<bool>(const bool &val) {
  write(val);
}

void BufferedOutputStream::write(uint64_t val) { writeRaw(bswap_64(val)); }
void BufferedOutputStream::write(int64_t val) { writeRaw(bswap_64(val)); }
void BufferedOutputStream::write(uint32_t val) { writeRaw(bswap_32(val)); }
void BufferedOutputStream::write(int32_t val) { writeRaw(bswap_32(val)); }
void BufferedOutputStream::write(double val) {
  doublecast caster;
  caster.dval = val;
  writeRaw(bswap_64(caster.ival));
}

void BufferedOutputStream::write(char val) { writeRaw(val); }
void BufferedOutputStream::write(unsigned char val) { writeRaw(val); }
void BufferedOutputStream::write(signed char val) { writeRaw(val); }
void BufferedOutputStream::write(bool val) { writeRaw(val); }

template <class T> void BufferedInputStream::readRaw(T &bytes) {
  unsigned char *dest = reinterpret_cast<unsigned char *>(&bytes);
  int length = sizeof(T);
  while (length-- && iter != end) {
    *dest++ = *iter++;
  }
}

void BufferedInputStream::read(uint64_t &val) {
  readRaw(val);
  val = bswap_64(val);
}

void BufferedInputStream::read(int64_t &val) {
  readRaw(val);
  val = bswap_64(val);
}

void BufferedInputStream::read(uint32_t &val) {
  readRaw(val);
  val = bswap_32(val);
}

void BufferedInputStream::read(int32_t &val) {
  readRaw(val);
  val = bswap_32(val);
}

void BufferedInputStream::read(double &val) {
  doublecast caster;
  readRaw(caster.ival);
  caster.ival = bswap_64(caster.ival);
  val = caster.dval;
}

void BufferedInputStream::read(char &val) { readRaw(val); }
void BufferedInputStream::read(unsigned char &val) { readRaw(val); }
void BufferedInputStream::read(signed char &val) { readRaw(val); }
void BufferedInputStream::read(bool &val) { readRaw(val); }

template <> void BufferedInputStream::read<std::string>(std::string &val) {
  int size;
  read(size);
  val.clear();
  val.reserve(size);
  read_container(back_inserter(val), size);
}

template <> void BufferedInputStream::read<SWA::String>(SWA::String &val) {
  std::string tmp;
  read(tmp);
  val = tmp;
}

template <>
void BufferedInputStream::read<SWA::Timestamp>(SWA::Timestamp &val) {
  int64_t tmp;
  read(tmp);
  val = SWA::Timestamp::fromNanosSinceEpoch(tmp);
}

template <> void BufferedInputStream::read<SWA::Duration>(SWA::Duration &val) {
  int64_t tmp;
  read(tmp);
  val = SWA::Duration::fromNanos(tmp);
}

} // namespace Kafka
