package OperatingSystems;

import java.io.*;
import java.util.ArrayList;
import java.util.*;


public class Interpreter {

    static List<String> instructions = Arrays.asList("semWait", "semSignal", "print", "printFromTo", "input", "readFile",
        "writeFile", "assign"
    );
    Queue<String> readyQueue;
    Queue<String> blockedQueue;
    Mutex mutex;
    Scheduler scheduler;
    //Add the programs to the hashMap
    HashMap<Integer, Pair> programs;
    HashMap<Integer, ArrayList<Pair>> programVariables;
    boolean isRunning;
    String op1 = "";
    String op2 = "";
    public Interpreter(int numOfInstructions) {
        readyQueue = new LinkedList<>();
        blockedQueue = new LinkedList<>();
        mutex = new Mutex();
        scheduler = new Scheduler(numOfInstructions);
        programs = new HashMap<>();
        programVariables = new HashMap<>();
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
        System.out.println("Started Executing The Print Method\n");
        if (a instanceof Integer) {
            System.out.println("This is the printed integer result " + (int) a + "\n");
        } else System.out.println("This is the printed string result " + (String) a + "\n");
    }

    public static void main(String[] args) throws IOException {
        String[] allThePrograms = {"src/Program_1", "src/Program_2", "src/Program_3"};
        Interpreter interpreter = new Interpreter(2); //NumberOfInstructions
        String program = "src/Program_";
        for (int i = 1; i <= 3; i++) {
            interpreter.programs.put((i), new Pair(program + "" + i, new Stack<>()));
            interpreter.programVariables.put((i), new ArrayList<Pair>());
        }
        interpreter.setAllPrograms(allThePrograms);
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


    public void runInstruction(String instruction, int id) throws IOException {
        isRunning = true;

        String currentInstruction = instruction;

        while (!instructions.contains(((Stack<String>) programs.get(id).value).peek())) {
            currentInstruction = ((Stack<String>) programs.get(id).value).peek();
            if (op1 == "" || op1.isEmpty()) {
                op1 = currentInstruction;
                ((Stack<String>) programs.get(id).value).pop();

            } else {
                op2 = currentInstruction;
                ((Stack<String>) programs.get(id).value).pop();
            }
        }


        if (!((Stack<String>) programs.get(id).value).peek().isEmpty())
            currentInstruction = ((Stack<String>) programs.get(id).value).peek();
        if (!op1.isEmpty() || !op2.isEmpty() || instructions.contains(currentInstruction)) {
            System.out.println("Program: " + id + " " + "Executing instruction: " + currentInstruction + "\n");
            scheduler.setCounter(scheduler.getCounter() + 1);
            currentInstruction = ((Stack<String>) programs.get(id).value).pop(); //SemWait
            switch (currentInstruction) {
                case "input":
                    System.out.println();
                    System.out.println("Please Enter Value");
                    Scanner sc = new Scanner(System.in);
                    String item = sc.next();
                    ((Stack<String>) programs.get(id).value).push(item);
                    op1 = "";
                    break;

                case "semWait":
                    if (!mutex.semWait(op1, id, this, scheduler)) {
                        ((Stack<String>) programs.get(id).value).push("semWait");
                        ((Stack<String>) programs.get(id).value).push(op1);
                    }
                    op1 = "";
                    break;
                case "semSignal":
                    mutex.semSignal(op1, this);
                    op1 = "";
                    break;

                case "assign":
                    assign(op2, op1, id);
                    op1 = "";
                    op2 = "";
                    break;
                case "print":
                    print(getValueOfVariables(id, op1));
                    break;
                case "printFromTo":
                    printFromTo(Integer.parseInt((String) getValueOfVariables(id, op2)), Integer.parseInt((String) getValueOfVariables(id, op1)));
                    op1 = "";
                    op2 = "";
                    break;

                case "writeFile":
                    writeFile((String) getValueOfVariables(id, op2), (String) getValueOfVariables(id, op1), id);
                    op1 = "";
                    op2 = "";
                    break;
                case "readFile":
                    String resultFile = readFile((String) getValueOfVariables(id, op1));
                    if (resultFile != "No File Exists") {
                        ((Stack<String>) programs.get(id).value).push(resultFile);
                    }
                    op1 = "";
                    break;
            }
        }
    }


    public void reverseStack(String[] lines, int id) {
        if (id == 0) return;
        for (int i = lines.length - 1; i >= 0; i--) {
            String[] terms = lines[i].trim().split("\\s+");
            for (String term : terms) {
                ((Stack<String>) programs.get(id).value).push(term);
            }
        }
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


    public void setAllPrograms(String[] programs) throws IOException {
        System.out.println("SKSDDSDSD");
        scheduler.scheduler(programs, this, 1);
    }

    public String readFile(String argument) throws IOException {
        String filePath = argument;
        filePath += ".txt"; //Program.txt
        try {
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
        } catch (FileNotFoundException e) {
            System.out.println("No File Exists");
            return "No File Exists";
        }

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
        System.out.println("Started Executing The Print From To Method\n");
        a++;
        while (a < b) {
            System.out.print(a + " ");
            a++;
        }
        System.out.println("### Finished Executing Print From To Method###\n");
    }


}
