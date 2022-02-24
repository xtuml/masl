// 
// Filename : RunTestSchedule.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;

public class RunTestSchedule extends CommandStub<VoidType> {

    private final File file;

    public RunTestSchedule(final File file) {
        super(ServerCommandId.RUN_TEST_SCHEDULE);
        this.file = file;
    }

    @Override
    public VoidType execute(final CommunicationChannel channel) throws IOException {
        final Reader reader = new BufferedReader(new FileReader(file));

        int charsRead = 0;
        final int size = (int) file.length();
        final char[] schedule = new char[size];
        while (charsRead < size) {
            charsRead += reader.read(schedule, charsRead, size - charsRead);
        }

        channel.writeData(file.getName());
        channel.writeData(schedule);
        channel.flush();
        return null;
    }

}
