/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 * Classification: UK OFFICIAL
 * ----------------------------------------------------------------------------
 */

#include "inspector/CommunicationChannel.hh"
#include "ConnectionError.hh"
#include <arpa/inet.h>
#include <errno.h>
#include <fcntl.h>
#include <format>
#include <iostream>
#include <netinet/in.h>
#include <string>
#include <sys/socket.h>
#include <unistd.h>
#include <vector>

namespace Inspector {

    CommunicationChannel::CommunicationChannel(
        std::string name, asio::any_io_executor executor, asio::ip::port_type port
    )
        : acceptor(executor, {asio::ip::tcp::v4(), port}),
          client_socket(executor),
          connected(false) {}

    CommunicationChannel::~CommunicationChannel() {
        disconnect();
    }

    bool CommunicationChannel::attemptConnect() {
        std::error_code ec;
        acceptor.accept(client_socket, ec);
        if (!ec) {
            connected = true;
            outputStream.setFp(client_socket.native_handle());
            inputStream.setFp(client_socket.native_handle());
        } else if (ec != asio::error::would_block) {
            throw ConnectionError(
                std::format("ConsoleRedirection::attemptConnect - Non-blocking socket accept failed - {}", ec.message())
            );
        }
        return connected;
    }

    void CommunicationChannel::disconnect() {
        if (connected) {
            outputStream.setFp(-1);
            inputStream.setFp(-1);
            client_socket.close();

            connected = false;
        }
    }

} // namespace Inspector
