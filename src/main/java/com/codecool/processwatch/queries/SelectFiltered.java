package com.codecool.processwatch.queries;

import com.codecool.processwatch.domain.Process;
import com.codecool.processwatch.domain.Query;

import java.util.stream.Stream;
/**
 * Selects only filtered processes acc. to fakeProcess from its source.
 */
public class SelectFiltered implements Query {

    private final Process fakeProcess;

    public SelectFiltered(Process fakeProcess) {
        this.fakeProcess = fakeProcess;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Process> run(Stream<Process> input) {
        try {
            if (fakeProcess.getPid() != -1) {
                return input.filter(u -> u.getPid() == fakeProcess.getPid());
            } else if (fakeProcess.getParentPid() != -1) {
                return input.filter(u -> u.getParentPid() == fakeProcess.getParentPid());
            } else if (!fakeProcess.getUserName().equals(null)) {
                return input.filter(u -> u.getUserName().equals(fakeProcess.getUserName()));
            }
        } catch (NullPointerException ignored) {}
        if (!fakeProcess.getName().equals(null)) {
            return input.filter(u -> u.getName().equals(fakeProcess.getName()));
        }
        return input;
    }
}
