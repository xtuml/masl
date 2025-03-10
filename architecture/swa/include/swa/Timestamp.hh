/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef SWA_Time_HH
#define SWA_Time_HH

#include <ctime>
#include <iomanip>
#include <iostream>
#include <limits>
#include <stdint.h>

#include "boost/functional/hash.hpp"
#include "boost/operators.hpp"
#include <nlohmann/json.hpp>

#include "ProgramError.hh"
#include "String.hh"
#include "math.hh"
#include <format>

namespace SWA {
    class Duration;
    class String;
    template <class T>
    class Sequence;

    class Timestamp : private boost::totally_ordered<Timestamp, boost::additive<Timestamp, Duration>> {
      private:
        enum Units {
            TICKS = 1LL,
            NANOS = TICKS * 1LL,
            MICROS = NANOS * 1000LL,
            MILLIS = MICROS * 1000LL,
            SECONDS = MILLIS * 1000LL,
            MINUTES = SECONDS * 60LL,
            HOURS = MINUTES * 60LL,
            DAYS = HOURS * 24LL
        };

        template <class T>
        Timestamp(Units units, T time);

      public:
        enum TimestampFields {
            CalendarYear = 0x8000,
            MonthOfYear = 0x4000,
            DayOfMonth = 0x2000,
            WeekYear = 0x1000,
            WeekOfYear = 0x0800,
            DayOfWeek = 0x0400,
            DayOfYear = 0x0200,
            HourOfDay = 0x0100,
            MinuteOfHour = 0x0080,
            SecondOfMinute = 0x0040,
            MilliOfSecond = 0x0020,
            MicroOfSecond = 0x0010,
            NanoOfSecond = 0x0008,
            MicroOfMilli = 0x0004,
            NanoOfMilli = 0x0002,
            NanoOfMicro = 0x0001
        };
        Timestamp();
        Timestamp(const timespec &time);
        Timestamp(const timeval &time);

        Timestamp(tm time, long nanos = 0);

        Timestamp(
            TimestampFields fields,
            int64_t value1,
            int64_t value2 = 0,
            int64_t value3 = 0,
            int64_t value4 = 0,
            int64_t value5 = 0,
            int64_t value6 = 0,
            int64_t value7 = 0,
            int64_t value8 = 0,
            int64_t value9 = 0,
            int64_t value10 = 0
        );

        int64_t calendarYear() const {
            return getTm().tm_year + 1900;
        }
        int64_t monthOfYear() const {
            return getTm().tm_mon + 1;
        }
        int64_t dayOfMonth() const {
            return getTm().tm_mday;
        }
        int64_t weekYear() const;
        int64_t weekOfYear() const;
        int64_t dayOfWeek() const {
            return (getTm().tm_wday + 6) % 7 + 1;
        }
        int64_t dayOfYear() const {
            return getTm().tm_yday + 1;
        }
        int64_t hourOfDay() const {
            return mod(ticks, DAYS) / HOURS;
        }
        int64_t minuteOfHour() const {
            return mod(ticks, HOURS) / MINUTES;
        }
        int64_t secondOfMinute() const {
            return mod(ticks, MINUTES) / SECONDS;
        }
        int64_t milliOfSecond() const {
            return mod(ticks, SECONDS) / MILLIS;
        }
        int64_t microOfMilli() const {
            return mod(ticks, MILLIS) / MICROS;
        }
        int64_t nanoOfMicro() const {
            return mod(ticks, MICROS) / NANOS;
        }
        int64_t microOfSecond() const {
            return mod(ticks, SECONDS) / MICROS;
        }
        int64_t nanoOfMilli() const {
            return mod(ticks, MILLIS) / NANOS;
        }
        int64_t nanoOfSecond() const {
            return mod(ticks, SECONDS) / NANOS;
        }
        int64_t nanoOfDay() const {
            return mod(ticks, DAYS) / NANOS;
        }

        int64_t daysSinceEpoch() const {
            return ticks / DAYS;
        }
        int64_t secondsSinceEpoch() const {
            return ticks / SECONDS;
        }
        int64_t nanosSinceEpoch() const {
            return ticks / NANOS;
        }

        template <class T>
        static Timestamp fromDaysSinceEpoch(T value) {
            return Timestamp(DAYS, value);
        }

        template <class T>
        static Timestamp fromNanosSinceEpoch(T value) {
            return Timestamp(NANOS, value);
        }

        template <class T>
        static Timestamp fromSecondsSinceEpoch(T value) {
            return Timestamp(SECONDS, value);
        }

        static const Timestamp &min();
        static const Timestamp &max();

        static Timestamp now();
        static Timestamp parse(const std::string &text);

        enum FormatField { Year, Month, Week, Day, Hour, Minute, Second };

        String format_iso_ymdhms(
            FormatField smallestField = Second,
            int decimalPlaces = 3,
            bool truncateDecimal = true,
            bool compactForm = false
        ) const;

        String format_iso_ywdhms(
            FormatField smallestField = Second,
            int decimalPlaces = 3,
            bool truncateDecimal = true,
            bool compactForm = false
        ) const;

        String format_iso_ydhms(
            FormatField smallestField = Second,
            int decimalPlaces = 3,
            bool truncateDecimal = true,
            bool compactForm = false
        ) const;

        String format_dmy() const;
        String format_mdy() const;
        String format_dtg() const;
        String format_time(
            FormatField smallestField = Second,
            int decimalPlaces = 3,
            bool truncateDecimal = true,
            bool compactForm = false
        ) const;

        friend std::ostream &operator<<(std::ostream &stream, const Timestamp &time);
        friend std::istream &operator>>(std::istream &stream, Timestamp &time);

        Timestamp &operator-=(const Duration &rhs);
        Timestamp &operator+=(const Duration &rhs);
        bool operator<(const Timestamp &rhs) const;
        bool operator==(const Timestamp &rhs) const;

        Duration operator-(const Timestamp &rhs) const;

        timespec getTimespec() const;
        timeval getTimeval() const;
        tm getTm() const;

        int64_t getTicks() const {
            return ticks;
        }

        // Returns a time representing midnight at the start of the day
        Timestamp getDate() const;

        // Returns a time representing the duration since midnight
        Duration getTime() const;

        Sequence<int64_t> getSplit(TimestampFields fields) const;

        Timestamp addYears(int64_t year);
        Timestamp addMonths(int64_t months);

        friend void to_json(nlohmann::json &json, const SWA::Timestamp v) {
            json = v.format_iso_ymdhms(Timestamp::Second, 9);
        }

        friend void from_json(const nlohmann::json &json, SWA::Timestamp &v) {
            v = parse(json.get<std::string>());
        }

      private:
        typedef int64_t Tick;

        template <class Iterator>
        static int64_t ticksFromFields(TimestampFields fields, Iterator value);

        template <class T>
        static Tick scaledToTicks(T scaled, Units scale) {
            if (scaled > (std::numeric_limits<Tick>::max()) / scale)
                throw SWA::ProgramError("Timestamp Overflow");
            if (scaled < (std::numeric_limits<Tick>::min()) / scale)
                throw SWA::ProgramError("Timestamp Underflow");
            return static_cast<Tick>(scaled) * scale;
        }
        static Tick scaledToTicks(double scaled, Units scale) {
            return scaledToTicks(static_cast<long double>(scaled), scale);
        }
        static Tick scaledToTicks(float scaled, Units scale) {
            return scaledToTicks(static_cast<long double>(scaled), scale);
        }
        static Tick scaledToTicks(long double scaled, Units scale) {
            if (scaled > static_cast<long double>(std::numeric_limits<Tick>::max()) / static_cast<std::int64_t>(scale))
                throw SWA::ProgramError("Timestamp Overflow");
            if (scaled < static_cast<long double>(std::numeric_limits<Tick>::min()) / static_cast<std::int64_t>(scale))
                throw SWA::ProgramError("Timestamp Underflow");
            return static_cast<Tick>(scaled * static_cast<std::int64_t>(scale));
        }

        static int64_t toTicks(tm &split, int64_t nanos);

        // Parsing
        struct format_error {};
        struct range_error {};
        static bool isDecimalPoint(char ch);
        static bool isTZChar(char ch);

        template <class Iterator>
        static int64_t readFixedSizeInt(Iterator &it, const Iterator &end, size_t length);

        template <class Iterator>
        static void skipCharacter(Iterator &it, const Iterator &end, char toSkip);

        template <class Iterator>
        static bool readDatePart(
            Iterator &it,
            const Iterator &end,
            Timestamp::TimestampFields &fields,
            std::vector<int64_t> &values,
            bool &useSeparators
        );

        template <class Iterator>
        static Tick readTicksFromDecimal(Iterator &it, const Iterator &end, size_t length, Units scale);

        template <class Iterator>
        static bool readTimePart(
            Iterator &it,
            const Iterator &end,
            Timestamp::TimestampFields &fields,
            std::vector<int64_t> &values,
            bool useSeparators
        );

        template <class Iterator>
        static Timestamp readDateTime(Iterator &it, const Iterator &end);

        static bool isInRange(int64_t value, int64_t min, int64_t max);

        Timestamp(TimestampFields fields, const std::vector<int64_t> &values);

      private:
        explicit Timestamp(int64_t ticks)
            : ticks(ticks) {}

        Tick ticks;
    };

    std::ostream &operator<<(std::ostream &stream, const SWA::Timestamp &time);
    std::istream &operator>>(std::istream &stream, SWA::Timestamp &time);

    template <class T>
    Timestamp::Timestamp(Units scale, T time)
        : ticks(scaledToTicks(time, scale)) {}

    inline Timestamp::TimestampFields operator|(Timestamp::TimestampFields field1, Timestamp::TimestampFields field2) {
        return Timestamp::TimestampFields(static_cast<uint32_t>(field1) | static_cast<uint32_t>(field2));
    }

    inline Timestamp::TimestampFields &
    operator|=(Timestamp::TimestampFields &field1, Timestamp::TimestampFields field2) {
        return field1 = field1 | field2;
    }

    inline Timestamp::TimestampFields operator&(Timestamp::TimestampFields field1, Timestamp::TimestampFields field2) {
        return Timestamp::TimestampFields(static_cast<uint32_t>(field1) & static_cast<uint32_t>(field2));
    }

    inline Timestamp::TimestampFields &
    operator&=(Timestamp::TimestampFields &field1, Timestamp::TimestampFields field2) {
        return field1 = field1 & field2;
    }

    inline Timestamp::TimestampFields operator^(Timestamp::TimestampFields field1, Timestamp::TimestampFields field2) {
        return Timestamp::TimestampFields(static_cast<uint32_t>(field1) ^ static_cast<uint32_t>(field2));
    }

    inline Timestamp::TimestampFields &
    operator^=(Timestamp::TimestampFields &field1, Timestamp::TimestampFields field2) {
        return field1 = field1 ^ field2;
    }

    inline Timestamp::TimestampFields operator~(Timestamp::TimestampFields field) {
        return Timestamp::TimestampFields(~static_cast<uint32_t>(field));
    }

    inline std::size_t hash_value(const SWA::Timestamp &src) {
        return ::boost::hash_value(src.getTicks());
    }

} // namespace SWA
template <>
struct std::formatter<SWA::Timestamp> : std::formatter<std::string> {

    auto format(const SWA::Timestamp &time, std::format_context &ctx) const {
        return std::formatter<std::string>::format(time.format_iso_ymdhms(SWA::Timestamp::Second, 9).s_str(), ctx);
    }
};

#endif
