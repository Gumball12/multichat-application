import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
  public static void main (String... args) throws IOException {
    // init socket, packet
    DatagramSocket sock = new DatagramSocket(10101);
    byte[] buf = new byte[256];
    DatagramPacket p = new DatagramPacket(buf, buf.length);

    // init user lists

    // serve loop
    for (int i = 0; i < 100; i++) {
      sock.receive(p);
      System.out.println(new String(buf));
    }

    // close socket
    sock.close();
  }
}