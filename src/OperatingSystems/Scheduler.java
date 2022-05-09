package OperatingSystems;

import java.io.IOException;
import java.util.Objects;

public class Scheduler {

    static int time=-1;
    static int counter;

    static String currentProgram = "";

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
        int programID;
        while (true) {
            for(String s : Interpreter.getReadyQueue()){
                System.out.println(s);
            }
            time++;
            if (time == 0) {
                Interpreter.getReadyQueue().add(programs[0]);
            } else if (time == 1) {
                Interpreter.getReadyQueue().add(programs[1]);

            } else if (time == 4) {
                Interpreter.getReadyQueue().add(programs[2]);
            }
                    //TODO runProgram and see its counter
            boolean finished = false;
            for (Integer key : Interpreter.getPrograms().keySet()) {
                if (Objects.equals(Interpreter.getPrograms().get(key).variable, currentProgram)) {
                    if ((int) Interpreter.getPrograms().get(key).value >= Interpreter.currentFileLines - 1) {
                        counter=0;
                        currentProgram = Interpreter.getReadyQueue().peek();
                        Interpreter.runProgram(Interpreter.getReadyQueue().poll());
                        finished= true;
                    }
                }
            }
            if(counter==2){
                Interpreter.isRunning=false;
                if(!finished){
                    Interpreter.getReadyQueue().add(currentProgram);
                }
                currentProgram="";

            }
            if (!Interpreter.isRunning && !Interpreter.getReadyQueue().isEmpty()) {
                counter=0;
                currentProgram = Interpreter.getReadyQueue().peek();
                Interpreter.runProgram(Interpreter.getReadyQueue().poll());
            }
            if(!Interpreter.isRunning && Interpreter.getReadyQueue().isEmpty() && Interpreter.getBlockedQueue().isEmpty()){
                break;
            }
            if(Interpreter.isRunning){
                Interpreter.runProgram(currentProgram);
            }

        }
    }
}


