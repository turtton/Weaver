package net.turtton.weaver;

public class TestServerVariables {

    private static int networkCompressionThreshold = -1;
    private static int port = 25565;

    public static void setNetworkCompressionThreshold(int value) {
        networkCompressionThreshold = value;
    }

    public static int getNetworkCompressionThreshold() {
        return networkCompressionThreshold;
    }

    public static void setPort(int portNumber) {
        port = portNumber;
    }

    public static int getPort() {
        return port;
    }
}
