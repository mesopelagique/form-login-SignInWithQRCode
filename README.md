# Sign In with QR Code

[![Language][swift-shield]][swift-url]
[![check][check-shield]][check-url]

Let user scan a QR code to login to the mobile app.

* **Type:** login

## Requirements

* A website with authenticated user by email.
* 4D 18R6 >= : bar code scanner are available only from this version
* Real iOS mobile device (simulator do not simulate camera)

## How to

### add this template to your project

* To use a login form template, the first thing you'll need to do is create a YourDatabase.4dbase/Resources/Mobile/form/login folder.
* Then drop the login form folder into it.
* For this template, add `"login":"/signinwithqrcode"` in the file project.4dmobileapp

### present a qr code and where

First on your website the user must be authenticated by any means.

Then if the user want to login on mobile app, you need to provide a QR code with login informations.

You could display it for instance in profile page of current user, with an action button.
(never display it automacally for security reason)

> A Javascript code could easily make an http request to get login information as string from 4d server and display the qr code in a popup.

#### generate the qr code data with login informations

You need to encode in your QR code some data in JSON string format.

We need the current user email and some data, we could call it token.

```4d
$qrCodeData:=JSON Stringify(New object("email"; $currentUserEmail; "token"; $token))
```

This token could contains an expiration date, uuid, random data, some user data, etc...
and be preferably encrypted (using `ENCRYPT BLOB` or `Crypto` class)

You could store it in memory (`Storage`?) or database to be able to check it in next step.

### manage authentication at 4d server side

The user will scan the QR code from mobile app and the login process begin at server side in `On Mobile App Authentication`.

In this database method you need to check the data received.

First we could check if the email is correct (but not mandatory is token is sufficient), maybe get expected token for this user.

Then if the passed data, for instance the `token`, are valid:

* check if in memory or database
* maybe decrypt it
* check expiration date

And according to that valid or not the mobile user authentication by returning `True` or `False`

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[swift-shield]: http://img.shields.io/badge/language-swift-orange.svg?style=flat
[swift-url]: https://developer.apple.com/swift/
[check-shield]: https://github.com/4d-for-ios/4d-for-ios-form-login-SignInWithQRCode/workflows/check/badge.svg
[check-url]: https://github.com/4d-for-ios/4d-for-ios-form-login-SignInWithQRCode/actions?workflow=check
