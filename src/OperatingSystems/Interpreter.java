package OperatingSystems;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.*;
import java.util.Map.Entry;


public class Interpreter {
    static List<String> instructions = Arrays.asList("print", "assign",
        "writeFile",
        "readFile", "printFromTo",
        "semWait", "semSignal");
    static Queue<String> readyQueue = new LinkedList<>();
    static Queue<String> blockedQueue = new LinkedList<>();
    //Add the programs to the hashMap
    static HashMap<Integer, Pair> programs = new HashMap<Integer, Pair>() {
        {
            put(1, new Pair("src/Program_1", 0));
            put(2, new Pair("src/Program_2", 0));
            put(3, new Pair("src/Program_3", 0));
        }

    };

    static HashMap<Integer, ArrayList<Pair>> programVariables = new HashMap<Integer, ArrayList<Pair>>() {{
        put(1, new ArrayList<Pair>());
        put(2, new ArrayList<Pair>());
        put(3, new ArrayList<Pair>());

    }};
    static boolean isRunning;
    static String[] allThePrograms = {"src/Program_1","src/Program_2","src/Program_3"};
    static int currentFileLines;


    public static Queue<String> getReadyQueue() {
        return readyQueue;
    }

    public static Queue<String> getBlockedQueue() {
        return blockedQueue;
    }

    public static void assign(Object x, Object y, int id, boolean flag, int i) {
        programVariables.get(id).add(new Pair((String) x, y));

    }

    public static void print(Object a, int id) {
        if (!Mutex.semWait("userOutput", id)) {
            Mutex.getUserOutputBlockedQueue().add(programs.get(id).variable);
            blockedQueue.add(programs.get(id).variable);

            return;
        }
        if (a instanceof Integer) {
            System.out.println("This is the printed integer result " + a);
        } else System.out.println("This is the printed string result " + a);
    }

    public static void main(String[] args) throws IOException {
        String x = readFile("src/Program_1", 1);
//        System.out.println(x);
//        setAllPrograms(allThePrograms);
//        runProgram(s);
        System.out.println(programVariables.get(1));
        String s = readFile("src/Program_1",0);
        String[] terms = s.trim().split("\\n");
        System.out.println(Arrays.toString(terms));
        System.out.println(terms.length);




    }


    public static HashMap<Integer, Pair> getPrograms() {
        return programs;
    }

    public static HashMap<Integer, ArrayList<Pair>> getProgramVariables() {
        return programVariables;
    }

    public static void readProgram(String Program, int id) throws IOException {
        isRunning = true;
        String result = readFile(Program, id);
        String[] terms = result.trim().split("\\s+");
        String[] lines = result.trim().split("\\n");
        currentFileLines=lines.length;

        int i = (int) programs.get(id).value;
        while (i < terms.length) {
            switch (terms[i]) {

                case "semWait":
                    Scheduler.counter++;
                    if (Mutex.semWait(terms[i + 1], id)) {
                    } else {
                        programs.get(id).value = i;
                        i--;
                    }
                    break;
                case "assign":
                    Scheduler.counter++;
                    String firstParams = terms[i + 1];
                    String secondParams = terms[i + 2];
                    if (secondParams.equals("readFile")) {
                        String fourthInput = terms[i + 3];
                        String readResult = readFile(fourthInput, id);
                        if (!readResult.equals("The resource is blocked")) {
                            assign(firstParams, readResult, id, true, i);
                        }
                    } else if (secondParams.equals("input")) {
                        Scanner sc = new Scanner(System.in);
                        System.out.println("Enter a value: ");
                        assign(firstParams, sc.next(), id, false, i);
                    } else {
                        assign(firstParams, secondParams, id, true, i);
                    }
//                    Scheduler.scheduler(allThePrograms);
                    break;
                case "semSignal":
                    Scheduler.counter++;
                    Mutex.semSignal(terms[i + 1]);
//                    Scheduler.scheduler(allThePrograms);
                    break;

                case "writeFile":
                    Scheduler.counter++;
                    writeFile(terms[i + 1], terms[i + 2], id);
//                    Scheduler.scheduler(allThePrograms);
                    break;


                case "print":
                    Scheduler.counter++;
                    print(terms[i + 1], id);
//                    Scheduler.scheduler(allThePrograms);
                    break;
                case "printFromTo":
                    Scheduler.counter++;
                    //TODO use the variables of this program not the terms
                    printFromTo(Integer.parseInt((String) programVariables.get(id).get(0).value) , Integer.parseInt((String) programVariables.get(id).get(1).value) , id);
//                    Scheduler.scheduler(allThePrograms);
                    break;

            }
            i++;
            programs.get(id).value=i;
            Scheduler.scheduler(allThePrograms);

        }
        isRunning = false;
    }

    public static void runProgram(String programName) throws IOException {

            for (Integer key : programs.keySet()) {
                if (programs.get(key).variable == programName) {
                    readProgram(programName, key);
                    break;

            }
        }
    }

    public static void setAllPrograms(String[] programs) throws IOException {
        Scheduler.scheduler(programs);

    }

    public static String readFile(String argument, int id) throws IOException {

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


    public static void writeFile(String filePath, String value, int id) throws IOException {
        try {
            if (!Mutex.semWait("file", id)) {
                Mutex.getFileBlockedQueue().add(programs.get(id).variable);
                blockedQueue.add(programs.get(id).variable);
            } else {
                ArrayList<Pair> result = programVariables.get(id);
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).variable == filePath) {
                        Object x = programVariables.get(id).get(i).value;
                        programVariables.get(id).get(i).value = x + value;
                        break;
                    }
                }
                FileWriter newWriter = new FileWriter(filePath + "" + ".txt");
                newWriter.write(value);
                newWriter.close();
                Mutex.semWait("file", id);
            }

        } catch (IOException e) {
            System.out.println("An error occurred");
            e.printStackTrace();
        }
    }


//    public static void print(int a) {
//        System.out.println("The Value of the first value is: " + a);
//
//    }

    public static void printFromTo(int a, int b, int id) {
            a++;
            while (a < b) {
                System.out.print(a + " ");
                a++;
            }

    }


}
