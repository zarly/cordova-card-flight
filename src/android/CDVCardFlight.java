package org.weeels.cardFlight;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cardflight.Reader;

import cardflight.CardFlight;
import cardflight.Reader;

import android.util.Log;

public class CDVCardFlight extends CordovaPlugin {

  private Reader reader;
  private CardFlightHandler handler;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    handler = new CardFlightHandler();
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    boolean success = true

    switch (action) {
      case "authorizeCardFlightAccount":
        this.authorizeCardFlightAccount(args, callbackContext);
        break;
      case "initializeReader":
        this.initializeReader(callbackContext);
        break;
      case "onReaderAttached":
        handler.onReaderConnected(callbackContext);
        break;
      case "onReaderConnecting":
        handler.onReaderConnecting(callbackContext);
        break;
      case "onSwipeDetected":
        handler.onSwipeDetected(callbackContext);
        break;
      case "onBatteryLow":
        handler.onBatteryLow(callbackContext);
        break;
      case "onReaderDisconnected":
        handler.onReaderDisconnected(callbackContext);
        break;
      case "tokenizeLastSwipe":
        handler.tokenizeCard(callbackContext);
        break;
      case "onReaderConnected":
        handler.onReaderConnected(callbackContext);
        break;
      case "watchForSwipe":
        this.watchForSwipe(callbackContext);
        break;
      case "onCardRead":
        handler.onCardRead(callbackContext);
        break;
      default:
        success = false
        break;
    }
    
    return success;
  }

  private void authorizeCardFlightAccount(JSONArray args, CallbackContext callbackContext) {
    String apiToken = args.getString(0);
    String stripeMerchantToken = args.getString(1);

    if (!apiToken || !stripeMerchantToken) {
      callbackContext.error("Need to send both an api token and a stripe merchant token to authorize cardflight");
    }

    CardFlight.getInstance().setApiTokenAndAccountToken(apiToken, stripeMerchantToken);
  }

  private void initializeReader(CallbackContext callbackContext) {
    reader = new Reader(getApplicationContext(), handler);
    callbackContext.success("CardFlight reader initialized");
  }

  private void watchForSwipe(CallbackContext callbackContext) {
    reader.beginSwipe();
    callbackContext.success("CardFlight reader awaiting swipe");
  }
}