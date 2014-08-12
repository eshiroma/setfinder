package com.erikashiroma.setsolver;

/**
 * Represents a single set card by storing its four properties (count, color, shade, shape).
 * A SetCard is immutable.
 *
 * Created by Erika on 8/10/2014.
 */
public class SetCard implements Comparable<SetCard> {
    public static final int PROPERTIES_COUNT = 4;

    public enum Count {
        ONE, TWO, THREE
    }
    public enum Color {
        GREEN, PURPLE, RED
    }
    public enum Shade {
        OUTLINED, SOLID, STRIPED
    }
    public enum Shape {
        DIAMOND, OVAL, SQUIGGLE
    }

    // card properties cannot be changed after creation
    private Count count;
    private Color color;
    private Shade shade;
    private Shape shape;

    /**
     * Constructs a new SetCard with the given properties.
     * @param count to be used
     * @param color to be used
     * @param shade to be used
     * @param shape to be used
     * @throws IllegalArgumentException if any given properties are null
     */
    public SetCard(Count count, Color color, Shade shade, Shape shape) {
        if (count == null || color == null || shade == null || shape == null) {
            throw new IllegalArgumentException("Invalid card properties given");
        }
        this.count = count;
        this.color = color;
        this.shade = shade;
        this.shape = shape;
    }

    /**
     * Constructs a new SetCard with the given properties, interpreting the
     * Strings as the appropriate enum values.
     * @param count to be used
     * @param color to be used
     * @param shade to be used
     * @param shape to be used
     * @throws IllegalArgumentException if any of the given String properties
     * are not a value of the appropriate enum
     */
    public SetCard(String count, String color, String shade, String shape) {
        this.count = Count.valueOf(count);
        this.color = Color.valueOf(color);
        this.shade = Shade.valueOf(shade);
        this.shape = Shape.valueOf(shape);
    }

    /** Getter methods for the card properties. No setters, as card is immutable */
    public Count getCount() { return count; }
    public Color getColor() { return color; }
    public Shade getShade() { return shade; }
    public Shape getShape() { return shape; }

    /** Returns a String listing the card properties
     * (in order of their comparative weight)
     * @return String representation of the card
     */
    @Override
    public String toString() {
        return count + " " + color + " " + shade + " " + shape;
    }

    /** @return true if all card properties are the same */
    public boolean equals(SetCard other) {
        return compareTo(other) == 0;
    }

    /** Compares cards by:
     *  <ol>
     *      <li>count (ONE &lt TWO &lt THREE)</li>
     *      <li>color (GREEN &lt PURPLE &lt RED)</li>
     *      <li>shade (OUTLINED &lt SOLID &lt STRIPED)</li>
     *      <li>shape (DIAMOND &lt OVAL &lt SQUIGGLE></li>
     *  </ol>
     *  Returns a negative integer if this card is less than the other, zero if equal,
     *  and a positive integer if greater.
     */
    public int compareTo(SetCard other) {
        if (count != other.count) {
            return count.compareTo(other.count);
        } else if (color != other.color) {
            return color.compareTo(other.color);
        } else if (shade != other.shade) {
            return shade.compareTo(other.shade);
        } else {
            return shape.compareTo(other.shape);
        }
    }

    /**
     * Returns a SetCard matching the given toString
     * @throws IllegalArgumentException if given cardString does not match any cards
     * @return SetCard
     */
    public static SetCard getSetCard(String cardString) {
        if (cardString == null) {
            throw new IllegalArgumentException("Invalid card string");
        }
        String[] prop = cardString.split(" ");
        if (prop.length != PROPERTIES_COUNT) {
            throw new IllegalArgumentException("Invalid card string");
        }

        Count count = Count.valueOf(prop[0]);
        Color color = Color.valueOf(prop[1]);
        Shade shade = Shade.valueOf(prop[2]);
        Shape shape = Shape.valueOf(prop[3]);

        return new SetCard(count, color, shade, shape);
    }
}
