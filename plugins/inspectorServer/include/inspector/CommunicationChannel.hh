/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */
#ifndef Inspector_CommunicationChannel_HH
#define Inspector_CommunicationChannel_HH

#include "BufferedIO.hh"
#include <asio/ip/tcp.hpp>
#include <asio/bind_cancellation_slot.hpp>

namespace Inspector {

    class CommunicationChannel {
      private:
        asio::ip::tcp::acceptor acceptor;
        asio::ip::tcp::socket client_socket;

        bool connected;

        BufferedOutputStream outputStream;
        BufferedInputStream inputStream;

        asio::cancellation_signal cancelAcceptSignal;
        asio::cancellation_signal cancelReadSignal;

      public:
        CommunicationChannel(std::string name, asio::any_io_executor executor, asio::ip::port_type port);
        ~CommunicationChannel();
        bool isConnected() const {
            return connected;
        }
        bool attemptConnect();
        void disconnect();

        template <typename Fn>
        void asyncAcceptReady(Fn callback) {
            acceptor.async_wait(
                asio::ip::tcp::acceptor::wait_read,
                bind_cancellation_slot(
                    cancelAcceptSignal.slot(),
                    [log = log, callback](const asio::error_code &ec) {
                        if (ec == asio::error::operation_aborted) {
                            return;
                        }
                        callback();
                    }
                )
            );
        }

        void cancelAccept() {
            cancelAcceptSignal.emit(asio::cancellation_type::all);
        }

        template <typename Fn>
        void asyncReadReady(Fn callback) {
            client_socket.async_wait(
                asio::ip::tcp::acceptor::wait_read,
                bind_cancellation_slot(
                    cancelReadSignal.slot(),
                    [log = log, callback](const asio::error_code &ec) {
                        if (ec == asio::error::operation_aborted) {
                            return;
                        }
                        callback();
                    }
                )
            );
        }

        void cancelRead() {
            cancelReadSignal.emit(asio::cancellation_type::all);
        }

        template <class T>
        CommunicationChannel &operator<<(const T &val);
        template <class T>
        CommunicationChannel &operator>>(T &val);

        BufferedOutputStream &getOutputStream() {
            return outputStream;
        }
        BufferedInputStream &getInputStream() {
            return inputStream;
        }

        bool empty() {
            return inputStream.empty();
        }

        bool ready() {
            return connected && inputStream.available();
        }
        void flush() {
            outputStream.flush();
        }

        asio::ip::tcp::acceptor &getAcceptor() {
            return acceptor;
        }
        asio::ip::tcp::socket &getClientSocket() {
            return client_socket;
        }
    };

    template <class T>
    CommunicationChannel &CommunicationChannel::operator<<(const T &val) {
        outputStream.write(val);
        return *this;
    }

    template <class T>
    CommunicationChannel &CommunicationChannel::operator>>(T &val) {
        inputStream.read(val);
        return *this;
    }

} // namespace Inspector

#endif
