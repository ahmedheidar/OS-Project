package OperatingSystems;

import java.io.IOException;

public class Scheduler {

    static int time;
    static int counter;

    public static int getCounter() {
        return counter;
    }

    public static void setCounter(int counter) {
        Scheduler.counter = counter;
    }

    public static void setTime(int time) {
        Scheduler.time = time;
    }

    public static int getTime() {
        return time;
    }

    public static void scheduler(String[] programs) throws IOException {
        while (true) {
            if (time == 0) {
                Interpreter.getReadyQueue().add(programs[0]);
            } else if (time == 1) {
                Interpreter.getReadyQueue().add(programs[1]);

            } else if (time == 4) {
                Interpreter.getReadyQueue().add(programs[2]);
            }
                    //TODO runProgram and see its counter
//            if (Interpreter.isRunning == false) {
//                Interpreter.runProgram(Interpreter.getReadyQueue().poll());
//                while (counter < 2) {
//                }
//
//
//            }

        }
    }
}


