/**
 * This class represents a player in the chess game.
 * It stores information about the player's assigned color.
 * @author Low Wan Jin
 */
public class Player {

    // Attribute to hold the player's color
    private String color;

    /**
     * Constructor to initialize a Player object with a specified color.
     *
     * @param color The color assigned to the player (e.g., "White", "Black").
     */
    public Player(String color) {
        this.color = color;
    }

    /**
     * Sets the player's color.
     *
     * @param color The color to be assigned to the player.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Retrieves the player's color.
     *
     * @return The color assigned to the player.
     */
    public String getColor() {
        return color;
    }
}