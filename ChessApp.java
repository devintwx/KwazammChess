import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * Main class - Entry point for the Kwazam Chess application.
 * Manages game initialization, GUI setup, and user interactions.
 * @author Tang Wei Xiong
 */
public class ChessApp implements ActionListener {

    private static final ArrayList<JButton> buttonArrayList = new ArrayList<>();
    private static KwazamChessGUI kwazamChessGUI;
    private static final KwazamChess chessGame = new KwazamChess();
    private static final KwazamChessBoard chessboard = chessGame.chessboard;
    private BoardSquare selectedSquare = null; // Track the currently selected square
    

    /**
     * Default constructor.
     * Initializes the game GUI, sets up the menu, and displays the chessboard.
     */
    public ChessApp() {
        kwazamChessGUI = new KwazamChessGUI();
        setupLayout(); // Initialize menu, icons, and pieces.
        displayBoard(); // Display the chessboard.

        // Set the initial message with the team color
    String initialTeam = chessGame.getPlayerTurn().getColor(); // Get the initial team (Blue)
    kwazamChessGUI.setMessage("Game start! Team " + initialTeam + " first.", initialTeam);

        // Add save functionality to the menu.
        kwazamChessGUI.addSaveMenuListener(e -> {
            int confirmDialog = JOptionPane.showConfirmDialog(null, "Are you willing to save the game?");
            if (confirmDialog == JOptionPane.YES_OPTION) {
                try {
                    chessGame.save(); // Save the game state.
                } catch (Exception exception) {
                    exception.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Unable to save. Please try again later....");
                }
            }
        });

        

        // Add load functionality to the menu.
kwazamChessGUI.addLoadMenuListener(e -> {
    int showConfirmDialog = JOptionPane.showConfirmDialog(null, "Are you willing to load the previous game?");
    if (showConfirmDialog == JOptionPane.YES_OPTION) {
        try {
            chessGame.load(); // Load the saved game state.
            updateGameStatus(false); // Refresh the board to reflect the loaded state.
            String team = chessGame.getPlayerTurn().getColor();
            kwazamChessGUI.setMessage("Let's move team " + team + ", now it is your turn!!!", team); // Pass team color
            JOptionPane.showMessageDialog(null, "Game loaded successfully!");
        } catch (Exception exception) {
            exception.printStackTrace();
            JOptionPane.showMessageDialog(null, "Game failed to load! Please try again...");
        }
    }
});

// Add restart functionality to the menu.
kwazamChessGUI.addRestartMenuListener(e -> {
    chessGame.restart(); // Reset the game state.
    updateGameStatus(false); // Refresh the board without ending the game.
    String initialTeamRestart = chessGame.getPlayerTurn().getColor();
    kwazamChessGUI.setMessage("Game start! Team " + initialTeamRestart + " first.", initialTeamRestart); // Pass team color
});
 kwazamChessGUI.setVisible(true); // Display the game GUI.
}
    /**
     * Sets up the menu, icons, and pieces for the game.
     */ 
    private void setupLayout() {
        kwazamChessGUI.setupMenu(); // Configure the game menu.
        kwazamChessGUI.setupIcons(); // Load icons for game pieces.
        chessGame.setupPieces(); // Place game pieces on the board.
    }

    /**
     * Handles button click events for making a move.
     * Checks if the move is valid, updates the board, and handles game state changes.
     *
     * @param e The ActionEvent triggered by the button click.
     */
@Override
public void actionPerformed(ActionEvent e) {
    JButton button = (JButton) e.getSource();
    int buttonIndex = buttonArrayList.indexOf(button);
    if (buttonIndex < 0 || buttonIndex >= chessboard.getBoardSize()) {
        System.out.println("Invalid button index: " + buttonIndex);
        return;
    }

    BoardSquare slot = chessboard.getSlot(buttonIndex);
    System.out.println("Clicked on square: (" + slot.getRowPosition() + ", " + slot.getColPosition() + ")");
    if (slot.hasPiece()) {
        System.out.println("Square contains piece: " + slot.getPlacedPiece().getPieceName());
    } else {
        System.out.println("Square is empty");
    }

    if (selectedSquare == null) {
        // If no square is selected, check if the clicked square has a piece
        if (slot.hasPiece() && slot.getPlacedPiece().getOwner().equals(chessGame.getPlayerTurn())) {
            selectedSquare = slot;
            System.out.println("Piece selected at: (" + selectedSquare.getRowPosition() + ", " + selectedSquare.getColPosition() + ")");
            highlightValidMoves(selectedSquare); // Highlight valid moves
        }
    } else {
        // If a square is already selected, check if the clicked square is the same as the selected square
        if (slot == selectedSquare) {
            // If the same square is clicked again, reset the selection
            System.out.println("Resetting selection for the same piece.");
            clearHighlights();
            selectedSquare = null;
        } else {
            // Attempt to move the piece to the new square
            System.out.println("Attempting to move from: (" + selectedSquare.getRowPosition() + ", " + selectedSquare.getColPosition() + ")");
            System.out.println("Attempting to move to: (" + slot.getRowPosition() + ", " + slot.getColPosition() + ")");

            boolean moved = chessGame.move(selectedSquare, slot); // Pass both selectedSquare and destination slot
            if (moved) {
                System.out.println("Move successful");
                clearHighlights(); // Clear highlights after a successful move
                selectedSquare = null; // Reset the selected square

                // Update the game status and message
                updateGameStatus(false); // Refresh the board

                // Update the message and bottom panel color based on the current player's turn
                String team = chessGame.getPlayerTurn().getColor();
                kwazamChessGUI.setMessage("Let's move team " + team + ", now it is your turn!!!", team);

                // Update the move counter
                kwazamChessGUI.updateMoveCounter(chessGame.getPlayerTurnNum());
            } else {
                // Show a pop-up window for invalid moves
                JOptionPane.showMessageDialog(null, "Invalid move! Please try again.", "Invalid Move", JOptionPane.ERROR_MESSAGE);
                System.out.println("Invalid move, resetting selection");
                clearHighlights();
                selectedSquare = null;
            }
        }
    }
}

    /**
     * Loads an image from the given path and optionally flips it vertically.
     *
     * @param path The path to the image file.
     * @param flipVertical Whether to flip the image vertically.
     * @return The loaded and processed image.
     */
    private Image loadImage(String path, boolean flipVertical) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(path)); // Load the image file.
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

        if (flipVertical && image != null) {
            AffineTransform affineTransform = AffineTransform.getScaleInstance(1, -1); // Flip the image vertically.
            affineTransform.translate(0, -image.getHeight(null));
            AffineTransformOp op = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = op.filter(image, null); // Apply the transformation.
        }

        return resizeImage(image, 40, 40); // Resize the image to fit the button.
    }

    /**
     * Resizes the given image to the specified dimensions.
     *
     * @param image The image to resize.
     * @param width The new width.
     * @param height The new height.
     * @return The resized image.
     */
    private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        Image temp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(temp, 0, 0, null);
        graphics2D.dispose();

        return resizedImage;
    }

    /**
     * Creates a button for each chessboard slot, adding an icon if a piece is present.
     *
     * @param index The index of the chessboard slot.
     */
    private void createButtonForSlot(int index) {
    BoardSquare slot = chessboard.getSlot(index);
    ChessPiece piece = slot.getPlacedPiece();
    Image image = null;

    if (piece != null) {
        if (piece.getOwner().equals(chessGame.getPlayerTurn())) {
            if (piece.getPieceName().equals("Ram") && piece.hasReachedEnd()) {
                image = loadImage(chessboard.getIcon(piece.getPieceName() + piece.getOwner().getColor()), true);
            } else {
                image = loadImage(chessboard.getIcon(piece.getPieceName() + piece.getOwner().getColor()), false);
            }
        } else {
            if (piece.getPieceName().equals("Ram") && piece.hasReachedEnd()) {
                image = loadImage(chessboard.getIcon(piece.getPieceName() + piece.getOwner().getColor()), false);
            } else {
                image = loadImage(chessboard.getIcon(piece.getPieceName() + piece.getOwner().getColor()), true);
            }
        }
    } else {
        image = null;
    }

    JButton button = new JButton();
    if (image != null) {
        button.setIcon(new ImageIcon(image));
    }
    button.setBackground(slot.getDefaultBackground()); // Set the button's background color
    button.addActionListener(this);

    buttonArrayList.add(button);
    kwazamChessGUI.panelInTheCenter.add(button);
}



    /**
     * Displays the entire chessboard by creating buttons for all slots.
     */
    private void displayBoard() {
        for (int i = 0; i < chessboard.getBoardSize(); i++) {
            createButtonForSlot(i); // Setup buttons for each slot.
        }
    }

    /**
     * Refreshes the chessboard to reflect the current game state.
     * Removes old buttons and recreates the board.
     *
     * @param endGame Indicates if the game has ended.
     */
private void updateGameStatus(boolean endGame) {
    kwazamChessGUI.panelInTheCenter.removeAll(); // Clear all buttons from the panel.
    buttonArrayList.clear(); // Clear the button list.
    displayBoard(); // Recreate the board.
    kwazamChessGUI.revalidate(); // Revalidate the panel.
    kwazamChessGUI.repaint(); // Repaint the panel.

    if (endGame) {
        buttonArrayList.forEach(button -> button.removeActionListener(this)); // Disable buttons after the game ends.
    } else {
        // Update the message and bottom panel color based on the current player's turn
        String team = chessGame.getPlayerTurn().getColor();
        kwazamChessGUI.setMessage("Let's move team " + team + ", now it is your turn!!!", team); // Pass team color
    }
}

    // Method to highlight valid moves
private void highlightValidMoves(BoardSquare square) {
    List<BoardSquare> validMoves = chessGame.getValidMoves(square);
    for (BoardSquare move : validMoves) {
        int index = move.getRowPosition() * chessboard.getWidth() + move.getColPosition();
        JButton button = buttonArrayList.get(index);
        button.setBackground(Color.GREEN); // Highlight valid moves with green
        button.addActionListener(this); // Re-add the ActionListener
    }
}
    // Method to clear highlights
    private void clearHighlights() {
    for (JButton button : buttonArrayList) {
        button.setBackground(Color.WHITE); // Reset to default background color
        button.setFocusable(true); // Ensure the button remains focusable
        button.addActionListener(this); // Re-add the ActionListener
    }
}



    /**
     * Main entry point of the application.
     * Initializes the ChessApp class to start the game.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new ChessApp(); // Create and run the game.
    }
}