package com.marketucan.plugins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.NetPrinter;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;

/**
 * This class echoes a string called from JavaScript.
 */
public class BrotherLabelPrinter extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callback) throws JSONException {
        if (action.equals("getNetworkedPrinters")) {
            String model = args.getString(0);
            this.getNetworkedPrinters(model, callback);
            return true;
        }

        if (action.equals("print")) {
            String ipAddress = args.getString(0);
            String macAddress = args.getString(1);
            String message = args.getString(2);
            this.print(ipAddress, macAddress, message, callback);
            return true;
        }

        return false;
    }

    public static Bitmap bmpFromBase64(String base64) {
        try {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private void print(String ipAddress, String macAddress, String message, final CallbackContext callback) {
        final Bitmap bitmap = bmpFromBase64(message);

        cordova.getThreadPool().execute(
            new Runnable() {
                public void run() {
                    try {
                        Printer printer = new Printer();
                        PrinterInfo printerInfo = new PrinterInfo();
                        printerInfo = printer.getPrinterInfo();

                        printerInfo.printerModel  = PrinterInfo.Model.QL_810W;
                        printerInfo.port          = PrinterInfo.Port.NET;
                        printerInfo.printMode     = PrinterInfo.PrintMode.ORIGINAL;
                        printerInfo.orientation   = PrinterInfo.Orientation.PORTRAIT;
                        printerInfo.paperSize     = PrinterInfo.PaperSize.CUSTOM;
                        printerInfo.isAutoCut     = true;
                        printerInfo.ipAddress     = ipAddress;
                        printerInfo.macAddress    = macAddress;

                        // ----> for brother developers team support <----
                        // the ip address and mac address are correctly set
                        // it crash everytime at this call, I tried allot of different configuration
                        printer.setPrinterInfo(printerInfo);

                        PrinterStatus status = printer.printImage(bitmap);

                        PluginResult result;
                        result = new PluginResult(PluginResult.Status.OK);
                        callback.sendPluginResult(result);
                    } catch(Exception exception) {
                        PluginResult result;
                        result = new PluginResult(PluginResult.Status.ERROR, exception.getMessage());
                        callback.sendPluginResult(result);
                    }
                }
            });
    }

    private void getNetworkedPrinters(String model, final CallbackContext callback) {
        PluginResult result;
        if (model == null) {
            result = new PluginResult(PluginResult.Status.ERROR);
            callback.sendPluginResult(result);
            return;
        }

        cordova.getThreadPool().execute(
            new Runnable() {
                public void run() {
                    try{
                        Printer myPrinter = new Printer();
                        PrinterInfo myPrinterInfo = new PrinterInfo();
                        NetPrinter[] netPrinters = myPrinter.getNetPrinters(model);
                        int netPrinterCount = netPrinters.length;

                        ArrayList<Map> netPrintersList = new ArrayList<Map>();
                        JSONArray args = new JSONArray();
                        for (int i = 0; i < netPrinterCount; i++) {
                            Map<String, String> netPrinter = new HashMap<String, String>();

                            JSONObject jsonPrinter = new JSONObject();
                            jsonPrinter.put("ipAddress", netPrinters[i].ipAddress);
                            jsonPrinter.put("macAddress", netPrinters[i].macAddress);
                            jsonPrinter.put("serialNumber", netPrinters[i].serNo);
                            jsonPrinter.put("nodeName", netPrinters[i].nodeName);
                            args.put(jsonPrinter);

                            netPrinter.put("ipAddress", netPrinters[i].ipAddress);
                            netPrinter.put("macAddress", netPrinters[i].macAddress);
                            netPrinter.put("serialNumber", netPrinters[i].serNo);
                            netPrinter.put("nodeName", netPrinters[i].nodeName);

                            netPrintersList.add(netPrinter);
                        }


                        PluginResult result;
                        result = new PluginResult(PluginResult.Status.OK, args);
                        callback.sendPluginResult(result);
                    } catch(Exception exception) {
                        PluginResult result;
                        result = new PluginResult(PluginResult.Status.ERROR, exception.getMessage());
                        callback.sendPluginResult(result);
                    }
                }
            });
    }
}
