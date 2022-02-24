//
// Filename : RunSchedule.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector;

import org.xtuml.masl.inspector.processInterface.ProcessConnection;
import org.xtuml.masl.inspector.processInterface.ProcessStatusEvent;
import org.xtuml.masl.inspector.processInterface.ProcessStatusListener;
import org.xtuml.masl.inspector.processInterface.WeakProcessStatusListener;

public class RunSchedule implements ProcessStatusListener {

    public RunSchedule() throws Exception {
        ProcessConnection.getConnection().setCatchConsole(false);
        ProcessConnection.getConnection().addProcessStatusListener(new WeakProcessStatusListener(this));
    }

    private volatile boolean finished = false;

    public void run(final java.io.File schedule) throws Exception {
        finished = false;
        System.out.print("Running Schedule " + schedule.getCanonicalPath() + "...");
        ProcessConnection.getConnection().runTestSchedule(schedule);
        Thread.yield();
        while (!finished) {
            System.out.print(".");
            Thread.sleep(250);
        }
        System.out.println("done");
    }

    @Override
    public void processStatusChanged(final ProcessStatusEvent e) {
        if (e.getStatus() == ProcessConnection.IDLE) {
            finished = true;
        }
    }

    public static void main(final String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        final RunSchedule runner = new RunSchedule();

        for (String arg : args) {
            runner.run(new java.io.File(arg));
        }
    }
}
