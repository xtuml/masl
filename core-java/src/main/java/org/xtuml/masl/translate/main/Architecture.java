//
// File: Architecture.java
//
// UK Crown Copyright (c) 2006. All Rights Reserved.
//
package org.xtuml.masl.translate.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xtuml.masl.cppgen.BinaryExpression;
import org.xtuml.masl.cppgen.BinaryOperator;
import org.xtuml.masl.cppgen.Class;
import org.xtuml.masl.cppgen.CodeFile;
import org.xtuml.masl.cppgen.DeclarationGroup;
import org.xtuml.masl.cppgen.Expression;
import org.xtuml.masl.cppgen.Function;
import org.xtuml.masl.cppgen.FundamentalType;
import org.xtuml.masl.cppgen.Library;
import org.xtuml.masl.cppgen.Literal;
import org.xtuml.masl.cppgen.Namespace;
import org.xtuml.masl.cppgen.Statement;
import org.xtuml.masl.cppgen.Type;
import org.xtuml.masl.cppgen.TypeUsage;
import org.xtuml.masl.cppgen.TypedefType;
import org.xtuml.masl.cppgen.Variable;
import org.xtuml.masl.cppgen.Visibility;
import org.xtuml.masl.translate.build.BuildSet;



public final class Architecture
{

  public final static Namespace NAMESPACE = new Namespace("SWA");

  public static BuildSet buildSet = new BuildSet("MaslCore");

  public final static Library  library           = new Library("swa").inBuildSet(buildSet);

  public final static Map<String, String> eventRegistrationFunctions = new HashMap<>();

  static
  {
    eventRegistrationFunctions.put("startup",           "registerStartupListener");
    eventRegistrationFunctions.put("initialising",      "registerInitialisingListener");
    eventRegistrationFunctions.put("initialised",       "registerInitialisedListener");
    eventRegistrationFunctions.put("started",           "registerStartedListener");
    eventRegistrationFunctions.put("preschedules",      "registerPreSchedulesListener");
    eventRegistrationFunctions.put("preschedule",       "registerPreScheduleListener");
    eventRegistrationFunctions.put("postschedule",      "registerPostScheduleListener");
    eventRegistrationFunctions.put("nextschedulephase", "registerNextSchedulePhaseListener");
    eventRegistrationFunctions.put("postschedules",     "registerPostSchedulesListener");
    eventRegistrationFunctions.put("threadstarted",     "registerThreadStartedListener");
    eventRegistrationFunctions.put("threadcompleting",  "registerThreadCompletingListener");
    eventRegistrationFunctions.put("threadcompleted",   "registerThreadCompletedListener");
    eventRegistrationFunctions.put("threadaborted",     "registerThreadAbortedListener");
    eventRegistrationFunctions.put("shutdown",          "registerShutdownListener");
  }

  public static class DynamicSingleton
  {

    static CodeFile        include = library.createInterfaceHeader("swa/DynamicSingleton.hh");

    private final Class    clazz;
    private final Function getSingleton;

    public DynamicSingleton ( final Type templateParam )
    {
      clazz = new Class("DynamicSingleton<" + templateParam.getName() + ">", NAMESPACE, include);
      getSingleton = clazz.createStaticFunction(clazz.createDeclarationGroup(), "getSingleton", Visibility.PUBLIC);
      getSingleton.setReturnType(new TypeUsage(templateParam, TypeUsage.Reference));
    }

    public Class getClazz ()
    {
      return clazz;
    }

    public Function getGetSingleton ()
    {
      return getSingleton;
    }

  }

  public final static CodeFile objectPtrInc = library.createInterfaceHeader("swa/ObjectPtr.hh");

  public final static Class objectPtr ( final TypeUsage pointee )
  {
    final Class ret = new Class("ObjectPtr", NAMESPACE, objectPtrInc);
    ret.addTemplateSpecialisation(pointee.getTemplateRefOnly());
    return ret;
  }

  public final static Expression nullPointer = new Variable("Null", NAMESPACE, objectPtrInc).asExpression();

  public final static CodeFile   toOneInc    = library.createInterfaceHeader("swa/ToOneRelationship.hh");

  public final static Class toOneRelationship ( final TypeUsage related )
  {
    final Class ret = new Class("ToOneRelationship", NAMESPACE, toOneInc);
    ret.addTemplateSpecialisation(related);
    return ret;
  }

  public final static CodeFile toManyInc = library.createInterfaceHeader("swa/ToManyRelationship.hh");

  public final static Class toManyRelationship ( final TypeUsage related )
  {
    final Class ret = new Class("ToManyRelationship", NAMESPACE, toManyInc);
    ret.addTemplateSpecialisation(related);
    return ret;
  }

  public final static CodeFile toManyAssocInc = library.createInterfaceHeader("swa/ToManyAssociative.hh");

  public final static Class toManyAssociative ( final TypeUsage related, final TypeUsage assoc )
  {
    final Class ret = new Class("ToManyAssociative", NAMESPACE, toManyAssocInc);
    ret.addTemplateSpecialisation(related);
    ret.addTemplateSpecialisation(assoc);
    return ret;
  }

  public final static CodeFile toOneAssocInc = library.createInterfaceHeader("swa/ToOneAssociative.hh");

  public final static Class toOneAssociative ( final TypeUsage related, final TypeUsage assoc )
  {
    final Class ret = new Class("ToOneAssociative", NAMESPACE, toOneAssocInc);
    ret.addTemplateSpecialisation(related);
    ret.addTemplateSpecialisation(assoc);
    return ret;
  }

  public final static CodeFile dictionaryInc = library.createInterfaceHeader("swa/Dictionary.hh");

  public final static Class dictionary ( final TypeUsage key, final TypeUsage value )
  {
    final Class ret = new Class("Dictionary", NAMESPACE, dictionaryInc);
    if ( key != null )
    {
      ret.addTemplateSpecialisation(key);
    }
    if ( value != null )
    {
      ret.addTemplateSpecialisation(value);
    }
    return ret;
  }

  public final static CodeFile sequenceInc = library.createInterfaceHeader("swa/Sequence.hh");

  public final static Class sequence ( final TypeUsage of )
  {
    final Class ret = new Class("Sequence", NAMESPACE, sequenceInc);
    if ( of != null )
    {
      ret.addTemplateSpecialisation(of);
    }
    return ret;
  }

  public final static Class range ( final TypeUsage of )
  {
    final Class ret = new Class("Range", NAMESPACE, sequenceInc);
    if ( of != null )
    {
      ret.addTemplateSpecialisation(of);
    }
    return ret;
  }

  public final static CodeFile bagInc = library.createInterfaceHeader("swa/Bag.hh");

  public final static Class bag ( final TypeUsage of )
  {
    final Class ret = new Class("Bag", NAMESPACE, bagInc);
    if ( of != null )
    {
      ret.addTemplateSpecialisation(of);
    }
    return ret;
  }

  public final static CodeFile setInc = library.createInterfaceHeader("swa/Set.hh");

  public final static Class set ( final TypeUsage of )
  {
    final Class ret = new Class("Set", NAMESPACE, setInc);
    ret.addTemplateSpecialisation(of);
    return ret;
  }

  public final static CodeFile stringInc            = library.createInterfaceHeader("swa/String.hh");
  public final static Class    stringClass          = new Class("String", NAMESPACE, stringInc);


  public final static CodeFile exceptionInc         = library.createInterfaceHeader("swa/Exception.hh");
  public final static Class    topException         = new Class("Exception", NAMESPACE, exceptionInc);

  public final static CodeFile programErrorInc      = library.createInterfaceHeader("swa/ProgramError.hh");
  public final static Class    programError         = new Class("ProgramError", NAMESPACE, programErrorInc);

  public final static CodeFile storageErrorInc      = library.createInterfaceHeader("swa/StorageError.hh");
  public final static Class    storageError         = new Class("StorageError", NAMESPACE, storageErrorInc);

  public final static CodeFile constraintErrorInc   = library.createInterfaceHeader("swa/ConstraintError.hh");
  public final static Class    constraintError      = new Class("ConstraintError", NAMESPACE, constraintErrorInc);

  public final static CodeFile relationshipErrorInc = library.createInterfaceHeader("swa/RelationshipError.hh");
  public final static Class    relationshipError    = new Class("RelationshipError", NAMESPACE, relationshipErrorInc);

  public final static CodeFile refAccessErrorInc    = library.createInterfaceHeader("swa/ReferentialAccessError.hh");
  public final static Class    refAccessError       = new Class("ReferentialAccessError", NAMESPACE, refAccessErrorInc);

  public final static CodeFile iopErrorInc          = library.createInterfaceHeader("swa/IOPError.hh");
  public final static Class    iopError             = new Class("IOPError", NAMESPACE, iopErrorInc);

  public final static CodeFile ioErrorInc           = library.createInterfaceHeader("swa/IOError.hh");
  public final static Class    ioError              = new Class("IOError", NAMESPACE, ioErrorInc);

  public final static CodeFile maslExceptionInc     = library.createInterfaceHeader("swa/MaslException.hh");
  public final static Class    maslException        = new Class("MaslException", NAMESPACE, maslExceptionInc);

  public static class Timestamp
  {

    public final static CodeFile timestampInc   = library.createInterfaceHeader("swa/Timestamp.hh");
    public final static Class    timestampClass = new Class("Timestamp", Architecture.NAMESPACE, timestampInc);

    public final static Expression createFromNanosSinceEpoch ( final Expression nanos )
    {
      return timestampClass.callStaticFunction("fromNanosSinceEpoch", nanos);
    }

    public final static Expression getTicks ( final Expression lhs )
    {
      return new Function("getTicks").asFunctionCall(lhs, false);
    }

    public final static Expression now = timestampClass.callStaticFunction("now");

    public final static Expression toCalendarYear ( final Expression timestamp )
    {
      return new Function("calendarYear").asFunctionCall(timestamp, false);
    }

    public final static Expression toMonthOfYear ( final Expression timestamp )
    {
      return new Function("monthOfYear").asFunctionCall(timestamp, false);
    }

    public final static Expression toDayOfMonth ( final Expression timestamp )
    {
      return new Function("dayOfMonth").asFunctionCall(timestamp, false);
    }

    public final static Expression toDayOfYear ( final Expression timestamp )
    {
      return new Function("dayOfYear").asFunctionCall(timestamp, false);
    }

    public final static Expression toWeekYear ( final Expression timestamp )
    {
      return new Function("weekYear").asFunctionCall(timestamp, false);
    }

    public final static Expression toWeekOfYear ( final Expression timestamp )
    {
      return new Function("weekOfYear").asFunctionCall(timestamp, false);
    }

    public final static Expression toDayOfWeek ( final Expression timestamp )
    {
      return new Function("dayOfWeek").asFunctionCall(timestamp, false);
    }

    public final static Expression toHourOfDay ( final Expression timestamp )
    {
      return new Function("hourOfDay").asFunctionCall(timestamp, false);
    }

    public final static Expression toMinuteOfHour ( final Expression timestamp )
    {
      return new Function("minuteOfHour").asFunctionCall(timestamp, false);
    }

    public final static Expression toSecondOfMinute ( final Expression timestamp )
    {
      return new Function("secondOfMinute").asFunctionCall(timestamp, false);
    }

    public final static Expression toMilliOfSecond ( final Expression timestamp )
    {
      return new Function("milliOfSecond").asFunctionCall(timestamp, false);
    }

    public final static Expression toMicroOfMilli ( final Expression timestamp )
    {
      return new Function("microOfMilli").asFunctionCall(timestamp, false);
    }

    public final static Expression toNanoOfMicro ( final Expression timestamp )
    {
      return new Function("nanoOfMicro").asFunctionCall(timestamp, false);
    }

    public final static Expression toMicroOfSecond ( final Expression duration )
    {
      return new Function("microOfSecond").asFunctionCall(duration, false);
    }

    public final static Expression toNanoOfMilli ( final Expression timestamp )
    {
      return new Function("nanoOfMilli").asFunctionCall(timestamp, false);
    }

    public final static Expression toNanoOfSecond ( final Expression timestamp )
    {
      return new Function("nanoOfSecond").asFunctionCall(timestamp, false);
    }

    public final static Expression getTime ( final Expression timestamp )
    {
      return new Function("getTime").asFunctionCall(timestamp, false);
    }

    public final static Expression getDate ( final Expression timestamp )
    {
      return new Function("getDate").asFunctionCall(timestamp, false);
    }

    public final static Expression addYears ( final Expression timestamp, final Expression years )
    {
      return new Function("addYears").asFunctionCall(timestamp, false, years);
    }

    public final static Expression addMonths ( final Expression timestamp, final Expression months )
    {
      return new Function("addMonths").asFunctionCall(timestamp, false, months);
    }


    public final static Expression splitCalendarYear = timestampClass.referenceStaticMember("CalendarYear");
    public final static Expression splitMonthOfYear  = timestampClass.referenceStaticMember("MonthOfYear");
    public final static Expression splitDayOfMonth   = timestampClass.referenceStaticMember("DayOfMonth");
    public final static Expression splitWeekYear     = timestampClass.referenceStaticMember("WeekYear");
    public final static Expression splitWeekOfYear   = timestampClass.referenceStaticMember("WeekOfYear");
    public final static Expression splitDayOfWeek    = timestampClass.referenceStaticMember("DayOfWeek");
    public final static Expression splitDayOfYear    = timestampClass.referenceStaticMember("DayOfYear");
    public final static Expression splitHourOfDay    = timestampClass.referenceStaticMember("HourOfDay");
    public final static Expression splitMinOfHour    = timestampClass.referenceStaticMember("MinuteOfHour");
    public final static Expression splitSecOfMin     = timestampClass.referenceStaticMember("SecondOfMinute");
    public final static Expression splitMilliOfSec   = timestampClass.referenceStaticMember("MilliOfSecond");
    public final static Expression splitMicroOfSec   = timestampClass.referenceStaticMember("MicroOfSecond");
    public final static Expression splitNanoOfSec    = timestampClass.referenceStaticMember("NanoOfSecond");
    public final static Expression splitMicroOfMilli = timestampClass.referenceStaticMember("MicroOfMilli");
    public final static Expression splitNanoOfMilli  = timestampClass.referenceStaticMember("NanoOfMilli");
    public final static Expression splitNanoOfMicro  = timestampClass.referenceStaticMember("NanoOfMicro");

    private static Expression combineFields ( final List<Expression> fields )
    {
      Expression result = null;

      for ( final Expression field : fields )
      {
        if ( result != null )
        {
          result = new BinaryExpression(result, BinaryOperator.BITOR, field);
        }
        else
        {
          result = field;
        }
      }

      if ( fields.size() > 1 )
      {
        final Type resultType = timestampClass.referenceNestedType("TimestampFields");
        return resultType.callConstructor(result);
      }
      else
      {
        return result;
      }
    }

    public final static Expression getCombine ( final List<Expression> fields, final List<Expression> values )
    {
      final List<Expression> params = new ArrayList<Expression>();
      params.add(combineFields(fields));
      params.addAll(values);
      return timestampClass.callConstructor(params);
    }

    public final static Expression getSplit ( final Expression lhs, final List<Expression> fields )
    {
      final List<Expression> params = new ArrayList<Expression>();
      params.add(combineFields(fields));
      return new Function("getSplit").asFunctionCall(lhs, false, params);
    }
  }

  public static class Duration
  {

    public final static CodeFile   durationInc   = library.createInterfaceHeader("swa/Duration.hh");
    public final static Class      durationClass = new Class("Duration", Architecture.NAMESPACE, durationInc);

    public final static Expression zero          = durationClass.callStaticFunction("zero");


    public final static Expression getTicks ( final Expression lhs )
    {
      return new Function("getTicks").asFunctionCall(lhs, false);
    }

    public final static Expression getCombine ( final List<Expression> fields, final List<Expression> values )
    {
      final List<Expression> params = new ArrayList<Expression>();
      params.add(combineFields(fields));
      params.addAll(values);
      return durationClass.callConstructor(params);
    }

    public final static Expression getSplit ( final Expression lhs, final List<Expression> fields )
    {
      final List<Expression> params = new ArrayList<Expression>();
      params.add(combineFields(fields));
      return new Function("getSplit").asFunctionCall(lhs, false, params);
    }


    private static Expression combineFields ( final List<Expression> fields )
    {
      Expression result = null;

      for ( final Expression field : fields )
      {
        if ( result != null )
        {
          result = new BinaryExpression(result, BinaryOperator.BITOR, field);
        }
        else
        {
          result = field;
        }
      }

      if ( fields.size() > 1 )
      {
        final Type resultType = durationClass.referenceNestedType("DurationFields");
        return resultType.callConstructor(result);
      }
      else
      {
        return result;
      }
    }

    public final static Expression toWeeks ( final Expression duration )
    {
      return new Function("weeks").asFunctionCall(duration, false);
    }

    public final static Expression toDays ( final Expression duration )
    {
      return new Function("days").asFunctionCall(duration, false);
    }

    public final static Expression toHours ( final Expression duration )
    {
      return new Function("hours").asFunctionCall(duration, false);
    }

    public final static Expression toMinutes ( final Expression duration )
    {
      return new Function("minutes").asFunctionCall(duration, false);
    }

    public final static Expression toSeconds ( final Expression duration )
    {
      return new Function("seconds").asFunctionCall(duration, false);
    }

    public final static Expression toMillis ( final Expression duration )
    {
      return new Function("millis").asFunctionCall(duration, false);
    }

    public final static Expression toMicros ( final Expression duration )
    {
      return new Function("micros").asFunctionCall(duration, false);
    }

    public final static Expression toNanos ( final Expression duration )
    {
      return new Function("nanos").asFunctionCall(duration, false);
    }


    public final static Expression toDayOfWeek ( final Expression duration )
    {
      return new Function("dayOfWeek").asFunctionCall(duration, false);
    }

    public final static Expression toHourOfDay ( final Expression duration )
    {
      return new Function("hourOfDay").asFunctionCall(duration, false);
    }

    public final static Expression toMinuteOfHour ( final Expression duration )
    {
      return new Function("minuteOfHour").asFunctionCall(duration, false);
    }

    public final static Expression toSecondOfMinute ( final Expression duration )
    {
      return new Function("secondOfMinute").asFunctionCall(duration, false);
    }

    public final static Expression toMilliOfSecond ( final Expression duration )
    {
      return new Function("milliOfSecond").asFunctionCall(duration, false);
    }

    public final static Expression toMicroOfMilli ( final Expression duration )
    {
      return new Function("microOfMilli").asFunctionCall(duration, false);
    }

    public final static Expression toNanoOfMicro ( final Expression duration )
    {
      return new Function("nanoOfMicro").asFunctionCall(duration, false);
    }

    public final static Expression toMicroOfSecond ( final Expression duration )
    {
      return new Function("microOfSecond").asFunctionCall(duration, false);
    }

    public final static Expression toNanoOfMilli ( final Expression duration )
    {
      return new Function("nanoOfMilli").asFunctionCall(duration, false);
    }

    public final static Expression toNanoOfSecond ( final Expression duration )
    {
      return new Function("nanoOfSecond").asFunctionCall(duration, false);
    }


    public final static Expression fromWeeks ( final Expression value )
    {
      return durationClass.callStaticFunction("fromWeeks", value);
    }

    public final static Expression fromDays ( final Expression value )
    {
      return durationClass.callStaticFunction("fromDays", value);
    }

    public final static Expression fromHours ( final Expression value )
    {
      return durationClass.callStaticFunction("fromHours", value);
    }

    public final static Expression fromMinutes ( final Expression value )
    {
      return durationClass.callStaticFunction("fromMinutes", value);
    }

    public final static Expression fromSeconds ( final Expression value )
    {
      return durationClass.callStaticFunction("fromSeconds", value);
    }

    public final static Expression fromMillis ( final Expression value )
    {
      return durationClass.callStaticFunction("fromMillis", value);
    }

    public final static Expression fromMicros ( final Expression value )
    {
      return durationClass.callStaticFunction("fromMicros", value);
    }

    public final static Expression fromNanos ( final Expression value )
    {
      return durationClass.callStaticFunction("fromNanos", value);
    }

    public final static Expression splitWeeks   = durationClass.referenceStaticMember("Weeks");
    public final static Expression splitDays    = durationClass.referenceStaticMember("Days");
    public final static Expression splitHours   = durationClass.referenceStaticMember("Hours");
    public final static Expression splitMinutes = durationClass.referenceStaticMember("Minutes");
    public final static Expression splitSeconds = durationClass.referenceStaticMember("Seconds");
    public final static Expression splitMillis  = durationClass.referenceStaticMember("Millis");
    public final static Expression splitMicros  = durationClass.referenceStaticMember("Micros");
    public final static Expression splitNanos   = durationClass.referenceStaticMember("Nanos");

  }

  public final static Function   delay             = new Function("delay", NAMESPACE, Duration.durationInc);

  public final static CodeFile   processInc        = library.createInterfaceHeader("swa/Process.hh");
  public final static Class      processClass      = new Class("Process", NAMESPACE, processInc);
  public final static Expression process           = processClass.callStaticFunction("getInstance");
  public final static Expression eventQueue        = new Function("getEventQueue").asFunctionCall(process, false);
  public final static Expression activityMonitor   = new Function("getActivityMonitor").asFunctionCall(process, false);
  public final static Expression mainQueue         = processClass.referenceStaticMember("MAIN_QUEUE");
  public final static Expression localQueue        = processClass.referenceStaticMember("LOCAL_QUEUE");
  public final static Function   runService        = processClass.createMemberFunction(processClass.createDeclarationGroup(),
                                                                                       "runService",
                                                                                       Visibility.PUBLIC);

  public final static CodeFile   serviceInteceptor = library.createInterfaceHeader("swa/ServiceInterceptor.hh");

  public static class Timer
  {

    public final static CodeFile    timerHeader = library.createInterfaceHeader("swa/EventTimers.hh");
    public final static Class       timerClass  = new Class("EventTimers", new Namespace("SWA"), timerHeader);
    public final static Class       timerHandle = timerClass.referenceNestedType("TimerIdType");

    private final static Expression instance    = timerClass.callStaticFunction("getInstance");

    public final static Expression  createTimer = new Function("createTimer").asFunctionCall(timerClass
                                                                                                       .callStaticFunction("getInstance"),
                                                                                             false);

    public final static Expression getTimer ( final Expression timerHandle )
    {
      return new Function("getTimer").asFunctionCall(instance, false, timerHandle);
    }

    public final static Expression isScheduled ( final Expression timerHandle )
    {
      return new Function("isScheduled").asFunctionCall(instance, false, timerHandle);
    }

    public final static Expression isExpired ( final Expression timerHandle )
    {
      return new Function("isExpired").asFunctionCall(instance, false, timerHandle);
    }

    public final static Expression getPeriod ( final Expression timerHandle )
    {
      return new Function("getPeriod").asFunctionCall(instance, false, timerHandle);
    }

    public final static Expression getExpiredAt ( final Expression timerHandle )
    {
      return new Function("getExpiredAt").asFunctionCall(instance, false, timerHandle);
    }

    public final static Expression getScheduledAt ( final Expression timerHandle )
    {
      return new Function("getScheduledAt").asFunctionCall(instance, false, timerHandle);
    }

    public final static Expression getMissed ( final Expression timerHandle )
    {
      return new Function("getMissed").asFunctionCall(instance, false, timerHandle);
    }

    public final static Expression getEvent ( final Expression timerHandle )
    {
      return new Function("getEvent").asFunctionCall(instance, false, timerHandle);
    }


    public final static Expression scheduleTimer ( final Expression timerHandle,
                                                         final Expression timeOut,
                                                         final Expression period,
                                                         final Expression timerEvent )
    {
      return new Function("scheduleTimer").asFunctionCall(instance, false, timerHandle, timeOut, period, timerEvent);
    }

    public final static Expression cancelTimer ( final Expression timerHandle )
    {
      return new Function("cancelTimer").asFunctionCall(instance, false, timerHandle);
    }

    public final static Expression deleteTimer ( final Expression timerHandle )
    {
      return new Function("deleteTimer").asFunctionCall(instance, false, timerHandle);
    }
  }

  /**
   * When a domain based service declaration is encountered during the parsing
   * of a MASL model file, the domain based service needs to be associated with
   * an interceptor type so that invocations of the service can be bound at
   * runtime rather than compile time. Therefore return the associated
   * interceptor class for the specified domain based service.
   * 
   */
  public final static Class createServiceInterceptor ( final Class serviceTag, final Function function )
  {
    final Class interceptorClass = new Class("ServiceInterceptor", NAMESPACE, serviceInteceptor);

    interceptorClass.addTemplateSpecialisation(new TypeUsage(serviceTag));
    interceptorClass.addTemplateSpecialisation(new TypeUsage(function.asFunctionPointerType()));

    return interceptorClass;
  }

  private final static CodeFile functionOverrider = library.createInterfaceHeader("swa/FunctionOverrider.hh");

  public final static Class createFunctionOverrider ( final TypeUsage functionType )
  {
    final Class overrider = new Class("FunctionOverrider", NAMESPACE, functionOverrider);
    overrider.addTemplateSpecialisation(functionType);
    return overrider;
  }

  public final static Expression registerProcessListener ( final String event, final Expression function )
  {
    if (!eventRegistrationFunctions.containsKey(event))
    {
      throw new IllegalArgumentException("Unrecognised process lifecycle event: " + event);
    }
    return new Function(eventRegistrationFunctions.get(event)).asFunctionCall(process, false, function);
  }

  public final static Class listenerConnection = Boost.signalConnection;

  public final static Expression getDomain ( final Expression domainName )
  {
    return new Function("getDomain").asFunctionCall(process, false, domainName);
  }

  public final static Expression registerSignalHandler ( final Expression signal, final Expression callback )
  {
    return new Function("addSignalCallback").asFunctionCall(activityMonitor, false, signal, callback);
  }

  public final static Expression registerStartupService ( final Expression callback )
  {
    return registerProcessListener("initialised", callback);
  }

  public final static Expression getDomainId ( final Expression domainName )
  {
    return new Function("getId").asFunctionCall(getDomain(domainName), false);
  }


  private final static CodeFile rangeIteratorInc = library.createInterfaceHeader("swa/RangeIterator.hh");

  public final static Class rangeIterator ( final TypeUsage of )
  {
    final Class ret = new Class("RangeIterator", NAMESPACE, rangeIteratorInc);
    ret.addTemplateSpecialisation(of);
    return ret;
  }

  public final static CodeFile parseInc = library.createInterfaceHeader("swa/parse.hh");

  public final static Expression parse ( final TypeUsage to, final Expression from )
  {
    final Function fn = new Function("parse", NAMESPACE, parseInc);
    fn.addTemplateSpecialisation(to);
    return fn.asFunctionCall(from);
  }

  public final static Expression parseBased ( final TypeUsage to, final Expression from, final Expression base )
  {
    final Function fn = new Function("parseBased", NAMESPACE, parseInc);
    fn.addTemplateSpecialisation(to);
    return fn.asFunctionCall(from, base);
  }

  public final static CodeFile   domainInc          = library.createInterfaceHeader("swa/Domain.hh");
  public final static Class      domainClass        = new Class("Domain", NAMESPACE, domainInc);

  public final static CodeFile   deviceInc          = library.createInterfaceHeader("swa/Device.hh");
  public final static Class      deviceClass        = new Class("Device", NAMESPACE, deviceInc);

  public final static CodeFile   stackInc           = library.createInterfaceHeader("swa/Stack.hh");
  public final static Class      stackClass         = new Class("Stack", NAMESPACE, stackInc);
  public final static Class      stackFrameClass    = new Class("StackFrame", NAMESPACE, stackInc);
  public final static Class      parameterClass     = new Class("Parameter", NAMESPACE, stackInc);
  public final static Class      localVarClass      = new Class("LocalVar", NAMESPACE, stackInc);

  public final static Function   console            = new Function("console",
                                                                   NAMESPACE,
                                                                   library.createInterfaceHeader("swa/console.hh"));

  public final static Type       ID_TYPE            = new TypedefType("IdType",
                                                                      NAMESPACE,
                                                                      new TypeUsage(FundamentalType.ULONGLONG),
                                                                      library.createInterfaceHeader("swa/types.hh"));

  public final static CodeFile   math               = library.createInterfaceHeader("swa/math.hh");

  public final static Function   pow                = new Function("pow", NAMESPACE, math);
  public final static Function   rem                = new Function("rem", NAMESPACE, math);
  public final static Function   mod                = new Function("mod", NAMESPACE, math);

  public final static CodeFile   collectionInc      = library.createInterfaceHeader("swa/collection.hh");

  public final static Function   copy_if            = new Function("copy_if", NAMESPACE, collectionInc);
  public final static Function   FIND_ONE           = new Function("find_one", NAMESPACE, collectionInc);
  public final static Function   FIND_ONLY          = new Function("find_only", NAMESPACE, collectionInc);

  public final static Expression forceTruePredicate = new Function("forceTrue", NAMESPACE, collectionInc).asFunctionPointer();

  private static final CodeFile  processMonitorInc  = library.createInterfaceHeader("swa/ProcessMonitor.hh");
  private static final Class     processMonitor     = new Class("ProcessMonitor", NAMESPACE, processMonitorInc);

  public final static CodeFile   mainInc            = library.createInterfaceHeader("swa/Main.hh");
  public final static Function   main               = new Function("main", NAMESPACE, mainInc);

  public static final Expression transitioningState ( final Expression domainId,
                                                      final Expression objectId,
                                                      final Expression instanceId,
                                                      final Expression oldState,
                                                      final Expression newState )
  {
    return new Function("transitioningState").asFunctionCall(processMonitor.callStaticFunction("getInstance"),
                                                             false,
                                                             domainId,
                                                             objectId,
                                                             instanceId,
                                                             oldState,
                                                             newState);
  }

  public static final Expression transitioningAssignerState ( final Expression domainId,
                                                              final Expression objectId,
                                                              final Expression oldState,
                                                              final Expression newState )
  {
    return new Function("transitioningAssignerState").asFunctionCall(processMonitor.callStaticFunction("getInstance"),
                                                                     false,
                                                                     domainId,
                                                                     objectId,
                                                                     oldState,
                                                                     newState);
  }

  private static CodeFile nameFormatterInc = library.createInterfaceHeader("swa/NameFormatter.hh");
  private static Class    nameFormatter    = new Class("NameFormatter", NAMESPACE, nameFormatterInc);

  public static Expression formatEventName ( final Expression domainId, final Expression objectId, final Expression eventId )
  {
    return nameFormatter.callStaticFunction("formatEventName", domainId, objectId, eventId);
  }

  public static Expression formatStateActionName ( final Expression domainId, final Expression objectId, final Expression stateId )
  {
    return nameFormatter.callStaticFunction("formatStateName", domainId, objectId, stateId);
  }

  public static Expression formatDomainServiceName ( final Expression domainId, final Expression serviceId )
  {
    return nameFormatter.callStaticFunction("formatDomainServiceName", domainId, serviceId);
  }

  public static Expression formatObjectName ( final Expression domainId, final Expression objectId )
  {
    return nameFormatter.callStaticFunction("formatObjectName", domainId, objectId);
  }

  public static final CodeFile navigateInc = library.createInterfaceHeader("swa/navigate.hh");

  public static final Function navigateOne ( final TypeUsage destObj )
  {
    final Function fn = new Function("navigate_one", NAMESPACE, navigateInc);
    fn.addTemplateSpecialisation(destObj);
    return fn;
  }

  public static final Function navigateMany ( final TypeUsage destObj )
  {
    final Function fn = new Function("navigate_many", NAMESPACE, navigateInc);
    fn.addTemplateSpecialisation(destObj);
    return fn;
  }

  public static final Function navigateOneBag ( final TypeUsage destObj )
  {
    final Function fn = new Function("navigate_one_bag", NAMESPACE, navigateInc);
    fn.addTemplateSpecialisation(destObj);
    return fn;
  }

  public static final Function navigateManyBag ( final TypeUsage destObj )
  {
    final Function fn = new Function("navigate_many_bag", NAMESPACE, navigateInc);
    fn.addTemplateSpecialisation(destObj);
    return fn;
  }

  public static final CodeFile correlateInc      = library.createInterfaceHeader("swa/correlate.hh");
  public static final Function correlateInstance = new Function("correlate_instance", NAMESPACE, correlateInc);
  public static final Function correlateSet      = new Function("correlate_set", NAMESPACE, correlateInc);
  public static final Function correlateBag      = new Function("correlate_bag", NAMESPACE, correlateInc);

  public static final CodeFile linkInc           = library.createInterfaceHeader("swa/link.hh");
  public static final Function link              = new Function("link", NAMESPACE, linkInc);
  public static final Function unlink            = new Function("unlink", NAMESPACE, linkInc);


  public static final CodeFile eventQueueInc     = library.createInterfaceHeader("swa/EventQueue.hh");

  public static final CodeFile eventInc          = library.createInterfaceHeader("swa/Event.hh");
  public static final Event    event             = new Event();

  public static class Event
  {

    private final Class    clazz;
    private final Function getDomainId;
    private final Function getObjectId;
    private final Function getEventId;
    private final Function getDestInstanceId;
    private final Function invoke;
    private final Function addParam;
    private final Function setSource;
    private final Function setDest;
    private final Class    eventPtr;

    private Event ()
    {
      clazz = new Class("Event", NAMESPACE, eventInc);
      final DeclarationGroup group = clazz.createDeclarationGroup();

      getDomainId = clazz.createMemberFunction(group, "getDomainId", Visibility.PUBLIC);
      getDomainId.setReturnType(new TypeUsage(FundamentalType.INT));
      getDomainId.setVirtual(true);
      getDomainId.setConst(true);

      getObjectId = clazz.createMemberFunction(group, "getObjectId", Visibility.PUBLIC);
      getObjectId.setReturnType(new TypeUsage(FundamentalType.INT));
      getObjectId.setVirtual(true);
      getObjectId.setConst(true);

      getEventId = clazz.createMemberFunction(group, "getEventId", Visibility.PUBLIC);
      getEventId.setReturnType(new TypeUsage(FundamentalType.INT));
      getEventId.setVirtual(true);
      getEventId.setConst(true);

      getDestInstanceId = clazz.createMemberFunction(group, "getDestInstanceId", Visibility.PUBLIC);
      getDestInstanceId.setReturnType(new TypeUsage(ID_TYPE));
      getObjectId.setConst(true);

      invoke = clazz.createMemberFunction(group, "invoke", Visibility.PUBLIC);
      invoke.setVirtual(true);
      invoke.setConst(true);

      addParam = clazz.createMemberFunction(group, "addParam", Visibility.PUBLIC);
      setSource = clazz.createMemberFunction(group, "setSource", Visibility.PUBLIC);
      setDest = clazz.createMemberFunction(group, "setDest", Visibility.PUBLIC);
      eventPtr = Boost.getSharedPtrType(new TypeUsage(clazz));
    }

    public Function getAddParam ()
    {
      return addParam;
    }

    public Function getSetSource ()
    {
      return setSource;
    }

    public Function getSetDest ()
    {
      return setDest;
    }

    public Class getEventPtr ()
    {
      return eventPtr;
    }

    public Class getClazz ()
    {
      return clazz;
    }

    public Function getGetDomainId ()
    {
      return getDomainId;
    }

    public Function getGetEventId ()
    {
      return getEventId;
    }

    public Function getGetObjectId ()
    {
      return getObjectId;
    }

    public Function getGetDestInstanceId ()
    {
      return getDestInstanceId;
    }

    public Function getInvoke ()
    {
      return invoke;
    }

  }

  public static class Logger
  {

    private final static Library loggingLib      = new Library("logging").inBuildSet(buildSet);
    private final static Namespace LOG_NAMESPACE = new Namespace("Logging");
    private final static CodeFile  loggingInc    = loggingLib.createInterfaceHeader("logging/Logging.hh");

    private final static Function  trace         = new Function("logging/trace.hh", LOG_NAMESPACE, loggingInc);
    private final static Function  debug         = new Function("logging/debug.hh", LOG_NAMESPACE, loggingInc);
    private final static Function  information   = new Function("logging/information.hh", LOG_NAMESPACE, loggingInc);
    private final static Function  notice        = new Function("logging/notice.hh", LOG_NAMESPACE, loggingInc);
    private final static Function  warning       = new Function("logging/warning.hh", LOG_NAMESPACE, loggingInc);
    private final static Function  error         = new Function("logging/error.hh", LOG_NAMESPACE, loggingInc);
    private final static Function  critical      = new Function("logging/critical.hh", LOG_NAMESPACE, loggingInc);
    private final static Function  fatal         = new Function("logging/fatal.hh", LOG_NAMESPACE, loggingInc);

    public static Statement logTrace ( final Expression message )
    {
      return trace.asFunctionCall(message).asStatement();
    }

    public static Statement logDebug ( final Expression message )
    {
      return debug.asFunctionCall(message).asStatement();
    }

    public static Statement logInformation ( final Expression message )
    {
      return information.asFunctionCall(message).asStatement();
    }

    public static Statement logNotice ( final Expression message )
    {
      return notice.asFunctionCall(message).asStatement();
    }

    public static Statement logWarning ( final Expression message )
    {
      return warning.asFunctionCall(message).asStatement();
    }

    public static Statement logError ( final Expression message )
    {
      return error.asFunctionCall(message).asStatement();
    }

    public static Statement logCritical ( final Expression message )
    {
      return critical.asFunctionCall(message).asStatement();
    }

    public static Statement logFatal ( final Expression message )
    {
      return fatal.asFunctionCall(message).asStatement();
    }

    public static Statement logTrace ( final Expression log, final Expression message )
    {
      return trace.asFunctionCall(log, message).asStatement();
    }

    public static Statement logDebug ( final Expression log, final Expression message )
    {
      return debug.asFunctionCall(log, message).asStatement();
    }

    public static Statement logInformation ( final Expression log, final Expression message )
    {
      return information.asFunctionCall(log, message).asStatement();
    }

    public static Statement logNotice ( final Expression log, final Expression message )
    {
      return notice.asFunctionCall(log, message).asStatement();
    }

    public static Statement logWarning ( final Expression log, final Expression message )
    {
      return warning.asFunctionCall(log, message).asStatement();
    }

    public static Statement logError ( final Expression log, final Expression message )
    {
      return error.asFunctionCall(log, message).asStatement();
    }

    public static Statement logCritical ( final Expression log, final Expression message )
    {
      return critical.asFunctionCall(log, message).asStatement();
    }

    public static Statement logFatal ( final Expression log, final Expression message )
    {
      return fatal.asFunctionCall(log, message).asStatement();
    }

  }

}
