//
// UK Crown Copyright (c) 2016. All Rights Reserved.
//
#include "swa/Timestamp.hh"
#include "swa/Duration.hh"
#include "swa/String.hh"
#include "swa/ProgramError.hh"
#include "swa/math.hh"
#include <iostream>
#include <sys/time.h>
#include <sys/times.h>
#include <errno.h>
#include <unistd.h>
#include <limits>
#include <sstream>
#include "boost/io/ios_state.hpp"
#include "swa/Sequence.hh"


namespace SWA
{
  timespec Timestamp::getTimespec() const
  {
    timespec time;
    time.tv_sec  = secondsSinceEpoch();
    time.tv_nsec = nanoOfSecond();

    while ( time.tv_nsec < 0 ) { time.tv_nsec+=1000000000; --time.tv_sec; }

    return time;
  }

  timeval Timestamp::getTimeval() const
  {
    timeval time;
    time.tv_sec  = secondsSinceEpoch();
    time.tv_usec = microOfSecond();

    while ( time.tv_usec < 0 ) { time.tv_usec+=1000000; --time.tv_sec; }

    return time;
  }

  tm Timestamp::getTm() const
  {
    time_t secs = secondsSinceEpoch();
    tm splits;

    gmtime_r(&secs,&splits);

    return splits;
  }

  const Timestamp& Timestamp::min() 
  {
    const static Timestamp instance(std::numeric_limits<Tick>::min());
    return instance;
  }

  const Timestamp& Timestamp::max() 
  {
    const static Timestamp instance(std::numeric_limits<Tick>::max());
    return instance;
  }


  Timestamp Timestamp::now() 
  {
    timeval time;
    gettimeofday(&time,0);
    return Timestamp(time);
  }

  Timestamp::Timestamp ( const timespec& time ) 
    : ticks(scaledToTicks(time.tv_sec,SECONDS)+scaledToTicks(time.tv_nsec,NANOS))
  {
  }

  Timestamp::Timestamp ( const timeval& time ) 
    : ticks(scaledToTicks(time.tv_sec,SECONDS)+scaledToTicks(time.tv_usec,MICROS))
  {
  }

  // Version of mktime that forces tm to be interpreted as UTC
  time_t mktime_utc ( tm* time )
  {
    // Set TZ to UTC so that mktime interprets time as UTC
    char* tz = getenv("TZ");
    setenv("TZ","",1);
    tzset();

    // Get the time
    time_t utctime = mktime(time);

    // Reset the timezone
    if (tz) setenv("TZ",tz,1); else unsetenv("TZ");
    tzset();

    return utctime;
  }

  int64_t Timestamp::toTicks ( tm& split, int64_t nanos )
  {
    time_t utctime = mktime_utc(&split);  

    if ( utctime == -1 )
    {
      std::ostringstream err;
      err << "Unable to convert time " << split.tm_year + 1900 << "-" << split.tm_mon+1 << "-" << split.tm_mday << "T" << split.tm_hour << ":" << split.tm_min << ":" << split.tm_sec << "..." << nanos << "ns\n";

      throw ProgramError(err.str());
    }

    return scaledToTicks(utctime,SECONDS) + scaledToTicks(nanos,NANOS);
  }




  Timestamp::Timestamp ( tm time, long nanos )
    : ticks(toTicks(time,nanos))
  {
  }

  Timestamp::Timestamp() 
    : ticks(0)
  {
  }

  Timestamp& Timestamp::operator-= ( const Duration& rhs )
  {
    int64_t rhsTicks = scaledToTicks(rhs.nanos(),NANOS);

    if ( rhsTicks < 0 && std::numeric_limits<Tick>::max()+rhsTicks < ticks ) throw ProgramError("Timestamp Overflow");
    if ( rhsTicks > 0 && std::numeric_limits<Tick>::min()+rhsTicks > ticks ) throw ProgramError("Timestamp Underflow");
    ticks-= rhsTicks;
    return *this;
  }


  Duration Timestamp::operator- ( const Timestamp& rhs ) const
  {
    return Duration::fromNanos(nanosSinceEpoch()-rhs.nanosSinceEpoch());
  }

  Timestamp& Timestamp::operator+= ( const Duration& rhs )
  {
    int64_t rhsTicks = scaledToTicks(rhs.nanos(),NANOS);

    if ( rhsTicks > 0 && std::numeric_limits<Tick>::max()-rhsTicks < ticks ) throw ProgramError("Timestamp Overflow");
    if ( rhsTicks < 0 && std::numeric_limits<Tick>::min()-rhsTicks > ticks ) throw ProgramError("Timestamp Underflow");

    ticks+= rhsTicks;
    return *this;
  }

  bool Timestamp::operator< ( const Timestamp& rhs ) const
  {
    return ticks < rhs.ticks;
  }

  bool Timestamp::operator== ( const Timestamp& rhs ) const
  {
    return ticks == rhs.ticks;
  }

  bool Timestamp::isDecimalPoint ( char ch )
  {
    return ch == '.' || ch == ',';
  }

  bool Timestamp::isTZChar ( char ch )
  {
    return ch == 'Z' || ch == '+' || ch == '-';
  }

  namespace // split utils
  {
    inline bool isLeap(int year)
    {
     return ( year % 4 == 0 && year % 100 != 0 )|| year % 400 == 0;
    }

    enum DayOfWeek { Monday=1, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday };

    // returns the day of jan 1st in specified year. No idea 
    // how this works, nicked fomr the internet! 
    DayOfWeek getJan1stDay ( int year )
    {
      int yy = (year-1)%100;
      int c=(year-1)-yy;
      int g = yy + yy/4;
      return DayOfWeek(1 + (((((c /100)%4)*5)+g)%7));
    }

    std::pair<int64_t,int64_t> getWeekYearAndWeek ( const tm& split )
    {
      // ISO 8601 week starts on Monday, first week of year is the one with the first Thursday
      int year = split.tm_year + 1900;
      int doy = split.tm_yday+1;
      int weekDay=split.tm_wday?split.tm_wday:7;

      int jan1WeekDay = (weekDay - doy%7 + 8)%7;

      int weekYear = year;
      int weekNo = 0;
      // check for prev cal year...
      if ( doy <= (8-jan1WeekDay) && jan1WeekDay > 4 )
      {
        // In final week of previous calendar year
        --weekYear;
        // 53 weeks in years starting with thurs or leap year starting with weds, 
        // so this year will start with fri or sat if py is leap.
        weekNo = ( jan1WeekDay == 5 || (jan1WeekDay==6 && isLeap(year-1) ) ) ? 53 : 52;
      }
      else if ( doy - weekDay + 4 > (isLeap(year)?366:365) )
      {
        // In first week of next week-year
        ++weekYear;
        weekNo = 1;
      }
      else
      {
        // Just a normal week in ISO-land
        weekNo = (doy+(7-weekDay)+(jan1WeekDay-1))/7;
        if ( jan1WeekDay > 4 ) --weekNo;
      }

      return std::pair<int64_t,int64_t>(weekYear,weekNo);
    }

  }



  template<class Iterator>
  int64_t Timestamp::readFixedSizeInt ( Iterator& it, const Iterator& end, size_t length )
  {
    std::string number;
    number.reserve(length);
    while ( it != end && std::isdigit(*it) && number.length() < length )
    {
      number.push_back(*it++);
    }
    if ( number.length() < length ) throw format_error();
    else return strtoll(number.c_str(),0,10);
  }

  template<class Iterator>
  void Timestamp::skipCharacter ( Iterator& it, const Iterator& end, char toSkip )
  {
    if ( it != end && *it == toSkip )
    {
      ++it;
    }
    else
    {
      throw format_error();
    }    
  }

  template<class Iterator>
  bool Timestamp::readDatePart ( Iterator& it, const Iterator& end, Timestamp::TimestampFields& fields, std::vector<int64_t>& values, bool& useSeparators )
  {
    static const int monthDays[]     = { 31,28,31,30,31,30,31,31,30,31,30,31 };
    static const int leapMonthDays[] = { 31,29,31,30,31,30,31,31,30,31,30,31 };
    static const char dateSep = '-';

    bool rangeValid = true;

    // Read Date Part

    int year = readFixedSizeInt(it,end,4);
    values.push_back(year);
    fields |= CalendarYear;

    if ( it == end ) return rangeValid;

    useSeparators = *it == dateSep;

    if ( useSeparators )
    {
      skipCharacter(it,end,dateSep);
      if ( it == end || ! ( isdigit(*it) || *it == 'W' ) ) throw format_error();
    }
 
    if ( it == end ) throw format_error();
    else if ( *it == 'W' )
    {
      // yyyy-Www format
      fields &= ~CalendarYear;
      fields |= WeekYear;

      ++it;
      int week = readFixedSizeInt(it,end,2);
      values.push_back(week);
      fields |= WeekOfYear;

      int jan1stDay = getJan1stDay(year);
      rangeValid = rangeValid && isInRange(week,1,(jan1stDay==Thursday||(isLeap(year)&&jan1stDay==Wednesday))?53:52);

      if ( useSeparators )
      {
        if ( it != end && *it == dateSep )
        {
          // yyyy-Www-d format
          skipCharacter(it,end,dateSep);
          int day = readFixedSizeInt(it,end,1);
          values.push_back(day);
          fields |= DayOfWeek;
          rangeValid = rangeValid && isInRange(day,1,7);
        }
      }
      else if ( it != end && std::isdigit(*it) )
      {
        // yyyyWwwd format
        int day = readFixedSizeInt(it,end,1);
        values.push_back(day);
        fields |= DayOfWeek;
        rangeValid = rangeValid && isInRange(day,1,7);
      }
    }
    else if ( std::isdigit(*it) ) 
    {
      int twodigits = readFixedSizeInt(it,end,2);
      if ( useSeparators )
      {
        if ( it != end && std::isdigit(*it) )
        {
          // yyyy-ddd format
          int yearDay = twodigits * 10 + readFixedSizeInt(it,end,1);
          values.push_back(yearDay);
          fields |= DayOfYear;
          rangeValid = rangeValid && isInRange(yearDay,1,isLeap(year)?366:365);
        }
        else
        {
          // yyyy-mm format
          int month = twodigits;
          values.push_back(month);
          fields |= MonthOfYear;
          rangeValid = rangeValid && isInRange(month,1,12);

          if ( it != end && *it == dateSep )
          {
            // yyyy-mm-dd format
            skipCharacter(it,end,dateSep);
            int day = readFixedSizeInt(it,end,2);
            values.push_back(day);
             fields |= DayOfMonth;
             rangeValid = rangeValid && isInRange(day,1,isLeap(year)?leapMonthDays[month-1]:monthDays[month-1]);
          }
        }
      }
      else
      {
        // No yyyymm format is allowed by standard (avoids 
        // confusion with yymmdd), so must be a third digit 
        int thirddigit = readFixedSizeInt(it,end,1);
        if ( it != end && std::isdigit(*it) )
        {
          // yyyymmdd format
          int month = twodigits;
          values.push_back(month);
          fields |= MonthOfYear;
          rangeValid = rangeValid && isInRange(month,1,12);

          int fourthdigit =  readFixedSizeInt(it,end,1);
          int day = thirddigit * 10 + fourthdigit;
          values.push_back(day);
          fields |= DayOfMonth;
          rangeValid = rangeValid && isInRange(day,1,isLeap(year)?leapMonthDays[month-1]:monthDays[month-1]);
        }
        else
        {       
          // yyyyddd format
          int yearDay = twodigits * 10 + thirddigit;
          values.push_back(yearDay);
          fields |= DayOfYear;
          rangeValid = rangeValid && isInRange(yearDay,1,isLeap(year)?366:365);
        }
      } 
    }
    return rangeValid;
  }

  template<class Iterator>
  Timestamp::Tick Timestamp::readTicksFromDecimal ( Iterator& it, const Iterator& end, size_t length, Units scale )
  {
    std::string decimal;
    decimal.reserve(length+20);
    while ( it != end && std::isdigit(*it) && decimal.length() < length )
    {
      decimal.push_back(*it++);
    }
    if ( decimal.length() < length ) throw format_error();

    if ( it != end && isDecimalPoint(*it) )
    {
      decimal.push_back('.');
      ++it;
      if ( it == end || !std::isdigit(*it) ) throw format_error();

      while ( it != end && std::isdigit(*it) )
      {
        decimal.push_back(*it++);
      }
    }

    return static_cast<int64_t>(roundl(strtold(decimal.c_str(),0)*scale));
  }


  template<class Iterator>
  bool Timestamp::readTimePart ( Iterator& it, const Iterator& end, Timestamp::TimestampFields& fields, std::vector<int64_t>& values, bool useSeparators )
  {
    const char timeSep = ':';

    int hours = 0;
    int minutes = 0;
    int seconds = 0;
    int nanos = 0;

    hours = readFixedSizeInt(it,end,2);

    if ( it == end ) throw format_error();

    if ( !isTZChar(*it) )
    { 
      if ( isDecimalPoint(*it) )
      {
        Tick ticksInHour = readTicksFromDecimal(it,end,0,HOURS);
        minutes = ticksInHour % HOURS / MINUTES ;
        seconds = ticksInHour % MINUTES / SECONDS;
        nanos   = ticksInHour % SECONDS;
      }
      else if ( !isTZChar(*it) )
      {
        if ( useSeparators ) skipCharacter(it,end,timeSep);
        minutes = readFixedSizeInt(it,end,2);

        if ( it == end ) throw format_error();

        if ( isDecimalPoint(*it) )
        {
          Tick ticksInMin = readTicksFromDecimal(it,end,0,MINUTES);
          seconds = ticksInMin % MINUTES / SECONDS;
          nanos   = ticksInMin % SECONDS;
        }
        else if ( !isTZChar(*it) )
        {
          if ( useSeparators ) skipCharacter(it,end,timeSep);
          Tick ticksInSecs = readTicksFromDecimal(it,end,2,SECONDS);
          seconds = ticksInSecs / SECONDS;
          nanos   = ticksInSecs % SECONDS;
        }
      }

    }

    if ( it == end ) throw format_error();

    int tzSign = 0;
    int tzHours = 0;
    int tzMinutes = 0;

    if ( *it == '+' || *it == '-' )
    {
      tzSign = (*it++ == '+')?1:-1;
      tzHours = readFixedSizeInt(it,end,2);

      if ( it != end && ( (useSeparators && *it == timeSep) || (!useSeparators && std::isdigit(*it)) ) )
      {
        if ( useSeparators ) skipCharacter(it,end,timeSep);
        tzMinutes = readFixedSizeInt(it,end,2);
      }
    }
    else if ( *it == 'Z' )
    {
      skipCharacter(it,end,'Z');
    }
    else 
    {
      throw format_error();
    }

    // Check ranges before adjusting for timezone
    bool inRange = isInRange(hours,0,(minutes||seconds||nanos)?23:24)
                && isInRange(minutes,0,59)
                && isInRange(seconds,0,60) // allow leap seconds, although unix time will just overflow to next second.
                && isInRange(nanos,0,999999999)
                && isInRange(tzHours,0,99) // can't find anything in standard to say max range for tzHours
                && isInRange(tzMinutes,0,59);

    hours   -= tzHours * tzSign;
    minutes -= tzMinutes * tzSign;

    values.push_back(hours);
    values.push_back(minutes);
    values.push_back(seconds);
    values.push_back(nanos);

    fields |= HourOfDay;
    fields |= MinuteOfHour;
    fields |= SecondOfMinute;
    fields |= NanoOfSecond;

    return inRange;
  }

  template<class Iterator>
  Timestamp Timestamp::readDateTime ( Iterator& it, const Iterator& end )
  {
    Timestamp::TimestampFields fields = Timestamp::TimestampFields();
    std::vector<int64_t> values;

    const char partSep = 'T';
    bool useSeparators = false;

    bool inRange = readDatePart(it,end,fields,values,useSeparators);

    // Read time if present and date was fully specified
    if ( (fields & (DayOfYear|DayOfWeek|DayOfMonth)) &&  it != end && *it == partSep )
    {
      skipCharacter(it,end,partSep);
      inRange = inRange && readTimePart(it,end,fields,values,useSeparators);
    }

    if ( !inRange ) throw range_error();

    return Timestamp(fields,values);
  }

  std::istream& operator>> ( std::istream& stream, Timestamp& time ) 
  { 
    std::istream::sentry se(stream);

    if ( se )
    {
      std::istreambuf_iterator<char> it(stream);
      std::istreambuf_iterator<char> end;

      try
      {
        time = Timestamp::readDateTime ( it,end );
      }
      catch ( const Timestamp::format_error& e )
      {
        stream.setstate(std::ios::failbit);
      }
      catch ( const Timestamp::range_error& e )
      {
        stream.setstate(std::ios::failbit);
      }
    }
    return stream;
  }

  Timestamp Timestamp::parse ( const std::string& text )
  {
    try
    {
      std::string::const_iterator it(text.begin());
      std::string::const_iterator end(text.end());
      Timestamp result = readDateTime(it,end);
      if ( it != end )
      {
        throw ProgramError("Invalid timestamp format - trailing characters");
      }
      return result;
    }
    catch ( const format_error& e )
    {
      throw ProgramError("Invalid timestamp format");
    }
    catch ( const range_error& e )
    {
      throw ProgramError("Timestamp out of range");
    }
  }

  std::ostream& operator<< ( std::ostream& stream, const Timestamp& time ) 
  { 
    return stream << time.format_iso_ymdhms ( Timestamp::Second, stream.precision(), stream.flags() & std::ios::showpoint );
  }


  String Timestamp::format_iso_ymdhms ( FormatField smallestField,
                                        int32_t     decimalPlaces,
                                        bool        truncateDecimal,
                                        bool        compactFormat ) const
  {
    Sequence<int64_t> split = getSplit(CalendarYear|MonthOfYear|DayOfMonth);

    std::ostringstream result;
    result << std::setfill('0');
    result << std::setw(4) << split[0];
    if ( smallestField >= Month ) result << (compactFormat?"":"-") << std::setw(2) << split[1];
    if ( smallestField >= Day )   result << (compactFormat?"":"-") << std::setw(2) << split[2];
    if ( smallestField >= Hour )  result << "T" << format_time(smallestField, decimalPlaces, truncateDecimal, compactFormat ) << "Z";
    return result.str();
  }

  String Timestamp::format_iso_ydhms ( FormatField smallestField,
                                       int32_t     decimalPlaces,
                                       bool        truncateDecimal,
                                       bool        compactFormat ) const
  {
    Sequence<int64_t> split = getSplit(CalendarYear|DayOfYear);

    std::ostringstream result;
    result << std::setfill('0');
    result << std::setw(4) << split[0];
    if ( smallestField >= Day )  result << (compactFormat?"":"-") << std::setw(3) << split[1];
    if ( smallestField >= Hour ) result << "T" << format_time(smallestField, decimalPlaces, truncateDecimal, compactFormat ) << "Z";
    return result.str();
  }

  String Timestamp::format_iso_ywdhms ( FormatField smallestField,
                                        int32_t     decimalPlaces,
                                        bool        truncateDecimal,
                                        bool        compactFormat ) const
  {
    Sequence<int64_t> split = getSplit(CalendarYear|WeekOfYear|DayOfWeek);

    std::ostringstream result;
    result << std::setfill('0');
    result << std::setw(4) << split[0];
    if ( smallestField >= Month ) result << (compactFormat?"W":"-W") << std::setw(2) << split[1];
    if ( smallestField >= Day )   result << (compactFormat?"":"-") << std::setw(1) << split[2];
    if ( smallestField >= Hour )  result << "T" << format_time(smallestField, decimalPlaces, truncateDecimal,compactFormat ) << "Z";
    return result.str();
  }

  String Timestamp::format_dmy () const
  {
    Sequence<int64_t> split = getSplit(::SWA::Timestamp::CalendarYear|::SWA::Timestamp::MonthOfYear|::SWA::Timestamp::DayOfMonth);

    std::ostringstream result;
    result << std::setfill('0');
    result << std::setw(2) << split[2] << "/" << std::setw(2) << split[1] <<  "/" << std::setw(4) << split[0];
    return result.str();
  }

  String Timestamp::format_mdy () const
  {
    Sequence<int64_t> split = getSplit(CalendarYear|MonthOfYear|DayOfMonth);

    std::ostringstream result;
    result << std::setfill('0');
    result << std::setw(2) << split[1] << "/" << std::setw(2) << split[2] <<  "/" << std::setw(4) << split[0];
    return result.str();
  }

  String Timestamp::format_dtg () const
  {
    // Military DTG format : DDHHMMZ MMM YY

    Sequence<int64_t> split = getSplit(CalendarYear|MonthOfYear|DayOfMonth|HourOfDay|MinuteOfHour);
    int year = split[0];
    int month = split[1];
    int day = split[2];
    int hour = split[3];
    int minute = split[4];
      
    std::string monthStr = "N/K";

    switch ( month )
    {
      case 1:  monthStr = "JAN"; break;
      case 2:  monthStr = "FEB"; break;
      case 3:  monthStr = "MAR"; break;
      case 4:  monthStr = "APR"; break;
      case 5:  monthStr = "MAY"; break;
      case 6:  monthStr = "JUN"; break;
      case 7:  monthStr = "JUL"; break;
      case 8:  monthStr = "AUG"; break;
      case 9:  monthStr = "SEP"; break;
      case 10: monthStr = "OCT"; break;
      case 11: monthStr = "NOV"; break;
      case 12: monthStr = "DEC"; break;
    }

    std::ostringstream result;
    result << std::setfill('0');
    result << std::setw(2) << day << std::setw(2) << hour << std::setw(2) << minute << "Z " << monthStr << " " << std::setw(2) << year%100;
    return result.str();
  }

  String Timestamp::format_time ( FormatField smallestField,
                                  int32_t     decimalPlaces,
                                  bool        truncateDecimal,
                                  bool        compactFormat ) const
  {
    Duration::FormatField minDurField;
    if ( smallestField < Hour ) return "";
    else if ( smallestField == Hour )   minDurField = Duration::Hour;
    else if ( smallestField == Minute ) minDurField = Duration::Minute;
    else                                minDurField = Duration::Second;
  
    return getTime().format_hms ( minDurField, decimalPlaces, truncateDecimal, compactFormat );
    
  }



  int64_t Timestamp::weekYear() const
  {
    return getWeekYearAndWeek(getTm()).first;
  }

  int64_t Timestamp::weekOfYear() const
  {
    return getWeekYearAndWeek(getTm()).second;
  }

  Sequence<int64_t> Timestamp::getSplit( TimestampFields fields ) const
  {
    Sequence<int64_t> result;

    if ( fields & (CalendarYear|MonthOfYear|DayOfMonth|DayOfYear|WeekYear|WeekOfYear|DayOfWeek) )
    {
      tm split = getTm();

      // YMD fields
      if ( fields & CalendarYear ) result.push_back(split.tm_year + 1900);
      if ( fields & MonthOfYear )  result.push_back(split.tm_mon + 1);
      if ( fields & DayOfMonth )   result.push_back(split.tm_mday);

      if ( fields & DayOfYear ) result.push_back(split.tm_yday+1);

      // YWD fields 
      if ( fields & (WeekYear | WeekOfYear ) )
      {
        std::pair<int64_t,int64_t> weekYearandWeek = getWeekYearAndWeek(split);

        if ( fields & WeekYear )    result.push_back(weekYearandWeek.first);
        if ( fields & WeekOfYear )  result.push_back(weekYearandWeek.second);
      }

      if ( fields & DayOfWeek )      result.push_back((split.tm_wday+6)%7+1);
      if ( fields & HourOfDay )      result.push_back(split.tm_hour); 
      if ( fields & MinuteOfHour )   result.push_back(split.tm_min); 
      if ( fields & SecondOfMinute ) result.push_back(split.tm_sec); 
    }
    else
    {
      if ( fields & HourOfDay )      result.push_back(hourOfDay()); 
      if ( fields & MinuteOfHour )   result.push_back(minuteOfHour()); 
      if ( fields & SecondOfMinute ) result.push_back(secondOfMinute());
    }

    if ( fields & MilliOfSecond )   result.push_back(milliOfSecond()); 
    if ( fields & MicroOfSecond )   result.push_back(microOfSecond()); 
    if ( fields & NanoOfSecond )    result.push_back(nanoOfSecond()); 

    if ( fields & MicroOfMilli ) result.push_back(microOfMilli()); 
    if ( fields & NanoOfMilli )  result.push_back(nanoOfMilli()); 

    if ( fields & NanoOfMicro )  result.push_back(nanoOfMicro()); 

    return result;
  }

  bool Timestamp::isInRange ( int64_t value, int64_t min, int64_t max )
  {
    return value <= max && value >= min;
  }

  template<class Iterator>
  int64_t Timestamp::ticksFromFields ( TimestampFields fields, Iterator value )
  {
    tm split = {0};

    if ( fields & Timestamp::CalendarYear )
    {
      split.tm_year = *value++ - 1900;
      if ( fields & MonthOfYear  )
      {
        split.tm_mon  = *value++ - 1;
        split.tm_mday = (fields & DayOfMonth) ? *value++ : 1;
      }
      else if ( fields & DayOfYear )
      {
        split.tm_mon = 0;
        split.tm_mday = *value++;
      }
      else
      {
        split.tm_mon = 0;
        split.tm_mday = 1;
      }
    }
    else if ( fields & WeekYear )
    { 
      int weekYear = *value++;
      int weekOfYear =  (fields & WeekOfYear) ? *value++ : 1;
      int dayOfWeek =  (fields & DayOfWeek) ? *value++ : 1;
      
      int jan1stDay = getJan1stDay(weekYear);

      int jan1stWeek = jan1stDay > Thursday ? 0 : 1;

      int dayOfYear = (weekOfYear-jan1stWeek)*7 + dayOfWeek - jan1stDay + 1;

      // use overflowing of tm_mday with tm_mon = jan to set day of year.
      split.tm_year = weekYear - 1900;
      split.tm_mon = 0;
      split.tm_mday = dayOfYear;
    }


    if ( fields & HourOfDay      )   { split.tm_hour = *value++;   }
    if ( fields & MinuteOfHour   )   { split.tm_min  = *value++;   }
    if ( fields & SecondOfMinute )   { split.tm_sec  = *value++;   }

    int64_t nanos = 0;
    if ( fields & MilliOfSecond  )   { nanos+= *value++ * 1000000; }
    if ( fields & MicroOfSecond  )   { nanos+= *value++ * 1000;    }
    if ( fields & NanoOfSecond   )   { nanos+= *value++;           }
    if ( fields & MicroOfMilli   )   { nanos+= *value++ * 1000;    }
    if ( fields & NanoOfMilli    )   { nanos+= *value++;           }
    if ( fields & NanoOfMicro    )   { nanos+= *value++;           }

    return toTicks(split,nanos);
  }

  Timestamp::Timestamp ( TimestampFields fields, 
                  const std::vector<int64_t>& values )
    : ticks(ticksFromFields(fields,values.begin()))
  {
  }

  Timestamp::Timestamp ( TimestampFields fields, 
                       int64_t value1, 
                       int64_t value2, 
                       int64_t value3, 
                       int64_t value4, 
                       int64_t value5, 
                       int64_t value6, 
                       int64_t value7, 
                       int64_t value8, 
                       int64_t value9, 
                       int64_t value10 )
  {
    int64_t values[] = { value1, value2, value3, value4, value5, value6, value7, value8, value9, value10 };
    ticks = ticksFromFields(fields,values);
  }



  Timestamp Timestamp:: getDate() const
  {
    return Timestamp::fromDaysSinceEpoch(daysSinceEpoch());
  }

  Duration Timestamp::getTime() const
  {
    return Duration::fromNanos(nanoOfDay());
  }

  Timestamp Timestamp::addYears( int64_t years )
  {
    tm split = getTm();
    split.tm_year += years;
    return Timestamp(split,nanoOfSecond());
  }

  Timestamp Timestamp::addMonths( int64_t months )
  {
    tm split = getTm();
    split.tm_mon += months;
    return Timestamp(split,nanoOfSecond());
  }




} //SWA
