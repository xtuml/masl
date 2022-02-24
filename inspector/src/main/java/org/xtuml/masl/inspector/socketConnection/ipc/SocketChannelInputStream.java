// 
// Filename : SocketChannelInputStream.java
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
package org.xtuml.masl.inspector.socketConnection.ipc;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketChannelInputStream implements DataInput {

    private static int defaultBufferSize = 1024 * 32;

    protected ByteBuffer buffer = ByteBuffer.allocateDirect(defaultBufferSize);

    protected SocketChannel channel;

    public SocketChannelInputStream(final SocketChannel channel) {
        this.channel = channel;
        buffer.flip();
    }

    private int readData() throws IOException {
        buffer.compact();
        final int n = channel.read(buffer);
        buffer.flip();
        return n;
    }

    public int available() throws IOException {
        if (buffer.remaining() == 0) {
            final boolean blocking = channel.isBlocking();
            channel.configureBlocking(false);
            readData();
            channel.configureBlocking(blocking);
        }
        return buffer.remaining();
    }

    @Override
    public boolean readBoolean() throws IOException {
        while (true) {
            try {
                return buffer.get() != 0;
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public byte readByte() throws IOException {
        while (true) {
            try {
                return buffer.get();
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public char readChar() throws IOException {
        while (true) {
            try {
                return buffer.getChar();
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public short readShort() throws IOException {
        while (true) {
            try {
                return buffer.getShort();
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public int readInt() throws IOException {
        while (true) {
            try {
                return buffer.getInt();
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public long readLong() throws IOException {
        while (true) {
            try {
                return buffer.getLong();
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public float readFloat() throws IOException {
        while (true) {
            try {
                return buffer.getFloat();
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public double readDouble() throws IOException {
        while (true) {
            try {
                return buffer.getDouble();
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public int readUnsignedByte() throws IOException {
        while (true) {
            try {
                return buffer.get() & 0xFF;
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public int readUnsignedShort() throws IOException {
        while (true) {
            try {
                return buffer.getShort() & 0xFFFF;
            } catch (final BufferUnderflowException e) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
        }
    }

    @Override
    public void readFully(final byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    @Override
    public void readFully(final byte[] b, final int off, final int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }

        int n = 0;
        while (n < len) {
            if (buffer.remaining() == 0) {
                if (readData() < 0) {
                    throw new EOFException();
                }
            }
            final int count = Math.min(len - n, buffer.remaining());
            buffer.get(b, off + n, count);
            n += count;
        }
    }

    @Override
    public int skipBytes(final int len) throws IOException {
        int n = 0;
        while (n < len) {
            if (buffer.remaining() == 0) {
                if (readData() < 0) {
                    return n;
                }
            }
            final int count = Math.min(len - n, buffer.remaining());
            buffer.position(buffer.position() + count);
            n += count;
        }
        return n;
    }

    @Override
    public String readLine() {
        return "";
    }

    @Override
    public String readUTF() {
        return "";
    }

}
