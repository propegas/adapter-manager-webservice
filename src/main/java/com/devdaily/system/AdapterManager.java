package com.devdaily.system;

import models.Adapter;
import models.AdapterConfigFile;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vgoryachev on 07.04.2016.
 * Package: com.devdaily.system.
 */
public class AdapterManager {

    static final Logger logger = LoggerFactory.getLogger(AdapterManager.class);

    private AdapterManager() {
        // default
    }

    public static String[] executeCommand(String command) throws SomeApplicationLevelException, InterruptedException {

        logger.info("... IN executeCommand ...");

        // build the system command we want to run
        List<String> commands = new ArrayList<>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(command);

        // execute the command
        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
        int result = 0;
        try {
            result = commandExecutor.executeCommand();
        } catch (IOException e) {
            logger.error("Command execution error: " + e);
            throw new SomeApplicationLevelException("Error while trying to build process for command execute.");
        } catch (InterruptedException e) {
            logger.error("Command execution error: " + e);
            throw e;
        }

        // get the stdout and stderr from the command that was run
        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

        // print the stdout and stderr
        logger.info("The numeric result of the command was: " + result);
        logger.info(String.format("STDOUT: %s", stdout));
        logger.info(String.format("STDERR: %s", stderr));


        return new String[]{stdout.toString(), stderr.toString()};

    }

    public static Map<String, java.io.Serializable> stopAdapter(Long id, Adapter adapter) {
        String command = adapter.getStopCommands();
        logger.info(String.format("**** Stopping command for adapter %s: %s",
                adapter.title, command));

        Map<String, java.io.Serializable> map = new HashMap<>();

        String output;
        String error;
        String[] fullOutput;
        try {
            fullOutput = AdapterManager.executeCommand(command);
        } catch (Exception e) {
            logger.error("Error while executing command: " + e);
            map.put("text", e.getCause() + " " + e.getMessage() + " " + e.toString());
            map.put("result", false);
            return map;
        }

        output = fullOutput[0];
        error = fullOutput[1];
        logger.info(String.format("Output: ***%s***", output));
        logger.info(String.format("Error: ***%s***", error));

        Pattern pattern = Pattern.compile(".*(STOPPED|SUCCESS).*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(output.toUpperCase());

        if (m.find() && !"".equals(output.trim())) {
            map.put("text", output + error);
            map.put("result", true);
            return map;
        } else {
            map.put("text", output + error);
            map.put("result", false);
            return map;
        }

    }

    private static String getShowLogCommand(int size, String type, Adapter adapter) {
        if ("errors".equals(type)) {
            if ("".equals(adapter.getErrorLogFile())) {
                logger.info(String.format("**** No Error Log file for adapter %s",
                        adapter.title));
                return null;
            }

            return String.format("tail -n %d %s",
                    size, adapter.getErrorLogFile());
        } else {
            if ("".equals(adapter.getLogFile())) {
                logger.info(String.format("**** No Main Log file for adapter %s",
                        adapter.title));
                return null;
            }
            return String.format("tail -n %d %s",
                    size, adapter.getLogFile());
        }
    }

    public static Map<String, java.io.Serializable> showLogAdapter(Long id,
                                                                   Adapter adapter,
                                                                   int receivedSize,
                                                                   String type) {

        String command;
        Map<String, java.io.Serializable> map = new HashMap<>();
        int size = receivedSize;

        if (receivedSize <= 0)
            size = 100;

        command = getShowLogCommand(size,type, adapter);
        if (command == null){
            logger.error("No log file in config");
            map.put("text", "No log file in adapter config");
            map.put("result", false);
            return map;
        }

        logger.info(String.format("**** Get Log command for adapter %s: %s",
                adapter.title, command));

        String output;
        String error;
        String[] fullOutput;
        try {
            fullOutput = AdapterManager.executeCommand(command);
        } catch (Exception e) {
            logger.error("Error while executing command: " + e);
            map.put("text", e.getCause() + " " + e.getMessage() + " " + e.toString());
            map.put("result", false);
            return map;
        }

        output = fullOutput[0];
        error = fullOutput[1];
        logger.debug(String.format("Output: ***%s***", output));
        logger.debug(String.format("Error: ***%s***", error));

        Pattern pattern = Pattern.compile(".*",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(output.toUpperCase());

        if (m.find() && !"".equals(output.trim())) {
            map.put("text", output + error);
            map.put("result", true);
            return map;
        } else {
            map.put("text", output + error);
            map.put("result", false);
            return map;
        }

    }

    public static Map<String, java.io.Serializable> startAdapter(Long id, Adapter adapter) {
        String command = adapter.getStartCommands();
       logger.debug(String.format("**** Starting command for adapter %s: %s",
                adapter.title, command));

        Map<String, java.io.Serializable> map = new HashMap<>();
        String output;
        String error;
        String[] fullOutput;
        try {
            fullOutput = AdapterManager.executeCommand(command);
        } catch (Exception e) {
            logger.error("Error while executing command: " + e);
            map.put("text", e.getCause() + " " + e.getMessage() + " " + e.toString());
            map.put("result", false);
            return map;

        }

        output = fullOutput[0];
        error = fullOutput[1];

        logger.debug(String.format("Output: ***%s***", output));
        logger.debug(String.format("Error: ***%s***", error));

        Pattern pattern = Pattern.compile(".*(STARTED|SUCCESS).*", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher m = pattern.matcher(output.toUpperCase());

        if (m.find() && !"".equals(output.trim())) {
            map.put("text", output + error);
            map.put("result", true);
            return map;
        } else {
            map.put("text", output + error);
            map.put("result", false);
            return map;
        }

    }

    public static Map showRawConfigFile(String confRawFile) {

        Map map = new HashMap<>();

        try {
            logger.debug("Try to read Raw content of file: " + confRawFile);
            FileInputStream fis = new FileInputStream(confRawFile);
            String contentFromFile = IOUtils.toString(fis, "ISO-8859-1");

            logger.debug("Raw content of file: " + contentFromFile);

            map.put("text", contentFromFile);
            map.put("result", true);

            fis.close();

            return map;

        } catch (FileNotFoundException e) {
            logger.debug("Error while file opening: " + e);
            map.put("text", e.getCause() + " " + e.getMessage() + " " + e.toString());
            map.put("result", false);
            return map;
        } catch (IOException e) {
            logger.debug("Error while file content reading: " + e);
            map.put("text", e.getCause() + " " + e.getMessage() + " " + e.toString());
            map.put("result", false);
            return map;
        }



    }

    public static Map saveConfigFileContent(AdapterConfigFile configFile,
                                            Adapter adapter,
                                            ConfigFileContent configFileContent) {
        HashMap<String,Object> map = new HashMap<>();

        map.put("text", "Success:" + configFileContent.getContent());
        map.put("result", true);

        return map;

    }

}
