//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
#ifndef Inspector_ConsoleRedirection_HH
#define Inspector_ConsoleRedirection_HH

#include "sockets/sockets.hh"

namespace Inspector {

  class ConsoleRedirection
  {
    private:
      int save_stdin;
      int save_stdout;
      int save_stderr;

      typedef SKT::InternetReliableListener ServerSocketType;
      typedef SKT::BufferedInternetReliableClient   ClientSocketType;
      typedef SKT::BufferedInternetReliableClient::socket_descriptor SocketDescriptorType;

      ServerSocketType  serverSocket;
      ClientSocketType* clientSocket;

      bool connected;
      bool redirecting;

    public:

      ConsoleRedirection ( int port );

      ~ConsoleRedirection();

      bool attemptConnect();

      void disconnect();

      void startRedirection();

      void stopRedirection();

      bool isConnected() const { return connected; }

  };

}

#endif
