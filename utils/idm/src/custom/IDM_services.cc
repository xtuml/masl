#include "IDM_OOA/__IDM_services.hh"

#include "idm/ProcessHandler.hh"
#include "swa/String.hh"

#include <stdint.h>

namespace masld_IDM {
    void masls_set_topic_config(const ::SWA::String &maslp_topic, const ::SWA::String &maslp_param_name, const ::SWA::String &maslp_param_value) {
        InterDomainMessaging::ProcessHandler::getInstance().setConsumerConfig(maslp_topic, maslp_param_name, maslp_param_value);
    }

    const bool localServiceRegistration_masls_set_topic_config = interceptor_masls_set_topic_config::instance().registerLocal(&masls_set_topic_config);

    void masls_overload1_set_topic_config(const ::SWA::String &maslp_topic, const ::SWA::String &maslp_param_name, int32_t maslp_param_value) {
        InterDomainMessaging::ProcessHandler::getInstance().setConsumerConfig(maslp_topic, maslp_param_name, maslp_param_value);
    }

    const bool localServiceRegistration_masls_overload1_set_topic_config =
        interceptor_masls_overload1_set_topic_config::instance().registerLocal(&masls_overload1_set_topic_config);

    void masls_overload2_set_topic_config(const ::SWA::String &maslp_topic, const ::SWA::String &maslp_param_name, bool maslp_param_value) {
        InterDomainMessaging::ProcessHandler::getInstance().setConsumerConfig(maslp_topic, maslp_param_name, maslp_param_value);
    }

    const bool localServiceRegistration_masls_overload2_set_topic_config =
        interceptor_masls_overload2_set_topic_config::instance().registerLocal(&masls_overload2_set_topic_config);

} // namespace masld_IDM
