public class Item {
    public enum types {
        STRING,
        INT,
        FUNCTION
    }

    types type;
    String value;
    String name;
    Item(String name, String value, types type) {
        this.type = type;
        this.value = value;
        this.name = name;
    }
}
