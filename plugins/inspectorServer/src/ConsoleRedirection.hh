
#pragma once

#include <asio/awaitable.hpp>
#include <asio/ip/tcp.hpp>

namespace Inspector {

  class ConsoleRedirection
  {
    private:
      int save_stdin;
      int save_stdout;
      int save_stderr;

      asio::ip::tcp::acceptor  acceptor;
      asio::ip::tcp::socket  client_socket;

      bool connected;
      bool redirecting;

    public:

      ConsoleRedirection ( asio::any_io_executor executor, asio::ip::port_type port );

      ~ConsoleRedirection();

      bool attemptConnect();

      void disconnect();

      void startRedirection();

      void stopRedirection();

      bool isConnected() const { return connected; }

  };

}
