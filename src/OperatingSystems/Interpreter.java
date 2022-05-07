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
    static Queue<String> readyQueue = new LinkedList<>();
    static Queue<String> blockedQueue = new LinkedList<>();
    //Add the programs to the hashMap
    static HashMap<Integer, String> programs = new HashMap<Integer, String>() {{
        put(1, "Program_1.txt");
        put(2, "Program_2.txt");
        put(3, "Program_3.txt");
    }};
    //Make static variables


    public static void print(String a) {
        if (Mutex.semWait("print")) {
            System.out.println("The Value of the first value is: " + a);
        } else {
            //blockedQueue;
        }
    }


    public static int assign(int x, int y) {
        if (Mutex.semWait("assign")) {
            x = y;
            return x;
        } else {
//            blockedQueue.add();
        }
        return 0;
    }

    public static void assign(String x, String y, int id, boolean flag) {
        if (flag == false) {
            if (!Mutex.semWait("userInput")) {
                blockedQueue.add(programs.get(id));
            } else {
                x = y;

            }
        } else {
            x = y;
        }

    }

    public void readProgram(String Program, int id) throws IOException {
        String[] terms = Program.trim().split("\\s+");
        int i = 0;
        while (i < terms.length) {
            switch (terms[i]) {
                case "semWait":
                    if (Mutex.semWait(terms[i + 1])) {
                    } else {
                        blockedQueue.add(programs.get(id));
                    }
                    break;
                case "assign":
                    String firstParams = terms[i + 1];
                    String secondParams = terms[i + 2];
                    if (secondParams.equals("readFile")) {
                        String fourthInput = terms[i + 3];
                        String readResult = readFile(fourthInput, id);
                        if (!readResult.equals("The resource is blocked")) {
                            assign(firstParams, readResult, id, true);
                        }
                    } else if (secondParams.equals("input")) {
                        Scanner sc = new Scanner(System.in);
                        assign(firstParams, sc.next(), id, false);
                    } else {
                        assign(firstParams, secondParams, id, true);
                    }


            }
        }
    }


    public String readFile(String argument, int id) throws IOException {
        if (!Mutex.semWait("file")) {
            blockedQueue.add(programs.get(id));
        } else {

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
        return "The resource is blocked";
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

    public static void main(String[] args) {

    }

}
