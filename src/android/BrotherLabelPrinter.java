package com.marketucan.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;

/**
 * This class echoes a string called from JavaScript.
 */
public class BrotherLabelPrinter extends CordovaPlugin {
    private String modelName = "";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("setPrinterModel")) {
            String model = args.getString(0);
            this.setPrinterModel(model, callbackContext);
            return true;
        }

        if (action.equals("getNetworkedPrinters")) {
            this.getNetworkedPrinters(callbackContext);
            return true;
        }
        return false;
    }

    private void setPrinterModel(String model, final CallbackContext callback) {
        PluginResult result;

        if (model == null) {
            result = new PluginResult(PluginResult.Status.ERROR);
            callback.sendPluginResult(result);
            return;
        }
        
        modelName = model;

        result = new PluginResult(PluginResult.Status.OK);
        callback.sendPluginResult(result);
    }

    private void getNetworkedPrinters(final CallbackContext callback) {
        cordova.getThreadPool().execute(
            new Runnable() {
                public void run() {
                    try{
                        Printer myPrinter = new Printer();
                        PrinterInfo myPrinterInfo = new PrinterInfo();
                        NetPrinter[] netPrinters = myPrinter.getNetPrinters(modelName);
                        int netPrinterCount = netPrinters.length;

                        ArrayList<Map> netPrintersList = null;
                        if(netPrintersList != null) netPrintersList.clear();
                        netPrintersList = new ArrayList<Map>();

                        for (int i = 0; i < netPrinterCount; i++) {
                            Map<String, String> netPrinter = new HashMap<String, String>();

                            netPrinter.put("ipAddress", netPrinters[i].ipAddress);
                            netPrinter.put("macAddress", netPrinters[i].macAddress);
                            netPrinter.put("serialNumber", netPrinters[i].serNo);
                            netPrinter.put("nodeName", netPrinters[i].nodeName);

                            netPrintersList.add(netPrinter);
                        }

                        JSONArray args = new JSONArray();
                        PluginResult result;

                        Boolean available = netPrinterCount > 0;
                        args.put(available);
                        args.put(netPrintersList);

                        result = new PluginResult(PluginResult.Status.OK, args);
                        callback.sendPluginResult(result);
                    } catch(Exception exception) {
                        exception.printStackTrace();

                        PluginResult result;
                        result = new PluginResult(PluginResult.Status.ERROR, exception.getMessage());
                        callback.sendPluginResult(result);
                    }
                }
            });
    }
}
