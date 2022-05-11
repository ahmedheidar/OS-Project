package OperatingSystems;

import java.io.*;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.*;
import java.util.Map.Entry;


public class Interpreter {

    List<String> instructions = Arrays.asList("semWait", "semSignal", "print", "printFromTo", "input", "readFile",
            "writeFile", "assign"
    );
    Queue<String> readyQueue;
    Queue<String> blockedQueue;
    Mutex mutex;
    Scheduler scheduler;

    //Add the programs to the hashMap
    HashMap<Integer, StackPairs> programs = new HashMap<Integer, StackPairs>() {
        {
            put(1, new StackPairs("src/Program_1", new Stack<>()));
            put(2, new StackPairs("src/Program_2", new Stack<>()));
            put(3, new StackPairs("src/Program_3", new Stack<>()));
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

    public void assign(Object x, Object y, int id) {
        programVariables.get(id).add(new Pair((String) x, y));

    }

    public void print(Object a) {
        System.out.println("Am Here in the print ONly method");
        if (a instanceof Integer) {
            System.out.println("This is the printed integer result " + (int) a);
        } else System.out.println("This is the printed string result " + (String) a);
    }

    public static void main(String[] args) throws IOException {
        Interpreter interpreter = new Interpreter();
        interpreter.setAllPrograms(interpreter.allThePrograms);
//        String result = interpreter.readFile("src/Program_1", 1);
//        String[] lines = result.trim().split("\\n+");
//        Stack<String> stack = new Stack<>();
//        for(int i= lines.length-1;i>=0;i--){
//        String[] terms = lines[i].trim().split("\\s+");
//            System.out.println(Arrays.toString(terms));
//            for (String term : terms) {
//                stack.push(term);
//            }
//        }


    }


    public HashMap<Integer, StackPairs> getPrograms() {
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


    public void readProgram2(String program, int id) throws IOException {
        isRunning = true;
        String result = readFile(program);
        String[] lines = result.trim().split("\\n+");

        String op1 = "";
        String op2 = "";
        while (!programs.get(id).stack.isEmpty()) {
            String currentInstruction = programs.get(id).stack.pop();
            if (!instructions.contains(currentInstruction)) {
                if (op1 == "" || op1.isEmpty()) {
                    op1 = currentInstruction;

                } else {
                    op2 = currentInstruction;
                }
            } else {
                String res="";
                String res2="";
                switch (currentInstruction) {
                    case "input":
                        scheduler.setCounter(scheduler.getCounter() + 1);
                        System.out.println();
                        System.out.println("Please Enter Value");
                        Scanner sc = new Scanner(System.in);
                        String item = sc.next();
                        programs.get(id).stack.push(item);
                        break;

                    case "semWait":
                        scheduler.setCounter(scheduler.getCounter() + 1);
                        if (!mutex.semWait(op1, id, this, scheduler)) {
                            programs.get(id).stack.push("semWait");
                            programs.get(id).stack.push(op1);
                            op1 = "";
                        }
                        break;
                    case "semSignal":
                        scheduler.setCounter(scheduler.getCounter() + 1);
                        mutex.semSignal(op1, this);
                        op1 = "";

                        break;

                    case "assign":
                        scheduler.setCounter(scheduler.getCounter() + 1);
                        assign(op2,op1, id);
                        op1 = "";
                        op2 = "";
                        break;
                    case "print":
                        scheduler.setCounter(scheduler.getCounter() + 1);
                        print(getValueOfVariables(id, op1));
                        break;

                    case "printFromTo":
                        scheduler.setCounter(scheduler.getCounter() + 1);
                        res=(String)getValueOfVariables(id,op2);
                         res2= (String)getValueOfVariables(id,op1);
                        printFromTo(Integer.parseInt(res), Integer.parseInt(res2));
                        op1 = "";
                        op2 = "";
                        break;

                    case "writeFile":
                        scheduler.setCounter(scheduler.getCounter() + 1);
                        res= (String)getValueOfVariables(id,op2);
                        res2 = (String)getValueOfVariables(id,op1);
                        writeFile(res, res2, id);
                        op1 = "";
                        op2 = "";
                        break;
                    case "readFile":
                        scheduler.setCounter(scheduler.getCounter() + 1);
                        String resultFile = readFile((String)getValueOfVariables(id,op1));
                        op1 = "";
                        programs.get(id).stack.push(resultFile);
                        break;


                }
                scheduler.scheduler(allThePrograms,this,id);
            }


        }
    }

    public void reverseStack(String[] lines, int id) {
        if(id==0)return;
        for (int i = lines.length - 1; i >= 0; i--) {
            String[] terms = lines[i].trim().split("\\s+");
            System.out.println(Arrays.toString(terms));
            for (String term : terms) {
                programs.get(id).stack.push(term);
            }
        }
    }
//
//        public void readProgram (String Program,int id) throws IOException {
//            isRunning = true;
//            String result = readFile(Program, id);
////        String[] terms = result.trim().split("\\s+");
//            String[] lines = result.trim().split("\\n");
//            System.out.println("The lines of Program: " + " " + id + Arrays.toString(lines));
//            currentFileLines = lines.length;
//            int i = (int) programs.get(id).value;
//            Object firstInput = null;
//            Object secondInput = null;
//            int j = 0;
//            if (i >= lines.length) {
//                return;
//            }
//            String[] terms = lines[i].trim().split("\\s+");
//            System.out.println("The terms of Program: " + " " + id + Arrays.toString(terms));
//
//
//            switch (terms[0]) {
//                case "semWait":
//                    scheduler.setCounter(scheduler.getCounter() + 1);
//                    if (mutex.semWait(terms[j + 1], id, this, scheduler)) {
//                    } else {
//                        programs.get(id).value = i;
//                        i--;
//                    }
//                    i++;
//                    break;
//                case "assign":
//                    currentProgramLines++;
//                    scheduler.setCounter(scheduler.getCounter() + 1);
//                    String firstParams = terms[j + 1];
//                    String secondParams = terms[j + 2];
//                    if (secondParams.equals("readFile")) {
//                        String fourthInput = (String) getValueOfVariables(id, terms[j + 3]);
//                        String readResult = readFile(fourthInput, id);
//                        if (!readResult.equals("The resource is blocked")) {
//                            assign(firstParams, readResult, id, true, i);
//                        }
//                    } else if (secondParams.equals("input")) {
//                        Scanner sc = new Scanner(System.in);
//                        System.out.println("Enter a value: ");
//                        assign(firstParams, sc.next(), id, false, i);
//                    } else {
//                        assign(firstParams, secondParams, id, true, i);
//                    }
//                    i++;
//                    break;
//                case "semSignal":
//                    currentProgramLines++;
//                    scheduler.setCounter(scheduler.getCounter() + 1);
//                    mutex.semSignal(terms[j + 1], this);
//                    i++;
//                    break;
//                case "writeFile":
//                    currentProgramLines++;
//                    scheduler.setCounter(scheduler.getCounter() + 1);
//                    firstInput = getValueOfVariables(id, terms[j + 1]);
//                    secondInput = getValueOfVariables(id, terms[j + 2]);
//                    writeFile((String) firstInput, (String) secondInput, id);
//                    i++;
//                    break;
//                case "print":
//                    System.out.print("Gowa print:");
//                    currentProgramLines++;
//                    firstInput = getValueOfVariables(id, terms[j + 1]);
//                    System.out.print("Gowa print w variable is :firstinput");
//                    print(firstInput, id);
//                    scheduler.setCounter(scheduler.getCounter() + 1);
//                    i++;
//                    break;
//                case "printFromTo":
//                    currentProgramLines++;
//                    scheduler.setCounter(scheduler.getCounter() + 1);
//                    firstInput = getValueOfVariables(id, terms[j + 1]);
//                    secondInput = getValueOfVariables(id, terms[j + 2]);
//                    printFromTo(Integer.parseInt(((String) firstInput)), Integer.parseInt(((String) secondInput)), id);
//                    i++;
//                    break;
//            }
//
//            programs.get(id).value = i;
//            System.out.println("Program: " + id + " Standing in " + i);
//            scheduler.scheduler(allThePrograms, this, id);
//
//
//            isRunning = false;
//        }

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
                readProgram2(programName, key);
                break;

            }
        }
    }

    public void setAllPrograms(String[] programs) throws IOException {
        scheduler.scheduler(programs, this, 1);
    }

    public String readFile(String argument) throws IOException {
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
                if (programVariables.get(id).get(i).variable.equals(filePath)) {
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




    public void printFromTo(int a, int b) {
        System.out.println("Starting the printFromTo Method");
        a++;
        while (a < b) {
            System.out.print(a + " ");
            a++;
        }
    }


}