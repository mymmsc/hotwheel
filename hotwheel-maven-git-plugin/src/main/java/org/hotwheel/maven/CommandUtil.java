package org.hotwheel.maven;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility used to execute command-line functions.
 *
 * @author jasonbruwer
 * @since v1.0
 */
public class CommandUtil {

    /**
     * The Value Object used to house the result of the transaction.
     */
    public static final class CommandResult {
        private int exitCode;
        private String[] resultLines;

        /**
         * Constructor that includes the Exit Code and Result lines of the file
         * execution.
         *
         * @param exitCodeParam The Exit Code.
         * @param resultLinesParam The command output result lines.
         */
        private CommandResult(int exitCodeParam, String[] resultLinesParam) {
            this.exitCode = exitCodeParam;
            this.resultLines = resultLinesParam;
        }

        /**
         * Gets the Exit Code.
         *
         * @return Exit Code.
         */
        public int getExitCode() {
            return this.exitCode;
        }

        /**
         * Gets the Result Lines.
         *
         * @return Result Lines.
         */
        public String[] getResultLines() {
            return this.resultLines;
        }

        /**
         * Combines all the {@code String[]} result lines into a single
         * {@code String}.
         *
         * @return The Result Lines as String.
         */
        @Override
        public String toString() {

            if (this.resultLines == null) {
                return null;
            }

            StringBuilder stringBuilder = new StringBuilder();
            for (String line : this.resultLines) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }

            return stringBuilder.toString();
        }
    }

    /**
     * Executes the given command line arguments as {@code commandParams}.
     *
     * @param logParam The Maven Log class.
     * @param commandParams The command and parameters to the command to
     *            execute.
     * @return The Execution Result as {@code CommandResult}.
     * @throws MojoExecutionException When something goes wrong during plugin
     *             execution.
     */
    public static CommandResult executeCommand(Log logParam, String... commandParams) throws MojoExecutionException {
        if (commandParams == null || commandParams.length == 0) {
            throw new MojoExecutionException("Unable to execute command. No commands provided.");
        }

        List<String> returnedLines = new ArrayList<>();

        try {
            Process process = Runtime.getRuntime().exec(commandParams);

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String readLine = null;
            while ((readLine = reader.readLine()) != null) {
                logParam.info("LINE[" + readLine + "]");
                returnedLines.add(readLine);
            }

            //Now for the error lines...
            if (returnedLines.isEmpty()) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                while ((readLine = errorReader.readLine()) != null) {
                    logParam.error("ERROR_LINE[" + readLine + "]");
                    returnedLines.add(readLine);
                }
            }

            int exitValue = -1000;
            try {
                exitValue = process.waitFor();
            }
            //
            catch (InterruptedException e) {
                logParam.error(e.getMessage());

                String commandString = (commandParams == null || commandParams.length == 0) ? "<unknown>" : commandParams[0];

                throw new MojoExecutionException("Unable to wait for command [" + commandString + "] to exit. " + e.getMessage(), e);
            }

            String[] rtnArr = {};
            return new CommandResult(exitValue, returnedLines.toArray(rtnArr));
        } catch (IOException ioExeption) {
            logParam.error(ioExeption.getMessage());
            String commandString = (commandParams == null || commandParams.length == 0) ? "<unknown>" : commandParams[0];

            throw new MojoExecutionException("Unable to execute command/s [" + commandString + "]. " + ioExeption.getMessage(), ioExeption);
        }
    }
}
