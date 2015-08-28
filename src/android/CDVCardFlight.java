package org.weeels.plugins.cardflight;

import android.util.Log;
import com.getcardflight.models.CardFlight;
import com.getcardflight.models.Reader;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class CDVCardFlight extends CordovaPlugin {

  private Reader reader;
  private CardFlightHandler handler;
  private CordovaInterface cdv;

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    handler = new CardFlightHandler(cordova);
    cdv = cordova;
  }

  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    boolean success = true;

    if (action.equals("authorizeCardFlightAccount")) {
      this.authorizeCardFlightAccount(args.getString(0), args.getString(1), callbackContext);
    } else if (action.equals("initializeReader")) {
      this.initializeReader(callbackContext);
    } else if (action.equals("onReaderAttached")) {
      handler.onReaderConnected(callbackContext);
    } else if (action.equals("onReaderConnecting")) {
      handler.onReaderConnecting(callbackContext);
    } else if (action.equals("onSwipeDetected")) {
      handler.onSwipeDetected(callbackContext);
    } else if (action.equals("onBatteryLow")) {
      handler.onBatteryLow(callbackContext);
    } else if (action.equals("onReaderDisconnected")) {
      handler.onReaderDisconnected(callbackContext);
    } else if (action.equals("tokenizeLastSwipe")) {
      handler.tokenizeCard(callbackContext);
    } else if (action.equals("onReaderConnected")) {
      handler.onReaderConnected(callbackContext);
    } else if (action.equals("watchForSwipe")) {
      this.watchForSwipe(callbackContext);
    } else if (action.equals("onCardRead")) {
      handler.onCardRead(callbackContext);
    } else if (action.equals("startOnReaderAttached")) {
      handler.onReaderAttached(callbackContext);
    } else if (action.equals("onReaderFail")){
      handler.onReaderFail(callbackContext);
    } else if (action.equals("stopSwipe")) {
      reader.StopSwipe();
    } else {
      success = false;
    }
    
    return success;
  }

  private void authorizeCardFlightAccount(String apiToken, String stripeMerchantToken, CallbackContext callbackContext) {
    if (apiToken == null || stripeMerchantToken == null) {
      logError("Need to send both an api token and a stripe merchant token to authorize cardflight");
      callbackContext.error("Need to send both an api token and a stripe merchant token to authorize cardflight");
    }

    CardFlight.getInstance().setApiTokenAndAccountToken(apiToken, stripeMerchantToken);
    log("CardFlight authorized on this device");
    callbackContext.success("CardFlight authorized on this device");
  }

  private void initializeReader(final CallbackContext callbackContext) {
    log("CardFlight reader initializing");
    // cdv.getThreadPool().execute(new Runnable() {
    //   public void run() {
    //     reader = new Reader(cdv.getActivity().getApplicationContext(), handler);
    //     callbackContext.success("CardFlight reader initialized");
    //     // reader = new Reader(this.cordova.getActivity().getApplicationContext(), handler, new AutoConfigHandler(callbackContext));
    //     // log("Reader AutoConfigHandler has cordova callback");
    //   }
    // });

    reader = new Reader(cdv.getActivity().getApplicationContext(), handler);
    this.handler.setReader(reader);
    callbackContext.success("CardFlight reader initialized");
  }

  private void watchForSwipe(CallbackContext callbackContext) {
    handler.resetCard();
    reader.beginSwipe();
    log("CardFlight reader awaiting swipe");
    callbackContext.success("CardFlight reader awaiting swipe");
  }

  private void log(String s) {
    Log.i("CDVCardFlight", s);
  }

  private void logError(String s) {
    Log.e("CDVCardFlight", s);
  }
}