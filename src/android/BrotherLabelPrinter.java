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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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

                        printerInfo.printerModel  = PrinterInfo.Model.QL_810W;
                        printerInfo.port          = PrinterInfo.Port.NET;
                        printerInfo.ipAddress     = ipAddress;
                        printerInfo.macAddress    = macAddress;
                        printerInfo.printMode     = PrinterInfo.PrintMode.ORIGINAL;
                        printerInfo.orientation   = PrinterInfo.Orientation.PORTRAIT;
                        printerInfo.paperSize     = PrinterInfo.PaperSize.CUSTOM;
                        
                        printer.setPrinterInfo(printerInfo);
      
                        LabelInfo labelInfo = new LabelInfo();
                        labelInfo.labelNameIndex  = printer.checkLabelInPrinter();
                        labelInfo.isAutoCut       = true;
                        labelInfo.isEndCut        = true;
                        labelInfo.isHalfCut       = false;
                        labelInfo.isSpecialTape   = false;

                        printer.setLabelInfo(labelInfo);

                        String labelWidth = "" + printer.getLabelParam().labelWidth;
                        String paperWidth = "" + printer.getLabelParam().paperWidth;
                        
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

    /*private void print(JSONObject printOptions, final callbackContext callback) {
        cordova.getThreadPool().execute(
            new Runnable() {
                public void run() {
                    try {

                        (JSONObject)printerConfig.get("printer");

                        Printer printer = new Printer();
                        PrinterInfo printerInfo = new PrinterInfo();

                        printerInfo.printerModel  = PrinterInfo.Model.QL_810W;
                        printerInfo.port          = PrinterInfo.Port.NET;
                        printerInfo.ipAddress     = ipAddress;
                        printerInfo.macAddress    = macAddress;
                        printerInfo.printMode     = PrinterInfo.PrintMode.ORIGINAL;
                        printerInfo.orientation   = PrinterInfo.Orientation.PORTRAIT;
                        printerInfo.paperSize     = PrinterInfo.PaperSize.CUSTOM;
      
                        
                    } catch(Exception exception) {
                        PluginResult result;
                        result = new PluginResult(PluginResult.Status.ERROR, exception.getMessage());
                        callback.sendPluginResult(result);
                    }
                }
            });
    }*/

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

                        Boolean available = netPrinterCount > 0;
                        args.put(available);
                        args.put(netPrintersList);

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
