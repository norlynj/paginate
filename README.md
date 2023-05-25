CMSC 125 Page Replacement Simulator
- program name: Paginate
- developers: Norlyn Jane Castillo, Jannah Esplanada, Charissa Mae Madriaga

## 🛠️ How to compile and run the source code

a. navigate to the src folder directory in the paginate/src

b. copy the following lines of instructions to compile the program

For Linux:
```
javac -cp .:lib/pdfbox.jar -d . com/model/*.java
javac -cp .:lib/pdfbox.jar -d . com/view/component/*.java
javac -cp .:lib/pdfbox.jar -d . com/view/*.java
javac -cp .:lib/pdfbox.jar -d . Main.java

```

For Windows:
```
 javac -cp ".;lib/pdfbox.jar" -d . com/model/*.java com/view/component/*.java com/view/*.java Main.java
 
```

c. lastly, run the program using the compiled Main class

For Linux:
```
java -cp .:lib/pdfbox.jar Main
```
------


For Windows:
```
java -cp ".;lib/pdfbox.jar" Main
```

Note: there are test cases under the resources/text directory that corresponds to the examples in the docx file