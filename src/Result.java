import java.util.ArrayList;
import java.util.List;

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
