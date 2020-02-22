/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package langamethreepointoserver;

import java.io.*;
import java.net.*;
import java.util.*;
import langamethreepointo.MovableObject;
import langamethreepointo.Player;

/**
 *
 * @author ah91099
 */
public class LanGameThreePointOServer implements Runnable {

    private static int winValue;

    ServerSocket server = null;
    Socket player1Socket, player2Socket = null;
    PlayerConnection player1Connection, player2Connection = null;
    ObjectOutputStream player1SocketOutStream;
    ObjectInputStream player1SocketInStream;
    ObjectOutputStream player2SocketOutStream;
    ObjectInputStream player2SocketInStream;
    private boolean closed;
    Player player1, player2;
    ArrayList<MovableObject> dots;
    ArrayList<MovableObject> dotsRem1;
    ArrayList<MovableObject> dotsCreate1;
    ArrayList<MovableObject> dotsRem2;
    ArrayList<MovableObject> dotsCreate2;

    private String ipAddress = "127.0.0.1";
    private int thePort = 8080;
    Thread runThread = null;
    private int threadDelay = 15;
    private int score1, score2;
    private int whoWon;

    public LanGameThreePointOServer() {
        player1 = new Player("SHIP1", 1, "SHIP1", null, "src\\sourceFiles\\redsquare.png", 100, 100, 50, 50, 800, 600);
        player2 = new Player("SHIP2", 2, "SHIP2", null, "src\\sourceFiles\\bluesquare.png", 300, 100, 50, 50, 800, 600);
        dots = new ArrayList<MovableObject>();
        dotsCreate1 = new ArrayList<MovableObject>();
        dotsCreate2 = new ArrayList<MovableObject>();
        dotsRem1 = new ArrayList<MovableObject>();
        dotsRem2 = new ArrayList<MovableObject>();

    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        LanGameThreePointOServer server = new LanGameThreePointOServer();
        System.out.println("How Many To Win");
        winValue = scan.nextInt();
        System.out.println("Start Server? (Reply yes)");
        String yes = scan.next();
        if (yes.toLowerCase().equals("yes")) {
            server.startServer();
        }

    }

    public void startServer() {
        try {
            InetAddress myself = null;
            myself = InetAddress.getLocalHost();

            System.out.println("Starting Server" + "\n"
                    + "Local hostname : " + myself.getHostName() + "\n"
                    + "Local IP Address : " + "" + myself.getHostAddress() + "\n");

            if (runThread != null) {
                if (runThread.isAlive()) {
                    return;
                }
            }
            if (runThread == null) {
                runThread = new Thread(this);
            }
            if (!runThread.isAlive()) {
                runThread.start();
            }

        } catch (Exception eeeee) {
            System.out.println("error in startSever: " + eeeee);
        }
    }

    public String buildSendStringOfBeginningObjects() {
        String sendString = "";

        sendString = "CREATE-PLAYER1 "
                + 1 + " "
                + 100 + " "
                + 100 + " "
                + player1.getWidth() + " "
                + player1.getHeight() + " "
                + "& ";
        sendString += "CREATE-PLAYER2 "
                + 2 + " "
                + 300 + " "
                + 100 + " "
                + player2.getWidth() + " "
                + player2.getHeight() + " "
                + "& ";


        return sendString;
    }

    public void run() {
        try {
            server = new ServerSocket(8080, 100);
        } catch (Exception noServerSocket) {
            System.out.println("\nUnable to create ServerSocket.");

            try {
                Thread.currentThread().sleep(threadDelay);
            } catch (Exception updateException) {
            }
            return;
        }

        while (true) {
            String movableObjectsToSend = "";

            closed = false;

            try {
                
                dots.clear();
                score1 = 0;
                score2 = 0;
                whoWon = 0;

                System.out.println("\nWaiting for client Player 1.");

                Thread.currentThread().sleep(threadDelay);

                // wait for a client to a connect that wants to play LanGame
                player1Socket = server.accept();

                // update the screen with the connection info
                System.out.println("\nClient Player 1 connected: ");
                System.out.println(player1Socket.getInetAddress().toString());

                // get the input and output streams
                player1SocketOutStream = new ObjectOutputStream(player1Socket.getOutputStream());
                player1SocketOutStream.flush();

                player1SocketInStream = new ObjectInputStream(player1Socket.getInputStream());

                System.out.println("\nPlayer 1 is ready to play.");

                Thread.currentThread().sleep(threadDelay);

                try {
                    // this is just the START message saying I want to play LanGame
                    String message = (String) player1SocketInStream.readObject();

                    player1SocketOutStream.writeObject("PLAYER1");
                    player1SocketOutStream.flush();

                    // now create and send ALL the MovableObjects to the client
                    // call a method to create the screen objects and return a String with
                    // all objects in proper format
                    movableObjectsToSend = buildSendStringOfBeginningObjects();
                    player1SocketOutStream.writeObject(movableObjectsToSend);
                    player1SocketOutStream.flush();

                    System.out.println("\nSent to Player 1 "
                            + player1Socket.getInetAddress().toString() + ":"
                            + movableObjectsToSend + "\n");

                    //System.out.println("\nMessage sent to Player 1:" + movableObjectsToSend + "\n");
                } catch (Exception errorX) {
                    System.out.println("\nUnable to get message from Player 1");
                }

                System.out.println("\nWaiting for client Player 2.");

                Thread.currentThread().sleep(threadDelay);

                // wait for a client to a connect that wants to play LanGame
                player2Socket = server.accept();

                // update the screen with the connection info
                System.out.println("\nClient Player 2 connected: ");
                System.out.println(
                        player2Socket.getInetAddress().toString());

                // get the input and output streams
                player2SocketOutStream = new ObjectOutputStream(player2Socket.getOutputStream());
                player2SocketOutStream.flush();

                player2SocketInStream = new ObjectInputStream(player2Socket.getInputStream());

                System.out.println("\nPlayer 2 is ready to play.");

                Thread.currentThread().sleep(threadDelay);

                try {
                    // this is just the START message saying I want to play LanGame
                    String message = (String) player2SocketInStream.readObject();

                    player2SocketOutStream.writeObject("PLAYER2");
                    player2SocketOutStream.flush();

                    // now create and send ALL the MovableObjects to the client
                    // call a method to create the screen objects and return a String with
                    // all objects in proper format
                    movableObjectsToSend = buildSendStringOfBeginningObjects();
                    player2SocketOutStream.writeObject(movableObjectsToSend);
                    player2SocketOutStream.flush();

                    System.out.println("\nSent to Player 2 "
                            + player2Socket.getInetAddress().toString() + ":"
                            + movableObjectsToSend + "\n");
                    //System.out.println("\nMessage sent to Player 2:" + movableObjectsToSend + "\n");

                } catch (Exception errorX) {
                    System.out.println("\nUnable to get message from Player 2");
                }

                // now we should build a player1Socket and player2Socket Object
                // these objects will continuously send and receive info from the client machine
                // we can't wait for one client before getting to the next client
                // thus player1Socket and player2Socket will have to be on their own threads
                player1SocketOutStream.writeObject("START-GAME");
                player1SocketOutStream.flush();
                player2SocketOutStream.writeObject("START-GAME");
                player2SocketOutStream.flush();

                // create connection Objects for both players
                // their threads will automatically start
                try {
                    player1Connection = new PlayerConnection(1, player1Socket, player1SocketInStream, player1SocketOutStream);
                    player2Connection = new PlayerConnection(2, player2Socket, player2SocketInStream, player2SocketOutStream);
                } catch (Exception error) {
                    System.out.println("error creating playerConnections " + error);
                }

                while (!closed) {

                    int rand = (int) (Math.random() * 200);
                    if (rand == 5) {
                        int x = (int) ((Math.random() * 700) + 50);
                        int y = (int) ((Math.random() * 500) + 50);
                        MovableObject dot = new MovableObject("", dots.size(), "dot", null, "dot.png", x, y, 50, 50);
                        dots.add(dot);
                        dotsCreate1.add(dot);
                        dotsCreate2.add(dot);
                    }

                    for (int i = 0; i < dots.size(); i++) {
                        MovableObject dot = dots.get(i);
                        if (player1.intersects(dot) && dot.getAlive() == 1) {
                            dotsRem1.add(dot);
                            dotsRem2.add(dot);
                            dots.get(i).setAlive(0);
                            score1+=5;
                            if(score2 >= winValue){
                                whoWon = 1;
                            }
                        }
                        if (player2.intersects(dot) && dot.getAlive() == 1) {
                            dotsRem1.add(dot);
                            dotsRem2.add(dot);
                            dots.get(i).setAlive(0);
                            score2+=5;
                            if(score2 >= winValue){
                               whoWon = 2;
                            }
                            
                        }
                        
                        
                    }

                    Thread.currentThread().sleep(threadDelay);
                    if (player1Socket == null) {
                        closed = true;
                    } else if (player2Socket == null) {
                        closed = true;
                    } else if (player1Connection == null) {
                        closed = true;
                    } else if (player2Connection == null) {
                        closed = true;
                    } else if (player1Connection.connection == null) {
                        closed = true;
                    } else if (player2Connection.connection == null) {
                        closed = true;
                    } else if (!player1Connection.connection.isConnected()) {
                        closed = true;
                    } else if (!player2Connection.connection.isConnected()) {
                        closed = true;
                    }

                }

                // shut down
                if (player1Socket != null) {
                    player1Socket.close();
                    player1Socket = null;
                }

                if (player2Socket != null) {
                    player2Socket.close();
                    player2Socket = null;
                }

                if (player1Connection != null) {
                    player1SocketOutStream.close();
                    player1SocketInStream.close();
                    player1Connection.closeThread();
                }

                if (player2Connection != null) {
                    player2SocketOutStream.close();
                    player2SocketInStream.close();
                    player2Connection.closeThread();
                }

                System.out.println("\nClosed connection for clients.\n");

                Thread.currentThread().sleep(threadDelay);

                //server.close();
                //server = null;
                closed = true;

            } catch (Exception error) {
                System.out.println("\nUnable to run server " + error);
                closed = true;
            }

        } // end of while (true)

    } // end of method run()

    public class PlayerConnection implements Runnable {

        Thread runThread = null;
        int threadDelay = 15;

        // internet variables
        Socket connection;
        InetAddress myself = null;
        ObjectOutputStream outStream;
        ObjectInputStream inStream;

        int player; // 1 or 2

        public PlayerConnection(int player, Socket connection, ObjectInputStream inStream, ObjectOutputStream outStream) {
            this.player = player;
            this.connection = connection;
            this.inStream = inStream;
            this.outStream = outStream;
            runThread = new Thread(this);
            runThread.start();
        }

        public void closeThread() {
            connection = null;
            runThread = null;
            inStream = null;
            outStream = null;
        }

        public void processCommands(String commands) {
            if (commands.equals("")) {
                return;
            }

            if (commands.equals("SHUTDOWN")) {
                closed = true;
                return;
            }

            // process commands and update global variables
            Scanner scan = new Scanner(commands);

            // COMMAND-OBJECT # x y w h &
            // all COMMAND statements must end with an &
            // MOVE-PLAYER1 # x y w h etc.
            // MOVE-PLAYER2 # x y w h etc.
            while (scan.hasNext()) {
                String command = scan.next();
                //System.out.println("command=" + command);
                int id = scan.nextInt();
                int x = scan.nextInt();
                int y = scan.nextInt();
                int width = scan.nextInt();
                int height = scan.nextInt();
                String endMarker = scan.next();    // &
                // more to come

                //String endOfCommand = scan.next();
                if (command.equals("MOVE-PLAYER1")) {
                    if (player1 != null) {
                        player1.setXY(x, y);
                    }
                } else if (command.equals("MOVE-PLAYER2")) {
                    if (player2 != null) 
                        player2.setXY(x, y);
                }

            } // end of while (scan.hasNext())

        } // end of public void processCommands(String commands)

        public String buildSendStringOfAllObjects() {
            String sendString = "";

            // only send the other player's movement to the client
            // the client is responsible for moving their own player, 
            // and we don't want to interfere with that
            if (player == 2 && player1 != null) {
                sendString += "MOVE-PLAYER1 "
                        + 1 + " "
                        + player1.getX() + " "
                        + player1.getY() + " "
                        + player1.getWidth() + " "
                        + player1.getHeight() + " "
                        + "& ";
            }
            if (player == 1 && player2 != null) {
                sendString += "MOVE-PLAYER2 "
                        + 2 + " "
                        + player2.getX() + " "
                        + player2.getY() + " "
                        + player2.getWidth() + " "
                        + player2.getHeight() + " "
                        + "& ";
            }
                sendString += "UPDATE-SCORE "
                        + 0 + " "
                        + score1 + " " 
                        + score2 + " " 
                        + 0 + " " 
                        + 0 + " " 
                        + "& ";
                
                sendString += "WINNER "
                        + whoWon + " "
                        + 0 + " " 
                        + 0 + " " 
                        + 0 + " " 
                        + 0 + " " 
                        + "& ";
                
            
            if (player == 1) {
                if (dotsCreate1.size() > 0) {
                    for (int i = 0; i < dotsCreate1.size();i++) {
                        MovableObject dot = dotsCreate1.get(i);
                        sendString += "CREATE-DOT "
                                + dot.getId() + " "
                                + dot.getX() + " "
                                + dot.getY() + " "
                                + dot.getWidth() + " "
                                + dot.getHeight() + " "
                                + "& ";
                    }
                    dotsCreate1.clear();
                }
                if (dotsRem1.size() > 0) {
                    for (int i = 0; i < dotsRem1.size();i++) {
                        MovableObject dot = dotsRem1.get(i);
                        sendString += "REMOVE-DOT "
                                + dot.getId() + " "
                                + dot.getX() + " "
                                + dot.getY() + " "
                                + dot.getWidth() + " "
                                + dot.getHeight() + " "
                                + "& ";
                    }
                    dotsRem1.clear();
                }
            }
            if (player == 2) {
                if (dotsCreate2.size() > 0) {
                    for (int i = 0; i < dotsCreate2.size();i++) {
                        MovableObject dot = dotsCreate2.get(i);
                        sendString += "CREATE-DOT "
                                + dot.getId() + " "
                                + dot.getX() + " "
                                + dot.getY() + " "
                                + dot.getWidth() + " "
                                + dot.getHeight() + " "
                                + "& ";
                    }
                    dotsCreate2.clear();
                }
                if (dotsRem2.size() > 0) {
                    for (int i = 0; i < dotsRem2.size();i++) {
                        MovableObject dot = dotsRem2.get(i);
                        sendString += "REMOVE-DOT "
                                + dot.getId() + " "
                                + dot.getX() + " "
                                + dot.getY() + " "
                                + dot.getWidth() + " "
                                + dot.getHeight() + " "
                                + "& ";
                    }
                    dotsRem2.clear();
                }
            }

            return sendString;
        }

        public void run() {

            while (!closed) {
                try {
                    // read from the client
                    //System.out.println("reading " + player);
                    String inMessage = (String) inStream.readObject();

                    //System.out.println("processing " + player + "with inMessage = " + inMessage);
                    // process the commands
                    processCommands(inMessage);

                    //System.out.println("sleeping " + player);
                    // wait for other threads to do their thing
                    Thread.currentThread().sleep(threadDelay);
                    

                    // write stuff back to the client
                    if (!closed) {
                        //System.out.println("writing " + player);
                        String out = buildSendStringOfAllObjects();
                        outStream.writeObject(out);
                        outStream.flush();
                    }
                    if(whoWon != 0){
                        Thread.currentThread().sleep(10000);
                        closed = true;
                    }

                    
                } // end of try
                catch (Exception e) {
                    if (!e.toString().startsWith("java.net.SocketException:")) {
                        System.out.println("error in playerconnection of player " + player + " : " + e);
                    }

                    connection = null;
                } // end of catch
            } // end of while
        } // end of method run()  	

    }

}
