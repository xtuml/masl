/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Duration_HH
#define SWA_Duration_HH

#include <ctime>
#include <iomanip>
#include <limits>
#include <nlohmann/json.hpp>
#include <ostream>
#include <stdint.h>

#include "String.hh"
#include "boost/functional/hash.hpp"
#include "boost/operators.hpp"

#include "ProgramError.hh"
#include <format>

namespace SWA {
class String;
template <class T> class Sequence;

class Duration
    : private boost::totally_ordered<
          Duration,
          boost::additive<
              Duration, boost::multiplicative<
                            Duration, int,
                            boost::multiplicative<
                                Duration, long,
                                boost::multiplicative<
                                    Duration, long long,
                                    boost::multiplicative<
                                        Duration, unsigned int,
                                        boost::multiplicative<
                                            Duration, unsigned long,
                                            boost::multiplicative<
                                                Duration, unsigned long long,
                                                boost::multiplicative<
                                                    Duration, double>>>>>>>>> {
  private:
    enum Units {
        TICKS = 1LL,
        NANOS = TICKS * 1LL,
        MICROS = NANOS * 1000LL,
        MILLIS = MICROS * 1000LL,
        SECONDS = MILLIS * 1000LL,
        MINUTES = SECONDS * 60LL,
        HOURS = MINUTES * 60LL,
        DAYS = HOURS * 24LL,
        WEEKS = DAYS * 7LL
    };

    template <class T>
    Duration(Units units, T time) : ticks(scaledToTicks(time, units)) {}

  public:
    Duration();
    Duration(const timespec &time);
    Duration(const timeval &time);

    enum DurationFields {
        Weeks = 0x80,
        Days = 0x40,
        Hours = 0x20,
        Minutes = 0x10,
        Seconds = 0x08,
        Millis = 0x04,
        Micros = 0x02,
        Nanos = 0x01
    };

    Duration(DurationFields fields, int64_t value1, int64_t value2 = 0,
             int64_t value3 = 0, int64_t value4 = 0, int64_t value5 = 0,
             int64_t value6 = 0, int64_t value7 = 0, int64_t value8 = 0);

    template <class T> static Duration fromWeeks(T value) {
        return Duration(WEEKS, value);
    }
    template <class T> static Duration fromDays(T value) {
        return Duration(DAYS, value);
    }
    template <class T> static Duration fromHours(T value) {
        return Duration(HOURS, value);
    }
    template <class T> static Duration fromMinutes(T value) {
        return Duration(MINUTES, value);
    }
    template <class T> static Duration fromSeconds(T value) {
        return Duration(SECONDS, value);
    }
    template <class T> static Duration fromMillis(T value) {
        return Duration(MILLIS, value);
    }
    template <class T> static Duration fromMicros(T value) {
        return Duration(MICROS, value);
    }
    template <class T> static Duration fromNanos(T value) {
        return Duration(NANOS, value);
    }

    int64_t weeks() const { return ticks / WEEKS; }
    int64_t days() const { return ticks / DAYS; }
    int64_t hours() const { return ticks / HOURS; }
    int64_t minutes() const { return ticks / MINUTES; }
    int64_t seconds() const { return ticks / SECONDS; }
    int64_t millis() const { return ticks / MILLIS; }
    int64_t micros() const { return ticks / MICROS; }
    int64_t nanos() const { return ticks / NANOS; }

    int64_t dayOfWeek() const { return ticks % WEEKS / DAYS; }
    int64_t hourOfDay() const { return ticks % DAYS / HOURS; }
    int64_t minuteOfHour() const { return ticks % HOURS / MINUTES; }
    int64_t secondOfMinute() const { return ticks % MINUTES / SECONDS; }
    int64_t milliOfSecond() const { return ticks % SECONDS / MILLIS; }
    int64_t microOfMilli() const { return ticks % MILLIS / MICROS; }
    int64_t nanoOfMicro() const { return ticks % MICROS / NANOS; }
    int64_t microOfSecond() const { return ticks % SECONDS / MICROS; }
    int64_t nanoOfMilli() const { return ticks % MILLIS / NANOS; }
    int64_t nanoOfSecond() const { return ticks % SECONDS / NANOS; }

    static const Duration &zero();
    static const Duration &min();
    static const Duration &max();

    static Duration clock();
    static Duration real();
    static Duration user();
    static Duration system();

    static Duration parse(const std::string &text);

    friend std::ostream &operator<<(std::ostream &stream, const Duration &time);
    friend std::istream &operator>>(std::istream &stream, Duration &time);

    enum Rounding { TowardsZero, TowardsNearest };

    enum FormatField { Week, Day, Hour, Minute, Second };

    String format_iso(FormatField largestField = Day,
                      FormatField smallestField = Second, bool hideZeros = true,
                      int32_t decimalPlaces = 3,
                      bool truncateDecimal = true) const;

    String format_hms(FormatField smallestField = Second, int decimalPlaces = 0,
                      bool truncateDecimal = true,
                      bool compactForm = false) const;

    String format(FormatField largestField, FormatField smallestField,
                  Rounding rounding, bool hideZeros, int32_t decimalPlaces,
                  bool truncateDecimal, int32_t fieldWidth,
                  const String &prefix, const String &timePrefix,
                  const Sequence<String> &suffixes) const;

    Duration &operator-=(const Duration &rhs);
    Duration &operator+=(const Duration &rhs);
    Duration &operator*=(int rhs);
    Duration &operator/=(int rhs);
    Duration &operator*=(long rhs);
    Duration &operator/=(long rhs);
    Duration &operator*=(long long rhs);
    Duration &operator/=(long long rhs);
    Duration &operator*=(unsigned int rhs);
    Duration &operator/=(unsigned int rhs);
    Duration &operator*=(unsigned long rhs);
    Duration &operator/=(unsigned long rhs);
    Duration &operator*=(unsigned long long rhs);
    Duration &operator/=(unsigned long long rhs);
    Duration &operator*=(double rhs);
    Duration &operator/=(double rhs);
    Duration &operator%=(const Duration &rhs);

    Duration operator-() const;
    bool operator<(const Duration &rhs) const { return ticks < rhs.ticks; }
    bool operator==(const Duration &rhs) const { return ticks == rhs.ticks; };
    long double operator/(const Duration &rhs) const {
        return static_cast<long double>(ticks) / rhs.ticks;
    }

    timespec getTimespec() const;
    timeval getTimeval() const;

    int64_t getTicks() const { return ticks; }

    // Returns the duration split down into the requested
    // components. The most significant field will be first
    // in the result vector, and subsequent fields will be
    // modulo the next highest requested field.
    Sequence<int64_t> getSplit(DurationFields fields) const;

  private:
    // Parsing
    struct format_error {};
    static bool isDecimalPoint(char ch);

    template <class Iterator>
    static void skipCharacter(Iterator &it, const Iterator &end, char toSkip);

    template <class Iterator>
    static int64_t readInt(Iterator &it, const Iterator &end);

    template <class Iterator>
    static long double readDecimal(Iterator &it, const Iterator &end);

    template <class Iterator>
    static Duration readDuration(Iterator &it, const Iterator &end);

  private:
    typedef int64_t Tick;

    template <class T> static Tick scaledToTicks(T scaled, Units scale) {
        if (scaled > (std::numeric_limits<Tick>::max()) / scale)
            throw SWA::ProgramError("Duration Overflow");
        // Check < zero so comparison doesn't fail for
        // unsigned types, as arithmetic is done unsigned, and
        // the min() value will overflow to a large +ve no
        if (scaled < 0 && scaled < (std::numeric_limits<Tick>::min()) / scale)
            throw SWA::ProgramError("Duration Underflow");
        return static_cast<Tick>(scaled) * scale;
    }
    static Tick scaledToTicks(double scaled, Units scale) {
        return scaledToTicks(static_cast<long double>(scaled), scale);
    }
    static Tick scaledToTicks(float scaled, Units scale) {
        return scaledToTicks(static_cast<long double>(scaled), scale);
    }
    static Tick scaledToTicks(long double scaled, Units scale) {
        if (scaled >
            static_cast<long double>(std::numeric_limits<Tick>::max()) /
                static_cast<std::int64_t>(scale))
            throw SWA::ProgramError("Duration Overflow");
        if (scaled <
            static_cast<long double>(std::numeric_limits<Tick>::min()) /
                static_cast<std::int64_t>(scale))
            throw SWA::ProgramError("Duration Underflow");
        return static_cast<Tick>(
            roundl(scaled * static_cast<std::int64_t>(scale)));
    }
    friend void to_json(nlohmann::json &json, const SWA::Duration &v) {
        json = v.format_iso(Duration::Day, Duration::Second, true, 9);
    }

    friend void from_json(const nlohmann::json &json, SWA::Duration &v) {
        v = parse(json.get<std::string>());
    }

  private:
    explicit Duration(int64_t ticks) : ticks(ticks) {}

  private:
    Tick ticks;
};

void delay(const Duration &time);

std::ostream &operator<<(std::ostream &stream, const SWA::Duration &time);
std::istream &operator>>(std::istream &stream, SWA::Duration &time);

inline Duration::DurationFields operator|(Duration::DurationFields field1,
                                          Duration::DurationFields field2) {
    return Duration::DurationFields(static_cast<uint32_t>(field1) |
                                    static_cast<uint32_t>(field2));
}

inline Duration::DurationFields &operator|=(Duration::DurationFields &field1,
                                            Duration::DurationFields field2) {
    return field1 = field1 | field2;
}

inline Duration::DurationFields operator&(Duration::DurationFields field1,
                                          Duration::DurationFields field2) {
    return Duration::DurationFields(static_cast<uint32_t>(field1) &
                                    static_cast<uint32_t>(field2));
}

inline Duration::DurationFields &operator&=(Duration::DurationFields &field1,
                                            Duration::DurationFields field2) {
    return field1 = field1 & field2;
}

inline std::size_t hash_value(const SWA::Duration &src) {
    return ::boost::hash_value(src.getTicks());
}

} // namespace SWA

template <> struct std::formatter<SWA::Duration> : std::formatter<std::string> {

    auto format(const SWA::Duration &dur, std::format_context &ctx) const {
        return std::formatter<std::string>::format(
            dur.format_iso(SWA::Duration::Day, SWA::Duration::Second, true, 9)
                .s_str(),
            ctx);
    }
};

#endif
