package com.codecool.processwatch.os;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.codecool.processwatch.domain.Process;
import com.codecool.processwatch.domain.ProcessSource;
import com.codecool.processwatch.domain.User;

/**
 * A process source using the Java {@code ProcessHandle} API to retrieve information
 * about the current processes.
 */
public class OsProcessSource implements ProcessSource {
    //ProcessSource: interface ami egy streamet ad vissza processekről
    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Process> getProcesses() {
        Stream<ProcessHandle> processStream = ProcessHandle.allProcesses();
        List<Process> processes = new ArrayList<>();
        long parentPId = 0;

        for (ProcessHandle ph : processStream.collect(Collectors.toList())) {
            if (ph.parent().isPresent()) {
                parentPId = ph.parent().get().pid();
            } else {
                parentPId = 0;
            }

            processes.add(new Process(ph.pid(),
                    parentPId,
                    new User(ph.info().user().orElse("n/a")),
                    ph.info().command().orElse("n/a"),
                    ph.info().arguments().orElse(new String[0])));
        }

        return processes.stream();
    }
}
