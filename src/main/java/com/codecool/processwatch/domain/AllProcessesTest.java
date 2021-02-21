package com.codecool.processwatch.domain;

import java.util.stream.Stream;
import java.util.Optional;
import java.util.stream.Stream;

public class AllProcessesTest {
    public static void main(String args[]) throws InterruptedException {
        System.out.println("---------------------------");
        System.out.println("All Processes:");
        Stream<ProcessHandle> processStream = ProcessHandle.allProcesses();
        processStream.forEach(process -> printInfo(process));
    }
    private static void printInfo(ProcessHandle processHandle) {
        System.out.println("---------");
        System.out.println("Id: " + processHandle.pid());
        System.out.println("isAlive(): " + processHandle.isAlive());
        System.out.println("number of childrens: " + processHandle.children().count());
        System.out.println("isSupportsNormalTermination(): " + processHandle.supportsNormalTermination());

        ProcessHandle.Info processInfo = processHandle.info();
        System.out.println("Info: " + processInfo.toString());
        System.out.println("Info arguments().isPresent(): " + processInfo.arguments().isPresent());
        System.out.println("Info command().isPresent(): " + processInfo.command().isPresent());
        System.out.println("Info totalCpuDuration().isPresent(): " + processInfo.totalCpuDuration().isPresent());
        System.out.println("Info user().isPresent(): " + processInfo.user().isPresent());
    }
}
