FILES="./SampleTestCases/input*.txt";

# compiling programs
javac Basic.java
javac EfficientSolutionBiola.java 

# generating results from basic algorithm
for f in $FILES
do
java Basic "$f" "basic_results.txt"
done

# generating result from efficient algorithm
for f in $FILES
do
java EfficientSolutionBiola "$f" "efficient_results.txt"
done