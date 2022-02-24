//
// Filename : ProcessConnection.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.processInterface;

import java.util.EnumSet;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import org.xtuml.masl.inspector.BreakpointController;
import org.xtuml.masl.inspector.BreakpointEvent;
import org.xtuml.masl.inspector.BreakpointListener;

public abstract class ProcessConnection implements BreakpointListener {

    public static final int IDLE = 0;
    public static final int RUNNING = 1;
    public static final int PAUSED = 2;
    public static final int BACKLOG = 3;

    private static ProcessConnection connection = null;

    static {
        final String connectionClass = System.getProperty("connectionClass",
                "org.xtuml.masl.inspector.socketConnection.ProcessConnection");
        try {
            connection = (ProcessConnection) Class.forName(connectionClass).newInstance();
            System.out.println("Connected to " + connection.getConnectionTitle());
        } catch (final Exception e) {
            System.err.println("Failed to instatiate connection class " + connectionClass);
            e.printStackTrace();
            connection = new NullConnection();
        }
    }

    public static ProcessConnection getConnection() {
        return connection;
    }

    protected final EnumSet<Capability> capabilities = EnumSet.noneOf(Capability.class);

    boolean checkCapability(final Capability capability) {
        return capabilities.contains(capability);
    }

    private void checkCapability(final Capability capability, final String method, final Class<?>[] params) {
        try {
            if (getClass().getMethod(method, params).getDeclaringClass() != ProcessConnection.class) {
                capabilities.add(capability);
            }
        } catch (final NoSuchMethodException e) {
            e.printStackTrace();
        } catch (final SecurityException e) {
            e.printStackTrace();
        }
    }

    public ProcessConnection() {
        checkCapability(Capability.TRACE_LINES, "setTraceLines", new Class[] { boolean.class });
        checkCapability(Capability.TRACE_BLOCKS, "setTraceBlocks", new Class[] { boolean.class });
        checkCapability(Capability.TRACE_EVENTS, "setTraceEvents", new Class[] { boolean.class });
        checkCapability(Capability.TRACE_EXCEPTIONS, "setTraceExceptions", new Class[] { boolean.class });
        checkCapability(Capability.STEP_LINES, "setStepLines", new Class[] { boolean.class });
        checkCapability(Capability.STEP_BLOCKS, "setStepBlocks", new Class[] { boolean.class });
        checkCapability(Capability.STEP_EXCEPTIONS, "setStepExceptions", new Class[] { boolean.class });
        checkCapability(Capability.STEP_EVENTS, "setStepEvents", new Class[] { boolean.class });
        checkCapability(Capability.ENABLE_TIMERS, "setEnableTimers", new Class[] { boolean.class });
        checkCapability(Capability.CATCH_CONSOLE, "setCatchConsole", new Class[] { boolean.class });
        checkCapability(Capability.CONTINUE, "continueExecution", new Class[] {});
        checkCapability(Capability.PAUSE, "pauseExecution", new Class[] {});
        checkCapability(Capability.STEP, "stepExecution", new Class[] {});
        checkCapability(Capability.SLOMO, "slomoExecution", new Class[] {});
        checkCapability(Capability.SET_BREAKPOINT, "setBreakpoint",
                new Class[] { SourcePosition.class, Boolean.class });
        checkCapability(Capability.GET_EVENT_QUEUE, "getEventQueue", new Class[] {});
        checkCapability(Capability.GET_STACK, "getStack", new Class[] {});

        if (System.getProperty("allowWrites", "true").equals("true")) {
            checkCapability(Capability.RUN_SCENARIO, "runScenario", new Class[] { DomainServiceMetaData.class });
            checkCapability(Capability.RUN_EXTERNAL, "runExternal", new Class[] { DomainServiceMetaData.class });
            checkCapability(Capability.RUN_DOMAIN_SERVICE, "invokeDomainService",
                    new Class[] { DomainServiceMetaData.class, DataValue[].class });
            checkCapability(Capability.RUN_TERMINATOR_SERVICE, "invokeTerminatorService",
                    new Class[] { TerminatorServiceMetaData.class, DataValue[].class });
            checkCapability(Capability.RUN_OBJECT_SERVICE, "invokeObjectService",
                    new Class[] { ObjectServiceMetaData.class, Integer.class, DataValue[].class });
            checkCapability(Capability.RUN_TEST_SCHEDULE, "runTestSchedule", new Class[] { java.io.File.class });
            checkCapability(Capability.FIRE_EVENT, "fireEvent",
                    new Class[] { EventMetaData.class, ObjectMetaData.class, Integer.class, DataValue[].class });
            checkCapability(Capability.SCHEDULE_EVENT, "scheduleEvent", new Class[] { TimerData.class });
            checkCapability(Capability.CANCEL_TIMER, "cancelTimer", new Class[] { TimerData.class });
            checkCapability(Capability.CREATE_INSTANCE_POPULATION, "createInstancePopulation",
                    new Class[] { ObjectMetaData.class, InstanceData[].class });
            checkCapability(Capability.CREATE_SINGLE_INSTANCE, "createSingleInstance",
                    new Class[] { ObjectMetaData.class, InstanceData.class });
            checkCapability(Capability.UPDATE_SINGLE_INSTANCE, "updateSingleInstance",
                    new Class[] { ObjectMetaData.class, InstanceData.class });
            checkCapability(Capability.DELETE_SINGLE_INSTANCE, "deleteSingleInstance",
                    new Class[] { ObjectMetaData.class, Object.class });
            checkCapability(Capability.CREATE_RELATIONSHIPS, "createRelationships",
                    new Class[] { RelationshipMetaData.class, RelationshipData[].class });
            checkCapability(Capability.CREATE_SUPERSUBTYPES, "createSuperSubtypes",
                    new Class[] { SuperSubtypeMetaData.class, SuperSubtypeData[].class });

        }

        BreakpointController.getInstance().addBreakpointListener(this);

    }

    public abstract String getConnectionTitle() throws java.rmi.RemoteException;

    public abstract ProcessMetaData getMetaData() throws java.rmi.RemoteException;

    @SuppressWarnings("unused")
    public void setTraceLines(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setTraceBlocks(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setTraceEvents(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setTraceExceptions(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setPluginFlag(final String pluginName, final String flagName, final boolean value)
            throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setPluginProperty(final String pluginName, final String propertyName, final String value)
            throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void invokePluginAction(final String pluginName, final String actionName) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setStepLines(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setStepBlocks(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setStepExceptions(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setStepEvents(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setEnableTimers(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setCatchConsole(final boolean enabled) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public boolean getTraceLines() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean getTraceBlocks() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean getTraceEvents() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean getTraceExceptions() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean getPluginFlag(final String pluginName, final String flagName) throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public String getPluginProperty(final String pluginName, final String propertyName)
            throws java.rmi.RemoteException {
        return "";
    }

    @SuppressWarnings("unused")
    public boolean getStepLines() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean getStepBlocks() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean getStepExceptions() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean getStepEvents() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean getEnableTimers() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public boolean getCatchConsole() throws java.rmi.RemoteException {
        return false;
    }

    @SuppressWarnings("unused")
    public int getAssignerState(final ObjectMetaData object) throws java.rmi.RemoteException {
        return 0;
    }

    @SuppressWarnings("unused")
    public int getInstanceCount(final ObjectMetaData object) throws java.rmi.RemoteException {
        return 0;
    }

    @SuppressWarnings("unused")
    public InstanceData getInstanceData(final ObjectMetaData object, final Object pk) throws java.rmi.RemoteException {
        return null;
    }

    @SuppressWarnings("unused")
    public void getInstanceData(final ObjectMetaData object, final InstanceDataListener listener)
            throws java.rmi.RemoteException {
        listener.setInstanceCount(0);
    }

    @SuppressWarnings("unused")
    public void getInstanceData(final ObjectMetaData object, final Object[] pks, final InstanceDataListener listener)
            throws java.rmi.RemoteException {
        listener.setInstanceCount(0);
    }

    @SuppressWarnings("unused")
    public void getRelatedInstanceData(final ObjectMetaData object, final Object pk, final int relId,
            final InstanceDataListener listener) throws java.rmi.RemoteException {
        listener.setInstanceCount(0);
    }

    @SuppressWarnings("unused")
    public void createInstancePopulation(final ObjectMetaData object, final InstanceData[] data)
            throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void createSingleInstance(final ObjectMetaData object, final InstanceData data)
            throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void updateSingleInstance(final ObjectMetaData object, final InstanceData data)
            throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void deleteSingleInstance(final ObjectMetaData object, final Object pk) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void createRelationships(final RelationshipMetaData rel, final RelationshipData[] data)
            throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void createSuperSubtypes(final SuperSubtypeMetaData ss, final SuperSubtypeData[] data)
            throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void runScenario(final DomainServiceMetaData scenario) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void runExternal(final DomainServiceMetaData external) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void runTestSchedule(final java.io.File file) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void invokeDomainService(final DomainServiceMetaData service, final DataValue<?>[] parameters)
            throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void invokeTerminatorService(final TerminatorServiceMetaData service, final DataValue<?>[] parameters)
            throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void continueExecution() throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void pauseExecution() throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void stepExecution() throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void slomoExecution() throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void setBreakpoint(final SourcePosition position, final Boolean enable) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public EventData[] getEventQueue() throws java.rmi.RemoteException {
        return new EventData[] {};
    }

    @SuppressWarnings("unused")
    public TimerData[] getTimerQueue() throws java.rmi.RemoteException {
        return new TimerData[] {};
    }

    @SuppressWarnings("unused")
    public StackFrame[] getStack() throws java.rmi.RemoteException {
        return new StackFrame[] {};
    }

    @SuppressWarnings("unused")
    public void invokeObjectService(final ObjectServiceMetaData service, final Integer pk,
            final DataValue<?>[] parameters) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void fireEvent(final EventMetaData event, final ObjectMetaData object, final Integer pk,
            final DataValue<?>[] parameters) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void scheduleEvent(final TimerData timer) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void cancelTimer(final TimerData timer) throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void closeConnection() throws java.rmi.RemoteException {
    }

    @SuppressWarnings("unused")
    public void lostConnection() throws java.rmi.RemoteException {
    }

    protected EventListenerList listenerList = new EventListenerList();

    private SourcePosition currentPosition = null;
    private int currentStatus = IDLE;

    public SourcePosition getCurrentPosition() {
        return currentPosition;
    }

    public int getCurrentStatus() {
        return currentStatus;
    }

    public void addProcessStatusListener(final ProcessStatusListener listener) {
        listenerList.add(ProcessStatusListener.class, listener);
    }

    public void removeProcessStatusListener(final ProcessStatusListener listener) {
        listenerList.remove(ProcessStatusListener.class, listener);
    }

    public void addBacklogListener(final BacklogListener listener) {
        listenerList.add(BacklogListener.class, listener);
    }

    public void removeBacklogListener(final BacklogListener listener) {
        listenerList.remove(BacklogListener.class, listener);
    }

    public java.util.EventListener[] getListeners(final Class<EventListener> listenerType) {
        return listenerList.getListeners(listenerType);
    }

    private void fireProcessStatusEvent(final ProcessStatusEvent e) {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ProcessStatusListener.class) {
                ((ProcessStatusListener) listeners[i + 1]).processStatusChanged(e);
            }
        }
    }

    private void fireBacklogEvent(final BacklogEvent e) {
        // Guaranteed to return a non-null array
        final Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == BacklogListener.class) {
                ((BacklogListener) listeners[i + 1]).backlogChanged(e);
            }
        }
    }

    public void setCurrentPosition(final SourcePosition position) {
        currentStatus = PAUSED;
        currentPosition = position;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                fireProcessStatusEvent(new ProcessStatusEvent(ProcessConnection.this, PAUSED, position));
            }
        });
    }

    public void setRunning() {
        currentStatus = RUNNING;
        currentPosition = null;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                fireProcessStatusEvent(new ProcessStatusEvent(ProcessConnection.this, RUNNING, null));
            }
        });
    }

    public void setIdle() {
        currentStatus = IDLE;
        currentPosition = null;

        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                fireProcessStatusEvent(new ProcessStatusEvent(ProcessConnection.this, IDLE, null));
            }
        });
    }

    public void setBacklog(final long backlogMillis) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                fireBacklogEvent(new BacklogEvent(ProcessConnection.this, backlogMillis));
            }
        });
    }

    @Override
    public void breakpointChanged(final BreakpointEvent e) {
        try {
            setBreakpoint(e.getPosition(), e.getActive());
        } catch (final java.rmi.RemoteException re) {
            re.printStackTrace();
        }
    }

}
