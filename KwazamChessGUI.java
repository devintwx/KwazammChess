import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * KwazamChessGUI class - GUI implementation for the Kwazam Chess game.
 * This class manages the main GUI setup, including menus, panels, and game flow.
 * @author Joey Tan Rou Yi
 */
public class KwazamChessGUI extends JFrame {

    // GUI panels
    public static final JPanel panelInTheCenter = new JPanel(new GridLayout(8, 7));
    private static final JPanel bottomPanel = new JPanel(new FlowLayout());
    private static final JPanel topPanel = new JPanel(new BorderLayout());

    // Menu components
    private static final JMenuBar mainMenuBar = new JMenuBar();
    private static final JMenu mainMenu = new JMenu("☰ Menu");
    private static final JMenuItem saveMenu = new JMenuItem("📥 Save"); 
    private static final JMenuItem loadMenu = new JMenuItem("📂 Load"); 
    private static final JMenuItem resetMenu = new JMenuItem("🔄 Reset"); 
    private static final JMenuItem helpMenu = new JMenuItem("❓ Help"); 
    private static final JFileChooser fileChooser = new JFileChooser(); 

    // Game-related components
    static final KwazamChess game = new KwazamChess();
    private static final KwazamChessBoard chessboard = game.chessboard;
    private static final JLabel message = new JLabel("Game start! Team Blue first.");
    private static final JLabel moveCounterLabel = new JLabel("Moves: 0"); // Move counter

    /**
     * Main entry point of the application. Displays the welcome menu.
     *
     * @param args command-line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            KwazamChessGUI gui = new KwazamChessGUI();
            gui.showWelcomeMenu();
        });
    }

    /**
     * Default constructor. Initializes the main window and sets up listeners.
     * Sets up a confirmation dialog for exiting the application.
     */
    public KwazamChessGUI() {
        super("Kwazam Chess");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });

        panelInTheCenter.setBackground(Color.WHITE); // Set the middle panel background color to white.
    }

    /**
     * Confirms exit with a dialog.
     */
    private void confirmExit() {
        String windowName = "Exit the Application";
        String windowInfo = "Exit Kwazam Chess?";
        int result = JOptionPane.showConfirmDialog(panelInTheCenter, windowInfo, windowName, JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
        }
    }

    private class BackgroundImagePanel extends JPanel {
    private BufferedImage backgroundImage;
    private float transparency = 0.77f; 

    public BackgroundImagePanel(String imagePath) {
        try {
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();
        }
    }

    /**
     * Sets the transparency level for the background image.
     *
     * @param transparency the transparency level (0.0 = fully transparent, 1.0 = fully opaque).
     */
    public void setTransparency(float transparency) {
        this.transparency = Math.max(0.0f, Math.min(1.0f, transparency)); 
        repaint(); 
    }
}

    
        /**
         * Displays the welcome menu before starting the game.
         */
        public void showWelcomeMenu() {
    // Create a panel with a background image using the inner class
    BackgroundImagePanel backgroundPanel = new BackgroundImagePanel("Assets/background.jpeg");

    JFrame welcomeScreen = new JFrame("♙Kwazam Chess");
    welcomeScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    welcomeScreen.setSize(400, 300);
    welcomeScreen.setLayout(new BorderLayout());

    // Title Panel
    JPanel titlePanel = new JPanel(new GridBagLayout());
    titlePanel.setOpaque(false); // Make the title panel transparent

    JLabel titleLabel = new JLabel("Kwazam Chess ♙", JLabel.CENTER);
    titleLabel.setFont(new Font("Serif", Font.BOLD, 30));
    titleLabel.setForeground(new Color(255, 255, 240)); //  text color

    // Add the title label to the title panel with constraints to center it
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.CENTER; // Center the label
    titlePanel.add(titleLabel, gbc);

    // Button Panel
    JPanel buttonPanel = new JPanel();
    buttonPanel.setOpaque(false); // Make the button panel transparent
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for vertical alignment

    // Create buttons with custom colors and same width as the title
    JButton startButton = create3DButton("Start");
    JButton creditButton = create3DButton("Credit");
    JButton exitButton = create3DButton("Exit");

    // Calculate the width of the title label
    int titleWidth = titleLabel.getPreferredSize().width;

    // Set preferred size for buttons to match the title width
    Dimension buttonSize = new Dimension(titleWidth, 30); // Same width as the title, height is 30
    startButton.setPreferredSize(buttonSize);
    startButton.setMaximumSize(buttonSize);
    creditButton.setPreferredSize(buttonSize);
    creditButton.setMaximumSize(buttonSize);
    exitButton.setPreferredSize(buttonSize);
    exitButton.setMaximumSize(buttonSize);

    // Center the buttons horizontally
    startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    creditButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Add buttons to the panel with vertical spacing
    buttonPanel.add(Box.createVerticalStrut(10)); // Add some vertical spacing
    buttonPanel.add(startButton);
    buttonPanel.add(Box.createVerticalStrut(10)); // Add some vertical spacing
    buttonPanel.add(creditButton);
    buttonPanel.add(Box.createVerticalStrut(10)); // Add some vertical spacing
    buttonPanel.add(exitButton);
    buttonPanel.add(Box.createVerticalStrut(20)); // Add extra bottom margin below the Exit button

    // Add ActionListeners
    startButton.addActionListener(e -> {
        welcomeScreen.dispose(); // Close the welcome screen
        new ChessApp(); // Start the game by creating an instance of ChessApp
    });

    creditButton.addActionListener(e -> showCredits());

    exitButton.addActionListener(e -> System.exit(0));

    // Add panels to the background panel
    backgroundPanel.setLayout(new BorderLayout());
    backgroundPanel.add(titlePanel, BorderLayout.CENTER); // Place title panel in the center
    backgroundPanel.add(buttonPanel, BorderLayout.SOUTH); // Place button panel at the bottom

    // Add the background panel to the frame
    welcomeScreen.add(backgroundPanel);
    welcomeScreen.setLocationRelativeTo(null); // Center the frame on the screen.
    welcomeScreen.setVisible(true);
}

public void setupMenu() {
    // Create a panel with a background image using the inner class
    BackgroundImagePanel backgroundPanel = new BackgroundImagePanel("Assets/background.jpg");

    mainMenu.add(saveMenu);
    mainMenu.add(loadMenu);
    mainMenu.add(resetMenu);
    mainMenu.add(helpMenu);
    mainMenuBar.add(mainMenu);

    // Add the move counter to the top-right corner
    JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    topRightPanel.add(moveCounterLabel);

    // Add the menu bar and move counter to the top panel
    topPanel.add(mainMenuBar, BorderLayout.WEST);
    topPanel.add(topRightPanel, BorderLayout.EAST);

    bottomPanel.add(message);

    // Set layout for the background panel
    backgroundPanel.setLayout(new BorderLayout());
    backgroundPanel.add(topPanel, BorderLayout.NORTH);
    backgroundPanel.add(panelInTheCenter, BorderLayout.CENTER);
    backgroundPanel.add(bottomPanel, BorderLayout.SOUTH);

    setLayout(new BorderLayout());
    add(backgroundPanel, BorderLayout.CENTER); // Add the background panel to the frame

    helpMenu.addActionListener(e -> showHelpImage());
}
    
    /**
         * Displays the credits in a new window.
         */
        private void showCredits() {
            JFrame creditFrame = new JFrame("Credits");
            creditFrame.setSize(400, 200);
            creditFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            creditFrame.setLayout(new BorderLayout());
    
            JTextArea creditText = new JTextArea();
            creditText.setText("♙Kwazam Chess\nDevelop by TT9L Group C\n\n1. 1211112069 Tang Wei Xiong\n2. 1211108003 Joey Tan Rou Yi\n3. 1211108404 Low Wan Jin\n4. 1211107904 Yeong Zi Yan");
            creditText.setFont(new Font("Serif", Font.PLAIN, 14));
            creditText.setEditable(false);
            creditText.setBackground(new Color(242, 227, 211));
            creditText.setForeground(new Color(0, 0, 0));
    
            creditFrame.add(creditText, BorderLayout.CENTER);
            creditFrame.setLocationRelativeTo(null); // Center the frame on the screen.
            creditFrame.setVisible(true);
        }
    
    /**
     * Helper method to create a 3D button with custom colors.
     *
     * @param text The text to display on the button.
     * @return A JButton with custom background and text colors, and a 3D effect.
     */
    private JButton create3DButton(String text) {
        JButton button = new JButton(text);
        
        // Set preferred size for the button
        button.setPreferredSize(new Dimension(200, 50)); // Set width and height
        
        // New background and foreground colors
        button.setBackground(new Color(205, 133, 63)); // A lighter brown for the button
        button.setForeground(Color.WHITE); // White text for better contrast
        button.setFocusPainted(false);
        button.setFont(new Font("Serif", Font.BOLD, 16)); // Changed font to Serif, bold, size 16
        
        // Set border for 3D effect
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        
        // Add mouse listeners for hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(222, 184, 135)); // Change to a lighter color on hover
                button.setBorder(BorderFactory.createLoweredBevelBorder());
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(205, 133, 63)); // Revert to original color
                button.setBorder(BorderFactory.createRaisedBevelBorder());
            }
        });
        
        return button;
    }

    

        /**
     * Sets a new message in the bottom panel and changes the background color based on the team's turn.
     *
     * @param word the message to display.
     * @param teamColor the color of the team whose turn it is ("B" for Blue, "R" for Red).
     */
    public void setMessage(String word, String teamColor) {
        message.setText(word); // Update the message text
    
        // Change the background color of the bottom panel based on the team's turn
        if (teamColor.equals("B")) {
            bottomPanel.setBackground(Color.BLUE); // Set background to blue for Team Blue
            message.setForeground(Color.WHITE); // Set text color to white for better visibility
        } else if (teamColor.equals("R")) {
            bottomPanel.setBackground(Color.RED); // Set background to red for Team Red
            message.setForeground(Color.WHITE); // Set text color to white for better visibility
        } else {
            bottomPanel.setBackground(Color.WHITE); // Default background color
            message.setForeground(Color.BLACK); // Default text color
        }
    
        bottomPanel.repaint(); // Refresh the bottom panel to apply the color change
    }   
    
        /**
         * Adds an ActionListener to the restart menu item.
         *
         * @param e the ActionListener to add.
         */
        public void addRestartMenuListener(ActionListener e) {
            resetMenu.addActionListener(e);
        }
    
        /**
         * Adds an ActionListener to the save menu item.
         *
         * @param e the ActionListener to add.
         */
        public void addSaveMenuListener(ActionListener e) {
            saveMenu.addActionListener(e);
        }
    
        /**
         * Adds an ActionListener to the load menu item.
         *
         * @param e the ActionListener to add.
         */
        public void addLoadMenuListener(ActionListener e) {
            loadMenu.addActionListener(e);
        }
        
    /**
         * Displays the help image in a new window.
         */
        private void showHelpImage() {
        try {
            // Load the image from the Assets folder
            BufferedImage helpImage = ImageIO.read(new File("Assets/help.jpg"));
            
            // Create a JFrame to display the image
            JFrame helpFrame = new JFrame("Help");
            
            // Set the size of the window to match the image dimensions
            helpFrame.setSize(helpImage.getWidth(), helpImage.getHeight());
            
            // Ensure the window cannot be resized
            helpFrame.setResizable(false);
            
            // Close the window when the user clicks the close button
            helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            
            // Add the image to a JLabel and center it in the window
            JLabel imageLabel = new JLabel(new ImageIcon(helpImage));
            helpFrame.add(imageLabel);
            
            // Center the help window relative to the main window
            helpFrame.setLocationRelativeTo(this);
            
            // Make the window visible
            helpFrame.setVisible(true);
        } catch (IOException e) {
            // Display an error message if the image fails to load
            JOptionPane.showMessageDialog(this, "Failed to load help image!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    /**
         * Updates the move counter in the top-right corner.
         *
         * @param moveCount the current move count.
         */
        public void updateMoveCounter(int moveCount) {
            moveCounterLabel.setText("Moves: " + moveCount); // Update the move counter label
        }

    
    /**
     * Sets up icons for the chessboard.
     */
    public void setupIcons() {
        chessboard.addRedIcon("Assets/TorR.png");
        chessboard.addRedIcon("Assets/XorR.png");
        chessboard.addRedIcon("Assets/BizR.png");
        chessboard.addRedIcon("Assets/SauR.png");
        chessboard.addRedIcon("Assets/RamR.png");

        chessboard.addBlueIcon("Assets/TorB.png");
        chessboard.addBlueIcon("Assets/XorB.png");
        chessboard.addBlueIcon("Assets/BizB.png");
        chessboard.addBlueIcon("Assets/SauB.png");
        chessboard.addBlueIcon("Assets/RamB.png");
    }
}