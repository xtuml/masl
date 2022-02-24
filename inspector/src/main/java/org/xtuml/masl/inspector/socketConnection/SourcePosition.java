//
// Filename : SourcePosition.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection;

import java.io.IOException;

import org.xtuml.masl.inspector.processInterface.ExecutableSource;
import org.xtuml.masl.inspector.socketConnection.ipc.CommunicationChannel;
import org.xtuml.masl.inspector.socketConnection.ipc.ReadableObject;
import org.xtuml.masl.inspector.socketConnection.ipc.WriteableObject;

public class SourcePosition extends org.xtuml.masl.inspector.processInterface.SourcePosition
        implements ReadableObject, WriteableObject {

    private final static int NO_ACTION = -1;
    private final static int DOMAIN_SERVICE = 0;
    private final static int TERMINATOR_SERVICE = 1;
    private final static int OBJECT_SERVICE = 2;
    private final static int STATE_ACTION = 3;

    public SourcePosition() {
    }

    public SourcePosition(final DomainServiceMetaData source, final int lineNo) {
        this.actionType = DOMAIN_SERVICE;
        this.domainService = source;
        this.lineNo = lineNo;
    }

    public SourcePosition(final TerminatorServiceMetaData source, final int lineNo) {
        this.actionType = TERMINATOR_SERVICE;
        this.terminatorService = source;
        this.lineNo = lineNo;
    }

    public SourcePosition(final ObjectServiceMetaData source, final int lineNo) {
        this.actionType = OBJECT_SERVICE;
        this.objectService = source;
        this.lineNo = lineNo;
    }

    public SourcePosition(final StateMetaData source, final int lineNo) {
        this.actionType = STATE_ACTION;
        this.stateAction = source;
        this.lineNo = lineNo;
    }

    @Override
    public int getLineNo() {
        return lineNo;
    }

    @Override
    public ExecutableSource getSource() {
        switch (actionType) {
        case DOMAIN_SERVICE:
            return domainService;
        case TERMINATOR_SERVICE:
            return terminatorService;
        case OBJECT_SERVICE:
            return objectService;
        case STATE_ACTION:
            return stateAction;
        default:
            return null;
        }
    }

    @Override
    public void read(final CommunicationChannel channel) throws IOException {
        domainService = null;
        terminatorService = null;
        objectService = null;
        stateAction = null;

        actionType = channel.readInt();

        if (actionType != NO_ACTION) {

            final int domainNo = channel.readInt();
            final int objectNo = channel.readInt();
            final int actionNo = channel.readInt();
            lineNo = channel.readInt();

            if (lineNo == 0) {
                lineNo = LAST_LINE;
            }

            final DomainMetaData domain = (ProcessConnection.getConnection().getMetaData()).getDomain(domainNo);

            switch (actionType) {
            case DOMAIN_SERVICE:
                domainService = domain.getService(actionNo);
                break;
            case TERMINATOR_SERVICE:
                terminatorService = domain.getTerminator(objectNo).getService(actionNo);
                break;
            case OBJECT_SERVICE:
                objectService = domain.getObject(objectNo).getService(actionNo);
                break;
            case STATE_ACTION:
                stateAction = domain.getObject(objectNo).getState(actionNo);
                break;
            }
        } else {
            lineNo = NO_LINE;
        }
    }

    @Override
    public void write(final CommunicationChannel channel) throws IOException {
        channel.writeData(actionType);

        switch (actionType) {
        case DOMAIN_SERVICE:
            channel.writeData(domainService.getDomain().getId());
            channel.writeData(-1);
            channel.writeData(domainService.getArchId());
            break;
        case TERMINATOR_SERVICE:
            channel.writeData(terminatorService.getTerminator().getDomain().getId());
            channel.writeData(terminatorService.getTerminator().getArchId());
            channel.writeData(terminatorService.getArchId());
            break;
        case OBJECT_SERVICE:
            channel.writeData(objectService.getObject().getDomain().getId());
            channel.writeData(objectService.getObject().getArchId());
            channel.writeData(objectService.getArchId());
            break;
        case STATE_ACTION:
            channel.writeData(stateAction.getObject().getDomain().getId());
            channel.writeData(stateAction.getObject().getArchId());
            channel.writeData(stateAction.getId());
            break;
        default:
            throw new IllegalStateException("Invalid source type : " + actionType);
        }

        channel.writeData(lineNo == LAST_LINE ? 0 : lineNo);
    }

    private DomainServiceMetaData domainService = null;

    private TerminatorServiceMetaData terminatorService = null;

    private ObjectServiceMetaData objectService = null;

    private StateMetaData stateAction = null;

    private int lineNo = -1;

    private int actionType = NO_ACTION;
}
