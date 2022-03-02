public class Environment {

    public Environment() {
        System.loadLibrary("Environment_interface");
    }

    public Environment(Object domain) {
      this();
    }

    public native void setenv(final String name, final String value);

    public native void unsetenv(final String name);

    public native String getenv(final String name);

    public native boolean isset(final String name);

}
