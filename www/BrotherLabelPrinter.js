module.exports = {
    setPrinterModel: function (model, success, error) {
        cordova.exec(success, error, 'BrotherLabelPrinter', 'SetPrinterModel', [model]);
    },

    getNetworkedPrinters: function (success, error) {
        cordova.exec(success, error, 'BrotherLabelPrinter', 'GetNetworkedPrinters');
    }
};