import java.io.*;
import java.util.*;

public class EffNav {

  /* Global variables */
  Map<char[], List<Integer>> map;
  String str1;
  String str2;
  int[][] matrix;
  Scanner scanner;
  StringBuilder sb;
  int delta = 30;
  Map<Character, Integer> charMap;
  // int[][] opt;
  String finalStr1;
  String finalStr2;

  public EffNav() {
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
  /* reads data from input file */
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

  /* generates the strings from the input data format */
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

  /* writes output, time complexity and space complexity to file */
  public void writeFile(
    String fileName,
    double time,
    double space,
    int minCost
  ) {
    try {
      FileWriter myWriter = new FileWriter(fileName);
      String s =
        minCost +
        "\n" +
        finalStr1 +
        "\n" +
        finalStr2 +
        "\n" +
        time +
        "\n" +
        space;
      myWriter.write(s);
      myWriter.close();
    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    } // catch()
  } // writeFile()

  /* gets the memory space used in KB */
  private static double getMemory() {
    double total = Runtime.getRuntime().totalMemory();
    return (total - Runtime.getRuntime().freeMemory()) / 10e3;
  } // getMe

  /* gets the time in seconds */
  private static double getTime() {
    return System.nanoTime() / 10e6;
  }

  /* handles the core sequence alignment functionality */
  public int alignmentBasic(String str1, String str2) {
    int[][] opt = new int[str1.length() + 1][str2.length() + 1];

    for (int i = 0; i <= str1.length(); i++) {
      opt[i][0] = delta * i;
    }
    for (int i = 0; i <= str2.length(); i++) {
      opt[0][i] = delta * i;
    }
    for (int i = 1; i <= str1.length(); i++) {
      for (int j = 1; j <= str2.length(); j++) {
        char c1 = str1.charAt(i - 1);
        char c2 = str2.charAt(j - 1);
        int a = charMap.get(c1);
        int b = charMap.get(c2);
        opt[i][j] =
          Math.min(
            matrix[a][b] + opt[i - 1][j - 1],
            delta + Math.min(opt[i][j - 1], opt[i - 1][j])
          );
      }
    }
    getFinalStringBasic(str1, str2, opt);
    return opt[str1.length()][str2.length()];
  }

  /* gets the final string */
  private void getFinalStringBasic(String str1, String str2, int[][] opt) {
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

  String reverseString(String s) {
    StringBuilder input1 = new StringBuilder(s);
    input1.reverse();
    return input1.toString();
  }

  public int[] costOfSplit(String x, String y) {
    int optVal[] = new int[y.length() + 1];

    for (int i = 0; i <= y.length(); ++i) {
      optVal[i] = delta * i;
    }

    for (int i = 1; i <= x.length(); i++) {
      int[] temp = new int[y.length() + 1];
      temp[0] = delta * i;
      for (int j = 1; j <= y.length(); j++) {
        char c1 = x.charAt(i - 1);
        char c2 = y.charAt(j - 1);
        int a = charMap.get(c1);
        int b = charMap.get(c2);
        temp[j] =
          Math.min(
            matrix[a][b] + optVal[j - 1],
            delta + Math.min(optVal[j], temp[j - 1])
          );
      }
      optVal = temp;
    }

    return optVal;
  }

  public int sequenceAlignmentEfficient(String x, String y) {
    if (x.length() <= 2 || y.length() <= 2) {
      return alignmentBasic(x, y);
    }

    String xLeft = x.substring(0, x.length() / 2);
    String xRight = x.substring(x.length() / 2);

    int[] leftToRight = costOfSplit(xLeft, y);
    int[] rightToLeft = costOfSplit(reverseString(xRight), reverseString(y));

    int splitIndex = -1;
    int minCost = Integer.MAX_VALUE;
    int n = leftToRight.length;

    for (int i = 0; i < n; i++) {
      if (leftToRight[i] + rightToLeft[n - i - 1] < minCost) {
        minCost = leftToRight[i] + rightToLeft[n - i - 1];
        splitIndex = i;
      }
    }

    int left = sequenceAlignmentEfficient(xLeft, y.substring(0, splitIndex));
    int right = sequenceAlignmentEfficient(xRight, y.substring(splitIndex));

    return left + right;
  }

  /* main function */
  public static void main(String[] args) throws IOException {
    String inputFilename = args[0];
    String outputFilename = args[1];
    double startSpace = getMemory();
    double startTime = getTime();
    EffNav eff = new EffNav();
    File inputFile = new File(inputFilename);
    eff.readFile(inputFile);
    int minCost = eff.sequenceAlignmentEfficient(eff.str1, eff.str2);
    double endSpace = getMemory();
    double endTime = getTime();
    double space = endSpace - startSpace;
    double time = endTime - startTime;
    File outputFile = new File(outputFilename);
    outputFile.createNewFile();
    eff.writeFile(outputFilename, time, space, minCost);
  }
}