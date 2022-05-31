package OperatingSystems;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Stack;

public class Scheduler {

    int time = 0;
    int counter;
    int quanta;
    static String currentProgram = "";

    public Scheduler(int quanta) {
        this.quanta = quanta;

    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }


    public void scheduler(String[] programs, Interpreter interpreter, int id) throws IOException {
        while (true) {
            if (time == 0) {
                String result = interpreter.readFile(programs[0]);
                String[] lines = result.trim().split("\\n+");
                interpreter.getReadyQueue().add(programs[0]);
                checkMemorySize(interpreter, 1, programs[0]);
            } else if (time == 1) {
                interpreter.getReadyQueue().add(programs[1]);
                String result = interpreter.readFile(programs[1]);
                String[] lines = result.trim().split("\\n+");
                checkMemorySize(interpreter, 2, programs[1]);
            } else if (time == 4) {
                String result = interpreter.readFile(programs[2]);
                String[] lines = result.trim().split("\\n+");
                interpreter.getReadyQueue().add(programs[2]);
                checkMemorySize(interpreter, 3, programs[2]);
            }
            System.out.println("Process In The Ready Queue: ");
            for (String s : interpreter.readyQueue) {
                System.out.println(s);
            }

            System.out.println();
            //TODO runProgram and see its counter
            boolean finished = false;
            ArrayList<Object> programRunning = interpreter.getTheProgram(id);
            PCB pcb;
            Stack<String> currentStack;
            if (programRunning == null) {
                //TODO clear the hardDisk
                programRunning = readFromHardDisk(interpreter, "HardDisk");
                pcb = (PCB) programRunning.get(0);
                currentStack = (Stack<String>) programRunning.get(4);
                PrintWriter writer = new PrintWriter("HardDisk.txt");
                writer.print("");
                writer.close();
                if (interpreter.memory[0] == null) {
                    interpreter.memory[0] = programRunning;
                } else if (interpreter.memory[20] == null) {
                    interpreter.memory[20] = programRunning;
                } else {
                    ArrayList<Object> curR = (ArrayList<Object>) interpreter.memory[0];
                    PCB pcbOfTheMemoryProgram = (PCB) curR.get(0);
                    if (pcbOfTheMemoryProgram.getProcessState() != State.RUNNING) {
                        interpreter.memory[0] = null;
                        addToHardDisk(curR);
                        interpreter.memory[0] = programRunning;
                    } else {
                        curR = (ArrayList<Object>) interpreter.memory[20];
                        pcbOfTheMemoryProgram = (PCB) curR.get(0);
                        if (pcbOfTheMemoryProgram.getProcessState() != State.RUNNING) {
                            interpreter.memory[20] = null;
                            addToHardDisk(curR);
                            interpreter.memory[20] = programRunning;
                        }
                    }
                }
            } else {
                pcb = (PCB) programRunning.get(0);
                currentStack = (Stack<String>) programRunning.get(4);
            }
            if (pcb.getProcessID() == id) {
                if (((Stack<String>) programRunning.get(4)).isEmpty()) {
                    finished = true;
                    counter = 0;
                    interpreter.setRunning(!interpreter.isRunning());
                }
            }
            if (interpreter.isRunning) {
                if (!(currentStack.isEmpty())) {
                    //Run the instruction that the pc arrow on it and increment the pc of it
                    pcb.setProcessState(State.RUNNING);
                    if (pcb.getProgramCounter() == 0) {
                        //TODO after the instruction get the id of the first program in the readyQueue
                        interpreter.runInstruction(currentStack.get((currentStack.size()) - 1), id);
                    } else {
                        interpreter.runInstruction(currentStack.peek(), id);
                    }
                    id = changePcbToBlocked(interpreter, pcb, id);
                    pcb.setProgramCounter(pcb.getProgramCounter() + 1);
                    time++;
                }
            }
            if (counter == quanta) {
                interpreter.isRunning = false;
                if (!finished) {
                    interpreter.getReadyQueue().add(currentProgram);
                    pcb.setProcessState(State.READY);
                }
                currentProgram = "";
            }
            if (!interpreter.isRunning && !interpreter.getReadyQueue().isEmpty()) {
                counter = 0;
                currentProgram = interpreter.getReadyQueue().poll();
                System.out.println("Current Program Running : " + currentProgram + "\n");
                if (currentProgram.equals("src/Program_1")) id = 1;
                else if (currentProgram.equals("src/Program_2")) id = 2;
                else id = 3;
                programRunning = interpreter.getTheProgram(id);
                if (programRunning == null) {
                    //TODO clear the hardDisk
                    programRunning = readFromHardDisk(interpreter, "HardDisk");
                    pcb = (PCB) programRunning.get(0);
                    currentStack = (Stack<String>) programRunning.get(4);
                    PrintWriter writer = new PrintWriter("HardDisk.txt");
                    writer.print("");
                    writer.close();
                    if (interpreter.memory[0] == null) {
                        interpreter.memory[0] = programRunning;
                    } else if (interpreter.memory[20] == null) {
                        interpreter.memory[20] = programRunning;
                    } else {
                        ArrayList<Object> curR = (ArrayList<Object>) interpreter.memory[0];
                        PCB pcbOfTheMemoryProgram = (PCB) curR.get(0);
                        if (pcbOfTheMemoryProgram.getProcessState() != State.RUNNING) {
                            interpreter.memory[0] = null;
                            addToHardDisk(curR);
                            interpreter.memory[0] = programRunning;
                        } else {
                            curR = (ArrayList<Object>) interpreter.memory[20];
                            pcbOfTheMemoryProgram = (PCB) curR.get(0);
                            if (pcbOfTheMemoryProgram.getProcessState() != State.RUNNING) {
                                interpreter.memory[20] = null;
                                addToHardDisk(curR);
                                interpreter.memory[20] = programRunning;
                            }
                        }
                    }
                } else {
                    pcb = (PCB) programRunning.get(0);
                    currentStack = (Stack<String>) programRunning.get(4);
                }
                if (!(currentStack.isEmpty())) {
                    pcb.setProcessState(State.RUNNING);
                    if (pcb.getProgramCounter() == 0) {
                        interpreter.runInstruction(currentStack.get((currentStack.size()) - 1), id);
                    } else {
                        interpreter.runInstruction(currentStack.peek(), id);
                    }
                    //TODO change the pcbState with Blocked after executing its instruction if its blocked state
                    if (currentStack.size() == 0) {
                        pcb.setProcessState(State.READY);
                    }
                    id = changePcbToBlocked(interpreter, pcb, id);
                    pcb.setProgramCounter(pcb.getProgramCounter() + 1);
                    time++;
                }
            }
            System.out.println("Current Program Running : " + currentProgram + "\n");
            System.out.println("Process In The Ready Queue After: ");
            for (String s : interpreter.readyQueue) {
                System.out.println(s);
            }
            System.out.println();
            System.out.println("Process in The Blocked Queue: " + "\n");
            System.out.println(interpreter.isRunning);
            for (String s : interpreter.blockedQueue) {
                System.out.println(s);
            }
            System.out.println();

            if (!interpreter.isRunning && interpreter.getReadyQueue().isEmpty() && interpreter.getBlockedQueue().isEmpty()) {
                break;
            }
        }
        System.out.println(time);
    }

    private int changePcbToBlocked(Interpreter interpreter, PCB pcb, int id) {
        String progNameNow;
        if (pcb.getProcessID() == 1) progNameNow = "src/Program_1";
        else if (pcb.getProcessID() == 2) progNameNow = "src/Program_2";
        else progNameNow = "src/Program_3";
        if (interpreter.blockedQueue.contains(progNameNow)) {
            pcb.setProcessState(State.BLOCKED);
            id = getIdProgram(interpreter, id);
        }
        return id;

    }

    private int getIdProgram(Interpreter interpreter, int id) {
        if (!interpreter.readyQueue.isEmpty()) {
            if (interpreter.readyQueue.peek().equals("src/Program_1")) id = 1;
            else if (interpreter.readyQueue.peek().equals("src/Program_2")) id = 2;
            else id = 3;
        }
        return id;
    }

    private void checkMemorySize(Interpreter interpreter, int id, String program) throws IOException {
        String result = interpreter.readFile(program);
        String[] lines = result.trim().split("\\n+");
        Stack<String> programInstructions = new Stack<>();
        interpreter.reverseStack(lines, id, programInstructions);
        ArrayList<Object> programDetails = new ArrayList<>();
        if (interpreter.memory[0] != null && interpreter.memory[20] != null) {
            ArrayList<Object> currentProgram = (ArrayList<Object>) interpreter.memory[0];
            System.out.println(currentProgram);
            PCB pcb = (PCB) currentProgram.get(0);
            if (pcb.getProcessState() != State.RUNNING) {
                interpreter.memory[0] = null; //will remove the currentProgram and add it to the harddisk
                addToHardDisk(currentProgram);
                programDetails.add(new PCB(id, State.READY, 0, new int[]{0, 19}));
                addProgramVariables(programDetails, programInstructions);
                interpreter.memory[0] = programDetails;


            } else {
                currentProgram = (ArrayList<Object>) interpreter.memory[20];
                pcb = (PCB) currentProgram.get(0);
                if (pcb.getProcessState() != State.RUNNING) {
                    interpreter.memory[20] = null;
                    addToHardDisk(currentProgram);
                    programDetails.add(new PCB(id, State.READY, 0, new int[]{0, 19}));
                    addProgramVariables(programDetails, programInstructions);
                    interpreter.memory[20] = programDetails;


                }
            }
        } else {
            if (interpreter.memory[0] == null) {
                programDetails.add(new PCB(id, State.READY, 0, new int[]{0, 19}));
                addProgramVariables(programDetails, programInstructions);
                interpreter.memory[0] = programDetails;
            } else if (interpreter.memory[20] == null) {
                programDetails.add(new PCB(id, State.READY, 0, new int[]{20, 39}));
                addProgramVariables(programDetails, programInstructions);
                interpreter.memory[20] = programDetails;
            }
        }

    }

    private void addProgramVariables(ArrayList<Object> programDetails, Stack<String> programInstructions) {
        programDetails.add(new Pair("", null));
        programDetails.add(new Pair("", null));
        programDetails.add(new Pair("", null));
        programDetails.add(programInstructions);
    }


    public ArrayList<Object> readFromHardDisk(Interpreter interpreter, String program) throws IOException {
        String result = interpreter.readFile(program);
        String lines[] = result.split("\\r?\\n");
        if (lines[0].equals("No")) {
            return null;
        }
        int start = 0;
        String[] linesToSplit;
        Stack<String> originalStack = new Stack<>();
        ArrayList<Object> resultOfFunction = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            linesToSplit = lines[i].split(" ");
            if (i == 0) {
                int processID = Integer.parseInt(linesToSplit[0]);
                String processState = (linesToSplit[1]);
                State processEnumState = State.valueOf(processState);
                int programCounter = Integer.parseInt(linesToSplit[2]);
                int[] memBound = {Integer.parseInt(linesToSplit[3]), Integer.parseInt(linesToSplit[4])};
                PCB pcb = new PCB(processID, processEnumState, programCounter, memBound);
                resultOfFunction.add(pcb);
            } else if (i == 1 || i == 2 || i == 3) {
                Pair variable = new Pair(linesToSplit[0], linesToSplit[1]);
                resultOfFunction.add(variable);
            } else {
                int end = lines.length - 1;
                while (end >= 4) {
                    originalStack.push(lines[end]);
                    end--;
                }
                resultOfFunction.add(originalStack);
                break;
            }

        }

        return resultOfFunction;

    }


    public static void addToHardDisk(ArrayList<Object> program) throws IOException {
        String s = convertPCB(program) + convertVariables(program) + convertInstructions(program);
        writeToHardDisk(s);
    }

    public static void writeToHardDisk(String program) throws IOException {
        try {
            String data = program + "\n";
            File f1 = new File("HardDisk.txt");
            if (!f1.exists()) {
                f1.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(f1.getName(), true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(data);
            bw.close();
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String convertPCB(ArrayList<Object> program) {
        PCB p = (PCB) (program).get(0);
        return p.getProcessID() + " " + p.getProcessState() + " " + p.getProgramCounter() + " " + String.valueOf(p.memoryBoundaries[0]) + " " + String.valueOf(p.memoryBoundaries[1]) + "\n";
    }

    public static String convertVariables(ArrayList<Object> program) {
        Pair pair1 = (Pair) program.get(1);
        Pair pair2 = (Pair) program.get(2);
        Pair pair3 = (Pair) program.get(3);
        String s1 = pair1.variable + " " + pair1.value + "\n";
        String s2 = pair2.variable + " " + pair2.value + "\n";
        String s3 = pair3.variable + " " + pair3.value + "\n";
        return s1 + s2 + s3;
    }

    public static String convertInstructions(ArrayList<Object> program) {
        Stack instructions = (Stack) program.get(4);
        String s = "";
        for (Object instruction : instructions) {
            s = instruction + "\n" + s;
        }
        return s.toString();
    }

}
