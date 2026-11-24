import java.util.List;
interface Scheduler {
    Result run(List<Process> processes, int quantum, int contextSwitch);
}
