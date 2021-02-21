package com.codecool.processwatch.queries;

import com.codecool.processwatch.domain.Process;
import com.codecool.processwatch.domain.Query;

import java.util.stream.Stream;

public class SelectNotRestricted implements Query {
    @Override
    public Stream<Process> run(Stream<Process> input) {
        return input.filter(x -> !x.getName().equals("n/a"));
    }
}
