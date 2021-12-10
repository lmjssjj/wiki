package com.example.lxn.agingtestnew.utils;


import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;



public class ShellUtils {
    private static final String TAG = "lxn-Aging-ShellUtils";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_SU = "su";


    /**
     *
     */
    public static class CommandResult {
        public String errorMsg;
        public int result;
        public String successMsg;

        public CommandResult(int result) {
            this.result = result;
        }
        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }

    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    /**
     *
     * @param command
     * @param isRoot
     * @return
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[]{command}, isRoot, true);
    }

    /**
     *
     * @param commands
     * @param isRoot
     * @return
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        String[] strArr;
        if (commands == null) {
            strArr = null;
        } else {
            strArr = (String[]) commands.toArray(new String[0]);
        }
        return execCommand(strArr, isRoot, true);
    }

    /**
     *
     * @param commands
     * @param isRoot
     * @return
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    /**
     *
     * @param command
     * @param isRoot
     * @param isNeedResultMsg
     * @return
     */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[]{command}, isRoot, isNeedResultMsg);
    }

    /**
     *
     * @param commands
     * @param isRoot
     * @param isNeedResultMsg
     * @return
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        String[] strArr;
        if (commands == null) {
            strArr = null;
        } else {
            strArr = (String[]) commands.toArray(new String[0]);
        }
        return execCommand(strArr, isRoot, isNeedResultMsg);
    }

    /**
     *
     */
    private static class StreamGobbler extends Thread {
        boolean abort = false;
        BufferedReader br;
        StringBuilder builder;
        InputStream is;
        String type;

        StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
            this.builder = new StringBuilder();
            this.abort = false;
        }

        public void abort() {
            this.abort = true;
        }

        public String getOutput() {
            return this.builder.toString();
        }

        @Override
        public void run() {
            try {
                this.br = new BufferedReader(new InputStreamReader(this.is));
                while (true) {
                    String line = this.br.readLine();
                    if (line == null || this.abort) {
                        break;
                    }
                    this.builder.append(line);
                    this.builder.append(ShellUtils.COMMAND_LINE_END);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } finally {
                try {
                    if (this.is != null) {
                        this.is.close();
                        this.is = null;
                    }
                    if (this.br != null) {
                        this.br.close();
                        this.br = null;
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }

    }

    /**
     * @param commands
     * @param isRoot
     * @param isNeedResultMsg
     * @return
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        Log.d(TAG, "execCommand(1, 2, 3) commands:" + commands + "\nisRoot:" + isRoot + "\nisNeedResultMsg:" + isNeedResultMsg);
        if (commands == null || commands.length == 0) {
            return new CommandResult(-1, null, null);
        }
        Process process = null;
        DataOutputStream dataOutputStream = null;
        String errorMsg = null;
        String successMsg = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            for (String command : commands) {
                if (command != null) {
                    os.write(command.getBytes());
                    os.writeBytes(COMMAND_LINE_END);
                    os.flush();
                }
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            StreamGobbler errorReader = new StreamGobbler(process.getErrorStream(), "ERROR");
            StreamGobbler inputReader = new StreamGobbler(process.getInputStream(), "INPUT");

            errorReader.start();
            inputReader.start();

            result = process.waitFor();
            Log.d(TAG, "result:" + result);

            errorReader.join();
            errorReader.abort();
            inputReader.join();
            inputReader.abort();

            if (isNeedResultMsg) {
                errorMsg = errorReader.getOutput();
                successMsg = inputReader.getOutput();
            }

            if (os != null) {
                os.close();
            }

            if (process != null) {
                process.destroy();
                dataOutputStream = os;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "e:");
        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.d(TAG, "e2:");
        }

        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        if (process != null) {
            process.destroy();
        }
        return new CommandResult(result, successMsg, errorMsg);
    }
}

