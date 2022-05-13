package OperatingSystems;

import java.io.IOException;
import java.util.Objects;

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
                interpreter.reverseStack(lines, 1);
                interpreter.getReadyQueue().add(programs[0]);
            } else if (time == 1) {
                interpreter.getReadyQueue().add(programs[1]);
                String result = interpreter.readFile(programs[1]);
                String[] lines = result.trim().split("\\n+");
                interpreter.reverseStack(lines, 2);

            } else if (time == 4) {
                String result = interpreter.readFile(programs[2]);
                String[] lines = result.trim().split("\\n+");
                interpreter.reverseStack(lines, 3);
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
                    if (interpreter.programs.get(id).stack.isEmpty()) {
                        finished = true;
                        counter = 0;
                        interpreter.setRunning(!interpreter.isRunning());
//                        }
                    }

                }
            }
            if (interpreter.isRunning) {
                id = getId(interpreter, id);
                interpreter.runInstruction(interpreter.programs.get(id).stack.peek(), id);
                time++;

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
                interpreter.runInstruction(interpreter.programs.get(id).stack.peek(), id);
                time++;
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

    public int getId(Interpreter interpreter, int id) {
        for (Integer key : interpreter.programs.keySet()) {
            if (interpreter.programs.get(key).variable.equals(currentProgram)) {
                id = key;
                break;
            }
        }
        return id;
    }
}
