import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

public class Basic {
    /* Global variables */
    Map<String, List<Integer>> map;
    String str1;
    String str2;
    int[][] matrix;
    Scanner scanner;
    StringBuilder sb;

    public Basic() {
        this.map = new HashMap<>();
        this.sb = new StringBuilder();
    }

    /* Functions */
    public void readFile(File file) {
        try {
            this.scanner = new Scanner(file);
            List<Integer> list = new ArrayList<>();
            while (scanner.hasNextLine()) {
                String str = scanner.nextLine();
                if (Character.isDigit(str.charAt(0))) {
                    list.add(Integer.parseInt(str));
                } else {
                    list = new ArrayList<>();
                    map.put(str, list);
                }
            }
            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        generateStrings();
    }

    private void generateStrings() {
        boolean first = true;
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            String str = entry.getKey();
            List<Integer> indices = entry.getValue();
            sb = new StringBuilder(str);
            for (int i : indices) {
                sb.insert(i + 1, str);
                str = sb.toString();
            }
            if (first) {
                this.str1 = sb.toString();
                sb.setLength(0);
                first = false;
            } else {
                this.str2 = sb.toString();
            }
        }
    }

    public void writeFile() {

    }

    private static double getMemory() {
        double total = Runtime.getRuntime().totalMemory();
        return (total - Runtime.getRuntime().freeMemory()) / 10e3;
    }

    private static double getTime() {
        return System.nanoTime() / 10e6;
    }

    public double alignment() {
        return 0.0;
    }

    public static void main(String[] args) {
        double startSpace = getMemory();
        double startTime = getTime();
        Basic basic = new Basic();
        File file = new File("SampleTestCases/input1.txt");
        basic.readFile(file);
        double endSpace = getMemory();
        double endTime = getTime();
        double space = endSpace - startSpace;
        double time = endTime - startTime;
        System.out.println("Strings: ");
        System.out.println("String 1: " + basic.str1);
        System.out.println("String 2: " + basic.str2);
        System.out.println("Complexity analysis: \nTime: " + time + " milliseconds\nSpace: " + space + " KB");
    }

}