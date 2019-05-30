import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
  public static void main (String... args) throws IOException, InterruptedException {
    String s = "client 2";
    byte[] buf = (s + " ").getBytes();

    DatagramSocket sock = new DatagramSocket();
    InetAddress addr = InetAddress.getByName("127.0.0.1");

    DatagramPacket p = new DatagramPacket(buf, buf.length, addr, 10101);

    for (int i = 0; i < 100; i++) {
      Thread.sleep(1000);
      sock.send(p);
    }
    sock.close();
  }
}