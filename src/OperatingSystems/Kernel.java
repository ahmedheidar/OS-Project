package OperatingSystems;

import java.io.IOException;

public interface Kernel {

    public String readFile(String argument) throws IOException;

    public void writeFile(String filePath, String value, int id) throws IOException;

    public void printFromTo(int a, int b);

    public void print(Object a);

}
