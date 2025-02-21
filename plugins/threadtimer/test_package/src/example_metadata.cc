#include <metadata/MetaData.hh>

struct Init {
    Init() { SWA::ProcessMetaData::getProcess().setName("PluginTest"); }

} init;
