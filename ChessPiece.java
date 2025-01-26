/**
 * This class represents a piece in the chess game.
 * It keeps track of the piece's name, the player who owns it,
 * and whether the piece has reached the board's opposite end.
 * @author Low Wan Jin
 */
public class ChessPiece {

    // Fields to store the piece's name, the owning player, 
    // and whether it has reached the board's opposite end.
    private String pieceName;
    private Player owner;
    private boolean hasReachedEnd;
    private boolean isFacingUp;


    // Additional temporary variables 
    private String tempName;
    private Player tempOwner;
    private boolean tempStatus;

    /**
     * Constructs a new ChessPiece instance with the specified name, owner, and end status.
     * 
     * @param pieceName The name of the chess piece.
     * @param owner The player who owns this piece.
     * @param hasReachedEnd Indicates if the piece has reached the board's opposite end.
     */
    public ChessPiece(String pieceName, Player owner, boolean hasReachedEnd) {
        this.pieceName = pieceName;
        this.owner = owner;
        this.hasReachedEnd = hasReachedEnd;
        this.isFacingUp = owner.getColor().equals("B");  // Blue facing upwards by default

        // Initialize additional temporary variables
        this.tempName = "";
        this.tempOwner = null;
        this.tempStatus = false;
    }

    /**
     * Sets the name of the chess piece.
     * 
     * @param pieceName The name of the chess piece.
     */
    public void setPieceName(String pieceName) {
        this.pieceName = pieceName;
    }

    /**
     * Sets the player who owns this chess piece.
     * 
     * @param owner The player who owns the piece.
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**
     * Retrieves the name of the chess piece.
     * 
     * @return The name of the chess piece.
     */
    public String getPieceName() {
        return pieceName;
    }
    
    public boolean isFacingUp() {
        return isFacingUp;
    }

    public void setFacingDirection(boolean isFacingUp) {
        this.isFacingUp = isFacingUp;
    }

    public boolean isOpponent(ChessPiece otherPiece) {
    return this.owner != null && otherPiece != null && !this.owner.equals(otherPiece.getOwner());
}

    

    /**
     * Retrieves the player who owns this chess piece.
     * 
     * @return The player who owns the piece.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Updates whether the piece has reached the board's opposite end.
     * 
     * @param hasReachedEnd A boolean indicating if the piece has reached the board's opposite end.
     */
    public void setHasReachedEnd(boolean hasReachedEnd) {
        this.hasReachedEnd = hasReachedEnd;
    }

    /**
     * Checks whether the piece has reached the board's opposite end.
     * 
     * @return A boolean indicating if the piece has reached the board's opposite end.
     */
    public boolean hasReachedEnd() {
        return hasReachedEnd;
    }

    /**
     * Returns a string representation of the ChessPiece object.
     * 
     * @return A string that describes the chess piece.
     */
    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceName='" + pieceName + '\'' +
                ", owner=" + owner +
                ", hasReachedEnd=" + hasReachedEnd +
                '}';
    }
}