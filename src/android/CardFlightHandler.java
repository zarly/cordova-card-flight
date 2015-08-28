package org.weeels.plugins.cardflight;

import android.util.Log;
import com.getcardflight.interfaces.CardFlightDeviceHandler;
import com.getcardflight.models.Card;
import com.getcardflight.models.Reader;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;

public class CardFlightHandler implements CardFlightDeviceHandler {
  
  private Card card;
  private CordovaInterface cordova;

  private CallbackContext cardReadCallbackContext;
  private CallbackContext readerAttachedCallbackContext;
  private CallbackContext readerConnectingCallbackContext;
  private CallbackContext readerDisconnectedCallbackContext;
  private CallbackContext readerConnectedCallbackContext;
  private CallbackContext onBeginSwipeCallbackContext;
  private CallbackContext onReaderFailCallbackContext;

  private Reader reader;

  private boolean conntectedAfterConnectingCalled;

  public CardFlightHandler(CordovaInterface c) {
    cordova = c;
  }

  public void setReader(Reader reader) {
    this.reader = reader;
  }

  private void sendSuccessToCallback(CallbackContext callbackContext, String message) {
    PluginResult result = new PluginResult(PluginResult.Status.OK, message);
    result.setKeepCallback(true);
    callbackContext.sendPluginResult(result);
  }

  private void sendErrorToCallback(CallbackContext callbackContext, String message) {
    PluginResult result = new PluginResult(PluginResult.Status.ERROR, message);
    result.setKeepCallback(true);
    callbackContext.sendPluginResult(result);
  }

  public void onCardRead(CallbackContext callbackContext) {
    log("Setting onCardRead callback");
    cardReadCallbackContext = callbackContext;
  }

  public void onReaderAttached(CallbackContext callbackContext) {
    log("Setting onReaderAttached callback");
    readerAttachedCallbackContext = callbackContext;
  }

  public void onReaderConnecting(CallbackContext callbackContext) {
    log("Setting onReaderConnecting callback");
    readerConnectingCallbackContext = callbackContext;
  }

  public void onReaderDisconnected(CallbackContext callbackContext) {
    log("Setting onReaderDisconnected callback");
    readerDisconnectedCallbackContext = callbackContext;
  }

  public void onReaderConnected(CallbackContext callbackContext) {
    log("Setting onReaderConnected callback");
    readerConnectedCallbackContext = callbackContext;
  }

  public void onSwipeDetected(CallbackContext callbackContext) {
    log("Setting onSwipeDetected callback");
    onBeginSwipeCallbackContext = callbackContext;
  }

  public void onReaderFail(CallbackContext callbackContext) {
    this.onReaderFailCallbackContext = callbackContext;
  }

  public void onBatteryLow(CallbackContext callbackContext) {
    logError("onBatteryLow not supported by Android CardFlight SDK");
    callbackContext.error("Cannot use onBatteryLow on Android");
  }

  public void tokenizeCard(CallbackContext callbackContext) {
    log("tokenizing card");
    if (card == null) {
      callbackContext.error("No card to tokenize");
      return;
    }

    TokenizationHandler tokenHandler = new TokenizationHandler(card, callbackContext);
    card.tokenize(tokenHandler, cordova.getActivity().getApplicationContext());
  }

  public void resetCard() {
    card = null;
  }

  @Override
  public void readerIsConnecting() {
    log("readerIsConnecting called");
    if (readerConnectingCallbackContext != null) {
      sendSuccessToCallback(readerConnectingCallbackContext, "Reader is connecting");
      // readerConnectingCallbackContext.success("Reader is connecting");
    }

    // conntectedAfterConnectingCalled = false;
    // cordova.getThreadPool().execute(new Runnable() {
    //   public void run() {
    //     log("Triggering connected from connecting");
    //     if (conntectedAfterConnectingCalled) {
    //       log("Connected called successfully so not mocking it");
    //     } else {
    //       if (readerAttachedCallbackContext != null) {
    //         readerAttachedCallbackContext.success("Reader attached");
    //       }

    //       if (readerConnectedCallbackContext != null) {
    //         readerConnectedCallbackContext.success("Reader attached");
    //       }
    //     }
    //   }
    // });
  }

  @Override
  public void readerIsAttached() {
    log("readerIsAttached called");
    if (readerAttachedCallbackContext != null) {
      sendSuccessToCallback(readerAttachedCallbackContext, "Reader attached");
      // readerAttachedCallbackContext.success("Reader attached");
    }

    if (readerConnectedCallbackContext != null) {
      sendSuccessToCallback(readerConnectedCallbackContext, "Reader attached");
      // readerConnectedCallbackContext.success("Reader attached");
    }
  }

  @Override
  public void readerIsDisconnected() {
    log("readerIsDisconnected called");
    if (readerDisconnectedCallbackContext != null) {
      sendSuccessToCallback(readerDisconnectedCallbackContext, "Reader disconnected");
      // readerDisconnectedCallbackContext.success("Reader disconnected");
    }
  }

  @Override
  public void deviceBeginSwipe() {
    log("deviceBeginSwipe called");
    if (onBeginSwipeCallbackContext != null) {
      sendSuccessToCallback(onBeginSwipeCallbackContext, "Reader swipe begin");
    }
  }

  @Override
  public void readerCardResponse(Card c) {
    log("readerCardResponse called");
    if (cardReadCallbackContext != null) {
      card = c;
      sendSuccessToCallback(cardReadCallbackContext, "Card read successfully");
      // cardReadCallbackContext.success("Card read successfully");
    }
  }

  @Override
  public void readerFail(String errorMessage, int errorCode) {
    logError("readerFail callback: " + errorMessage);

    if (errorCode == 485 || errorCode == 483) {
      // CardFlight doesn't respect changing the
      // timeout value for swipes, so retry swiping if we timeout or if there's a swipe error
      if (this.reader != null) {
        this.reader.beginSwipe();
      }
    }

    // Still let the error code go to
    if (onReaderFailCallbackContext != null) {
      sendErrorToCallback(onReaderFailCallbackContext, Integer.toString(errorCode));
    }
  }

  private void log(String s) {
    Log.i("CDVCardFlightDeviceHandler", s);
  }

  private void logError(String s) {
    Log.e("CDVCardFlightDeviceHandler", s);
  }
}