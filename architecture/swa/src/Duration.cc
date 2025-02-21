/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include "swa/Duration.hh"
#include "swa/String.hh"
#include "swa/ProgramError.hh"
#include "swa/Sequence.hh"
#include "swa/math.hh"
#include <iostream>
#include <sys/time.h>
#include <sys/times.h>
#include <errno.h>
#include <unistd.h>
#include <limits>
#include <sstream>

namespace
{
const int64_t CLK_TKS_PER_SEC = sysconf (_SC_CLK_TCK);
}

namespace SWA
{
  timespec Duration::getTimespec() const
  {
    timespec time;
    time.tv_sec  = seconds();
    time.tv_nsec = nanoOfSecond();

    while ( time.tv_nsec < 0 ) { time.tv_nsec+=1000000000; --time.tv_sec; }

    return time;
  }

  timeval Duration::getTimeval() const
  {
    timeval time;
    time.tv_sec  = seconds();
    time.tv_usec = microOfSecond();

    while ( time.tv_usec < 0 ) { time.tv_usec+=1000000; --time.tv_sec; }

    return time;
  }

  void delay ( const Duration& time )
  {
    timespec ts = time.getTimespec();
    timespec remaining;

    while ( nanosleep(&ts,&remaining) )
    {
      if ( errno == EINTR )
      {
        ts = remaining;
      }
      else
      {
        throw ProgramError(strerror(errno));
      }
    }

  }

  const Duration& Duration::zero() 
  {
    const static Duration instance;
    return instance;
  }

  const Duration& Duration::min() 
  {
    const static Duration instance(std::numeric_limits<Tick>::min());
    return instance;
  }

  const Duration& Duration::max() 
  {
    const static Duration instance(std::numeric_limits<Tick>::max());
    return instance;
  }


  Duration Duration::real() 
  {
    timespec time;
    clock_gettime(CLOCK_REALTIME,&time);
    return Duration(time);
  }

  Duration Duration::user() 
  {
    tms time;
    times(&time);
    return Duration(time.tms_utime*SECONDS/CLK_TKS_PER_SEC);
  }

  Duration Duration::system() 
  {
    tms time;
    times(&time);
    return Duration(time.tms_stime*SECONDS/CLK_TKS_PER_SEC);
  }

  Duration Duration::clock() 
  {
    return Duration(::clock()*SECONDS/CLOCKS_PER_SEC);
  }

  Duration::Duration ( const timespec& time ) 
    : ticks(scaledToTicks(time.tv_sec,SECONDS)+scaledToTicks(time.tv_nsec,NANOS))
  {
  }

  Duration::Duration ( const timeval& time ) 
    : ticks(scaledToTicks(time.tv_sec,SECONDS)+scaledToTicks(time.tv_usec,MICROS))
  {
  }


  Duration::Duration() 
    : ticks(0)
  {
  }

  Duration& Duration::operator-= ( const Duration& rhs )
  {
    if ( rhs.ticks < 0 && std::numeric_limits<Tick>::max()+rhs.ticks < ticks ) throw ProgramError("Duration Overflow");
    if ( rhs.ticks > 0 && std::numeric_limits<Tick>::min()+rhs.ticks > ticks ) throw ProgramError("Duration Underflow");
    ticks-= rhs.ticks;
    return *this;
  }

  Duration& Duration::operator+= ( const Duration& rhs )
  {
    if ( rhs.ticks > 0 && std::numeric_limits<Tick>::max()-rhs.ticks < ticks ) throw ProgramError("Duration Overflow");
    if ( rhs.ticks < 0 && std::numeric_limits<Tick>::min()-rhs.ticks > ticks ) throw ProgramError("Duration Underflow");

    ticks+= rhs.ticks;
    return *this;
  }

  Duration& Duration::operator*= ( int rhs )
  {
    if ( std::numeric_limits<Tick>::max()/rhs < ticks ) throw ProgramError("Duration Overflow ");
    if ( std::numeric_limits<Tick>::min()/rhs > ticks ) throw ProgramError("Duration Underflow");
    ticks*= rhs;
    return *this;
  }

  Duration& Duration::operator*= ( unsigned int rhs )
  {
    if ( std::numeric_limits<Tick>::max()/rhs < ticks ) throw ProgramError("Duration Overflow ");
    if ( std::numeric_limits<Tick>::min()/rhs > ticks ) throw ProgramError("Duration Underflow");
    ticks*= rhs;
    return *this;
  }

  Duration& Duration::operator/= ( int rhs )
  {
    // No range check required... ticks will always get closer to zero
    ticks/= rhs;
    return *this;
  }

  Duration& Duration::operator/= ( unsigned int rhs )
  {
    // No range check required... ticks will always get closer to zero
    ticks/= rhs;
    return *this;
  }


  Duration& Duration::operator*= ( long rhs )
  {
    if ( std::numeric_limits<Tick>::max()/rhs < ticks ) throw ProgramError("Duration Overflow ");
    if ( std::numeric_limits<Tick>::min()/rhs > ticks ) throw ProgramError("Duration Underflow");
    ticks*= rhs;
    return *this;
  }

  Duration& Duration::operator*= ( unsigned long rhs )
  {
    if ( static_cast<Tick>(std::numeric_limits<Tick>::max()/rhs) < ticks ) throw ProgramError("Duration Overflow ");
    if ( static_cast<Tick>(std::numeric_limits<Tick>::min()/rhs) > ticks ) throw ProgramError("Duration Underflow");
    ticks*= rhs;
    return *this;
  }

  Duration& Duration::operator/= ( long rhs )
  {
    // No range check required... ticks will always get closer to zero
    ticks/= rhs;
    return *this;
  }

  Duration& Duration::operator/= ( unsigned long rhs )
  {
    // No range check required... ticks will always get closer to zero
    ticks/= rhs;
    return *this;
  }

  Duration& Duration::operator*= ( long long rhs )
  {
    if ( std::numeric_limits<Tick>::max()/rhs < ticks ) throw ProgramError("Duration Overflow ");
    if ( std::numeric_limits<Tick>::min()/rhs > ticks ) throw ProgramError("Duration Underflow");
    ticks*= rhs;
    return *this;
  }

  Duration& Duration::operator*= ( unsigned long long rhs )
  {
    if ( static_cast<Tick>(std::numeric_limits<Tick>::max()/rhs) < ticks ) throw ProgramError("Duration Overflow ");
    if ( static_cast<Tick>(std::numeric_limits<Tick>::min()/rhs) > ticks ) throw ProgramError("Duration Underflow");
    ticks*= rhs;
    return *this;
  }

  Duration& Duration::operator/= ( long long rhs )
  {
    // No range check required... ticks will always get closer to zero
    ticks/= rhs;
    return *this;
  }

  Duration& Duration::operator/= ( unsigned long long rhs )
  {
    // No range check required... ticks will always get closer to zero
    ticks/= rhs;
    return *this;
  }

  Duration& Duration::operator*= ( double rhs )
  {
    if ( std::numeric_limits<Tick>::max()/rhs < ticks ) throw ProgramError("Duration Overflow ");
    if ( std::numeric_limits<Tick>::min()/rhs > ticks ) throw ProgramError("Duration Underflow");
    ticks= static_cast<int64_t>(static_cast<long double>(ticks)*rhs);
    return *this;
  }

  Duration& Duration::operator/= ( double rhs )
  {
    if ( std::numeric_limits<Tick>::max()*rhs < ticks ) throw ProgramError("Duration Overflow ");
    if ( std::numeric_limits<Tick>::min()*rhs > ticks ) throw ProgramError("Duration Underflow");

    ticks = static_cast<int64_t>(static_cast<long double>(ticks)/rhs);
    return *this;
  }

  Duration& Duration::operator%= ( const Duration& rhs )
  {
    // No range check required... ticks will always get closer to zero
    ticks%= rhs.ticks;
    return *this;
  }


  Duration Duration::operator-() const
  {
    if ( -std::numeric_limits<Tick>::max() > ticks ) throw ProgramError("Duration Overflow");
    Duration res;
    res.ticks = -ticks;
    return res;
  }


  template<class Iterator>
  void Duration::skipCharacter ( Iterator& it, const Iterator& end, char toSkip )
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


  bool Duration::isDecimalPoint ( char ch )
  {
    return ch == '.' || ch == ',';
  }

  template<class Iterator>
  int64_t Duration::readInt ( Iterator& it, const Iterator& end )
  {
    if ( it == end || ! std::isdigit(*it) ) throw format_error();

    std::string number;
    number.reserve(10);

    while ( it != end && std::isdigit(*it) )
    {
      number.push_back(*it++);
    }
    errno = 0;
    int64_t result = strtoll(number.c_str(),0,10);

    if ( errno == ERANGE )
    {
      throw ProgramError("Duration Overflow");
    }

    return result;
  }

  template<class Iterator>
  long double Duration::readDecimal ( Iterator& it, const Iterator& end )
  {
    if ( it == end || !isDecimalPoint(*it++) ) throw format_error();
    if ( it == end || !std::isdigit(*it) ) throw format_error();

    std::string decimal("0.");

    while ( it != end && std::isdigit(*it) )
    {
      decimal.push_back(*it++);
    }
    return strtold(decimal.c_str(),0);

  }


  template<class Iterator>
  Duration Duration::readDuration ( Iterator& it, const Iterator& end )
  {
    bool negative = false;
    if ( it == end ) throw format_error();
    if ( *it == '-' ) 
    {
      ++it;
      negative = true;
    }
    else if ( *it == '+' )
    {
      ++it;
    }

    skipCharacter(it,end,'P');
    if ( it == end ) throw format_error();

    Duration result;

    enum FieldOrder { FNone, FYears, FMonths, FDays, FHours, FMinutes, FSeconds };

    bool finished = false;
    FieldOrder lastEncountered = FNone;
    bool inTimePart = false;
    bool indeterminate = false;

    while ( it != end && !finished && ( std::isdigit(*it) || (!inTimePart && *it == 'T')) )
    {
      if ( *it == 'T' )
      {
        inTimePart = true;
        ++it;
      }

      int64_t value = readInt(it,end);
      long double decimal = 0.0;

      if ( it == end ) throw format_error();
      else if ( isDecimalPoint(*it) )
      {
        decimal = readDecimal(it,end);
        finished = true;
      }

      switch ( *it++ )
      {
        case 'W':
          if ( inTimePart || lastEncountered > FNone ) throw format_error();
          result += Duration(WEEKS,value);
          if ( finished ) result += Duration(WEEKS,decimal);
          finished = true; // For some reason, weeks cannot be combined with other fields in IS08601
          break;
        case 'Y':
          if ( inTimePart || lastEncountered >= FYears ) throw format_error();
          lastEncountered = FYears;
          if ( value > 0 || decimal > 0 ) indeterminate = true;
          break;
        case 'D':
          if ( inTimePart || lastEncountered >= FDays ) throw format_error();
          lastEncountered = FDays;
          result += Duration(DAYS,value);
          if ( finished ) result += Duration(DAYS,decimal);
          break;
        case 'H':
          if ( !inTimePart || lastEncountered >= FHours ) throw format_error();
          lastEncountered = FHours;
          result += Duration(HOURS,value);
          if ( finished ) result += Duration(HOURS,decimal);
          break;
        case 'M':
          if ( inTimePart )
          {
            if ( lastEncountered >= FMinutes ) throw format_error();
            lastEncountered = FMinutes;
            result += Duration(MINUTES,value);
            if ( finished ) result += Duration(MINUTES,decimal);
          }
          else
          {
            if ( lastEncountered >= FMonths ) throw format_error();
            lastEncountered = FMonths;
            if ( value > 0 || decimal > 0 ) indeterminate = true;
          }
          break;
        case 'S':
          if ( !inTimePart || lastEncountered >= FSeconds ) throw format_error();
          lastEncountered = FSeconds;
          result += Duration(SECONDS,value);
          if ( finished ) result += Duration(SECONDS,decimal);
          finished = true; // Nothing smaller than seconds
          break;
        default:
          throw format_error();
          break;
      }
    }
    if ( indeterminate ) throw format_error();
    if ( negative ) result = -result;
    
    return result;
  }


  Duration Duration::parse( const std::string& text )
  {
    try
    {
      std::string::const_iterator it(text.begin());
      std::string::const_iterator end(text.end());

      Duration result = readDuration(it,end);
      if ( it != end )
      {
        throw ProgramError("Invalid timestamp format");
      }

      return result;
    }
    catch ( const format_error& e )
    {
      throw ProgramError("Invalid duration format");
    }

  }

  std::istream& operator>> ( std::istream& stream, Duration& time ) 
  {  
    std::istream::sentry se(stream);

    if ( se )
    {
      std::istreambuf_iterator<char> it(stream);
      std::istreambuf_iterator<char> end;

      try
      {
        time = Duration::readDuration ( it,end );
      }
      catch ( const Duration::format_error& e )
      {
        stream.setstate(std::ios::failbit);
      }
    }
    return stream;
  }

  std::ostream& operator<< ( std::ostream& stream, const Duration& time ) 
  {  
    return stream << time.format_iso(Duration::Day,Duration::Second,true,stream.precision(),!(stream.flags() & std::ios::showpoint));
  }



  String Duration::format_iso ( FormatField largestField, 
                                FormatField smallestField, 
                                bool        hideZeros, 
                                int32_t     decimalPlaces, 
                                bool        truncateDecimal ) const
  {
    static const std::string suffixes[] = { "W", "D", "H", "M", "S" };
    return format ( largestField,
                    smallestField, 
                    TowardsNearest,
                    hideZeros, 
                    decimalPlaces,
                    truncateDecimal,
                    0,
                    "P",
                    "T",
                    Sequence<String>(suffixes+largestField,suffixes+smallestField+1) ); 
  }

  String Duration::format_hms ( FormatField smallestField,
                                int         decimalPlaces,
                                bool        truncateDecimal,
                                bool        compactForm ) const
  {
    static const std::string suffixes[] = { ":", ":" };

    return format ( Hour,
                    smallestField,
                    TowardsZero,
                    false,
                    decimalPlaces,
                    truncateDecimal,
                    2,
                    "",
                    "",
                    compactForm?Sequence<String>():Sequence<String>(suffixes,suffixes+smallestField-Hour) ); 
  }

  String Duration::format     ( FormatField             largestField, 
                                FormatField             smallestField, 
                                Rounding                rounding,
                                bool                    hideZeros, 
                                int32_t                 decimalPlaces, 
                                bool                    truncateDecimal, 
                                int32_t                 fieldWidth, 
                                const String&           prefix,
                                const String&           timePrefix,
                                const Sequence<String>& suffixes ) const
  {
    static const Units divisors[] = { WEEKS, DAYS, HOURS, MINUTES, SECONDS }; 

    if ( largestField > smallestField ) throw ProgramError("Invalid field specification");

    int64_t nanosRemaining = nanos();

    std::string result;
    if ( nanosRemaining < 0 )
    {
      result += "-";
      nanosRemaining = -nanosRemaining;
    }
    result += prefix;

    // Ensure that rounding is done correctly. Need to do this 
    // first, rather than rely on the double at the end to 
    // ensure that any overflows ripple through. 
    int32_t decimalFactor = 1;
    for ( int32_t d = decimalPlaces; d > 0; --d )
    {
      decimalFactor *= 10;
    }

    int64_t truncator = divisors[smallestField] / decimalFactor;
    if ( rounding == TowardsNearest )
    {
      nanosRemaining += truncator/2;
    }
    nanosRemaining = ( nanosRemaining / truncator ) * truncator; 


    bool addTimePrefix = largestField > Hour;

    for ( int i = largestField; i < smallestField; ++i )
    {
      int64_t value = nanosRemaining / divisors[i];
      nanosRemaining %= divisors[i];

      addTimePrefix = addTimePrefix || i == Hour;

      if ( value > 0 || !hideZeros )
      {
        std::ostringstream str;
        str << std::setfill('0') << std::setw(fieldWidth) << value;
        if ( addTimePrefix )
        {
          result += timePrefix;
          addTimePrefix = false;
        }
        result += str.str();
        if ( Sequence<String>::size_type(i-largestField) < suffixes.size() )
        {
          result+= suffixes[i-largestField].s_str();
        }
      }
    }

    // smallestField left to do
    // input       places truncate hide_zero output
    // PT1M0.01S   2       yes     no        PT1M0.01S 
    // PT1M0.001S  2       yes     no        PT1M0S 
    // PT1M0.01S   2       yes     yes       PT1M0.01S 
    // PT1M0.001S  2       yes     yes       PT1M 
    // PT1M0.01S   2       no      no        PT1M0.01S 
    // PT1M0.001S  2       no      no        PT1M0.00S 
    // PT1M0.01S   2       no      yes       PT1M0.01S 
    // PT1M0.001S  2       no      yes       PT1M0.00S NB does not hide trailing zero field if truncate false

    long double value = static_cast<long double>(nanosRemaining) / static_cast<int64_t>(divisors[smallestField]);
    if ( value > 0.0 || !truncateDecimal || !hideZeros || result == prefix.s_str() )
    {
      addTimePrefix = addTimePrefix || smallestField == Hour;

      std::ostringstream str;
      str << std::setfill('0') << std::setw(fieldWidth + decimalPlaces + (decimalPlaces>0?1:0) ) << std::fixed << std::setprecision(decimalPlaces) << value;
      std::string valStr = str.str();
      if ( truncateDecimal )
      {
        if ( decimalPlaces > 0 )
        {
          std::string::size_type lastNonZero = valStr.find_last_not_of('0');
          if ( lastNonZero != std::string::npos )
          {
            valStr = valStr.substr(0,valStr.find_last_not_of('0')+1);
          }
          if ( *valStr.rbegin() == '.' ) valStr = valStr.substr(0,valStr.size()-1);
        }
        if ( hideZeros && result != prefix.s_str() )
        {
          if ( valStr.find_first_not_of('0') == std::string::npos )
          {
            valStr = "";
          }
        }          
      }
      if ( valStr.size() )
      {
        if ( addTimePrefix )
        {
          result += timePrefix;
        }
        result += valStr;
        if ( Sequence<String>::size_type(smallestField-largestField) < suffixes.size() )
        {
          result+= suffixes[smallestField-largestField].s_str();
        }
      }
    }
    return result;    
  }


  Sequence<int64_t> Duration::getSplit( DurationFields fields ) const
  {
    Sequence<int64_t> result;

    int64_t remaining = ticks;

    if ( fields & Weeks )   { result.push_back(remaining / WEEKS);   remaining %= WEEKS;   }
    if ( fields & Days )    { result.push_back(remaining / DAYS);    remaining %= DAYS;    }
    if ( fields & Hours )   { result.push_back(remaining / HOURS);   remaining %= HOURS;   }
    if ( fields & Minutes ) { result.push_back(remaining / MINUTES); remaining %= MINUTES; }
    if ( fields & Seconds ) { result.push_back(remaining / SECONDS); remaining %= SECONDS; }
    if ( fields & Millis )  { result.push_back(remaining / MILLIS);  remaining %= MILLIS;  }
    if ( fields & Micros )  { result.push_back(remaining / MICROS);  remaining %= MICROS;  }
    if ( fields & Nanos )   { result.push_back(remaining / NANOS);   remaining %= NANOS;   }

    return result;
  }

  Duration::Duration ( DurationFields fields, 
                       int64_t value1, 
                       int64_t value2, 
                       int64_t value3, 
                       int64_t value4, 
                       int64_t value5, 
                       int64_t value6, 
                       int64_t value7, 
                       int64_t value8 )
  {
    int64_t values[] = { value1, value2, value3, value4, value5, value6, value7, value8 };
    int64_t* value = values;

    Duration result;

    if ( fields & Weeks )   { result+= fromWeeks  (*value++); }
    if ( fields & Days )    { result+= fromDays   (*value++); }
    if ( fields & Hours )   { result+= fromHours  (*value++); }
    if ( fields & Minutes ) { result+= fromMinutes(*value++); }
    if ( fields & Seconds ) { result+= fromSeconds(*value++); }
    if ( fields & Millis )  { result+= fromMillis (*value++); }
    if ( fields & Micros )  { result+= fromMicros (*value++); }
    if ( fields & Nanos )   { result+= fromNanos  (*value++); }

    ticks = result.ticks;
  }




} //SWA
