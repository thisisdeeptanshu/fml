import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static ArrayList<Item> items = new ArrayList<Item>();
    static ArrayList<Item> localItems = new ArrayList<Item>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean working = true;
        while (working) {
            System.out.print(">>> ");
            String command = sc.nextLine();
            if (command.toLowerCase().equals("credits")) System.out.println("https://thisisdeeptanshu.github.io");
            else if (command.toLowerCase().equals("exit")) {
                System.out.println("Good day.");
                working = false;
            }
            else {
                String[] commands = reverse(split(command));
                working = operate(commands);
            }
        }
    }

    static String[] split(String s) {
        ArrayList<String> sList = new ArrayList<String>();
        boolean inDoubleQuotes = false;
        int bracketsLevel = 0;
        String word = "";
        for (char c : s.toCharArray()) {
            if (c == '\"' || c == '\'') inDoubleQuotes = !inDoubleQuotes;
            if (c == '(' || c == '[') bracketsLevel++;
            if (c == ')' || c == ']') bracketsLevel--;
            if (c == ' ' && !inDoubleQuotes && bracketsLevel == 0) {
                sList.add(word);
                word = "";
            } else {
                word += c;
            }
        }
        sList.add(word);
        return sList.toArray(new String[sList.size()]);
    }

    static String[] reverse(String[] strings) {
        String[] strings2 = new String[strings.length];
        int j = 0;
        for (int i = strings.length-1; i > -1; i--) {
            strings2[j] = strings[i];
            j++;
        }
        return strings2;
    }

    static String clean(String string) {
        char[] s = string.toCharArray();
        if ((s[0] == s[s.length - 1] && (s[0] == '\"' || s[0] == '\'')) || (s[0] == '(' && s[s.length - 1] == ')')) return string.substring(1, s.length - 1);
        return string;
    }

    static boolean isOperator(String s) {
        return !(Operators.getOperator(s.toCharArray()[0]) == Operators.operators.NONE);
    }

    static Operators.operators getOperator(String s) {
        return Operators.getOperator((s.toCharArray()[0]));
    }

    static boolean isInt(char[] charArr) {
        for (char c : charArr) {
            if (c != '.') {
                int d = Character.getNumericValue(c);
                if (d > 9 || d < 0) return false;
            }
        }
        return true;
    }

    static void setTempVal(String val) {
        if (val.equals("_programfunction_")) return;
        if (!isInt(val.toCharArray()) && clean(val).equals(val)) val = (getValue(val) == null) ? val : getValue(val);
        localItems.add(new Item("temp", val, isInt(val.toCharArray()) ? Item.types.INT : Item.types.STRING));
    }

    static boolean surroundedWithBrackets(char[] charArr) {
        int opening = 0;
        int closing = 0;
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i] == '(') opening = i;
            else if (charArr[i] == ')') closing = i;
        }
        return opening < closing;
    }

    static String[] getFunctionInfo(char[] charArrArg) {
        String name = "";String arg = "";
        int bracketsLevel = 0;
        boolean inside = false;
        for (char c : charArrArg) {
            if (c == '(') {
                arg += c;
                inside = true;
                bracketsLevel++;
                continue;
            }
            else if (c == ')') {
                bracketsLevel--;
                if (bracketsLevel == 0) {
                    arg += c;
                    break;
                }
            }
            if (!inside) name += c;
            else arg += c;
        }
        return new String[] {name, arg};
    }

    static String[] getArgs(String name) {
        name = name.split("\\[")[1].split("]")[0];
        return split(name);
    }

    static String dealWithFunctions(String function) {
        if (!surroundedWithBrackets(function.toCharArray())) return function;
        String[] info = getFunctionInfo(function.toCharArray());
        String name = "";
        if (!info[0].equals("")) name = clean(info[0]);
        String arg = clean(info[1]);

        if (name.equals("print")) Functions.printFunction(arg);
        else if (name.equals("input")) return Functions.inputFunction(arg);
        else if (name.equals("int")) return Functions.intFunction(arg);
        else if (name.equals("function")) {
            Functions.functionFunction(arg);
            return "_programfunction_";
        } else if (name.equals("")) {
            Functions.dealWithIt(arg);
            return clean(items.get(items.size() - 1).value);
        } else {
            for (Item i : items) {
                if (i.name.split("\\[")[0].equals(name)) {
                    String[] placeHolderArgs = getArgs(i.name);
                    String[] args = arg.split(" ");
                    arg = i.value;

                    for (int j = 0; j < placeHolderArgs.length; j++) arg = arg.replace(placeHolderArgs[j], args[j]);

                    Functions.dealWithIt(arg);
                    return clean(items.get(items.size() - 1).value);
                }
            }
        }

        return arg;
    }

    static String getValue(String s) {
        for (Item i : items) {
            if (i.name.equals(s)) {
                return i.value;
            }
        }
        return null;
    }

    static void deleteExistingName(String name) {
        if (getValue(name) != null) {
            int deletionIndex = 1000;
            for (Item item : items) if (item.name.equals(name)) deletionIndex = items.indexOf(item);
            if (deletionIndex != 1000) items.remove(deletionIndex);
        }
    }

    static boolean operate(String[] commands) {
        boolean working = true;

        Operators.operators nextOperator = Operators.operators.NONE;
        for (String command : commands) {
            if (!isOperator(command)) {
                command = dealWithFunctions(command);
                setTempVal(command);
            }
            if (nextOperator == Operators.operators.PLUS) {
                if (localItems.get(localItems.size() - 1).type == localItems.get(localItems.size() - 2).type && localItems.get(localItems.size() - 1).type == Item.types.STRING) localItems.add(new Item("temp", clean(localItems.get(localItems.size() - 1).value) + clean(localItems.get(localItems.size() - 2).value), Item.types.STRING));
                else if (localItems.get(localItems.size() - 1).type == localItems.get(localItems.size() - 2).type && localItems.get(localItems.size() - 1).type == Item.types.INT) localItems.add(new Item("temp", String.valueOf(Float.valueOf(localItems.get(localItems.size() - 1).value) + Float.valueOf(localItems.get(localItems.size() - 2).value)), Item.types.INT));
                else {
                    System.err.println("Values have to be of the same type.");
                    working = false;
                    break;
                }
            } else if (nextOperator == Operators.operators.MINUS) {
                if (localItems.get(localItems.size() - 1).type == localItems.get(localItems.size() - 2).type && localItems.get(localItems.size() - 1).type == Item.types.INT) localItems.add(new Item("temp", String.valueOf(Float.valueOf(localItems.get(localItems.size() - 1).value) - Float.valueOf(localItems.get(localItems.size() - 2).value)), Item.types.INT));
                else {
                    System.err.println("Values have to be of type int.");
                    working = false;
                    break;
                }
            } else if (nextOperator == Operators.operators.MULTIPLY) {
                if (localItems.get(localItems.size() - 1).type == localItems.get(localItems.size() - 2).type && localItems.get(localItems.size() - 1).type == Item.types.INT) localItems.add(new Item("temp", String.valueOf(Float.valueOf(localItems.get(localItems.size() - 1).value) * Float.valueOf(localItems.get(localItems.size() - 2).value)), Item.types.INT));
                else {
                    System.err.println("Values have to be of type int.");
                    working = false;
                    break;
                }
            } else if (nextOperator == Operators.operators.DIVIDE) {
                if (localItems.get(localItems.size() - 1).type == localItems.get(localItems.size() - 2).type && localItems.get(localItems.size() - 1).type == Item.types.INT) localItems.add(new Item("temp", String.valueOf(Float.valueOf(localItems.get(localItems.size() - 1).value) / Float.valueOf(localItems.get(localItems.size() - 2).value)), Item.types.INT));
                else {
                    System.err.println("Values have to be of type int.");
                    working = false;
                    break;
                }
            } else if (nextOperator == Operators.operators.POW) {
                if (localItems.get(localItems.size() - 1).type == localItems.get(localItems.size() - 2).type && localItems.get(localItems.size() - 1).type == Item.types.INT) localItems.add(new Item("temp", String.valueOf(Math.pow(Float.valueOf(localItems.get(localItems.size() - 1).value), Float.valueOf(localItems.get(localItems.size() - 2).value))), Item.types.INT));
                else {
                    System.err.println("Values have to be of type int.");
                    working = false;
                    break;
                }
            } else if (nextOperator == Operators.operators.EQUAL_TO) {
                deleteExistingName(commands[commands.length - 1]);
                items.add(new Item(localItems.get(localItems.size() - 1).value, localItems.get(localItems.size() - 2).value, localItems.get(localItems.size() - 2).type));
            }
            nextOperator = getOperator(command);
        }
        localItems.clear();

        return working;
    }
}
