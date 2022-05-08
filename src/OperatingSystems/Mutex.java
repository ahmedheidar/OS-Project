package OperatingSystems;

import java.util.*;

public class Mutex {

    static int printKey = 1;
    static int assignKey = 1;
    static int readAndWriteFileKey = 1;
    static Queue<String> userOutputBlockedQueue = new LinkedList<>();
    static Queue<String> userInputBlockedQueue = new LinkedList<>();
    static Queue<String> fileBlockedQueue = new LinkedList<>();

    public static Queue<String> getUserOutputBlockedQueue() {
        return userOutputBlockedQueue;
    }

    public static void setUserOutputBlockedQueue(Queue<String> userOutputBlockedQueue) {
        Mutex.userOutputBlockedQueue = userOutputBlockedQueue;
    }

    public static void setUserInputBlockedQueue(Queue<String> userInputBlockedQueue) {
        Mutex.userInputBlockedQueue = userInputBlockedQueue;
    }

    public static void setFileBlockedQueue(Queue<String> fileBlockedQueue) {
        Mutex.fileBlockedQueue = fileBlockedQueue;
    }

    public static Queue<String> getUserInputBlockedQueue() {
        return userInputBlockedQueue;
    }

    public static Queue<String> getFileBlockedQueue() {
        return fileBlockedQueue;
    }


    public static boolean semWait(String a, int id) {
        String program;
        switch (a) {
            case "userOutput":
                if (printKey == 1) {
                    printKey--;
                    return true;
                } else {
                    program = Interpreter.getPrograms().get(id).variable;
                    userOutputBlockedQueue.add(program);
                    Interpreter.getBlockedQueue().add(program);
                }
                break;

            case "userInput":
                if (assignKey == 1) {
                    assignKey--;
                    return true;
                } else {
                    program = Interpreter.getPrograms().get(id).variable;
                    userInputBlockedQueue.add(program);
                    Interpreter.getBlockedQueue().add(program);
                }
                break;
            case "file":
                if (readAndWriteFileKey == 1) {
                    readAndWriteFileKey--;
                    return true;
                } else {
                    program = Interpreter.getPrograms().get(id).variable;
                    fileBlockedQueue.add(program);
                    Interpreter.getBlockedQueue().add(program);
                }
                break;
        }
        return false;

    }


    public static void semSignal(String a) {
        String program;
        switch (a) {
            case "userOuput":
                printKey++;
                program = userOutputBlockedQueue.poll();
                Interpreter.getBlockedQueue().remove();
                Interpreter.getReadyQueue().add(program);
                break;
            case "userInput":
                assignKey++;
                program = userInputBlockedQueue.poll();
                Interpreter.getBlockedQueue().remove();
                Interpreter.getReadyQueue().add(program);
                break;

            case "file":
                readAndWriteFileKey++;
                program = fileBlockedQueue.poll();
                Interpreter.getBlockedQueue().remove();
                Interpreter.getReadyQueue().add(program);
                break;
        }
    }

}

