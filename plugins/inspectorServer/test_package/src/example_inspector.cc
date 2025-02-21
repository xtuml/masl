#include <inspector/ProcessHandler.hh>

struct Init {
    Init() { Inspector::ProcessHandler::getInstance(); }

} init;
