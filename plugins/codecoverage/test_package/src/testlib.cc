#include <swa/Main.hh>
#include <swa/Process.hh>

struct Init {
    Init() { SWA::Process::getInstance().setProjectName("PluginTest"); }
} init;

int main(int argc, char **argv) { return SWA::main(argc, argv); }
