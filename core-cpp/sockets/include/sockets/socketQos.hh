//============================================================================
// UK Crown Copyright (c) 2008. All rights reserved.
//
// File:  socketQos.hh 
//
// Description:    
//    Networks can be configured for Quality Of Service. The masl network has
// implemented QoS using DiffServ. DiffServ enables data streams to be associated
// with pre-defined DiffServ Code point (DSCP) values which enables routers to 
// manage the streams. 
//    If a masl application uses sockets to transport data then the socket must
// be associated with a suitable QoS value. At a minimum masl traffic should be
// associated with the default DSCP value, as this gives the lowest QoS guarantees. 
// Apply this DSCP value to traffic produced by very low priority streams such as
// utility or inspection tools. All other traffic should be tagged with the associated
// protocol (TCP/UDP/MCAST) qos values defined below.
//     By default the client and listener applications defined by this library are 
// automatically configured for QoS (see socketSockets.hh), a developer does not have 
// to explicitly set dscp unless a non-standard value needs to be applied.
//============================================================================//

#ifndef SOCKET_qos__
#define SOCKET_qos__

#include "sockets/socketOption.hh"
#include "sockets/socketProtocol.hh"

namespace SKT {

// *******************************************************************
// *******************************************************************
// *******************************************************************
class QoS
{
  public:

     // These are the DSCP values that should 
     // be used by masl network traffic. Use a NO_QOS value that is out of range of
     // a valid 6 bit QoS value.
     enum { DEFAULT_QOS = 0, TCP_QOS = 26, UDP_QOS = 26, MCAST_QOS = 26, NO_QOS = 1000 };

  public:

     // **********************************************************
     //! Use this function to apply QoS to the Client and Listener 
     //! classes defined by the actual socket library. It will look
     //! inside the client and base the dscp value on the type of 
     //! protocol family it finds during compilation. This method
     //! is automatically called from within the base class 
     //! Socket<P>::Socket (all client/listener apps must extend from 
     //! this class)
     // ***********************************************************
     template <class T> 
     static void setQoS (const T& socket) 
     { 
        if (QoSForProtocol< typename T::protocol_family_type >::dscp_value != NO_QOS){
            setQoS(socket.getSocketFd().descriptor_,QoSForProtocol< typename T::protocol_family_type >::dscp_value); 
        }
     }

     // ****************************************************************
     //! Use these set of functions to apply QoS to raw socket descriptors
     //! created by user applications that do not use this socket library.
     // *****************************************************************
     static void setTCPQoS     (const int descriptor) { setQoS(descriptor,TCP_QOS);     }
     static void setUDPQoS     (const int descriptor) { setQoS(descriptor,UDP_QOS);     }
     static void setMCASTQoS   (const int descriptor) { setQoS(descriptor,MCAST_QOS);   }
     static void setDefaultQoS (const int descriptor) { setQoS(descriptor,DEFAULT_QOS); }

     // **************************************************************
     //! Use these set of functions to apply user defined DSCP and 
     //! Explicit Congestion Notification (ECN) values to raw socket 
     //! descriptors. The ECN value is stored in two bits of the IP packet
     //! so its value should never be greater than 3. While the DSCP 
     //! value is stored in a six bit field of the IP packet and so its 
     //! value should not exceed 63.
     // ***************************************************************
     static inline void setQoS(const int descriptor, const unsigned char dscp);
     static inline void setQoS(const int descriptor, const unsigned char dscp, unsigned char ecn);

  private:
    QoS();
   ~QoS();
      
  private:
     // Declare a template class but do not define it. It should be fully specialised
     // for all the protocol families that dscp values need to be associated with. The
     // implementation of the static templated setQoS(T& socket) method above uses
     // these specialisations to extract the required dscp value during compile time.
     // As this class has no implementation if it is instantiated in favour of a full
     // specialisation a compile time error will be reported.
     template<class T> struct QoSForProtocol;
};

// *******************************************************************
// *******************************************************************
// *******************************************************************
template<> struct QoS::QoSForProtocol< StreamProtocol  <InetAddressFamily> > { static const int64_t dscp_value = QoS::TCP_QOS; };
template<> struct QoS::QoSForProtocol< StreamProtocol  <UnixAddressFamily> > { static const int64_t dscp_value = QoS::NO_QOS;  };
template<> struct QoS::QoSForProtocol< DatagramProtocol<InetAddressFamily> > { static const int64_t dscp_value = QoS::UDP_QOS; };
template<> struct QoS::QoSForProtocol< DatagramProtocol<UnixAddressFamily> > { static const int64_t dscp_value = QoS::NO_QOS;  };
template<> struct QoS::QoSForProtocol< IPProtocol<InetAddressFamily> >       { static const int64_t dscp_value = QoS::UDP_QOS; };
template<> struct QoS::QoSForProtocol< IPProtocol<UnixAddressFamily> >       { static const int64_t dscp_value = QoS::NO_QOS;  };

// *******************************************************************
// *******************************************************************
// *******************************************************************
inline void QoS::setQoS(const int descriptor, const unsigned char dscp)
{
   ::SKT::IpTos ipTos;
   ipTos.get(descriptor);
   unsigned char ecn = ipTos.getValue() & 0x3; // ECN contained in bottom two bits.
   QoS::setQoS(descriptor,dscp,ecn);
}

// *******************************************************************
// *******************************************************************
// *******************************************************************
inline void QoS::setQoS(const int descriptor, const unsigned char dscp, const unsigned char ecn)
{
   int value = (dscp << 2) | ecn;
   ::SKT::IpTos(value).set(descriptor);
}

} // end namespace SKT 
#endif
