import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ACSScheduler implements Scheduler {

    private static final int AGING_INTERVAL = 4;
    private final Deque<Process> tier1 = new ArrayDeque<>();
    private final List<Process> tier2 = new ArrayList<>();
    private final Map<Process, Integer> demotedAt = new HashMap<>();
    private List<Process> byArrival;
    private int nextArrival;

    public Result run(List<Process> processes, int quantum, int contextSwitch) {
        List<String> timeline = new ArrayList<>();

        // Clean state so the same list can be run again
        tier1.clear();
        tier2.clear();
        demotedAt.clear();
        for (Process p : processes) {
            p.remaining = p.burst;
            p.response = -1;
            p.finish = 0;
        }
        byArrival = new ArrayList<>(processes);
        byArrival.sort(Comparator.comparingInt((Process p) -> p.arrival)); // stable: ties keep input order
        nextArrival = 0;

        int time = 0;
        int finished = 0;
        Process last = null;

        while (finished < processes.size()) {
            admitArrivals(time);

            Process next;
            boolean fromTier1;
            if (!tier1.isEmpty()) {
                next = tier1.pollFirst();
                fromTier1 = true;
            } else if (!tier2.isEmpty()) {
                next = pickFromTier2(time);
                tier2.remove(next);
                demotedAt.remove(next);
                fromTier1 = false;
            } else {
                timeline.add(line(time, "IDLE", time));
                time++;
                last = null;
                continue;
            }

            // Context switch: only when switching to a different process
            if (last != null && last != next && contextSwitch > 0) {
                for (int i = 0; i < contextSwitch; i++) {
                    timeline.add(line(time, "CONTEXT SWITCH (" + last.name + " -> " + next.name + ")", time));
                    time++;
                    admitArrivals(time);
                }
            }

            // Tier 1: at most one quantum. Tier 2: runs until it completes (non-preemptive)
            int slice = fromTier1 ? Math.min(quantum, next.remaining) : next.remaining;
            for (int i = 0; i < slice; i++) {
                if (next.response == -1) {
                    next.response = time - next.arrival;
                }
                timeline.add(line(time, next.name + " running (remaining: " + next.remaining + ")", time));
                time++;
                next.remaining--;
                admitArrivals(time);
            }

            if (next.remaining == 0) {
                next.finish = time;
                finished++;
            } else {

                tier2.add(next);
                demotedAt.put(next, time);
            }
            last = next;
        }

        timeline.add("T=" + time + ": Tier 1: [Empty] | Tier 2: [Empty]");
        return new Result(processes, timeline);
    }

    private void admitArrivals(int time) {
        while (nextArrival < byArrival.size() && byArrival.get(nextArrival).arrival <= time) {
            tier1.addLast(byArrival.get(nextArrival));
            nextArrival++;
        }
    }

    // Lowest effective priority number wins; ties go to the earlier arrival.
    private Process pickFromTier2(int time) {
        Process best = null;
        for (Process p : tier2) {
            if (best == null) {
                best = p;
                continue;
            }
            int ep = effectivePriority(p, time);
            int eb = effectivePriority(best, time);
            if (ep < eb || (ep == eb && p.arrival < best.arrival)) {
                best = p;
            }
        }
        return best;
    }

    // ===== Tier 3 STUB: replace with Person 3's aging function =====
    // priority - floor(waiting / 4), waiting counted from the moment of demotion
    private int effectivePriority(Process p, int now) {
        int waited = now - demotedAt.get(p);
        return p.priority - waited / AGING_INTERVAL;
    }

    // ===== Per-time-step output (the "demotion matrix" row) =====
    // Queues show the processes that are waiting at the start of this time step.
    private String line(int time, String what, int now) {
        return "T=" + time + ": " + what
                + " | Tier 1: " + formatTier1()
                + " | Tier 2: " + formatTier2(now);
    }

    private String formatTier1() {
        if (tier1.isEmpty()) return "[Empty]";
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Process p : tier1) {
            if (!first) sb.append(", ");
            sb.append(p.name).append(" (remaining: ").append(p.remaining)
                    .append(", Priority: ").append(p.priority).append(")");
            first = false;
        }
        return sb.append("]").toString();
    }

    private String formatTier2(int now) {
        if (tier2.isEmpty()) return "[Empty]";
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Process p : tier2) {
            if (!first) sb.append(", ");
            sb.append(p.name).append(" (remaining: ").append(p.remaining)
                    .append(", Priority: ").append(effectivePriority(p, now)).append(")");
            first = false;
        }
        return sb.append("]").toString();
    }
}