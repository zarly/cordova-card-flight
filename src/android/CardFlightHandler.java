package org.weeels.plugins.cardflight;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CallbackContext;
import com.getcardflight.models.Card;
import com.getcardflight.interfaces.*;
import android.util.Log;

public class CardFlightHandler implements CardFlightDeviceHandler {
  
  private Card card;
  private CordovaInterface cordova;

  private CallbackContext cardReadCallbackContext;
  private CallbackContext readerAttachedCallbackContext;
  private CallbackContext readerConnectingCallbackContext;
  private CallbackContext readerDisconnectedCallbackContext;
  private CallbackContext readerConnectedCallbackContext;

  public CardFlightHandler(CordovaInterface c) {
    cordova = c;
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
    logError("onSwipeDetected not supported by Android CardFlight SDK");
    callbackContext.error("Cannot use onSwipeDetected on Android");
  }

  public void onBatteryLow(CallbackContext callbackContext) {
    logError("onBatteryLow not supported by Android CardFlight SDK");
    callbackContext.error("Cannot use onBatteryLow on Android");
  }

  public void tokenizeCard(CallbackContext callbackContext) {
    log("tokenizing card");
    if (card == null) {
      callbackContext.error("No card to tokenize");
    }

    TokenizationHandler tokenHandler = new TokenizationHandler(card, callbackContext);
    card.tokenize(tokenHandler, cordova.getActivity().getApplicationContext());
  }

  public void resetCard() {
    card = null;
  }

  @Override
  public void readerCardResponse(Card c) {
    log("readerCardResponse called");
    if (cardReadCallbackContext != null) {
      card = c;
      cardReadCallbackContext.success("Card read successfully");
    }
  }

  @Override
  public void readerIsConnecting() {
    log("readerIsConnecting called");
    if (readerConnectingCallbackContext != null) {
      readerConnectingCallbackContext.success("Reader is connecting");
    }
  }

  @Override
  public void readerIsAttached() {
    log("readerIsAttached called");
    if (readerAttachedCallbackContext != null) {
      readerAttachedCallbackContext.success("Reader attached");
    }

    if (readerConnectedCallbackContext != null) {
      readerConnectedCallbackContext.success("Reader attached");
    }
  }

  @Override
  public void readerIsDisconnected() {
    log("readerIsDisconnected called");
    if (readerDisconnectedCallbackContext != null) {
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
    Log.i("CDVCardFlightDeviceHandler", s);
  }

  private void logError(String s) {
    Log.e("CDVCardFlightDeviceHandler", s);
  }
}