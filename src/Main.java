import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Cloud-Cluster CPU Scheduling Simulator ===");

        int n = readInt("Number of processes: ", 1);

        List<Process> processes = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            System.out.println("--- Process " + i + " ---");
            System.out.print("Name: ");
            String name = sc.next();
            int arrival = readInt("Arrival time (>= 0): ", 0);
            int burst = readInt("Burst time (>= 1): ", 1);
            int priority = readInt("Priority (>= 1, lower number = higher priority): ", 1);
            processes.add(new Process(name, arrival, burst, priority));
        }

        int quantum = readInt("Time quantum (>= 1): ", 1);
        int cs = readInt("Context switch time (>= 0): ", 0);

        System.out.println();
        System.out.println("Choose the algorithm:");
        System.out.println("1) Preemptive SJF");
        System.out.println("2) Adaptive Container Scheduler (ACS)");
        int choice = readInt("Your choice: ", 1);

        Scheduler scheduler = chooseScheduler(choice);
        if (scheduler == null) {
            System.out.println("This algorithm is not available.");
            return;
        }

        Result result = scheduler.run(processes, quantum, cs);
        Metrics.calculate(result.processes);

        System.out.println();
        System.out.println("=== Timeline ===");
        for (String line : result.timeline) {
            System.out.println(line);
        }

        System.out.println();
        System.out.println("=== Results ===");
        Metrics.printTable(result.processes);
    }

    // Integration point: Person 2 adds the ACS scheduler here (see README).
    static Scheduler chooseScheduler(int choice) {
        if (choice == 1) {
            return new SJFScheduler();
        }
        if (choice == 2) {
            // TODO (Person 2): return new ACSScheduler();
            return null;
        }
        return null;
    }

    // Reads an integer that is at least "min"; asks again if the input is invalid.
    static int readInt(String message, int min) {
        while (true) {
            System.out.print(message);
            if (!sc.hasNextInt()) {
                sc.next();
                System.out.println("Please enter a whole number.");
                continue;
            }
            int value = sc.nextInt();
            if (value < min) {
                System.out.println("The value must be at least " + min + ".");
                continue;
            }
            return value;
        }
    }
}
