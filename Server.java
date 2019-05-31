/**
 * java multi-chat application (Server-side)
 * 
 * @author HeaJun Seo
 * @since 2019-05-31
 * @repo https://github.com/Gumball12/multichat-application
 */

// import modules
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.util.HashMap;

// server class
public class Server {
  // sendor fields
  private static DatagramSocket sendorSock = null;
  private static InetAddress addr = null;

  // user field
  private static HashMap<String, String> users = new HashMap<String, String>();

  /**
   * main
   * 
   * @param args
   * @throws IOException
   */
  public static void main (String... args) throws IOException {
    // init sendor socket, adress
    sendorSock = new DatagramSocket();
    addr = InetAddress.getByName("127.0.0.1");

    // define socket (try-with resource statement)
    try (DatagramSocket sock = new DatagramSocket(10100)) { // server: 10100      
      // declare buffer, packet
      byte[] buf = new byte[256];
      DatagramPacket pack = new DatagramPacket(buf, buf.length);

      String message = null;

      // state message
      System.out.println("Server is ready!");

      // serve loop
      while (true) {
        // define buffer, packet (for "fflush")
        buf = new byte[256];
        pack = new DatagramPacket(buf, buf.length);

        // wait until receive
        sock.receive(pack);

        // convert message
        message = new String(buf).trim();
        System.out.println(message);

        // check init command
        if (message.matches("^//port:.*")) {
          // add user
          String[] userdata = message.substring(7).split("\\|"); // get port(0) and username(1) using regular expression
          users.put(userdata[0], userdata[1]); // set user data
          sendor(">> " + userdata[1] + " 입장");
        } else if (message.matches("^//close:.*")) {
          // remove user
          String[] userdata = message.substring(8).split("\\|"); // get userdata (port: [0], username: [1])
          users.remove(userdata[0]); // using port number
          sendor(">> " + userdata[1] + " 퇴장");
        } else {
          // send message to users
          sendor(message);
        }
      }
    }
  }

  /**
   * send message to all clients
   * 
   * @param message
   */
  private static void sendor (String message) {
    // conv to binary (http GET-like)
    byte[] buf = ("users=" + String.join("|", users.values()) + "&msg=" + message + " ").getBytes();

    // to all clients
    users.keySet().forEach(key -> {
      try {
        // send packet
        sendorSock.send(new DatagramPacket(buf, buf.length, addr, Integer.parseInt(key)));
      } catch (IOException e) {
        System.out.println("Failed to send");
      }
    });
  }
}