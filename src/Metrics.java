import java.util.List;


class Metrics {


    // (Response time is recorded by the scheduler when the process first runs.)
    static void calculate(List<Process> processes) {
        for (Process p : processes) {
            p.turnaround = p.finish - p.arrival;
            p.waiting = p.turnaround - p.burst;
        }
    }

    static double averageWaiting(List<Process> processes) {
        double sum = 0;
        for (Process p : processes) {
            sum += p.waiting;
        }
        return sum / processes.size();
    }

    static double averageTurnaround(List<Process> processes) {
        double sum = 0;
        for (Process p : processes) {
            sum += p.turnaround;
        }
        return sum / processes.size();
    }

    static void printTable(List<Process> processes) {
        System.out.printf("%-8s| %-8s| %-6s| %-7s| %-8s| %-11s| %-8s%n",
                "Process", "Arrival", "Burst", "Finish", "Waiting", "Turnaround", "Response");
        System.out.println("--------+---------+-------+--------+---------+------------+---------");
        for (Process p : processes) {
            System.out.printf("%-8s| %-8d| %-6d| %-7d| %-8d| %-11d| %-8d%n",
                    p.name, p.arrival, p.burst, p.finish, p.waiting, p.turnaround, p.response);
        }
        System.out.println();
        System.out.printf("Average Waiting Time    = %.2f%n", averageWaiting(processes));
        System.out.printf("Average Turnaround Time = %.2f%n", averageTurnaround(processes));
    }
}
