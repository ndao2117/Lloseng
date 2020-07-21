// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient {
  // Instance variables **********************************************

  /**
   * The user ID.
   */
  String userID;

  /**
   * The interface type variable. It allows the implementation of the display
   * method in the client.
   */
  ChatIF clientUI;

  // Constructors ****************************************************

  /**
   * Constructs an instance of the chat client.
   *
   * @param host     The server to connect to.
   * @param port     The port number to connect on.
   * @param clientUI The interface type variable.
   */

  public ChatClient(String userID, String host, int port, ChatIF clientUI) throws IOException {
    super(host, port); // Call the superclass constructor
    this.userID = userID;
    this.clientUI = clientUI;
    openConnection();
    sendToServer("#login " + userID);
  }

  // Instance methods ************************************************

  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI.
   */
  public void handleMessageFromClientUI(String message) {
    try {
      sendToServer(message);
    } catch (IOException e) {
      clientUI.display("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  /**
   * This method handles all commands coming from the UI
   *
   * @param message The message from the UI.
   */
  public void handleCommandFromClientUI(String message) {
    String[] command = message.split(" ");

    switch (command[0]) {
      case "#quit":
        quit();
        break;
      case "#logoff":
        try {
          closeConnection();
        } catch (IOException e) {
        }
        break;
      case "#sethost":
        if (isConnected()) {
          clientUI.display("Unable to set host.");
        } else {
          setHost(command[1]);
        }
        break;
      case "#setport":
        if (isConnected()) {
          clientUI.display("Unable to set port.");
        } else {
          setPort(Integer.parseInt(command[1]));
        }
        break;
      case "#login":
        try {
          openConnection();
        } catch (IOException e) {
        }
        break;
      case "#gethost":
        clientUI.display(this.getHost());
        break;
      case "#getport":
        clientUI.display(Integer.toString(this.getPort()));
        break;
      default:
        clientUI.display("Please enter a valid command.");
        break;
    }
  }

  /**
   * Hook method called after the connection has been closed. The default
   * implementation does nothing. The method may be overriden by subclasses to
   * perform special processing such as cleaning up and terminating, or attempting
   * to reconnect.
   */
  protected void connectionClosed() {
    clientUI.display("Disconnected.");
  }

  /**
   * Hook method called after a connection has been established. The default
   * implementation does nothing. It may be overridden by subclasses to do
   * anything they wish.
   */
  protected void connectionEstablished() {
    clientUI.display("Connected.");
  }

  /**
   * Hook method called each time an exception is thrown by the client's thread
   * that is waiting for messages from the server. The method may be overridden by
   * subclasses.
   * 
   * @param exception the exception raised.
   */
  protected void connectionException(Exception exception) {
    clientUI.display("Lost connection to the server.");
  }

  /**
   * This method terminates the client.
   */
  public void quit() {
    try {
      closeConnection();
    } catch (IOException e) {
    }
    System.exit(0);
  }
}
// End of ChatClient class
