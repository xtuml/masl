/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#include <asio/ip/tcp.hpp>
#include <unistd.h>

#include "ConnectionError.hh"
#include "ConsoleRedirection.hh"
#include <iostream>
#include <format>

namespace Inspector {

    ConsoleRedirection::ConsoleRedirection(asio::any_io_executor executor, asio::ip::port_type port)
        : save_stdin(-1),
          save_stdout(-1),
          save_stderr(-1),
          acceptor(executor, {asio::ip::tcp::v4(), port}),
          client_socket(executor),
          connected(false),
          redirecting(false) {}

    ConsoleRedirection::~ConsoleRedirection() {
        disconnect();
    }

    bool ConsoleRedirection::attemptConnect() {
        std::error_code ec;
        acceptor.accept(client_socket, ec);
        if (!ec) {
            connected = true;
        } else if (ec != asio::error::would_block) {
            throw ConnectionError(
                std::format("ConsoleRedirection::attemptConnect - Non-blocking socket accept failed - {}", ec.message())
            );
        }
        return connected;
    }

    void ConsoleRedirection::disconnect() {
        if (connected) {
            stopRedirection();
            client_socket.close();
            connected = false;
        }
    }

    void ConsoleRedirection::startRedirection() {
        if (connected && !redirecting) {
            // Save the original std file handles
            save_stdin = dup(STDIN_FILENO);
            save_stdout = dup(STDOUT_FILENO);
            save_stderr = dup(STDERR_FILENO);

            // Redirect std file handles to the console socket
            dup2(client_socket.native_handle(), STDIN_FILENO);
            dup2(client_socket.native_handle(), STDOUT_FILENO);
            dup2(client_socket.native_handle(), STDERR_FILENO);

            redirecting = true;
        }
    }

    void ConsoleRedirection::stopRedirection() {
        if (connected && redirecting) {
            redirecting = false;

            // Connect the std files back to their originals
            dup2(save_stdin, STDIN_FILENO);
            dup2(save_stdout, STDOUT_FILENO);
            dup2(save_stderr, STDERR_FILENO);

            // Close the saved file handles
            close(save_stdin);
            close(save_stdout);
            close(save_stderr);

            // Clear the error status of the streams
            std::cout.clear();
            std::cerr.clear();
            std::cin.clear();
        }
    }

} // namespace Inspector
