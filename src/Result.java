import java.util.ArrayList;
import java.util.List;

// Shared contract: what a scheduler returns after running.
class Result {
    List<Process> processes;
    List<String> timeline;

    Result() {
        processes = new ArrayList<>();
        timeline = new ArrayList<>();
    }

    Result(List<Process> processes, List<String> timeline) {
        this.processes = processes;
        this.timeline = timeline;
    }
}
