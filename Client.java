// import libraries
import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.BindException;

import java.util.HashMap;
import java.util.Scanner;

// client class
public class Client {
  // main method
  public static void main (String... args) throws IOException {
    // call methods
    sendor();
    // receiver();
  }

  private static void sendor () throws IOException {
    // init socket, address
    DatagramSocket sock = new DatagramSocket();
    InetAddress addr = InetAddress.getByName("127.0.0.1");

    // init scanner, message object
    Scanner sc = new Scanner(System.in);
    String message = null;

    // send loop
    do {
      // get message
      message = sc.nextLine();
      
      // convert to binary
      byte[] buf = (message + " ").getBytes();
      
      // init packet (server port: 10100)
      DatagramPacket pack = new DatagramPacket(buf, buf.length, addr, 10100);

      // send packet
      sock.send(pack);
    } while (!message.equals("exit"));

    sc.close();
    sock.close();
  }

  private static void receiver () {
    // declare socket
    DatagramSocket sock = null;
    int startPortNumber = 10101; // client port num: 10101 ~

    // get available port
    while (sock == null) {
      try {
        sock = new DatagramSocket(startPortNumber);
      } catch (BindException be) {
        startPortNumber++;
        continue;
      }
    }

    // print port num
    Sysmte.out.println("socket opened! >> port: " + startPortNumber);

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