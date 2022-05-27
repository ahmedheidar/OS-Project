package OperatingSystems;

import java.io.*;
import java.util.ArrayList;
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
        String programLength;
        int instructionSize;
        while (true) {
            if (time == 0) {
                String result = interpreter.readFile(programs[0]);
                String[] lines = result.trim().split("\\n+");
                interpreter.getReadyQueue().add(programs[0]);


            } else if (time == 1) {
                interpreter.getReadyQueue().add(programs[1]);
                String result = interpreter.readFile(programs[1]);
                String[] lines = result.trim().split("\\n+");

            } else if (time == 4) {
                String result = interpreter.readFile(programs[2]);
                String[] lines = result.trim().split("\\n+");
                interpreter.getReadyQueue().add(programs[2]);
            }
            System.out.println("Process In The Ready Queue: ");
            for (String s : interpreter.readyQueue) {
                System.out.println(s);
            }
            System.out.println();
            //TODO runProgram and see its counter
            boolean finished = false;
            if (id != 0) {
                if (Objects.equals(interpreter.getPrograms().get(id).variable, currentProgram)) {
                    if (((Stack<String>) interpreter.programs.get(id).value).isEmpty()) {
                        finished = true;
                        counter = 0;
                        interpreter.setRunning(!interpreter.isRunning());
//                        }
                    }

                }
            }
            if (interpreter.isRunning) {
                id = getId(interpreter, id);
                if (!((Stack<String>) interpreter.programs.get(id).value).isEmpty()) {
                    interpreter.runInstruction(((Stack<String>) interpreter.programs.get(id).value).peek(), id);
                    time++;
                }


            }
            if (counter == quanta) {
                interpreter.isRunning = false;
                if (!finished) {
                    interpreter.getReadyQueue().add(currentProgram);
                }
                currentProgram = "";
            }
            if (!interpreter.isRunning && !interpreter.getReadyQueue().isEmpty()) {
                counter = 0;
                currentProgram = interpreter.getReadyQueue().poll();
                System.out.println("Current Program Running : " + currentProgram + "\n");
                for (Integer key : interpreter.programs.keySet()) {
                    if (interpreter.programs.get(key).variable.equals(currentProgram)) {
                        id = key;
                        break;
                    }
                }
                if (!((Stack<String>) interpreter.programs.get(id).value).isEmpty()) {
                    interpreter.runInstruction(((Stack<String>) interpreter.programs.get(id).value).peek(), id);
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

    private void checkMemorySize(Interpreter interpreter, int id, String program) throws IOException {
        String result = interpreter.readFile(program);
        String[] lines = result.trim().split("\\n+");
        Stack<String> programInstructions = new Stack<>();
        interpreter.reverseStack(lines, id, programInstructions);
        ArrayList<Object> programDetails = new ArrayList<>();
        if (interpreter.memory[0] != (Integer) 0 && interpreter.memory[20] != (Integer) 0) {
            ArrayList<Object> currentProgram = (ArrayList<Object>) interpreter.memory[0];
            PCB pcb = (PCB) currentProgram.get(0);
            if (pcb.getProcessState() != State.RUNNING) {
                interpreter.memory[0] = 0;
            }


        } else {
            if (interpreter.memory[0] == (Integer) 0) {
                programDetails.add(new PCB(id, State.READY, 1, new int[]{0, 19}));
                programDetails.add(new Pair("x", null));
                programDetails.add(new Pair("y", null));
                programDetails.add(new Pair("z", null));
                programDetails.add(programInstructions);
                interpreter.memory[0] = programDetails;
            } else if (interpreter.memory[20] == (Integer) 0) {
                programDetails.add(new PCB(id, State.READY, 1, new int[]{20, 39}));
                programDetails.add(new Pair("x", null));
                programDetails.add(new Pair("y", null));
                programDetails.add(new Pair("z", null));
                programDetails.add(programInstructions);
                interpreter.memory[20] = programDetails;
            }
        }


    }

    public int getId(Interpreter interpreter, int id) {
        for (Integer key : interpreter.programs.keySet()) {
            if (interpreter.programs.get(key).variable.equals(currentProgram)) {
                id = key;
                break;
            }
        }
        return id;
    }

    public static void addToHardDisk(ArrayList<Object> program) throws IOException {
        String s = convertPCB(program)+convertVariables(program)+convertInstructions(program);
        writeToHardDisk(s);
    }
    public static void writeToHardDisk(String program) throws IOException {
        try {
            String data = program+"\n";
            File f1 = new File("HardDisk.txt");
            if(!f1.exists()) {
                f1.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(f1.getName(),true);
            BufferedWriter bw = new BufferedWriter(fileWritter);
            bw.write(data);
            bw.close();
            System.out.println("Done");
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String convertPCB(ArrayList<Object> program){
        PCB p = (PCB) (program).get(0);
        return p.getProcessID()+" "+ p.getProcessState()+" " + p.getProgramCounter()+" " + String.valueOf(p.memoryBoundaries[0])+" "+String.valueOf(p.memoryBoundaries[1])+"\n";
    }

    public static String convertVariables(ArrayList<Object> program){
        Pair pair1 = (Pair) program.get(1);
        Pair pair2 = (Pair) program.get(2);
        Pair pair3 = (Pair) program.get(3);
        String s1 = pair1.variable+" "+pair1.value+"\n";
        String s2 = pair2.variable+" "+pair2.value+"\n";
        String s3 = pair3.variable+" "+pair3.value+"\n";
        return s1+s2+s3;

    }
    public static String convertInstructions(ArrayList<Object> program) {
        Stack instructions = (Stack) program.get(4);
        String s = "";
        for (Object instruction:instructions) {
            s = instruction+"\n" +s;
        }
        return s.toString();
    }



    public static void main(String[] args) throws IOException {
        PCB p = new PCB(1,State.READY,5,new int[]{1,10});
        String pcb  = p.getProcessID()+" "+ p.getProcessState()+" " + p.getProgramCounter()+" " + String.valueOf(p.memoryBoundaries[0])+" "+String.valueOf(p.memoryBoundaries[1]);
//        System.out.println(pcb);
        String s ="semWait userInput\n" +
                "assign a input\n" +
                "assign b input\n" +
                "semSignal userInput\n" +
                "semWait file\n" +
                "writeFile a b\n" +
                "semSignal file\n" +
                "\n";
        Interpreter interpreter =  new Interpreter(1);
        String[] lines = s.trim().split("\\n+");
        Stack<String> programInstructions = new Stack<>();
//        interpreter.reverseStack(lines, 1, programInstructions);
        programInstructions.push("assign a");
        programInstructions.push("input");
        programInstructions.push("semWait userSignal");
        ArrayList<Object> programDetails = new ArrayList<>();
        programDetails.add(new PCB(1, State.READY, 1, new int[]{0, 19}));
        programDetails.add(new Pair("x", null));
        programDetails.add(new Pair("y", null));
        programDetails.add(new Pair("z", null));
        programDetails.add(programInstructions);
        for (Object o:programDetails) {
            System.out.println(o.toString());
        }
        addToHardDisk(programDetails);

    }

}
