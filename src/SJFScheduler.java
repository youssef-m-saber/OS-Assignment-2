import java.util.ArrayList;
import java.util.List;

// Preemptive Shortest Job First (Shortest Remaining Time First) with context switching.
.
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
        Process last = null;  

        while (finished < processes.size()) {
            Process next = pickShortest(processes, time);

        
            if (next == null) {
                timeline.add("T=" + time + ": IDLE");
                time++;
                last = null;  
                continue;
            }

           
            if (last != null && last != next && contextSwitch > 0) {
                for (int i = 0; i < contextSwitch; i++) {
                    timeline.add("T=" + time + ": CONTEXT SWITCH (" + last.name + " -> " + next.name + ")");
                    time++;
                }
             
                next = pickShortest(processes, time);
            }

         
            if (next.response == -1) {
                next.response = time - next.arrival;  
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
