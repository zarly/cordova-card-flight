
#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>

#import "CFTReader.h"
#import "CFTCard.h"
#import "CFTSessionManager.h"

@import MediaPlayer;

@interface CDVCardFlight : CDVPlugin <CFTReaderDelegate>

@property NSString* readerAttachedCallbackID;
@property NSString* readerConnectingCallbackID;
@property NSString* readerSwipeCallbackID;
@property NSString* cardReadCallbackID;
@property NSString* readerBatteryLowCallbackID;
@property NSString* readerUndetectedCallbackID;
@property NSString* readerConnectedCallbackID;
@property NSString* readerDetatchedCallbackID;
@property NSString* currentCallbackID;

@property(readonly) CFTReader* reader;
@property(readonly) CFTCard* card;
@property(readonly) BOOL cardFlightAuthorized;

// exposed to JS layer
- (void)authorizeCardFlightAccount:(CDVInvokedUrlCommand*)command;
- (void)initializeReader:(CDVInvokedUrlCommand*)command;
- (void)tokenizeLastSwipe:(CDVInvokedUrlCommand*)command;
- (void)watchForSwipe:(CDVInvokedUrlCommand*)command;

- (void)onReaderAttached:(CDVInvokedUrlCommand*)command;
- (void)onReaderConnecting:(CDVInvokedUrlCommand*)command;
- (void)onSwipeDetected:(CDVInvokedUrlCommand*)command;
- (void)onCardRead:(CDVInvokedUrlCommand*)command;
- (void)onBatteryLow:(CDVInvokedUrlCommand*)command;
- (void)onReaderNotDetected:(CDVInvokedUrlCommand*)command;
- (void)onReaderConnected:(CDVInvokedUrlCommand*)command;
- (void)onReaderDisconnected:(CDVInvokedUrlCommand*)command;

// used internally
- (void)readerIsAttached;
- (void)readerIsConnecting;
- (void)readerSwipeDetected;
- (void)readerBatteryLow;
- (void)readerNotDetected;
- (void)readerIsConnected:(BOOL)isConnected withError:(NSError *)error;
- (void)readerCardResponse:(CFTCard *)card withError:(NSError *)error;
- (void)transactionResult:(CFTCharge *)charge withError:(NSError *)error;
- (void)readerIsDisconnected;

- (void)sendSuccessMessage:(NSString *)responseString;
- (void)sendSuccessMessage:(NSString *)responseString withCallbackID:(NSString *)callbackID;
- (void)sendSuccessDictionary:(NSDictionary *)responseBody;
- (void)sendSuccessDictionary:(NSDictionary *)responseBody withCallbackID:(NSString *)callbackID;
- (void)sendErrorMessage:(NSString *)errorMessage;
- (void)sendErrorMessage:(NSString *)errorMessage withCallbackID:(NSString *)callbackID;

@end