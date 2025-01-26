import java.awt.*;


/**
 * Represents a board square on a chessboard with coordinates and a piece.
 * @author Yeong Zi Yan
 */
public class BoardSquare {
    private ChessPiece placedPiece;
    private Integer rowPosition;
    private Integer colPosition;
    private boolean isHighlighted;
    private boolean isValidMove;
    private Color defaultBackground; // Store the default background color

    public BoardSquare(int row, int col) {
        this.rowPosition = row;
        this.colPosition = col;
        this.placedPiece = null;
        this.isHighlighted = false;
        this.isValidMove = false;

        // Set the default background color based on the position
        if ((row + col) % 2 == 0) {
            this.defaultBackground = new Color(181, 153, 132);
        } else {
            this.defaultBackground = new Color(242, 227, 211);
        }
    }

    public BoardSquare(int row, int col, ChessPiece piece) {
        this(row, col); // Call the main constructor
        this.placedPiece = piece;
    }


    public ChessPiece getPlacedPiece() {
        return placedPiece;
    }
    
    public void setPlacedPiece(ChessPiece piece) {
        this.placedPiece = piece;
    }

    public void assignPiece(ChessPiece piece) {
        this.placedPiece = piece;
    }

    public Integer getRowPosition() {
        return rowPosition;
    }

    public Integer getColPosition() {
        return colPosition;
    }

    public boolean hasPiece() {
        return placedPiece != null;
    }
    
    public void setRowPosition(int row) {
        this.rowPosition = row;
    }

    public void setColPosition(int col) {
        this.colPosition = col;
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public void setHighlighted(boolean highlighted) {
        isHighlighted = highlighted;
    }

    public boolean isValidMove() {
        return isValidMove;
    }

    public void setValidMove(boolean validMove) {
        isValidMove = validMove;
    }

    public void clearHighlights() {
        isHighlighted = false;
        isValidMove = false;
    }

    @Override
    public String toString() {
        return "BoardSquare{" +
                "row=" + rowPosition +
                ", col=" + colPosition +
                ", highlighted=" + isHighlighted +
                ", validMove=" + isValidMove +
                '}';
    }

    // Method to set the background color
    public void setBackgroundColor(Color color) {
        if (color != null) {
            this.defaultBackground = color;
        }
    }

    // Method to get the default background color
    /**
     * Returns the default background color of the square.
     *
     * @return The default background color.
     */
    public Color getDefaultBackground() {
        return defaultBackground;
    }

    /**
     * Resets the square's background color to its default.
     */
    public void resetBackgroundColor() {
        this.defaultBackground = ((rowPosition + colPosition) % 2 == 0) 
            ? new Color(245, 222, 179) 
            : new Color(165, 42, 42); 
    }

    
}
