package com.dc;


public class DivineCloudCli {

    public static void main(String [] args) {
        if(args == null || args.length < 2) {
            System.out.println("Invalid arguments passed.");
            System.out.println("Usage: ");
            System.out.println("dc-cli -r runbookFilePath -n nodesPerStepFilePath -p propertiesFilePath -c credentialProviderFilePath -o outputFilePath");
            System.exit(1);
        }

        CliArgs cliArgs = parse(args);

        System.out.println(cliArgs);
    }

    private static CliArgs parse(String[] args) {
        CliArgs result = new CliArgs();
        int currentPointer = 0;
        while(currentPointer < args.length) {
            String type = args[currentPointer];
            switch(type) {
                case "-r" :
                    result.runbookFile = args[++currentPointer];
                    break;
                case "-n" :
                    result.nodesPerStepFile = args[++currentPointer];
                    break;
                case "-p" :
                    result.propertiesFile = args[++currentPointer];
                    break;
                case "-c" :
                    result.credentialsProviderFile = args[++currentPointer];
                    break;
                case "-o" :
                    result.outputFile = args[++currentPointer];
                    break;

                default:
            }
            ++currentPointer;

        }


        return result;
    }
}

class CliArgs {
    String nodesPerStepFile;
    String runbookFile;
    String outputFile;
    String credentialsProviderFile;
    String propertiesFile;
    boolean emitOutput;


    @Override
    public String toString() {
        return "CliArgs{" +
                "nodesPerStepFile='" + nodesPerStepFile + '\'' +
                ", runbookFile='" + runbookFile + '\'' +
                ", outputFile='" + outputFile + '\'' +
                ", credentialsProviderFile='" + credentialsProviderFile + '\'' +
                ", propertiesFile='" + propertiesFile + '\'' +
                ", emitOutput=" + emitOutput +
                '}';
    }
}