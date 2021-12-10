
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PowerControl {

    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_SU = "su";
    private static final String MTK_DISABLE_CHARGE_CMD = "echo '0 1' > proc/mtk_battery_cmd/current_cmd";
    private static final String MTK_ENABLE_CHARGE_CMD = "echo '0 0' > proc/mtk_battery_cmd/current_cmd";


    /*
    * Monitor power
    * */
    public static int MAX_LEVEL = 80;
    public static int MIN_LEVEL = 65;
    BroadcastReceiver mBatteryChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);

                boolean isVolOk = checkBatteryVoltage();
                if (level >= MAX_LEVEL && isVolOk) {
                    boolean z;
                    boolean charged = disableCharge() < 0;
                    
                } else if (level <= MIN_LEVEL || !isVolOk) {
                    enableCharge();
                }

            }
        }
    };

    private static final String COMMAND_DUMP_BATTERY = "dumpsys battery";
    private static int MIN_VOLTAGE = 3800;
    private static boolean checkBatteryVoltage() {
        CommandResult result = execCommand(new String[]{COMMAND_DUMP_BATTERY}, false, true);
        if (result.result == 0) {
            for (String str : result.successMsg.split("\r|\n")) {
                if (!(str == null || str.contains("charging") || !str.contains("voltage"))) {
                    String[] vol = str.trim().split(":");
                    if (vol.length >= 2) {
                        try {
                            if (Integer.parseInt(vol[1].trim()) > MIN_VOLTAGE) {
                                return true;
                            }
                            return false;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return false;
    }


    public static int enableCharge() {
        String CMD;
        CMD = MTK_ENABLE_CHARGE_CMD;
        CommandResult result = execCommand(new String[]{CMD}, false, true);
        return result.result;
    }

    public static int disableCharge() {
        String CMD;
        CMD = MTK_DISABLE_CHARGE_CMD;
        CommandResult result = execCommand(new String[]{CMD}, false, true);
        return result.result;
    }

    /**
     * @param commands
     * @param isRoot
     * @param isNeedResultMsg
     * @return
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
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
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                    this.builder.append(COMMAND_LINE_END);
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
}
