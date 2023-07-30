// (C) 2022 - UK CROWN OWNED COPYRIGHT. All rights reserved.
// The copyright of this Software is vested in the Crown and the Software is the property of the Crown.
domain Test is

  object Result;

  public service print_results(); pragma external(1);

  public service pass ( file : in anonymous string, line : in anonymous integer );
  public service fail ( file : in anonymous string, line : in anonymous integer, failmessage : in anonymous string );

  public service check ( file : in anonymous string, line : in anonymous integer, passed : in anonymous boolean, failmessage : in anonymous string );

  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous integer,   rhs : in anonymous integer ); pragma filename("check_equal.integer.svc");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous string,    rhs : in anonymous string ); pragma filename("check_equal.string.svc");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous duration,  rhs : in anonymous duration ); pragma filename("check_equal.duration.svc");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous timestamp, rhs : in anonymous timestamp ); pragma filename("check_equal.timestamp.svc");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous boolean,   rhs : in anonymous boolean ); pragma filename("check_equal.boolean.svc");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous real,      rhs : in anonymous real, epsilon : in anonymous real ); pragma filename("check_equal.real.svc");

  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous sequence of anonymous boolean, rhs : in anonymous sequence of anonymous boolean ); pragma filename("check_equal.boolseq.svc");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous sequence of anonymous integer, rhs : in anonymous sequence of anonymous integer ); pragma filename("check_equal.intseq.svc");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous sequence of anonymous string, rhs : in anonymous sequence of anonymous string ); pragma filename("check_equal.strseq.svc");

  public service check_equal_set ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous set of anonymous boolean, rhs : in anonymous set of anonymous boolean ); pragma filename("check_equal.boolset.svc");
  public service check_equal_set ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous set of anonymous integer, rhs : in anonymous set of anonymous integer ); pragma filename("check_equal.intset.svc");
  public service check_equal_set ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous set of anonymous string, rhs : in anonymous set of anonymous string ); pragma filename("check_equal.strset.svc");

  public service check_equal_bag ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous bag of anonymous boolean, rhs : in anonymous bag of anonymous boolean ); pragma filename("check_equal.boolbag.svc");
  public service check_equal_bag ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous bag of anonymous integer, rhs : in anonymous bag of anonymous integer ); pragma filename("check_equal.intbag.svc");
  public service check_equal_bag ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous bag of anonymous string, rhs : in anonymous bag of anonymous string ); pragma filename("check_equal.strbag.svc");

  public service check_null ( file : in anonymous string, line : in anonymous integer, obj : in instance );
  public service check_not_null ( file : in anonymous string, line : in anonymous integer, obj : in instance );

  public service check_size ( file : in anonymous string, line : in anonymous integer, seq : in anonymous sequence of anonymous integer, size : in anonymous integer ); pragma filename("check_size.integer.svc");
  public service check_size ( file : in anonymous string, line : in anonymous integer, seq : in anonymous sequence of anonymous string, size : in anonymous integer ); pragma filename("check_size.string.svc");
  public service check_size ( file : in anonymous string, line : in anonymous integer, seq : in anonymous sequence of instance, size : in anonymous integer ); pragma filename("check_size.instance.svc");

  public service pass ( file : in anonymous string, line : in anonymous integer ) return boolean; pragma filename("pass.fn");
  public service fail ( file : in anonymous string, line : in anonymous integer, failmessage : in anonymous string ) return boolean; pragma filename("fail.fn");

  public service check ( file : in anonymous string, line : in anonymous integer, passed : in anonymous boolean, failmessage : in anonymous string ) return boolean; pragma filename("check.fn");

  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous integer,   rhs : in anonymous integer ) return boolean; pragma filename("check_equal.integer.fn");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous string,    rhs : in anonymous string ) return boolean; pragma filename("check_equal.string.fn");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous duration,  rhs : in anonymous duration ) return boolean; pragma filename("check_equal.duration.fn");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous timestamp, rhs : in anonymous timestamp ) return boolean; pragma filename("check_equal.timestamp.fn");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous boolean,   rhs : in anonymous boolean ) return boolean; pragma filename("check_equal.boolean.fn");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous real,      rhs : in anonymous real, epsilon : in anonymous real ) return boolean; pragma filename("check_equal.real.fn");

  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous sequence of anonymous boolean, rhs : in anonymous sequence of anonymous boolean ) return boolean; pragma filename("check_equal.boolseq.fn");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous sequence of anonymous integer, rhs : in anonymous sequence of anonymous integer ) return boolean; pragma filename("check_equal.intseq.fn");
  public service check_equal ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous sequence of anonymous string, rhs : in anonymous sequence of anonymous string ) return boolean; pragma filename("check_equal.strseq.fn");

  public service check_equal_set ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous set of anonymous boolean, rhs : in anonymous set of anonymous boolean ) return boolean; pragma filename("check_equal.boolset.fn");
  public service check_equal_set ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous set of anonymous integer, rhs : in anonymous set of anonymous integer ) return boolean; pragma filename("check_equal.intset.fn");
  public service check_equal_set ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous set of anonymous string, rhs : in anonymous set of anonymous string ) return boolean; pragma filename("check_equal.strset.fn");

  public service check_equal_bag ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous bag of anonymous boolean, rhs : in anonymous bag of anonymous boolean ) return boolean; pragma filename("check_equal.boolbag.fn");
  public service check_equal_bag ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous bag of anonymous integer, rhs : in anonymous bag of anonymous integer ) return boolean; pragma filename("check_equal.intbag.fn");
  public service check_equal_bag ( file : in anonymous string, line : in anonymous integer, lhs : in anonymous bag of anonymous string, rhs : in anonymous bag of anonymous string ) return boolean; pragma filename("check_equal.strbag.fn");

  public service check_null ( file : in anonymous string, line : in anonymous integer, obj : in instance ) return boolean; pragma filename("check_null.fn");
  public service check_not_null ( file : in anonymous string, line : in anonymous integer, obj : in instance ) return boolean; pragma filename("check_not_null.fn");

  public service check_size ( file : in anonymous string, line : in anonymous integer, seq : in anonymous sequence of anonymous integer, size : in anonymous integer ) return boolean; pragma filename("check_size.integer.fn");
  public service check_size ( file : in anonymous string, line : in anonymous integer, seq : in anonymous sequence of anonymous string, size : in anonymous integer ) return boolean; pragma filename("check_size.string.fn");
  public service check_size ( file : in anonymous string, line : in anonymous integer, seq : in anonymous sequence of instance, size : in anonymous integer ) return boolean; pragma filename("check_size.instance.fn");


  //! Deliver all pending events in the queue and handle the resulting actions.
  //! If more events result, continue servicing the queue until it is empty.
  public service service_event_queue();

  //! Returns a sequence of all active scheduled timers at the time of
  //! invocation. The sequence is in expiration order.
  public service get_scheduled_timers() return sequence of timer;

  //! Fire the timer referenced by `t`. This will cause the associated event to
  //! be added to the event queue and the timer will be put in the expired state
  //! (or rescheduled if it is a recurring timer). This service does not
  //! manipulate system time and therefore subsequent calls to `t'expired_at` may
  //! result in a timestamp in the future. Additionally, recurring timers will be
  //! rescheduled with a new expiration time based on the original expiration.
  //! Firing timers manually should only be done in limited cases to enable
  //! testing.
  public service fire_timer(t: in timer);

  //! Retrieve and fire the next scheduled timer. A handle to the timer that was
  //! fired is returned. If no timers are scheduled, `program_error` is raised.
  public service fire_next_timer() return timer;

  //! Retrieve and fire all timers scheduled at the time of invocation. Handle
  //! all resultant events. If a recurring timer is rescheduled or a new timer
  //! is scheduled as the result of handling an event, the newly scheduled
  //! timers are not fired. Timers are fired in order of expiration and events
  //! are handled after each unique expiration time.
  public service fire_scheduled_timers(); pragma filename("fire_scheduled_timers.svc");

  //! Retrieve and fire all timers as described above. Limit to timers
  //! scheduled at or before `stop_at`.
  public service fire_scheduled_timers(stop_at: in timestamp); pragma filename("fire_scheduled_timers.stop_at.svc");

  public service run_test(); pragma scenario(1);


  object Result is
    file  : preferred string;
    line  : preferred integer;
    count : preferred integer;
    pass : boolean;
    message : string;

    public service register_pass ( file : in anonymous string, line : in anonymous integer );
    public service register_fail ( file : in anonymous string, line : in anonymous integer, message : in anonymous string );

  end object;

end domain;
pragma service_domain(true);
pragma build_set("UtilityDomains");
