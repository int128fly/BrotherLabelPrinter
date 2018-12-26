module.exports = {
    setPrinterModel: function (model, success, error) {
        cordova.exec(success, error, 'BrotherLabelPrinter', 'setPrinterModel', [model]);
    },

    getNetworkedPrinters: function (success, error) {
        cordova.exec(success, error, 'BrotherLabelPrinter', 'getNetworkedPrinters');
    }
};