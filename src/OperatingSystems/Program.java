package OperatingSystems;

public class Program {
    String filePath;
    int arrivalTime;
    //Make constructor
    public Program(String filePath, int arrivalTime) {
        this.filePath = filePath;
        this.arrivalTime = arrivalTime;
    }

    //Make getters
    public String getFilePath() {
        return filePath;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }


}
