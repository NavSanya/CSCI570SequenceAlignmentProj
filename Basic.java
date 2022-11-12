import java.io.*;
import java.util.*;

public class Basic {

  /* Global variables */
  Map<String, List<Integer>> map;
  String str1;
  String str2;
  int[][] matrix;
  Scanner scanner;
  StringBuilder sb;
  int delta = 30;
  Map<Character, Integer> charMap;
  int[][] opt;
  String finalStr1;
  String finalStr2;

  public Basic() {
    this.map = new HashMap<>();
    this.sb = new StringBuilder();
    matrix =
      new int[][] {
        { 0, 110, 48, 94 },
        { 110, 0, 118, 48 },
        { 48, 118, 0, 110 },
        { 94, 48, 110, 0 },
      };
    this.charMap = new HashMap<>();
    charMap.put('A', 0);
    charMap.put('C', 1);
    charMap.put('G', 2);
    charMap.put('T', 3);
    finalStr1 = "";
    finalStr2 = "";
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
        this.str2 = sb.toString();
        sb.setLength(0);
        first = false;
      } else {
        this.str1 = sb.toString();
      }
    }
  }

  public void writeFile(String fileName, double time, double space) {
    try {
      FileWriter myWriter = new FileWriter(fileName);
      String s =
        opt[str1.length() - 1][str2.length() - 1] + "\n" + finalStr1 + "\n" + finalStr2 + "\n" + time + "\n" + space;
      myWriter.write(s);
      myWriter.close();
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  private static double getMemory() {
    double total = Runtime.getRuntime().totalMemory();
    return (total - Runtime.getRuntime().freeMemory()) / 1e3;
  }

  private static double getTime() {
    return System.nanoTime() / 1e6;
  }

  public void alignment() {
    opt = new int[str1.length() + 1][str2.length() + 1];

    for (int i = 0; i <= str1.length(); i++) {
      opt[i][0] = delta * i;
    }
    for (int i = 0; i <= str2.length(); i++) {
      opt[0][i] = delta * i;
    }
    for (int i = 1; i <= str1.length(); i++) {
      for (int j = 1; j <= str2.length(); j++) {
        char c1 = str1.charAt(i - 1);/* changed */
        char c2 = str2.charAt(j - 1);/* changed */
        int a = charMap.get(c1);
        int b = charMap.get(c2);
        opt[i][j] =
          Math.min(
            matrix[a][b] + opt[i - 1][j - 1],
            delta + Math.min(opt[i][j - 1], opt[i - 1][j])
          );
      }
    }
  }

  private void getFinalString() {
    int l1 = str1.length();
    int l2 = str2.length();
    int i = l1;
    int j = l2;
    finalStr1 = "";
    finalStr2 = "";
    while (i != 0 || j != 0) {
      //mismatch check
      if (
        i >= 1 &&
        j >= 1 &&
        opt[i][j] ==
        matrix[charMap.get(str1.charAt(i - 1))][charMap.get(
            str2.charAt(j - 1)
          )] +
        opt[i - 1][j - 1]
      ) {
        finalStr1 += (str1.charAt(i - 1));
        finalStr2 += (str2.charAt(j - 1));
        i--;
        j--;
      } else if (j >= 1 && opt[i][j] == delta + opt[i][j - 1]) { //gap
        finalStr1 = finalStr1 + '_';
        finalStr2 = finalStr2 + str2.charAt(j - 1);
        j--;
      } else { //gap
        finalStr1 = finalStr1 + str1.charAt(i - 1);
        finalStr2 = finalStr2 + '_';
        i--;
      }
    }

    StringBuilder input1 = new StringBuilder();
    input1.append(finalStr1);
    input1.reverse();
    finalStr1 = input1.toString();

    StringBuilder input2 = new StringBuilder();
    input2.append(finalStr2);
    input2.reverse();
    finalStr2 = input2.toString();
  }

  public static void main(String[] args) throws IOException {
    double startSpace = getMemory();
    double startTime = getTime();
    String iFN = args[0];
    Basic basic = new Basic();
    File file = new File(iFN);
    basic.readFile(file);
    basic.alignment();
    basic.getFinalString();
    double endSpace = getMemory();
    double endTime = getTime();
    double space = endSpace - startSpace;
    double time = endTime - startTime;
    System.out.println("Strings: ");
    System.out.println("String 1: " + basic.finalStr1);
    System.out.println("String 2: " + basic.finalStr2);
    int n1 = basic.str1.length();
    int n2 = basic.str2.length();
    System.out.println(basic.opt[n1 - 1][n2 - 1]);
    System.out.println(
      "Complexity analysis: \nTime: " +
      time +
      " milliseconds\nSpace: " +
      space +
      " KB"
    );
    File myObj;
    String ofN = args[1];
    myObj = new File(ofN);
    myObj.createNewFile();

    basic.writeFile(ofN, time, space);
  }
}
