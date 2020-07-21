// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract superclass in order
 * to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer {
  // Instance variables **********************************************

  /**
   * The interface type variable. It allows the implementation of the display
   * method in the server.
   */
  ChatIF serverUI;

  // Constructors ****************************************************

  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port, ChatIF serverUI) {
    super(port);
    this.serverUI = serverUI;

    try {
      listen();
    } catch (IOException e) {
      serverUI.display("Error: Can't setup connection!" + " Terminating server.");
      System.exit(1);
    }
  }

  // Instance methods ************************************************

  /**
   * This method handles any messages received from the client.
   *
   * @param msg    The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    if (String.valueOf(msg).startsWith("#login ")) {
      if (client.getInfo("user id") != null) {
        try {
          client.sendToClient("User ID already set");
        } catch (IOException e) {
        }
      } else {
        client.setInfo("user id", String.valueOf(msg).substring(7));
      }
    } else {
      if (client.getInfo("user id") != null) {
        serverUI.display("Message received: " + msg + " from " + client);
        sendToAllClients(client.getInfo("user id") + ": " + msg);
      } else {
        try {
          client.sendToClient("User ID not set");
          client.close();
        } catch (IOException e) {
        }
      }
    }
  }

  /**
   * This method handles all commands coming from the UI
   *
   * @param message The command from the UI.
   */
  public void handleCommandFromServerUI(String message) {
    String[] command = message.split(" ");

    switch (command[0]) {
      case "#quit":
        try {
          close();
        } catch (IOException e) {
        } finally {
          System.exit(0);
        }
        break;
      case "#stop":
        stopListening();
        break;
      case "#close":
        try {
          close();
        } catch (IOException e) {
        }
        break;
      case "#setport":
        if (isListening()) {
          serverUI.display("Unable to set port.");
        } else if (getNumberOfClients() > 0) {
          serverUI.display("Unable to set port.");
        } else {
          setPort(Integer.parseInt(command[1]));
        }
        break;
      case "#start":
        if (isListening()) {
          serverUI.display("Already listening.");
        } else {
          try {
            listen();
          } catch (Exception ex) {
            serverUI.display("ERROR - Could not listen for clients!");
          }
        }
        break;
      case "#getport":
        serverUI.display(String.valueOf(getPort()));
        break;
      default:
        serverUI.display("Please enter a valid command.");
        break;
    }
  }

  /**
   * This method handles all data coming from the UI
   *
   * @param message The message from the UI.
   */
  public void handleMessageFromServerUI(String message) {
    sendToAllClients(message);
  }

  /**
   * This method overrides the one in the superclass. Called when the server
   * starts listening for connections.
   */
  protected void serverStarted() {
    serverUI.display("Server listening for connections on port " + getPort());
  }

  /**
   * This method overrides the one in the superclass. Called when the server stops
   * listening for connections.
   */
  protected void serverStopped() {
    serverUI.display("Server has stopped listening for connections.");
  }

  /**
   * Hook method called each time a new client connection is accepted. The default
   * implementation does nothing.
   * 
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
    serverUI.display("Client " + client.getId() + " connected to the server.");
  }

  /**
   * Hook method called each time a client disconnects. The default implementation
   * does nothing. The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    serverUI.display("Client " + client.getId() + " disconnected from the server.");
  }

  /**
   * Hook method called each time an exception is thrown in a ConnectionToClient
   * thread. The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client    the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
    serverUI.display("Lost connection to client " + client.getId());
  }
}
// End of EchoServer class
