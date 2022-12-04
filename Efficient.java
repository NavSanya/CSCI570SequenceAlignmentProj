import java.io.*;
import java.util.*;

public class Efficient {

  /* Global variables */
  Map<char[], List<Integer>> map;
  String str1;
  String str2;
  int[][] matrix;
  StringBuilder sb;
  int delta = 30;
  Map<Character, Integer> charMap;
  String finalStr1;
  String finalStr2;
  Scanner scanner;

  public Efficient() {
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

  public int align(String x, String y) {
    int[][] opt = new int[x.length() + 1][y.length() + 1];
    for (int i = 0; i <= x.length(); i++) {
      opt[i][0] = delta * i;
    }
    for (int i = 0; i <= y.length(); i++) {
      opt[0][i] = delta * i;
    }
    for (int i = 1; i <= x.length(); i++) {
      for (int j = 1; j <= y.length(); j++) {
        char c1 = x.charAt(i - 1);
        char c2 = y.charAt(j - 1);
        int a = charMap.get(c1);
        int b = charMap.get(c2);
        opt[i][j] =
          Math.min(
            matrix[a][b] + opt[i - 1][j - 1],
            delta + Math.min(opt[i][j - 1], opt[i - 1][j])
          );
      }
    }
    return opt[x.length()][y.length()];
  }

  public int findSplitPoint(String x, String y) {
    int midPointX = x.length() / 2;
    List<Integer> xLeftValues = new ArrayList<>();
    List<Integer> xRightValues = new ArrayList<>();
    String xLeft = x.substring(0, midPointX);
    String xRight = x.substring(midPointX, x.length());
    xRight = new StringBuilder(xRight).reverse().toString();

    List<String> ySubStringsLeft = findAllSubStrings(y, false);
    for (String subString : ySubStringsLeft) {
      int value = align(xLeft, subString);
      xLeftValues.add(value);
    }
    List<String> ySubStringsRight = findAllSubStrings(y, true);
    for (String subString : ySubStringsRight) {
      int value = align(xRight, subString);
      xRightValues.add(value);
    }
    int n = xLeftValues.size();
    int optimalValue = Integer.MAX_VALUE;
    int optimalSplitIndex = 0;
    for (int i = 0; i < n; i++) {
      int val = xLeftValues.get(i) + xRightValues.get(n - i - i);
      if (optimalValue > val) {
        optimalSplitIndex = ySubStringsLeft.get(i).length() - 1;
        optimalValue = val;
      }
    }
    return optimalSplitIndex;
  }

  private List<String> findAllSubStrings(String str, boolean reverse) {
    List<String> result = new ArrayList<>();
    if (reverse == false) {
      for (int j = 0; j < str.length(); j++) {
        result.add(str.substring(0, j + 1));
      }
    } else {
      str = new StringBuilder(str).reverse().toString();
      for (int j = 0; j < str.length(); j++) {
        result.add(str.substring(0, j + 1));
      }
    }

    return result;
  }

  /* gets the final string */
  public void getFinalString(String str1, String str2, int[][] opt) {
    int i = str1.length();
    int j = str2.length();
    StringBuilder input1 = new StringBuilder();
    StringBuilder input2 = new StringBuilder();
    while (i != 0 || j != 0) {
      if (
        (i >= 1) &&
        (j >= 1) &&
        (
          opt[i][j] ==
          matrix[charMap.get(str1.charAt(i - 1))][(
              charMap.get(str2.charAt(j - 1))
            )] +
          opt[i - 1][j - 1]
        )
      ) {
        input1.append(str1.charAt(i - 1));
        input2.append(str2.charAt(j - 1));
        i--;
        j--;
      } else if (j >= 1 && opt[i][j] == delta + opt[i][j - 1]) {
        input1.append('_');
        input2.append(str2.charAt(j - 1));
        j--;
      } else { // gap
        input1.append(str1.charAt(i - 1));
        input2.append('_');
        i--;
      }
    }
    input1.reverse();
    finalStr1 = input1.toString();
    input2.reverse();
    finalStr2 = input2.toString();
  }

  private int alignment(String x, String y) {
    if (y.length() < 2) {
      return 0;
    }
    int splitY = findSplitPoint(str1, str2);
    System.out.println("Split Y at: " + splitY);
    int midX = x.length() / 2;
    int leftHalf = alignment(x.substring(0, midX), y.substring(0, splitY));
    int rightHalf = alignment(
      x.substring(midX, x.length()),
      y.substring(splitY, y.length())
    );
    return leftHalf + rightHalf;
  }

  private void generateStrings() {
    boolean first = true;
    for (Map.Entry<char[], List<Integer>> entry : map.entrySet()) {
      char[] str = entry.getKey();
      List<Integer> indices = entry.getValue();
      sb = new StringBuilder(new String(str));
      for (int i : indices) {
        sb.insert(i + 1, str);
        str = sb.toString().toCharArray();
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
          map.put(str.toCharArray(), list);
        }
      }
      scanner.close();
    } catch (FileNotFoundException e) {
      System.out.println("File not found");
    }
    generateStrings();
  }

  /* gets the memory space used in KB */
  private static double getMemory() {
    double total = Runtime.getRuntime().totalMemory();
    return (total - Runtime.getRuntime().freeMemory()) / 10e3;
  }

  /* gets the time in seconds */
  private static double getTime() {
    return System.nanoTime() / 10e6;
  }

  /* writes output, time complexity and space complexity to file */
  public void writeFile(String fileName, double time, double space, int s) {
    try {
      FileWriter myWriter = new FileWriter(fileName);
      myWriter.write("Cost: " + s);
      myWriter.close();
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws IOException {
    String inputFilename = "SampleTestCases/input1.txt";
    String outputFilename = "biola2.txt";
    double startSpace = getMemory();
    double startTime = getTime();
    Efficient efficient = new Efficient();
    File inputFile = new File(inputFilename);
    efficient.readFile(inputFile);
    int cost = efficient.alignment(efficient.str1, efficient.str2);
    double endSpace = getMemory();
    double endTime = getTime();
    double space = endSpace - startSpace;
    double time = endTime - startTime;
    File outputFile = new File(outputFilename);
    outputFile.createNewFile();
    efficient.writeFile(outputFilename, time, space, cost);
  }
}
