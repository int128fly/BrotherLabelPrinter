module.exports = {
    coolMethod: function (arg0, success, error) {
        console.log('got into plugin call');
        cordova.exec(success, error, 'BrotherLabelPrinter', 'coolMethod', [arg0]);
    }
};