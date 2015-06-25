package org.weeels.cardFlight;

import org.apache.cordova.CallbackContext;
import cardflight.CardFlightDeviceHandler;
import cardflight.CardFlightTokenizationHandler;
import android.util.Log;

public class CardFlightHandler implements CardFlightDeviceHandler {
  
  private Card card;

  private CallbackContext cardReadCallbackContext;
  private CallbackContext readerAttachedCallbackContext;
  private CallbackContext readerConnectingCallbackContext;
  private CallbackContext readerDisconnectedCallbackContext;
  private CallbackContext readerConnectedCallbackContext;

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
    logError("onSwipeDetected not supported by Android CardFlight SDK");
    callbackContext.error("Cannot use onSwipeDetected on Android");
  }

  public void onBatteryLow(CallbackContext callbackContext) {
    logError("onBatteryLow not supported by Android CardFlight SDK");
    callbackContext.error("Cannot use onBatteryLow on Android");
  }

  public void tokenizeCard(CallbackContext callbackContext) {
    log("tokenizing card");
    if (!card) {
      callbackContext.error("No card to tokenize");
    }

    card.tokenize(new CardFlightTokenizationHandler() {
      @Override
      public void tokenizationSuccessful(String result){
        log("tokenization success callback");
        if (!card.token) {
          callbackContext.error("Got tokenization success callback but token was null");
        } else {
          callbackContext.success(card.token);
        }
      }

      @Override
      public void tokenizationFailed(String error, int errorCode) {
        log("tokenization fail callback");
        logError(error);
        callbackContext.error(error);
      },
    },
    getApplicationContext());
  }

  @Override
  public void readerCardResponse(Card c) {
    log("readerCardResponse called");
    if (cardReadCallbackContext) {
      card = c;
      cardReadCallbackContext.success("Card read successfully");
    }
  }

  @Override
  public void readerIsConnecting() {
    log("readerIsConnecting called");
    if (readerConnectingCallbackContext) {
      readerConnectingCallbackContext.success("Reader is connecting");
    }
  }

  @Override
  public void readerIsAttached() {
    log("readerIsAttached called");
    if (readerAttachedCallbackContext) {
      readerAttachedCallbackContext.success("Reader attached");
    }

    if (readerConnectedCallbackContext) {
      readerConnectedCallbackContext.success("Reader attached");
    }
  }

  @Override
  public void readerIsDisconnected() {
    log("readerIsDisconnected called");
    if (readerDisconnectedCallbackContext) {
      readerDisconnectedCallbackContext.success("Reader disconnected");
    }
  }

  @Override
  public void deviceBeginSwipe() {
    log("deviceBeginSwipe called");
  }

  @Override
  public void readerFail(String errorMessage, int errorCode) {
    logError(errorMessage);
  }

  private void log(String s) {
    Log.i("CardFlightHandler", s);
  }

  private void logError(String s) {
    Log.e("CardFlightHandler", s);
  }
}