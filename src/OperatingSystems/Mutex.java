package OperatingSystems;

import java.util.*;

public class Mutex {

    static int printKey;
    static int assignKey;
    static int readAndWriteFileKey;
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



    public static boolean semWait(String a) {
        switch (a) {
            case "userOutput":
                if (printKey == 1) {
                    printKey--;
                    return true;
                }
                break;

            case "userInput":
                if (assignKey == 1) {
                    assignKey--;
                    return true;
                }
                break;
            case "file":
                if (readAndWriteFileKey == 1) {
                    readAndWriteFileKey--;
                    return true;
                }
                break;
        }
        return false;

    }


    public static void semSignal(String a) {
        switch (a) {
            case "print":
                printKey++;
                break;
            case "assign":
                assignKey++;
                break;
            case "readFile":
            case "writeFile":
                readAndWriteFileKey++;
                break;
        }
    }

}

