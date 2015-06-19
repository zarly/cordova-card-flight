var exec = require('cordova/exec'),
    cardFlight,
    CardFlight = function(){}


CardFlight.prototype.authorize = function(apiToken, merchantToken, successCallback, errorCallback){
  cordova.exec(successCallback, errorCallback, 'CDVCardFlight', 'authorizeCardFlightAccount', [apiToken, merchantToken])
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

CardFlight.prototype.onBatteryLow = function(cb){
  cordova.exec(cb, cb, 'CDVCardFlight', 'onBatteryLow', [])
}

CardFlight.prototype.onReaderDisconnected = function(cb){
  cordova.exec(cb, cb, 'CDVCardFlight', 'onReaderDisconnected', [])
}

CardFlight.prototype.tokenizeLastSwipe = function(successCb, errorCb){
  cordova.exec(successCb, errorCb, 'CDVCardFlight', 'tokenizeLastSwipe', [])
}

CardFlight.prototype.onReaderConnected = function(successCb, errorCb){
  cordova.exec(successCb, errorCb, 'CDVCardFlight', 'onReaderConnected', [])
}

CardFlight.prototype.onCardRead = function(successCb, errorCb){
  cordova.exec(successCb, errorCb, 'CDVCardFlight', 'onCardRead', [])
}

CardFlight.prototype.watchForSwipe = function(successCb, errorCb){
  cordova.exec(successCb, errorCb, 'CDVCardFlight', 'watchForSwipe', [])
}

cardFlight = new CardFlight()
module.exports = cardFlight