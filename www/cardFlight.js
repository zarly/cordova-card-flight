cordova.define("cordova-plugin-cardflight.cardFlight", function(require, exports, module) { var exec = require('cordova/exec'),
    cardFlight,
    CardFlight = function(){}


var errorObjFunc = function(cb){
  return function(message){
    cb(new Error(message))
  }
}

CardFlight.prototype.authorize = function(apiToken, merchantToken, successCallback, errorCallback){
  cordova.exec(successCallback, errorObjFunc(errorCallback), 'CDVCardFlight', 'authorizeCardFlightAccount', [apiToken, merchantToken])
}

CardFlight.prototype.startReader = function(cb){
  cordova.exec(cb, cb, 'CDVCardFlight', 'initializeReader', [])
}

CardFlight.prototype.onReaderAttached = function(cb){
  cordova.exec(cb, cb, 'CDVCardFlight', 'onReaderAttached', [])
}

CardFlight.prototype.onReaderConnecting = function(cb){
  cordova.exec(cb, cb, 'CDVCardFlight', 'onReaderConnecting', [])
}

CardFlight.prototype.onSwipeDetected = function(cb){
  cordova.exec(cb, cb, 'CDVCardFlight', 'onSwipeDetected', [])
}

CardFlight.prototype.onReaderFail = function(cb){
    cordova.exec(cb, cb, 'CDVCardFlight', 'onReaderFail', [])
  }

CardFlight.prototype.onBatteryLow = function(cb){
  cordova.exec(cb, cb, 'CDVCardFlight', 'onBatteryLow', [])
}

CardFlight.prototype.onReaderDisconnected = function(cb){
  cordova.exec(cb, cb, 'CDVCardFlight', 'onReaderDisconnected', [])
}

CardFlight.prototype.tokenizeLastSwipe = function(successCb, errorCb){
  cordova.exec(function(dataString){
    // console.log("tokenizeLastSwipe -- converting data to JSON: "+dataString)
    // successCb(JSON.parse(dataString))
    successCb(dataString)
  }, errorObjFunc(errorCb), 'CDVCardFlight', 'tokenizeLastSwipe', [])
}

CardFlight.prototype.onReaderConnected = function(successCb, errorCb){
  cordova.exec(successCb, errorCb, 'CDVCardFlight', 'onReaderConnected', [])
}

CardFlight.prototype.onCardRead = function(successCb, errorCb){
  cordova.exec(function(dataString){
    // console.log("onReaderConnected -- converting data to JSON: "+dataString)
    // successCb(JSON.parse(dataString))
    successCb(dataString)
  }, errorObjFunc(errorCb), 'CDVCardFlight', 'onCardRead', [])
}

CardFlight.prototype.watchForSwipe = function(successCb, errorCb){
  cordova.exec(successCb, errorObjFunc(errorCb), 'CDVCardFlight', 'watchForSwipe', [])
}

CardFlight.prototype.stopSwipe = function(successCb, errorCb){
  cordova.exec(successCb, errorObjFunc(errorCb), 'CDVCardFlight', 'stopSwipe', [])
}

cardFlight = new CardFlight()
module.exports = cardFlight
});

