package OperatingSystems;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

public class Interpreter {
    static List<String> instructions = Arrays.asList("print", "assign",
        "writeFile",
        "readFile", "printFromTo",
        "semWait", "semSignal");


    public static void print(String a) {
        System.out.println("The Value of the first value is: " + a);
    }

    public static int assign(int x, int y) {
        x = y;
        return x;
    }

    public static String assign(String x, String y) {
        x = y;
        return x;
    }

    public void readInstruction(String instruction) {
        switch (instruction) {
            case "print":
                break;
            case "assign":

        }
    }



    public String readFile(String argument, int pid) throws IOException {
        String filePath = argument;
        filePath += ".txt"; //Program.txt
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        //assign
        //print
        //Null
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        // delete the last new line separator
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();
        return stringBuilder.toString();
    }

    public void writeFile(String filePath, String value) throws IOException {
        try {
            FileWriter newWriter = new FileWriter(filePath + "" + ".txt");
            newWriter.write(value);
            newWriter.close();

        } catch (IOException e) {
            System.out.println("An error occurred");
            e.printStackTrace();
        }
    }


    public static void print(int a) {
        System.out.println("The Value of the first value is: " + a);


    }

    public static void printFromTo(int a, int b) {
        a++;
        while (a < b) {
            System.out.print(a + " ");
            a++;
        }
    }
}
