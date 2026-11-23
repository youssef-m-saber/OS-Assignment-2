import java.util.List;

// Shared contract: every scheduling algorithm implements this interface.
interface Scheduler {
    Result run(List<Process> processes, int quantum, int contextSwitch);
}
