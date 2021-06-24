package me.ahoo.cosid;

/**
 * @author ahoo wang
 */
public interface InstanceId {
    InstanceId NONE = new DefaultInstanceId( "0.0.0.0", 0);

    String getInstanceId();

    class DefaultInstanceId implements InstanceId {

        private final String host;
        private final int port;
        private final String instanceId;

        public DefaultInstanceId( String host, int port) {
            this.host = host;
            this.port = port;
            this.instanceId = String.format("%s:%s", host, port);
        }

        @Override
        public String getInstanceId() {
            return instanceId;
        }

        @Override
        public String toString() {
            return instanceId;
        }
    }
}