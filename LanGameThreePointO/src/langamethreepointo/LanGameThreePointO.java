package langamethreepointo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class LanGameThreePointO extends JFrame {

    Thread runThread = null;
    int threadDelay = 15;

    Socket connection;
    InetAddress myself = null;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    String whoAmI;
    String whoAreYou;
    private int score1, score2;

    private boolean gameOver;
    private boolean gameIsPlaying;
    boolean stayInWaitLoop;// stay in game loop waiting for an update of the board
    String whoWon = "";

    private JButton exitButton;

    private BufferedImage back;

    private String ipAddress;
    private int thePort;

    // define a mainPanel for components
    JPanel mainPanel;

    // define a menu bar variable to hold JMenus
    JMenuBar menuBar;

    // define some JMenus and their JMenuItems
    // define a JMenu called fileMenu and add menuItems
    JMenu fileMenu;
    JMenu serverMenu;

    JMenuItem exitMenuItem;
    JMenuItem debugMenuItem;
    
    JMenuItem portMenuItem;
    JMenuItem ipMenuItem;
    JMenuItem joinServer;
    JMenuItem makeServer;

    // define JPanels for a BorderLayout
    JPanel northPanel;
    SouthPanel southPanel;
    JPanel westPanel;
    JPanel eastPanel;
    DrawPanel centerPanel;
    
    String debugString = "";

    int id = 0;  // this is created on the server
    Player playerMe, playerYou;
    ArrayList<MovableObject> dots;

    public void initialize() {

        gameOver = true;
        back = null;

        mainPanel = new JPanel();
        
        dots = new ArrayList<MovableObject>();

        try {
            myself = InetAddress.getLocalHost();

            ipAddress = myself.getHostAddress();
            System.out.println(ipAddress + "in here");
        } catch (Exception e) {
            ipAddress = "10.11.14.59";
        }

        thePort = 8080;
        
        // ***** assignments for menu variables *****
        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        exitMenuItem = new JMenuItem("Exit");
        debugMenuItem = new JMenuItem("Gebug Commands");

        serverMenu = new JMenu("Server");
        ipMenuItem = new JMenuItem("IP - " + ipAddress);
        portMenuItem = new JMenuItem("Port - " + thePort);
        joinServer = new JMenuItem("Join Server");
        makeServer = new JMenuItem("Make Server");


        exitButton = new JButton("Exit");


        // add menuItems to the fileMenu
        fileMenu.add(exitMenuItem);
        
        serverMenu.add(debugMenuItem);
        serverMenu.add(portMenuItem);
        serverMenu.add(ipMenuItem);
        serverMenu.add(joinServer);
        serverMenu.add(makeServer);

        whoAmI = "";
        whoAreYou = "";

        // add menuItems to the editMenu

        // add menus to the menuBar
        menuBar.add(serverMenu);

        // attach the JMenuBar to the Window
        setJMenuBar(menuBar);
        // ***** create JPanels for a BorderLayout *****
        northPanel = new JPanel();
        southPanel = new SouthPanel();
        southPanel.setListeners();
        westPanel = new JPanel();
        eastPanel = new JPanel();
        centerPanel = new DrawPanel();

        mainPanel.setLayout(new BorderLayout());
        centerPanel.setLayout(new GridLayout(2, 2, 20, 20));

        centerPanel.setBackground(Color.BLACK);
        northPanel.setBackground(new Color(100, 100, 100));
        southPanel.setBackground(new Color(100, 100, 100));
        westPanel.setBackground(new Color(100, 100, 100));
        eastPanel.setBackground(new Color(100, 100, 100));


        southPanel.add(exitButton);

        mainPanel.add(northPanel, BorderLayout.NORTH);
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.add(eastPanel, BorderLayout.EAST);
        mainPanel.add(westPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        southPanel.setFocusable(true);
        southPanel.requestFocus();

        centerPanel.repaint();
        // make the mainPanel be the main content area and show it
        setContentPane(mainPanel);

        setVisible(true);  // always show the screen last

        southPanel.setFocusable(true);
        southPanel.requestFocus();
    }//end of initialize

    public LanGameThreePointO() {

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initialize();
    }//end of Constructor

    public String getPlayerString() {
        String output;
        if (playerMe != null) {
            output = "MOVE-PLAYER" + playerMe.getId()
                    + " " + playerMe.getId()
                    + " " + playerMe.getX()
                    + " " + playerMe.getY()
                    + " " + playerMe.getWidth()
                    + " " + playerMe.getHeight()
                    + " & ";
        } else {
            output = "";
        }
        output+=debugString;
        debugString = "";
        return output;
    }

    public void updateObjects(String input) {

        if (input.equals("")) {
            return;
        }

        Scanner scan = new Scanner(input);
        while (scan.hasNext()) {
            // "CREATE-PLAYER1 # x y w h &" or "MOVE-PLAYER1 # x y w h &" etc.
            String command = scan.next();
            int id = scan.nextInt();
            int x = scan.nextInt();
            int y = scan.nextInt();
            int width = scan.nextInt();
            int height = scan.nextInt();
            String endMarker = scan.next();    // &

            if (command.equals("CREATE-PLAYER1")) {

                if (whoAmI.equals("PLAYER1")) {
                    playerMe = new Player("PLAYER1", id, "PLAYER1", centerPanel, "/sourceFiles/redSquare.png", x, y, width, height, centerPanel.getHeight(), centerPanel.getWidth());
                    playerMe.setSpeedXY(3, 3);
                } else {
                    playerYou = new Player("PLAYER1", id, "PLAYER1", centerPanel, "/sourceFiles/redSquare.png", x, y, width, height, centerPanel.getHeight(), centerPanel.getWidth());
                    playerYou.setSpeedXY(3, 3);
                }

            } else if (command.equals("CREATE-PLAYER2")) {

                if (whoAmI.equals("PLAYER2")) {
                    playerMe = new Player("PLAYER2", id, "PLAYER2", centerPanel, "/sourceFiles/blueSquare.png", x, y, width, height, centerPanel.getHeight(), centerPanel.getWidth());
                    playerMe.setSpeedXY(3, 3);
                } else {
                    playerYou = new Player("PLAYER2", id, "PLAYER2", centerPanel, "/sourceFiles/blueSquare.png", x, y, width, height, centerPanel.getHeight(), centerPanel.getWidth());
                    playerYou.setSpeedXY(3, 3);
                }
            } else if (command.equals("MOVE-PLAYER1") && whoAmI.equals("PLAYER2")) {
                playerYou.setX(x);
                playerYou.setY(y);
                playerYou.setWidth(width);
                playerYou.setHeight(height);
                playerYou.setAlive(1);
            } else if (command.equals("MOVE-PLAYER2") && whoAmI.equals("PLAYER1")) {
                playerYou.setX(x);
                playerYou.setY(y);
                playerYou.setWidth(width);
                playerYou.setHeight(height);
                playerYou.setAlive(1);
            } else if (command.equals("CREATE-DOT")) {
                MovableObject dot = new MovableObject("PLAYER", id, "dot", centerPanel, "/sourceFiles/dot.png", x, y, width, height);
                dots.add(dot);
            } 
            else if(command.equals("REMOVE-DOT")){
                dots.get(id).setAlive(0);
            }
            else if (command.equals("UPDATE-SCORE")) {
                score1 = x;
                score2 = y;
            }
            
            else if (command.equals("WINNER")){
                if(id!=0){
                    setTitle("Player" + id + " Wins");
                    try {
                       wait(5000); 
                    } catch (Exception e) {
                        
                    }
                    
                
                }
            }
            
            
            

        }
    }

    public static void main(String[] args) {
        LanGameThreePointO client = new LanGameThreePointO();
    }

    class SouthPanel extends JPanel implements KeyListener, ActionListener, Runnable {
        // start of actionPerformed (ActionListener interface)
        // handle button clicks here

        public SouthPanel() {
            // allow buttons to listen for clicks
            super();
        }

        public void setListeners() {
            exitMenuItem.addActionListener(this);
            debugMenuItem.addActionListener(this);
            
            ipMenuItem.addActionListener(this);
            portMenuItem.addActionListener(this);
            makeServer.addActionListener(this);
            joinServer.addActionListener(this);
            exitButton.addActionListener(this);
            addKeyListener(this);
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            Object source = e.getSource();
            if (source == exitMenuItem) {
                gameOver = true;
                gameIsPlaying = false;
                if (runThread != null) {
                    runThread.stop();
                    // runThread.destroy();
                    runThread = null;
                }
                System.exit(0);
            } // end of if
               
            else if(source == debugMenuItem){
                debugString = JOptionPane.showInputDialog(this, "Command", "");
                System.out.println(debugString);
            }
            

            
            else if (source == ipMenuItem) {
                String tempIPAddress = JOptionPane.showInputDialog(this, "IP Address", ipAddress);
                if (tempIPAddress != null) {
                    ipAddress = tempIPAddress;
                    System.out.println(ipAddress);
                    ipMenuItem.setText("IP - " + ipAddress);
                    portMenuItem.setText("Port - " + thePort);
                }else {
                    System.out.println("NULL");
                }
            } // end of if
            else if (source == portMenuItem) {
                String tempPort = JOptionPane.showInputDialog(this, "Port", "" + thePort);
                if (tempPort != null) {
                    tempPort = tempPort.trim();
                    thePort = Integer.parseInt(tempPort);
                    ipMenuItem.setText("IP - " + ipAddress);
                    portMenuItem.setText("Port - " + thePort);
                }//end of if
            } else if (source == exitButton) {
                int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Exit?", JOptionPane.YES_NO_OPTION);
                if (option == 0) {
                    System.exit(0);
                }

            } else if (source == joinServer) {
                startGame();
            }

        }//end of actionPerformed

        public void joinServer() {
            try {

                myself = InetAddress.getLocalHost();

                System.out.println(" in startClientServer() - ready to get a connection");
                // get a connection
                try {
                    outStream = null;
                    inStream = null;
                    connection = null;
                    System.out.println("ip = " + ipAddress);
                    connection = new Socket(InetAddress.getByName(ipAddress), thePort);
                } catch (Exception connectError) {
                    JOptionPane.showMessageDialog(this, "Unable to connect to " + ipAddress);
                    connection = null;
                    dots.clear();
                    back = null;
                    LanGameThreePointO.this.dispose();
                    new LanGameThreePointO();
                }

                System.out.println("Connected to server");

                outStream = new ObjectOutputStream(connection.getOutputStream());
                outStream.flush();
                inStream = new ObjectInputStream(connection.getInputStream());

                String command = "START";

                outStream.writeObject(command);
                outStream.flush();
                System.out.println("Waiting for server");
                // CCCCC RECEIVE RECEIVE RECEIVE RECEIVE RECEIVE   player1 or player2
                String response = (String) inStream.readObject();

                System.out.println("I am " + response);

                // get all game variables set
                whoAmI = response;
                whoWon = "";

                if (whoAmI.equals("PLAYER1")) {
                    whoAreYou = "PLAYER2";
                } else if (whoAmI.equals("PLAYER2")) {
                    whoAreYou = "PLAYER1";
                } else {
                    // major problem
                    System.out.println("major problem");
                }

                System.out.println("Waiting for server");
                String objects = (String) inStream.readObject();

                System.out.println("From server:" + "\n" + objects);

                updateObjects(objects);
                repaint();

                Thread.currentThread().sleep(threadDelay);

                System.out.println("Waiting for server to send START-GAME command");
                // CCCCC RECEIVE RECEIVE RECEIVE RECEIVE RECEIVE
                String ready = (String) inStream.readObject();

                System.out.println("From server:" + "\n" + ready);

                gameIsPlaying = true;
            } catch (Exception error) {
                // update the screen with an error message
                error.printStackTrace();

            }
        }

        private void quitGame() {
            gameOver = true;
            gameIsPlaying = false;
            if (runThread != null) {
                runThread.stop();
                runThread = null;
            }
        }

        private void startGame() {

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

            gameOver = false;
            System.out.println("Starting Game Thread");

        }
        // thread to delay for the runButton
        // do it all here so we have control of the buttons

        public void run() {
            System.out.println("In game thread");

            joinServer();

            gameOver = false;
            gameIsPlaying = true;

            try {
                while (gameIsPlaying) {
                    // this will redraw everything on the centerPanel
                    if (!gameOver) {
                        centerPanel.repaint();
                    }

                    setFocusable(true);
                    requestFocus();

                    Thread.currentThread().sleep(threadDelay);

                    if (gameIsPlaying) {
                        // CCCCC SEND SEND SEND SEND SEND SEND SEND SEND
                        // send back your player location
                        String outCommand = getPlayerString();

                        //outCommand = "";
                        outStream.writeObject(outCommand);
                        outStream.flush();

                        // give other threads a chance
                        Thread.currentThread().sleep(threadDelay);

                        // CCCCC RECEIVE RECEIVE RECEIVE RECEIVE RECEIVE
                        // get updated movements, deletes, etc.
                        String objects = (String) inStream.readObject();

                        // make all changes in objects
                        updateObjects(objects);
                        if(score1 > score2){
                            setTitle("PLAYER 1 IS IN THE LEAD!");
                        }
                        else if(score2 > score1){
                            setTitle("PLAYER 2 IS IN THE LEAD!");
                        }
                        else {
                            setTitle("CURRENTLY TIED!");
                        }
                            
                            

                        // give other threads a chance
                        Thread.currentThread().sleep(threadDelay);
                    }

                }
   
                
                
            } catch (Exception e) {
                if (e.toString().contains("Software caused connection abort") || e.toString().contains("java.io.EOFException")) {
                    System.out.println("They shut it down");
                    dots.clear();
                    back = null;
                    LanGameThreePointO.this.dispose();
                    new LanGameThreePointO();
                    //System.exit(0);
                } else {
                    System.out.println("ERROR IN RUN!");
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }  // end of keyTyped(KeyEvent e)

        @Override
        public void keyPressed(KeyEvent e) {
            //System.out.println("pressed : " + e.getKeyChar());
            if (!gameIsPlaying) {
                return;
            }
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
                playerMe.setVerticalDir(1);

            } else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
                playerMe.setVerticalDir(-1);

            } else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                playerMe.setHorizntalDir(-1);

            } else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                playerMe.setHorizntalDir(1);

            }
            playerMe.move();
        }  // end of keyPressed(KeyEvent e)

        // start of keyReleased (KeyListener interface)
        public void keyReleased(KeyEvent e) {

            if (!gameIsPlaying) {
                return;
            }
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
                playerMe.setVerticalDir(0);

            }
            if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
                playerMe.setVerticalDir(0);

            }
            if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
                playerMe.setHorizntalDir(0);

            }
            if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
                playerMe.setHorizntalDir(0);

            }
            playerMe.move();
        }//end of KeyReleased

    } // end of centerPanel class

    class DrawPanel extends JPanel {

        public DrawPanel() {
            super();
        }

        public void update(Graphics g) {
            paintComponent(g);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent((Graphics2D) g);
            Graphics2D g2 = (Graphics2D) g;
            if (back == null) {
                back = (BufferedImage) (createImage(getWidth(), getHeight()));
            }
            g2.drawImage(back, null, 0, 0);
            Graphics gMemory = back.createGraphics();

            gMemory.setColor(Color.BLACK);
            gMemory.fillRect(0, 0, getWidth(), getHeight());

            
            
           
            if (playerYou != null && playerYou.getId() == 1) {
                playerYou.draw(gMemory);
                gMemory.setColor(Color.GREEN);
                drawCenteredString(gMemory, score1 + "", playerYou.getX() + playerYou.getWidth()/2, playerYou.getY() + playerYou.getHeight()/2, gMemory.getFont());
            }            
            if (playerYou != null && playerYou.getId() == 2) {
                playerYou.draw(gMemory);
                gMemory.setColor(Color.GREEN);
                drawCenteredString(gMemory, score2 + "", playerYou.getX() + playerYou.getWidth()/2, playerYou.getY() + playerYou.getHeight()/2, gMemory.getFont());
            }   
            if (playerMe != null && playerMe.getId() == 1) {
                playerMe.draw(gMemory);
                gMemory.setColor(Color.GREEN);
                drawCenteredString(gMemory, score1 + "", playerMe.getX() + playerMe.getWidth()/2, playerMe.getY() + playerMe.getHeight()/2, gMemory.getFont());
            }            
            if (playerMe != null && playerMe.getId() == 2) {
                playerMe.draw(gMemory);
                gMemory.setColor(Color.GREEN);
                drawCenteredString(gMemory, score2 + "", playerMe.getX() + playerMe.getWidth()/2, playerMe.getY() + playerMe.getHeight()/2, gMemory.getFont());
            }
            
            if(dots.size()>0){
                for(MovableObject dot : dots){
                    if(dot.getAlive() == 1)
                        dot.draw(gMemory);
                }
                    
            }

            g2.drawImage(back, null, 0, 0);
        }
        
        
        
                
        
        public void drawCenteredString(Graphics g, String str, int x, int y, Font font){
            FontMetrics m = g.getFontMetrics();
            int nX,nY;
            nX = x - (m.stringWidth(str) / 2);
            nY = y - (m.getHeight() / 2);
            
            g.drawString(str, nX, nY+10);
        }
            

    }
}
