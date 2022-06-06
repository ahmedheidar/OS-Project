# Operating System

As part of the "CSEN 602: Computer Operatng Systems" course. We have implemented a basic operating system to simulate how operating systems function. All using Java.

### Main Components:

* **Program files**:
  * Written as text files.
  * Contain the program code where it can choose to semWait or signal on the available system Resources.

* **Interpreter**:
  * The main component in the operating system.
  * Include all the resourses such as: Assigning variables, Reading and Writing files, Printing outputs.
  * Starts the scheduler.

* **Scheduler**:
  * The Scheduler uses the ***Round Robin*** algorithm.
  * It receives the programs whenever they arrive.
  * Responsible for executing the programs.
  * Responsible for managing the memory and the hard disk.

* **Mutex**:
  * Responsible for Managing the resourses availabe.
