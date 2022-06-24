package cli;

import dataanalysis.composite.MEGTest;
import multiobjectiveoptimization.composite.MEGRun;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(subcommands = {MEGRun.class, MEGTest.class},
        header = """
                ___  ___ _____ _____ 
                |  \\/  ||  ___|  __ \\
                | .  . || |__ | |  \\/
                | |\\/| ||  __|| | __ 
                | |  | || |___| |_\\ \\
                \\_|  |_/\\____/ \\____/
                
                Multi-objective Ensemble Generation
                A BeerWare software :)
                """,
        version = "MEG v1.0-SNAPSHOT"
)
public class MEG implements Runnable {

    @Option(names = {"-V", "--version"}, versionHelp = true, description = "display version info")
    boolean versionInfoRequested;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display this help message")
    boolean usageHelpRequested;

    public static void main(String[] args) {
        new CommandLine(new MEG()).execute(args);
    }

    @Override
    public void run() {
        // empty
    }
}
