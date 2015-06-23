#import "CDVCardFlight.h"

@implementation CDVCardFlight

@synthesize readerAttachedCallbackID;
@synthesize readerConnectingCallbackID;
@synthesize readerSwipeCallbackID;
@synthesize cardReadCallbackID;
@synthesize readerBatteryLowCallbackID;
@synthesize readerUndetectedCallbackID;
@synthesize readerConnectedCallbackID;
@synthesize readerDetatchedCallbackID;
@synthesize currentCallbackID;


- (CDVPlugin *)initWithWebView:(UIWebView *)theWebView {
  self = (CDVCardFlight *)[super initWithWebView:theWebView];
  _cardFlightAuthorized = NO;
  return self;
}

- (void)readerIsAttached {
  if (self.readerAttachedCallbackID) {
    [self sendSuccessMessage:@"Reader is attached" withCallbackID:self.readerAttachedCallbackID];
  }
}

- (void)readerIsConnecting {
  if (self.readerConnectingCallbackID) {
    [self sendSuccessMessage:@"Reader is connecting" withCallbackID:self.readerConnectingCallbackID];
  }
}

- (void)readerSwipeDetected {
  if (self.readerSwipeCallbackID) {
    [self sendSuccessMessage:@"Swipe detected" withCallbackID:self.readerSwipeCallbackID];
  }
}

- (void)readerBatteryLow {
  if (self.readerBatteryLowCallbackID) {
    [self sendSuccessMessage:@"Reader battery is low" withCallbackID:self.readerBatteryLowCallbackID];
  }
}

- (void)readerNotDetected {
  if (self.readerUndetectedCallbackID) {
    [self sendSuccessMessage:@"Reader not detected" withCallbackID:self.readerUndetectedCallbackID];
  }
}

- (void)readerIsConnected:(BOOL)isConnected withError:(NSError *)error {
  if (!self.readerConnectedCallbackID) {
    return NSLog(@"No readerConnectedCallbackID to trigger");
  }
  
  if (error) {
    [self sendErrorMessage:[error localizedDescription] withCallbackID:self.readerConnectedCallbackID];
  } else if (isConnected) {
    [self sendSuccessMessage:@"Reader connected" withCallbackID:self.readerConnectedCallbackID];
  } else {
    [self sendErrorMessage:@"Reader is not connected, no error given" withCallbackID:self.readerConnectedCallbackID];
  }
}

- (void)transactionResult:(CFTCharge *)charge withError:(NSError *)error {
  if (error) {
    NSLog(@"transactionResult callback with error: %@", error);
  } else {
    // TODO: handle success
  }
}


- (void)readerIsDisconnected {
  if (self.readerDetatchedCallbackID) {
    [self sendSuccessMessage:@"Reader disconnected" withCallbackID:self.readerDetatchedCallbackID];
  }
}

- (void)onReaderAttached:(CDVInvokedUrlCommand*)command {
  [self setReaderAttachedCallbackID:[command callbackId]];
}

- (void)onReaderConnecting:(CDVInvokedUrlCommand*)command {
  [self setReaderConnectingCallbackID:[command callbackId]];
}

- (void)onSwipeDetected:(CDVInvokedUrlCommand*)command {
  [self setReaderSwipeCallbackID:[command callbackId]];
}

- (void)onCardRead:(CDVInvokedUrlCommand*)command {
  [self setCardReadCallbackID:[command callbackId]];
}

- (void)onBatteryLow:(CDVInvokedUrlCommand*)command {
  [self setReaderBatteryLowCallbackID:[command callbackId]];
}

- (void)onReaderNotDetected:(CDVInvokedUrlCommand*)command {
  [self setReaderUndetectedCallbackID:[command callbackId]];
}

- (void)onReaderConnected:(CDVInvokedUrlCommand*)command {
  [self setReaderConnectedCallbackID:[command callbackId]];
}

- (void)onReaderDisconnected:(CDVInvokedUrlCommand*)command {
  [self setReaderDetatchedCallbackID:[command callbackId]];
}

- (void)authorizeCardFlightAccount:(CDVInvokedUrlCommand*)command {
  [self setCurrentCallbackID:[command callbackId]];

  NSString* cardFlightAPIToken = [command.arguments objectAtIndex:0];
  NSString* stripeMerchantToken = [command.arguments objectAtIndex:1];

  if (!cardFlightAPIToken) {
    return [self sendErrorMessage:@"Need to pass a CardFlight API token to initialize CardFlight"];
  }

  if (!stripeMerchantToken) {
    return [self sendErrorMessage:@"Need to pass a Stripe Merchant token to initialize CardFlight"];
  }

  [[CFTSessionManager sharedInstance] setApiToken:cardFlightAPIToken
                                      accountToken:stripeMerchantToken];

  _cardFlightAuthorized = YES;

  [self sendSuccessMessage:@"CardFlight authorized on this device"];
}

- (void)initializeReader:(CDVInvokedUrlCommand*)command {
  [self setCurrentCallbackID:[command callbackId]];

  _reader = [[CFTReader alloc] initWithReader:0];
  [_reader setDelegate:self];

  [self sendSuccessMessage:@"CardFlight reader ready"];
}

- (void)watchForSwipe:(CDVInvokedUrlCommand*)command {
  [self setCurrentCallbackID:[command callbackId]];

  [_reader beginSwipe];
  [self sendSuccessMessage:@"CardFlight reader awaiting swipe"];
}

- (void)readerCardResponse:(CFTCard *)card withError:(NSError *)error {
  if (error) {
    [self sendErrorMessage:[error localizedDescription] withCallbackID:self.cardReadCallbackID];
  } else {
    _card = card;
    [self sendSuccessMessage:@"Card read successfully" withCallbackID:self.cardReadCallbackID];

    // NSDictionary* dict = @{
    //   @"message": @"Card read successfully",
    //   @"cardName": [[NSString alloc] initWithString:card.name],
    //   @"cardNumber": [[NSString alloc] initWithString:card.encryptedSwipedCardNumber],
    //   @"cardExpirationMonth": [[NSString alloc] initWithString:card.expirationMonth],
    //   @"cardExpirationYear": [[NSString alloc] initWithString:card.expirationYear]
    // };

    // NSError *error; 
    // NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict 
    //                                         options:0
    //                                         error:&error];

    // if (!jsonData) {
    //   if (error) {
    //     [self sendErrorMessage:[error localizedDescription] withCallbackID:cardReadCallbackID];
    //   } else {
    //     [self sendErrorMessage:@"No json data found" withCallbackID:cardReadCallbackID];
    //   }
    // } else {
    //   NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    //   NSLog(@"Returning json: %@", jsonString);
    //   [self sendSuccessMessage:jsonString withCallbackID:self.cardReadCallbackID];
    // }
  }
}

- (void)tokenizeLastSwipe:(CDVInvokedUrlCommand*)command {
  NSString* tokenizeCallbackID = [command callbackId];

  if (!_card) {
    return [self sendErrorMessage:@"No card to tokenize" withCallbackID:tokenizeCallbackID];
  }

  [_card tokenizeCardWithSuccess:^{
      // NSDictionary* dict = @{
      //   @"message": @"Card tokenized with Stripe successfully",
      //   @"cardName": [[NSString alloc] initWithString:_card.name],
      //   @"cardNumber": [[NSString alloc] initWithString:_card.encryptedSwipedCardNumber],
      //   @"cardExpirationMonth": [[NSString alloc] initWithString:_card.expirationMonth],
      //   @"cardExpirationYear": [[NSString alloc] initWithString:_card.expirationYear],
      //   @"token": [[NSString alloc] initWithString: _card.cardToken]
      //   // @"cardName": _card.name,
      //   // @"cardNumber": _card.encryptedSwipedCardNumber,
      //   // @"cardExpirationMonth": _card.expirationMonth,
      //   // @"cardExpirationYear": _card.expirationYear,
      //   // @"token": _card.cardToken
      // };
      // [self sendSuccessDictionary:dict withCallbackID:tokenizeCallbackID];
      [self sendSuccessMessage:_card.cardToken withCallbackID:tokenizeCallbackID];
    }
    failure:^(NSError *error){
      [self sendErrorMessage:[error localizedDescription] withCallbackID:tokenizeCallbackID];
    }];
}

- (void)sendSuccessDictionary:(NSDictionary *)responseBody {
  CDVPluginResult* goingToCauseError = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:responseBody];
  CDVPluginResult* safe = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[goingToCauseError argumentsAsJSON]];
  [self.commandDelegate sendPluginResult:safe callbackId:self.currentCallbackID];
}

- (void)sendSuccessDictionary:(NSDictionary *)responseBody withCallbackID:(NSString *)callbackID {
  CDVPluginResult* goingToCauseError = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:responseBody];
  CDVPluginResult* safe = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:[goingToCauseError argumentsAsJSON]];
  [self.commandDelegate sendPluginResult:safe callbackId:callbackID];
}

- (void)sendSuccessMessage:(NSString *)responseString {
  CDVPluginResult* successResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:responseString];
  [self.commandDelegate sendPluginResult:successResult callbackId:self.currentCallbackID];
}

- (void)sendSuccessMessage:(NSString *)responseString withCallbackID:(NSString *)callbackID {
  CDVPluginResult* successResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:responseString];
  [self.commandDelegate sendPluginResult:successResult callbackId:callbackID];
}

- (void)sendErrorMessage:(NSString *)errorMessage {
  CDVPluginResult* errorResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];
  [self.commandDelegate sendPluginResult:errorResult callbackId:self.currentCallbackID];
}

- (void)sendErrorMessage:(NSString *)errorMessage withCallbackID:(NSString *)callbackID {
  CDVPluginResult* errorResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];
  [self.commandDelegate sendPluginResult:errorResult callbackId:callbackID];
}

@end