/*
 * ----------------------------------------------------------------------------
 * (c) 2005-2023 - CROWN OWNED COPYRIGHT. All rights reserved.
 * The copyright of this Software is vested in the Crown
 * and the Software is the property of the Crown.
 * ----------------------------------------------------------------------------
 * SPDX-License-Identifier: Apache-2.0
 * ----------------------------------------------------------------------------
 */

#ifndef ServiceInterceptor__
#define ServiceInterceptor__

#include "swa/ProgramError.hh"
#include <functional>
#include <iostream>

namespace SWA {

// *****************************************************************
//! \brief A domain service invocation router.
//!
//! Invocations of domain services need to be bound at runtime rather than
//! compile time to allow for the insertion of test stubs during unit testing
//! and the replacement of local implementations with their remote
//! counter-parts. This templated class provides the required functionality.
//!
//! An instance of this class is created for each domain based service. An
//! example specialisation of the ServiceInterceptor templated class is shown
//! below.
//!
//!   typedef ::SWA::ServiceInterceptor<masls_tune_receiver_tag,void
//!   (int32_t,int32_t,int32_t)> maslsi_tune_receiver;
//!
//! At the call sites for this method a direct invocation of the method is
//! replaced with a call to the associated Interceptors callService method.
//!
//! For example:
//!
//!       I_RXC::masls_tune_receiver(receiver_id,frequency,scale);
//!
//! would be replaced by
//!
//!       I_RXC::maslsi_tune_receiver::instance().callService(receiver_id,frequency,scale);
//!
//! The interceptor class is populated with the required methods, via a static
//! registration mechanism based on which libraries are included within an
//! executable. The callService method will therefore invoke whichever
//! implementation has been registered. If a local implementation is found, then
//! this will be called, otherwise the call is forwarded to the remote
//! implementation. If no servcie is found then an exception will be raised.
//!
//! It is also possible to include both the local and remote implmentations
//! within an executable and at the MASL level direct the code generator to call
//! the required implementation. This replaces the generic callService
//! invocation with a specific method invocation based on the information
//! supplied.
//!
//! For example:
//!
//!    MASL code     :   I_RXC::tune_receiver(receiver_id,frequency,scale)
//!    pragma scope("remote"); Generated c++ :
//!    I_RXC::maslsi_tune_receiver::instance().callRemote(receiver_id,frequency,scale);
//!
//!    MASL code     :   I_RXC::tune_receiver(receiver_id,frequency,scale)
//!    pragma scope("local"); Generated c++ :
//!    I_RXC::maslsi_tune_receiver::instance().callLocal(receiver_id,frequency,scale);
//!
//!
// *****************************************************************
template <class U, class T> class ServiceInterceptor {
  public:
    typedef std::function<T> ServiceFunType;

    ServiceFunType callService() {
        check(localService, remoteService);
        return localService != 0 ? localService : remoteService;
    }
    ServiceFunType callLocal() {
        check(localService);
        return localService;
    }
    ServiceFunType callRemote() {
        check(remoteService);
        return remoteService;
    }
    ServiceFunType callCallback() {
        check(callbackService);
        return callbackService;
    }

  public:
    static ServiceInterceptor &instance() {
        static ServiceInterceptor instance;
        return instance;
    }

    bool registerLocal(ServiceFunType local) {
        localService = local;
        callbackService = local;
        return true;
    }
    bool registerRemote(ServiceFunType remote) {
        remoteService = remote;
        return true;
    }
    bool registerCallback(ServiceFunType callback) {
        callbackService = callback;
        return true;
    }

  private:
    ServiceInterceptor() : localService(), remoteService(), callbackService() {}
    ~ServiceInterceptor() {}

    // Prevent copy and assignment
    ServiceInterceptor(const ServiceInterceptor &rhs);
    ServiceInterceptor &operator=(const ServiceInterceptor &rhs);

    void check(ServiceFunType serviceFn) const {
        if (serviceFn == 0) {
            throw SWA::ProgramError(
                std::string(
                    "ServiceInterceptor has no registered method for ") +
                typeid(U).name());
        }
    }

    void check(ServiceFunType serviceFn1, ServiceFunType serviceFn2) const {
        if (serviceFn1 == 0 && serviceFn2 == 0) {
            throw SWA::ProgramError(
                std::string(
                    "ServiceInterceptor has no registered method for ") +
                typeid(U).name());
        }
    }

  private:
    ServiceFunType localService;
    ServiceFunType remoteService;
    ServiceFunType callbackService;
};

} // end namespace SWA

#endif
