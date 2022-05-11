package OperatingSystems;

import java.io.IOException;
import java.util.Objects;

public class Scheduler {

    int time = -1;
    int counter;

    static String currentProgram = "";

    public Scheduler() {

    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void scheduler(String[] programs, Interpreter interpreter, int id) throws IOException {



        while (true){
            time++;
            if (time == 0) {
                String result = interpreter.readFile(programs[0]);
                String[] lines = result.trim().split("\\n+");
                interpreter.reverseStack(lines,1);
                interpreter.getReadyQueue().add(programs[0]);
            } else if (time == 1) {
                interpreter.getReadyQueue().add(programs[1]);
                String result = interpreter.readFile(programs[1]);
                String[] lines = result.trim().split("\\n+");
                interpreter.reverseStack(lines,2);

            } else if (time == 4) {
                String result = interpreter.readFile(programs[2]);
                String[] lines = result.trim().split("\\n+");
                interpreter.reverseStack(lines,3);
                interpreter.getReadyQueue().add(programs[2]);
            }
            //TODO runProgram and see its counter
            boolean finished = false;
            if (id != 0) {
                if (Objects.equals(interpreter.getPrograms().get(id).variable, currentProgram)) {
                    if (interpreter.programs.get(id).stack.isEmpty()) {
                        finished = true;
                        counter = 0;
                        interpreter.setRunning(!interpreter.isRunning());
                        if (!interpreter.getReadyQueue().isEmpty()) {
                            currentProgram = interpreter.getReadyQueue().peek();
                            interpreter.runProgram(interpreter.getReadyQueue().poll());
                        }
                    }

                }
            }


            if (counter == 2) {
                interpreter.isRunning = false;
                if (!finished) {
                    interpreter.getReadyQueue().add(currentProgram);
                }
                currentProgram = "";


            }
            if (!interpreter.isRunning && !interpreter.getReadyQueue().isEmpty()) {
                counter = 0;
                currentProgram = interpreter.getReadyQueue().peek();
                interpreter.runProgram(interpreter.getReadyQueue().poll());

            }
            if (!interpreter.isRunning && interpreter.getReadyQueue().isEmpty() && interpreter.getBlockedQueue().isEmpty()) {
                break;
            }
            if (interpreter.isRunning) {
                interpreter.runProgram(currentProgram);
            }


        }
    }
}