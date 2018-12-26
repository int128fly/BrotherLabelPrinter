module.exports = {
    getNetworkedPrinters: function (model, success, error) {
        cordova.exec(success, error, 'BrotherLabelPrinter', 'getNetworkedPrinters', [
            model
        ]);
    },

    print: function (ipAddress, macAddress, message, success, error) {
        cordova.exec(success, error, 'BrotherLabelPrinter', 'print', [
            ipAddress,
            macAddress,
            message
        ]);
    }
};