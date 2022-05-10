package OperatingSystems;

import java.io.*;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.*;
import java.util.Map.Entry;


public class Interpreter {
    Queue<String> readyQueue;
    Queue<String> blockedQueue;
    Mutex mutex;
    Scheduler scheduler;
    //Add the programs to the hashMap
    HashMap<Integer, Pair> programs = new HashMap<Integer, Pair>() {
        {
            put(1, new Pair("src/Program_1", 0));
            put(2, new Pair("src/Program_2", 0));
            put(3, new Pair("src/Program_3", 0));
        }

    };

    HashMap<Integer, ArrayList<Pair>> programVariables = new HashMap<Integer, ArrayList<Pair>>() {{
        put(1, new ArrayList<Pair>());
        put(2, new ArrayList<Pair>());
        put(3, new ArrayList<Pair>());

    }};
    boolean isRunning;
    String[] allThePrograms = {"src/Program_1", "src/Program_2", "src/Program_3"};
    int currentFileLines;
    int currentProgramLines;

    public Interpreter() {
        readyQueue = new LinkedList<>();
        blockedQueue = new LinkedList<>();
        mutex = new Mutex();
        scheduler = new Scheduler();

    }


    public Queue<String> getReadyQueue() {
        return readyQueue;
    }

    public Queue<String> getBlockedQueue() {
        return blockedQueue;
    }

    public void assign(Object x, Object y, int id, boolean flag, int i) {
        programVariables.get(id).add(new Pair((String) x, y));

    }

    public void print(Object a, int id) {
            System.out.println("Am Here in the print ONly method");
        if (a instanceof Integer) {
            System.out.println("This is the printed integer result " + (int) a);
        } else System.out.println("This is the printed string result " + (String) a);
    }

    public static void main(String[] args) throws IOException {
        Interpreter interpreter = new Interpreter();
        interpreter.setAllPrograms(interpreter.allThePrograms);

    }


    public HashMap<Integer, Pair> getPrograms() {
        return programs;
    }

    public HashMap<Integer, ArrayList<Pair>> getProgramVariables() {
        return programVariables;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public int getCurrentFileLines() {
        return currentFileLines;
    }

    public void setCurrentFileLines(int currentFileLines) {
        this.currentFileLines = currentFileLines;
    }

    public void readProgram(String Program, int id) throws IOException {
        isRunning = true;
        String result = readFile(Program, id);
//        String[] terms = result.trim().split("\\s+");
        String[] lines = result.trim().split("\\n");
        System.out.println("The lines of Program: "+" "+id+Arrays.toString(lines));
        currentFileLines = lines.length;
        int i = (int) programs.get(id).value;
        Object firstInput = null;
        Object secondInput = null;
            int j = 0;
            if(i>=lines.length){
                return ;
            }
            String[] terms = lines[i].trim().split("\\s+");
        System.out.println("The terms of Program: "+" "+id+Arrays.toString(terms));


        switch (terms[0]) {
                case "semWait":
                    scheduler.setCounter(scheduler.getCounter() + 1);
                    if (mutex.semWait(terms[j + 1], id, this, scheduler)) {
                    } else {
                        programs.get(id).value = i;
                        i--;
                    }
                    i++;
                    break;
                case "assign":
                    currentProgramLines++;
                    scheduler.setCounter(scheduler.getCounter() + 1);
                    String firstParams = terms[j + 1];
                    String secondParams = terms[j + 2];
                    if (secondParams.equals("readFile")) {
                        String fourthInput = (String) getValueOfVariables(id, terms[j + 3]);
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
                    i++;
                    break;
                case "semSignal":
                    currentProgramLines++;
                    scheduler.setCounter(scheduler.getCounter() + 1);
                    mutex.semSignal(terms[j + 1], this);
                    i++;
                    break;
                case "writeFile":
                    currentProgramLines++;
                    scheduler.setCounter(scheduler.getCounter() + 1);
                    firstInput = getValueOfVariables(id, terms[j + 1]);
                    secondInput = getValueOfVariables(id, terms[j + 2]);
                    writeFile((String) firstInput, (String) secondInput, id);
                    i++;
                    break;
                case "print":
                    System.out.print("Gowa print:");
                    currentProgramLines++;
                    firstInput = getValueOfVariables(id, terms[j + 1]);
                    System.out.print("Gowa print w variable is :firstinput");
                    print(firstInput, id);
                    scheduler.setCounter(scheduler.getCounter() + 1);
                    i++;
                    break;
                case "printFromTo":
                    currentProgramLines++;
                    scheduler.setCounter(scheduler.getCounter() + 1);
                    firstInput = getValueOfVariables(id, terms[j + 1]);
                    secondInput = getValueOfVariables(id, terms[j + 2]);
                    printFromTo(Integer.parseInt(((String) firstInput)), Integer.parseInt(((String) secondInput)), id);
                    i++;
                    break;
            }

            programs.get(id).value = i;
            System.out.println("Program: "+id+" Standing in "+i);
            scheduler.scheduler(allThePrograms, this, id);


        isRunning = false;
    }

    private Object getValueOfVariables(int id, String variableName) {
        ArrayList<Pair> result = programVariables.get(id);
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).variable.equals(variableName)) {
                return programVariables.get(id).get(i).value;
            }
        }
        return null;
    }

    public void runProgram(String programName) throws IOException {
        for (Integer key : programs.keySet()) {
            if (programs.get(key).variable.equals(programName)) {
                readProgram(programName, key);
                break;

            }
        }
    }

    public void setAllPrograms(String[] programs) throws IOException {
        scheduler.scheduler(programs, this, 0);
    }

    public String readFile(String argument, int id) throws IOException {
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


    public void writeFile(String filePath, String value, int id) throws IOException {
        try {
            File file = new File(filePath);
            file.createNewFile();
            PrintWriter pw = new PrintWriter(file);
            pw.println(value);
            for (int i = 0; i < programVariables.get(id).size(); i++) {
                if (programVariables.get(id).get(i).variable == filePath) {
                    programVariables.get(id).get(i).value = value;
                    break;
                }
            }
            pw.close();
        } catch (IOException e) {
            System.out.println("An error occurred");
            e.printStackTrace();
        }
    }


    public void printFromTo(int a, int b, int id) {
        System.out.println("Starting the printFromTo Method");
        a++;
        while (a < b) {
            System.out.print(a + " ");
            a++;
        }
    }


}