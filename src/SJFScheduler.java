import java.util.ArrayList;
import java.util.List;

// Preemptive Shortest Job First (Shortest Remaining Time First) with context switching.
// The simulation moves forward one time unit at a time, so a newly arrived shorter
// process can preempt the running one at any time unit.
class SJFScheduler implements Scheduler {

    public Result run(List<Process> processes, int quantum, int contextSwitch) {
        List<String> timeline = new ArrayList<>();

        // Start from a clean state (so the same list can be run again)
        for (Process p : processes) {
            p.remaining = p.burst;
            p.response = -1;
            p.finish = 0;
        }

        int time = 0;
        int finished = 0;
        Process last = null;   // the process that used the CPU just before (null = CPU was idle / start)

        while (finished < processes.size()) {
            Process next = pickShortest(processes, time);

            // 1) Nothing is ready: the CPU is idle for one time unit
            if (next == null) {
                timeline.add("T=" + time + ": IDLE");
                time++;
                last = null;   // after idle there is nothing to save, so no context switch later
                continue;
            }

            // 2) A different process must run: pay the context switch time
            if (last != null && last != next && contextSwitch > 0) {
                for (int i = 0; i < contextSwitch; i++) {
                    timeline.add("T=" + time + ": CONTEXT SWITCH (" + last.name + " -> " + next.name + ")");
                    time++;
                }
                // Other processes may have arrived during the switch, so choose again
                next = pickShortest(processes, time);
            }

            // 3) Run the chosen process for one time unit
            if (next.response == -1) {
                next.response = time - next.arrival;   // first time on the CPU
            }
            timeline.add("T=" + time + ": " + next.name);
            time++;
            next.remaining--;

            if (next.remaining == 0) {
                next.finish = time;
                finished++;
            }
            last = next;
        }

        return new Result(processes, timeline);
    }

    // Returns the arrived, unfinished process with the smallest remaining time.
    // Tie: earlier arrival first; if arrival is also equal, the earlier one in the input list.
    private Process pickShortest(List<Process> processes, int time) {
        Process best = null;
        for (Process p : processes) {
            if (p.arrival <= time && p.remaining > 0) {
                if (best == null
                        || p.remaining < best.remaining
                        || (p.remaining == best.remaining && p.arrival < best.arrival)) {
                    best = p;
                }
            }
        }
        return best;
    }
}
