class Process {
    String name;
    int arrival, burst, priority;
    int remaining, finish, waiting, turnaround;
    int response = -1;

    Process(String name, int arrival, int burst, int priority) {
        this.name = name;
        this.arrival = arrival;
        this.burst = burst;
        this.priority = priority;
        this.remaining = burst;
    }
}
