import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.IntStream;
import javax.swing.JOptionPane;

/**
 * Represents the Kwazam Chess game.
 * Manages the game state, player turns, piece movements, and file operations for saving/loading the game.
 * @author Tang Wei Xiong
 */
public class KwazamChess {
    public KwazamChessBoard chessboard;
    private Player player1;
    private Player player2;
    private final List<Player> playerList = new ArrayList<>();

    private static ChessPiece queue = null;
    private static BoardSquare temp = null;
    private static boolean hasWinner;
    private static boolean canMove = false;
    private static String type;
    private static int fromX, fromY, toX, toY, x, y;
    private int playerTurnNum = 0;

    // Constructor
    public KwazamChess() {
        chessboard = new KwazamChessBoard();
        player1 = new Player("B"); // Blue
        player2 = new Player("R"); // Red
        playerList.add(player2);
        playerList.add(player1);
        setPlayerTurnNum(0); // Initialize turn number

        setupPieces();
        printBoardState(); // Print the board state after setup
    }

    // Restart the game
    public void restart() {
        chessboard.clear();
        setupPieces();
        setPlayerTurnNum(0);
        hasWinner = false;
    }

    public void save() throws IOException {
        // Prompt the user to name the save file
        String fileName = JOptionPane.showInputDialog(null, "Enter a name for the save file:", "Save Game", JOptionPane.PLAIN_MESSAGE);
        if (fileName == null || fileName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Save cancelled. No file name provided.");
            return; // User cancelled or entered an empty name
        }

        // Ensure the file has a .txt extension
        if (!fileName.toLowerCase().endsWith(".txt")) {
            fileName += ".txt";
        }

        File saveFile = new File(fileName);

        // Confirm before overwriting an existing file
        if (saveFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(null,
                "File already exists. Do you want to overwrite it?",
                "Confirm Overwrite",
                JOptionPane.YES_NO_OPTION);
            if (overwrite != JOptionPane.YES_OPTION) {
                return; // User chose not to overwrite
            }
        }

        // Save the game state to the file
        try (PrintWriter printWriter = new PrintWriter(saveFile)) {
            // Save board state in a grid format
            for (int i = 0; i < chessboard.getHeight(); i++) {
                for (int j = 0; j < chessboard.getWidth(); j++) {
                    ChessPiece piece = chessboard.getSlot(i, j).getPlacedPiece();
                    if (piece == null) {
                        printWriter.print("  ----  "); // Empty square
                    } else {
                        String pieceStr = piece.getOwner().getColor() + piece.getPieceName();
                        if (piece.getPieceName().equals("Ram") && piece.hasReachedEnd()) {
                            pieceStr += " (End)";
                        }
                        printWriter.print(String.format("  %-6s", pieceStr)); // Align pieces in a grid
                    }
                }
                printWriter.println(); // Add line break after each row
            }

            // Save game state
            printWriter.println("\nPlayer to Move: " + getPlayerTurn().getColor());
            printWriter.println("Move Count: " + getPlayerTurnNum());
        }

        JOptionPane.showMessageDialog(null, "Game saved successfully to " + fileName);
    }

    /**
     * Loads a game state from a user-selected file.
     */
    public void load() throws FileNotFoundException {
        // Get a list of all .txt files in the current directory
        File[] saveFiles = new File(".").listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (saveFiles == null || saveFiles.length == 0) {
            JOptionPane.showMessageDialog(null, "No save files found.");
            return;
        }

        // Create a drop-down menu for the user to choose a file
        String[] fileNames = new String[saveFiles.length];
        for (int i = 0; i < saveFiles.length; i++) {
            fileNames[i] = saveFiles[i].getName();
        }

        String selectedFileName = (String) JOptionPane.showInputDialog(null,
            "Choose a save file to load:",
            "Load Game",
            JOptionPane.PLAIN_MESSAGE,
            null,
            fileNames,
            fileNames[0]);

        if (selectedFileName == null) {
            JOptionPane.showMessageDialog(null, "Load cancelled. No file selected.");
            return; // User cancelled the selection
        }

        File selectedFile = new File(selectedFileName);

        // Load the game state from the selected file
        try (Scanner scanner = new Scanner(selectedFile)) {
            chessboard.clear();

            // Load board state
            for (int i = 0; i < chessboard.getHeight(); i++) {
                String line = scanner.nextLine().trim(); // Read the entire line
                String[] pieces = line.split("\\s{2,}"); // Split by two or more spaces

                for (int j = 0; j < pieces.length; j++) {
                    String pieceStr = pieces[j].trim();
                    if (!pieceStr.equals("----")) { // Skip empty squares
                        // Extract color (first character)
                        String color = pieceStr.substring(0, 1);
                        // Extract piece name and check for "(End)" for Ram
                        boolean reachEnd = pieceStr.contains("(End)");
                        String pieceName = reachEnd ? pieceStr.substring(1, pieceStr.indexOf(" ")) : pieceStr.substring(1);

                        // Create piece with proper owner
                        Player owner = color.equals("B") ? player1 : player2;
                        ChessPiece piece = new ChessPiece(pieceName, owner, reachEnd);
                        chessboard.addChessPiece(i, j, piece);
                    }
                }
            }

            // Load game state if available
        while (scanner.hasNextLine()) {
        String line = scanner.nextLine().trim();
        if (line.startsWith("Player to Move:")) {
            String playerColor = line.substring(line.lastIndexOf(" ") + 1);
            setPlayerTurnNum(playerColor.equals("B") ? 0 : 1); // Set player turn

            // Flip the board if it's Red's turn
            if (playerColor.equals("R")) {
                chessboard.reverse();
            }
        } else if (line.startsWith("Move Count:")) {
            int moveCount = Integer.parseInt(line.substring(line.lastIndexOf(" ") + 1));
            setPlayerTurnNum(moveCount); // Set move count
        }
    }

            hasWinner = false;
            JOptionPane.showMessageDialog(null, "Game loaded successfully from " + selectedFileName);

            // Update the message to reflect whose turn it is
    String team = getPlayerTurn().getColor();

            
        }
    }

    // Setup pieces on the board
    public void setupPieces() {
    System.out.println("Setting up pieces...");
    String[] redArrangement = {"Tor", "Biz", "Sau", "Biz", "Xor"}; // Row 0 (Red team)
    String[] blueArrangement = {"Xor", "Biz", "Sau", "Biz", "Tor"}; // Row 7 (Blue team)
    String ramPiece = "Ram";
    boolean ramHasReachedEnd = false;
    boolean otherPiecesReachedEnd = true;

    IntStream.range(0, chessboard.getHeight()).forEach(i -> {
        IntStream.range(0, chessboard.getWidth()).forEach(j -> {
            if (i == 0 && j < redArrangement.length) {
                ChessPiece piece = new ChessPiece(redArrangement[j], player2, otherPiecesReachedEnd);
                chessboard.addChessPiece(i, j, piece);
                System.out.println("Placed " + piece.getPieceName() + " at (" + i + ", " + j + ")");
            } else if (i == 1) {
                ChessPiece piece = new ChessPiece(ramPiece, player2, ramHasReachedEnd);
                chessboard.addChessPiece(i, j, piece);
                System.out.println("Placed " + piece.getPieceName() + " at (" + i + ", " + j + ")");
            } else if (i == 6) {
                ChessPiece piece = new ChessPiece(ramPiece, player1, ramHasReachedEnd);
                chessboard.addChessPiece(i, j, piece);
                System.out.println("Placed " + piece.getPieceName() + " at (" + i + ", " + j + ")");
            } else if (i == 7 && j < blueArrangement.length) {
                ChessPiece piece = new ChessPiece(blueArrangement[j], player1, otherPiecesReachedEnd);
                chessboard.addChessPiece(i, j, piece);
                System.out.println("Placed " + piece.getPieceName() + " at (" + i + ", " + j + ")");
            }
        });
    });
    System.out.println("Pieces setup complete.");
}

    // Overloaded piece setup method for game loading purpose
    public void setupPiece(int x, int y, String pieceName) {
        boolean reachEnd = false;
        if (pieceName.contains("ReachEnd")) {
            pieceName = pieceName.substring(0, pieceName.length() - 8); // remove "ReachEnd" from the pieceName
            reachEnd = true;
        }
        Player player = pieceName.charAt(0) == 'B' ? player1 : player2;
        chessboard.addChessPiece(x, y, new ChessPiece(pieceName.substring(1), player, reachEnd));
    }

    // Move a piece from a slot to another slot
    public boolean move(BoardSquare fromSquare, BoardSquare toSquare) {
    System.out.println("Move method called");
    System.out.println("Selected square: (" + fromSquare.getRowPosition() + ", " + fromSquare.getColPosition() + ")");
    System.out.println("Destination square: (" + toSquare.getRowPosition() + ", " + toSquare.getColPosition() + ")");

    ChessPiece piece = fromSquare.getPlacedPiece();
    if (piece == null || !piece.getOwner().equals(getPlayerTurn())) {
        System.out.println("No piece or not player's turn");
        return false;
    }

    int fromX = fromSquare.getRowPosition();
    int fromY = fromSquare.getColPosition();
    int toX = toSquare.getRowPosition();
    int toY = toSquare.getColPosition();

    boolean canMove = isValidMove(piece.getPieceName(), fromX, fromY, toX, toY, piece);
    System.out.println("Move valid: " + canMove);

    if (canMove) {
        // Capture logic: If the destination square has an opponent's piece, remove it
        ChessPiece destinationPiece = toSquare.getPlacedPiece();
        if (destinationPiece != null && !destinationPiece.getOwner().equals(piece.getOwner())) {
            System.out.println("Capturing opponent's piece: " + destinationPiece.getPieceName());
            toSquare.setPlacedPiece(null); // Remove the opponent's piece

            // Check if the captured piece is a "Sau"
            if (destinationPiece.getPieceName().equals("Sau")) {
                System.out.println("Sau captured! Game over.");
                hasWinner = true; // Set the game to end
                String winner = piece.getOwner().getColor(); // The capturing player wins
                JOptionPane.showMessageDialog(null, "Team " + winner + " wins by capturing the Sau!");
                return true; // End the game
            }
        }

        // Move the piece to the destination square
        toSquare.setPlacedPiece(piece);
        fromSquare.setPlacedPiece(null);

        // Check if the Ram has reached the opposite end
        if (piece.getPieceName().equals("Ram")) {
            int oppositeEnd = chessboard.isFlipped() ? 0 : 7;
            if (toX == (piece.getOwner().getColor().equals("B") ? 0 : oppositeEnd)) {
                piece.setHasReachedEnd(true); // Set the "reached end" status
                System.out.println("Ram reached end: hasReachedEnd = true");
            }
        }

        playerTurnNum++; // Increment the turn counter

        // Check for a winner after the move
        String winner = getWinner();
        if (winner != null) {
            System.out.println("Team " + winner + " wins!");
            JOptionPane.showMessageDialog(null, "Team " + winner + " wins!");
            return true; // End the game
        }

        // Flip the board after a successful move
        chessboard.reverse();
        System.out.println("Move successful, board flipped");

        // Change Xor/Tor state every two moves
        if (playerTurnNum % 2 == 0) {
            changeState();
            System.out.println("Xor/Tor state changed");
        }

        return true;
    } else {
        System.out.println("Invalid move");
        return false;
    }
}



    // Check whether a piece can be moved in that turn
    public boolean isMovable(BoardSquare slot) {
    if (slot.getPlacedPiece() == null) {
        System.out.println("No piece on the square");
        return false;
    }

    Player currentPlayer = getPlayerTurn();
    Player pieceOwner = slot.getPlacedPiece().getOwner();

    System.out.println("Current player: " + currentPlayer.getColor());
    System.out.println("Piece owner: " + pieceOwner.getColor());

    return pieceOwner.equals(currentPlayer);
}

    // Check whether a piece follows its movement rules
    public boolean isValidMove(String type, int fromX, int fromY, int toX, int toY, ChessPiece queue) {
    switch (type) {
        case "Ram":
            return isValidRamMove(fromX, fromY, toX, toY, queue);
        case "Tor":
            return isValidTorMove(fromX, fromY, toX, toY);
        case "Xor":
            return isValidXorMove(fromX, fromY, toX, toY);
        case "Biz":
            return isValidBizMove(toX - fromX, toY - fromY);
        case "Sau":
            return isValidSauMove(toX - fromX, toY - fromY);
        default:
            return false;
    }
}

private boolean isValidRamMove(int fromX, int fromY, int toX, int toY, ChessPiece ram) {
    System.out.println("Ram move check: from (" + fromX + ", " + fromY + ") to (" + toX + ", " + toY + ")");
    System.out.println("Ram hasReachedEnd: " + ram.hasReachedEnd());

    // Ram can only move in the same column
    if (fromY != toY) {
        System.out.println("Invalid move: Not in the same column");
        return false;
    }

    // Calculate the direction of movement
    int deltaX = toX - fromX;

    // Check if the destination is within the board bounds
    if (toX < 0 || toX >= chessboard.getHeight() || toY < 0 || toY >= chessboard.getWidth()) {
        System.out.println("Invalid move: Out of bounds");
        return false;
    }

    // Check if the destination square is occupied by a piece of the same color
    ChessPiece destinationPiece = chessboard.getSlot(toX, toY).getPlacedPiece();
    if (destinationPiece != null && destinationPiece.getOwner().equals(ram.getOwner())) {
        System.out.println("Invalid move: Cannot capture a piece of the same color");
        return false; // Cannot capture a piece of the same color
    }

    // Determine the opposite end based on the board's flipped state and the Ram's owner
    int oppositeEnd;
    if (chessboard.isFlipped()) {
        oppositeEnd = ram.getOwner().getColor().equals("B") ? 7 : 0; // Flipped board
    } else {
        oppositeEnd = ram.getOwner().getColor().equals("B") ? 0 : 7; // Normal board
    }

    // Determine the direction of movement based on whether the Ram has reached the end
    if (ram.hasReachedEnd()) {
        // Ram is moving backward (toward its own side)
        if (deltaX == 1) {
            // Prevent the Ram from moving back to its starting row (row 0 for Red, row 7 for Blue)
            if (toX == (ram.getOwner().getColor().equals("B") ? 7 : 0)) {
                System.out.println("Invalid move: Ram cannot move back to its starting row");
                return false;
            }
            System.out.println("Valid backward move");
            return true;
        }
        // Allow the Ram to move forward to the opponent's starting row to capture the Sau
        else if (deltaX == -1 && toX == oppositeEnd) {
            System.out.println("Valid forward move to capture Sau at the end");
            return true;
        }
    } else {
        // Ram is moving forward (toward the opponent's side)
        if (deltaX == -1) {
            // Check if the destination is the opposite end
            if (toX == oppositeEnd) {
                ram.setHasReachedEnd(true); // Set the "reached end" status
                System.out.println("Ram reached end: hasReachedEnd = true");
            }
            System.out.println("Valid forward move");
            return true;
        }
    }

    // Invalid move
    System.out.println("Invalid move: Direction or step size incorrect");
    return false;
}



// Helper method to check if a slot is empty
private boolean isSlotEmpty(int row, int col) {
    if (row < 0 || row >= chessboard.getHeight() || col < 0 || col >= chessboard.getWidth()) {
        return false; // Slot is out of bounds
    }
    return chessboard.getSlot(row, col).getPlacedPiece() == null;
}

private boolean isValidTorMove(int fromX, int fromY, int toX, int toY) {
    int deltaX = toX - fromX;
    int deltaY = toY - fromY;

    // Tor can move any distance orthogonally (same row or same column)
    if (deltaX == 0 && deltaY != 0) {
        // Moving along the same row
        int step = deltaY > 0 ? 1 : -1;
        for (int y = fromY + step; y != toY; y += step) {
            if (!isSlotEmpty(fromX, y)) {
                return false; // Path is blocked
            }
        }
    } else if (deltaY == 0 && deltaX != 0) {
        // Moving along the same column
        int step = deltaX > 0 ? 1 : -1;
        for (int x = fromX + step; x != toX; x += step) {
            if (!isSlotEmpty(x, fromY)) {
                return false; // Path is blocked
            }
        }
    } else {
        return false; // Not a valid orthogonal move
    }

    // Check if the destination square has a piece of the same color
    ChessPiece destinationPiece = chessboard.getSlot(toX, toY).getPlacedPiece();
    if (destinationPiece != null && destinationPiece.getOwner().equals(chessboard.getSlot(fromX, fromY).getPlacedPiece().getOwner())) {
        return false; // Cannot capture a piece of the same color
    }

    return true; // Valid move
}

    private boolean isValidXorMove(int fromX, int fromY, int toX, int toY) {
    int deltaX = toX - fromX;
    int deltaY = toY - fromY;

    // Xor can move any distance diagonally
    if (Math.abs(deltaX) == Math.abs(deltaY)) {
        int stepX = deltaX > 0 ? 1 : -1;
        int stepY = deltaY > 0 ? 1 : -1;
        int x = fromX + stepX;
        int y = fromY + stepY;
        while (x != toX && y != toY) {
            if (!isSlotEmpty(x, y)) {
                return false; // Path is blocked
            }
            x += stepX;
            y += stepY;
        }
    } else {
        return false; // Not a valid diagonal move
    }

    // Check if the destination square has a piece of the same color
    ChessPiece destinationPiece = chessboard.getSlot(toX, toY).getPlacedPiece();
    if (destinationPiece != null && destinationPiece.getOwner().equals(chessboard.getSlot(fromX, fromY).getPlacedPiece().getOwner())) {
        return false; // Cannot capture a piece of the same color
    }

    return true; // Valid move
}
    private boolean isValidBizMove(int x, int y) {
        return ((x == -2 || x == 2) && (y == 1 || y == -1)) || ((x == -1 || x == 1) && (y == -2 || y == 2));
    }

    private boolean isValidSauMove(int x, int y) {
        return (x == -1 || x == 0 || x == 1) && (y == -1 || y == 0 || y == 1);
    }

    // Change the Xor to Tor or vice versa after every 4 turns
public void changeState() {
    for (int i = 0; i < chessboard.getBoardSize(); i++) {
        BoardSquare slot = chessboard.getSlot(i);
        ChessPiece piece = slot.getPlacedPiece();
        if (piece != null) {
            if (piece.getPieceName().equals("Xor")) {
                piece.setPieceName("Tor");
            } else if (piece.getPieceName().equals("Tor")) {
                piece.setPieceName("Xor");
            }
        }
    }
}

    // Check whether a team has won the game
    public String getWinner() {
        long numOfSau = chessboard.getSlots().stream()
                .map(BoardSquare::getPlacedPiece)
                .filter(Objects::nonNull)
                .filter(piece -> piece.getPieceName().equals("Sau"))
                .count();
        if (numOfSau == 1) {
            hasWinner = true;
            return chessboard.getSlots().stream()
                    .map(BoardSquare::getPlacedPiece)
                    .filter(Objects::nonNull)
                    .filter(piece -> piece.getPieceName().equals("Sau"))
                    .map(piece -> piece.getOwner().getColor())
                    .findFirst().orElse(null);
        }
        hasWinner = false;
        return null;
    }

    // Get which player's turn it is
    public Player getPlayerTurn() {
        return hasWinner ? playerList.get((playerTurnNum - 1) % 2) : playerList.get(playerTurnNum % 2);
    }

    // playerTurnNum getter
    public int getPlayerTurnNum() {
        return playerTurnNum;
    }

    // playerTurnNum setter
    public void setPlayerTurnNum(int playerTurnNum) {
        this.playerTurnNum = playerTurnNum % 2 == 0 ? 1 : playerTurnNum; // Start with Blue (Player 1)
    }

    /**
     * Checks if a move from one position to another is valid for a given piece
     * @param fromSquare The starting square
     * @param toSquare The destination square
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMoveSquares(BoardSquare fromSquare, BoardSquare toSquare) {
        if (fromSquare.getPlacedPiece() == null) {
            return false;
        }

        ChessPiece piece = fromSquare.getPlacedPiece();
        if (!piece.getOwner().equals(getPlayerTurn())) {
            return false;
        }

        int fromX = fromSquare.getRowPosition();
        int fromY = fromSquare.getColPosition();
        int toX = toSquare.getRowPosition();
        int toY = toSquare.getColPosition();

        // Check if destination has a piece of the same color
        if (toSquare.getPlacedPiece() != null && 
            toSquare.getPlacedPiece().getOwner().equals(piece.getOwner())) {
            return false;
        }

        return isValidMove(piece.getPieceName(), fromX, fromY, toX, toY, piece);
    }


    
    /**
     * Gets all valid moves for a piece at a given square
     * @param square The square containing the piece to check
     * @return List of all valid destination squares
     */
    public List<BoardSquare> getValidMoves(BoardSquare square) {
        List<BoardSquare> validMoves = new ArrayList<>();
        
        if (square.getPlacedPiece() == null || 
            !square.getPlacedPiece().getOwner().equals(getPlayerTurn())) {
            return validMoves;
        }

        for (int i = 0; i < chessboard.getHeight(); i++) {
            for (int j = 0; j < chessboard.getWidth(); j++) {
                BoardSquare targetSquare = chessboard.getSlot(i, j);
                if (isValidMoveSquares(square, targetSquare)) {
                    validMoves.add(targetSquare);
                }
            }
        }
        
        return validMoves;
    }

    public void printBoardState() {
    System.out.println("Current board state:");
    for (int i = 0; i < chessboard.getHeight(); i++) {
        for (int j = 0; j < chessboard.getWidth(); j++) {
            ChessPiece piece = chessboard.getSlot(i, j).getPlacedPiece();
            if (piece != null) {
                System.out.print(piece.getPieceName() + " ");
            } else {
                System.out.print("null ");
            }
        }
        System.out.println();
    }

    
}
}