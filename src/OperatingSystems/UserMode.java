package OperatingSystems;

import java.io.IOException;

public interface UserMode {
    public void scheduler(String[] programs, Interpreter interpreter, int id) throws IOException;

    public int getId(Interpreter interpreter, int id);
}
