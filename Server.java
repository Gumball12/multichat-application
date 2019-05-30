// import modules
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

// server class
public class Server {
  public static void main (String... args) throws IOException {
    // define socket
    DatagramSocket sock = new DatagramSocket(10100); // server: 10100
    
    // init buffer, packet
    byte[] buf = new byte[256]; // need guard
    String message = null;
    DatagramPacket pack = new DatagramPacket(buf, buf.length);

    // serve loop
    do {
      // wait until receive
      sock.receive(pack);

      // convert message
      message = new String(buf);
      System.out.println(message);
    } while (!message.equals("exit"));

    // close socket
    sock.close();
  }
}