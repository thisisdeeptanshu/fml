import java.util.ArrayList;
import java.util.Scanner;

public class Functions {
    static void dealWithIt(String arg) {
        ArrayList<Item> atmli = new ArrayList<Item>();
        for (Item i : Main.localItems) atmli.add(i);

        String[] _split = Main.split(arg);
        String[] args = new String[2 + _split.length];
        args[0] = "_tempitem_"; args[1] = "="; for (int j = 2; j < _split.length + 2; j++) {args[j] = _split[j-2];}

        Main.operate(Main.reverse(args));

        for (Item i : atmli) Main.localItems.add(i);
    }

    static void printFunction(String arg) {
        dealWithIt(arg);
        System.out.println(Main.clean(Main.items.get(Main.items.size() - 1).value));
    }

    static String inputFunction(String arg) {
        dealWithIt(arg);
        System.out.print(Main.clean(Main.items.get(Main.items.size() - 1).value));
        Scanner sc = new Scanner(System.in);
        return "\"" + sc.nextLine() + "\"";
    }

    static String intFunction(String arg) {
        dealWithIt(Main.clean(arg));
        arg = Main.clean(Main.items.get(Main.items.size() - 1).value);
        if (arg.toCharArray()[0] == '(' && arg.toCharArray()[arg.length() - 1] == ')') arg = arg.substring(1, arg.length() - 1);
        if ((arg.toCharArray()[0] == '\"' && arg.toCharArray()[arg.toCharArray().length - 1] == '\"') || (arg.toCharArray()[0] == '\'' && arg.toCharArray()[arg.toCharArray().length - 1] == '\'')) return arg.substring(1, arg.length() - 1);
        else if (Main.isInt(arg.toCharArray())) return arg;
        else {
            for (Item i : Main.items) {
                if (i.name.equals(arg)) {
                    return i.value;
                }
            }
        }
        return null;
    }

    static void functionFunction(String arg) {
        Main.localItems.add(new Item("temp", arg, Item.types.FUNCTION));
    }
}
