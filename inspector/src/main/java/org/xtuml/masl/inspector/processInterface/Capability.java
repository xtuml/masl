//
// File: Capability.java
//
// UK Crown Copyright (c) 2008. All Rights Reserved.
//
package org.xtuml.masl.inspector.processInterface;

public enum Capability {
    TRACE_LINES, TRACE_BLOCKS, TRACE_EVENTS, TRACE_EXCEPTIONS, TRACE_IPC_INPUT, TRACE_IPC_OUTPUT, STEP_LINES,
    STEP_BLOCKS, STEP_EXCEPTIONS, STEP_EVENTS, ENABLE_TIMERS, CATCH_CONSOLE, RUN_SCENARIO, RUN_EXTERNAL,
    RUN_DOMAIN_SERVICE, RUN_TERMINATOR_SERVICE, RUN_OBJECT_SERVICE, FIRE_EVENT, SCHEDULE_EVENT, CANCEL_TIMER, CONTINUE,
    PAUSE, STEP, SLOMO, SET_BREAKPOINT, GET_EVENT_QUEUE, GET_STACK, REMOVE_INSTANCE, RUN_TEST_SCHEDULE,
    CREATE_INSTANCE_POPULATION, CREATE_SINGLE_INSTANCE, UPDATE_SINGLE_INSTANCE, DELETE_SINGLE_INSTANCE,
    CREATE_RELATIONSHIPS, CREATE_SUPERSUBTYPES;

    public boolean isAvailable() {
        return ProcessConnection.getConnection().checkCapability(this);
    }
}
