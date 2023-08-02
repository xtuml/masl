#include <iostream>

struct C {
    C() {     std::cout << "init" << std::endl;
    }
} init;


bool g() {
    std::cout << "hello" << std::endl;
    return true;
}

bool i = g();
