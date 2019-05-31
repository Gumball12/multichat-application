/**
 * java multi-chat application (Client-side)
 * 
 * @author HeaJun Seo
 * @since 2019-05-31
 * @repo https://github.com/Gumball12/multichat-application
 */

// import libraries
import java.io.IOException;

import java.awt.BorderLayout;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.BindException;

import java.util.Arrays;
import java.util.function.Consumer;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

// frame class (swing)
@SuppressWarnings("serial") // serializable warning
class ClientForm extends JFrame {
  // fields
  JTextArea textArea = null;
  DefaultListModel<String> userList = null;

  /**
   * constructor
   * 
   * @param send callback
   */
  public ClientForm (Consumer<String> send) {
    // text field
    JTextField textField = new JTextField(15);
    textField.addActionListener(evt -> {
      send.accept(textField.getText()); // call callback function
      textField.setText(""); // clear text
    });

    // text area
    textArea = new JTextArea(10, 30);
    textArea.setEditable(false);

    // user lists
    userList = new DefaultListModel<String>();
    JList listPanel = new JList(userList);

    // add elements and "pack" this frame
    add(textField, BorderLayout.PAGE_END);
    add(new JScrollPane(textArea), BorderLayout.CENTER); // with scroll bar
    add(new JScrollPane(listPanel), BorderLayout.EAST); // with scroll bar

    pack();
    
    // set frame visibility
    setVisible(true);
  }

  /**
   * append message to text area
   * 
   * @param msg
   */
  public void updateMessage (String msg) {
    textArea.append(msg + "\n");
    textArea.setCaretPosition(textArea.getDocument().getLength()); // scroll to bottom
  }

  /**
   * update userlist
   * 
   * @param users username list
   */
  public void updateUser (String[] users) {
    userList.clear();
    Arrays.asList(users).forEach(user -> { userList.addElement(user); });
  }
}

// client class
public class Client {
  // fields
  private static DatagramSocket sendorSock = null;
  private static InetAddress addr = null;
  private static ClientForm clientForm = null;

  private static String username = null;
  private static String port = null;

  /**
   * main
   * 
   * @param args
   * @throws IOException
   */
  public static void main (String... args) throws IOException {
    initSendor();
    receiver();
  }

  /**
   * init sendor
   * 
   * @throws IOException
   */
  private static void initSendor() throws IOException {
    // init fields
    sendorSock = new DatagramSocket();
    addr = InetAddress.getByName("127.0.0.1"); // host ip (localhost)

    // create client frame
    clientForm = new ClientForm(text -> { // with callback
      sendor(text, false); // send to message

      // "> close": exit command
      if (text.equals("> close")) {
        System.out.println("!" + port);
        sendor("//close:" + port + "|" + username, true);
        sendorSock.close(); // close socket
        System.exit(0); // end client
      }
    });
  }

  /**
   * send message to server
   * 
   * @param message
   */
  private static void sendor (String message, boolean isCommand) {
    // convert to binary
    byte[] buf = ((isCommand == false ? username + ": " : "") + message + " ").getBytes();

    // declare packet instance
    DatagramPacket pack = null;

    // init packet (server port: 10100)
    pack = new DatagramPacket(buf, buf.length, addr, 10100);

    try {
      // send packet(message) to server
      sendorSock.send(pack);
    } catch (IOException e) {
      System.out.println("Failed to send");
    }
  }

  /**
   * receive server messages
   * 
   * @throws IOException
   */
  private static void receiver () throws IOException {
    // declare socket
    DatagramSocket sock = null;
    int startPortNumber = 10101; // client port num: 10101 ~

    // get available port
    while (sock == null) {
      try {
        sock = new DatagramSocket(startPortNumber); // get socket with "available" port
        port = String.valueOf(startPortNumber); // itos
      } catch (BindException be) { // port is already uses
        startPortNumber++;
        continue;
      }
    }

    // print port num
    System.out.println("socket opened! >> port: " + port);

    // get user name
    do {
      username = JOptionPane.showInputDialog(null, "이름을 입력해주세요");
    } while (username == null);

    // init port
    sendor("//port:" + port + "|" + username, true);

    // declare buffer, packet
    byte[] buf = null;
    DatagramPacket pack = null;

    String message = null;

    // serve loop
    do {
      // define buffer, packet (for "fflush")
      buf = new byte[256];
      pack = new DatagramPacket(buf, buf.length);

      // wait until receive
      sock.receive(pack);

      // convert message
      message = new String(buf).trim();
      System.out.println(message);

      // get datas
      String[] datas = message.split("\\&"); // [0]: users, [1]: msg
      String[] users = datas[0].substring(6).split("\\|");
      String msg = datas[1].substring(4);

      // update userlist
      clientForm.updateUser(users);

      // append message to text area
      clientForm.updateMessage(msg);
    } while (!message.equals("exit"));

    // close socket
    sock.close();
  }
}