#
# UK Crown Copyright (c) 2016. All Rights Reserved
#
simple_add_interface_library ( 
  NAME     swa-headers     
  EXPORT   MaslCore 
  INCLUDES ALL
  )

simple_add_shared_library ( 
  NAME     swa
  SOURCES
           ActivityMonitor.cc
           CommandLine.cc
           Domain.cc
           Duration.cc
           Event.cc
           EventQueue.cc
           EventTimer.cc
           EventTimers.cc
           Exception.cc
           FileDescriptorListener.cc
           ListenerPriority.cc
           Logging.cc
           Main.cc
           NameFormatter.cc
           PluginRegistry.cc
           Process.cc
           ProcessMonitor.cc
           RealTimeSignalListener.cc
           Schedule.cc
           Stack.cc
           StackFrame.cc
           TimerListener.cc
           Timestamp.cc
           console.cc
           parse.cc
  LINKS   
           swa-headers
           logging
           rt 
           dl
  EXPORT   MaslCore 
  INCLUDES ALL
  )
