// 
// Filename : pm_communication_channel.hh
//
// UK Crown Copyright (c) 2005. All Rights Reserved
//
#ifndef Inspector_CommunicationChannel_HH
#define Inspector_CommunicationChannel_HH

#include "sockets/sockets.hh"
#include "BufferedIO.hh"

namespace Inspector {

  class CommunicationChannel
  {
    private:
      typedef SKT::InternetReliableListener ServerSocketType;
      typedef SKT::InternetReliableClient   ClientSocketType;
      typedef SKT::InternetReliableClient::socket_descriptor SocketDescriptorType;

      ServerSocketType  serverSocket;
      ClientSocketType* clientSocket;

      bool connected;

      BufferedOutputStream outputStream;
      BufferedInputStream inputStream;

    public:
      CommunicationChannel ( int port );
      ~CommunicationChannel();
      bool isConnected() const { return connected; }
      bool attemptConnect();
      void disconnect();

      int getServerFd() const { return serverSocket.getSocketFd().descriptor_; }
      int getClientFd() const { return clientSocket->getSocketFd().descriptor_; }

      template<class T> CommunicationChannel& operator<<(const T& val);
      template<class T> CommunicationChannel& operator>>(T& val);

      BufferedOutputStream& getOutputStream() { return outputStream; }
      BufferedInputStream& getInputStream() { return inputStream; }

      bool empty() { return inputStream.empty(); }

      bool ready() { return connected && inputStream.available(); }
      void flush() { outputStream.flush(); }
  };


  template<class T>
  CommunicationChannel& CommunicationChannel::operator<<(const T& val)
  {
    outputStream.write(val);
    return *this;
  }

  template<class T>
  CommunicationChannel& CommunicationChannel::operator>>(T& val)
  {
    inputStream.read(val);
    return *this;
  }

}

#endif
