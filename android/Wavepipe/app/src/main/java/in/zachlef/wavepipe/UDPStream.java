package in.zachlef.wavepipe;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class UDPStream {
    public enum Command {
        OPEN,
        CLOSE
    }

    public static final int PACKET_SIZE = 64 * 2;
    public static final int SAMPLE_RATE = 22000;
    public static final int PORT = 9134;
//    public static final

    public UDPStream(String host, int port) {
        setTarget(host, port);
        buf = new byte[PACKET_SIZE];
    }

    private String host;
    private int port;
    private DatagramSocket socket;
    private SampleReceivedHandler sampleReceivedHandler;
    private byte[] buf;

    public void initSocket() throws SocketException {
        socket = new DatagramSocket();
    }
    public void sendCommand(Command cmd) throws IOException {
        DatagramPacket packet = new DatagramPacket(
                cmd.toString().getBytes(StandardCharsets.UTF_8),
                cmd.toString().length(),
                InetAddress.getByName(host),
                port
                );

        socket.send(packet);
    }

    public void receiveSample() throws IOException {
        DatagramPacket inbound = new DatagramPacket(buf, PACKET_SIZE);
        socket.receive(inbound);

        // do callback
        if (sampleReceivedHandler != null) {
            sampleReceivedHandler.handleSampleReceived(inbound.getData());
        }
    }

    public byte[] getBuf() {
        return this.buf;
    }

    public void setTarget(String host, int port) {
        this.host = host;
        this.port = port;
    }
}