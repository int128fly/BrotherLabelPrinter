var exec = require('cordova/exec');

module.export = {
    coolMethod: function (arg0, success, error) {
        exec(success, error, 'BrotherLabelPrinter', 'coolMethod', [arg0]);
    }
};