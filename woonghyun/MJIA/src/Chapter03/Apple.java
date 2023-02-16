package Chapter03;

public class Apple {
    private int weight = 0;
    private Color color;

    public Apple(int weight, Color color) {
        this.weight = weight;
        this.color = color;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String toString() {
        return String.format("Apple{color=%s, weight=%d}", this.color, this.weight);
    }
}
