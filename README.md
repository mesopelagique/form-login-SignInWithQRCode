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

Currently it is not possible to select it in projet editor but we could add it manually

* To use a login form template, the first thing you'll need to do is create a YourDatabase.4dbase/Resources/Mobile/form/login folder.
* Then drop the login form folder into it.
* For this template, add `"login":"/signinwithqrcode"` in the file project.4dmobileapp

### present a qr code and where

First on your website the user must be authenticated by any means.

Then if the user want to login on mobile app, you need to provide a QR code with login informations.

You could display it for instance in profile page of current user, with an action button.
(never display it automacally for security reason)

A Javascript code could easily make an http request to get login information as string from 4d server and display the qr code in a popup.
- http://davidshimjs.github.io/qrcodejs/
- ...

![qr code example](https://www.unitag.io/qreator/generate?crs=Ppv8rOENN3V1lAwTz82zPgJCeRt23RLSE9SsU4N%252BrxqvqbQ3Jcp2mx6vf2r8SwBqCYZyrw7ynPLb6pN5DHtnvIBwX3cI%252BhHo3%252FTdpnq00x5Dz5WJXFFgLYGRIHPG%252F2An4XAjrnUB%252BQsHns11WbwlRmKBH9rGtUpQCNSetpgeAsGQAsuSGeae90AkOK7cy%252BeqEiQDCtXe%252BnPw%252Bom9YrArrSrYHFOBR6dnqLlX2D%252F%252B%252F%252BGTS5MR2bzdSKSU%252BvFuvb%252BZIWcktuakh%252BEbxHZZUdbv%252B7OMtENATOYD%252FLkgIoRo8UVZd1ATjTUw3K%252BJNEzliqp%252B6svzuay61hqYCRtF2IHtXxXo6BiGxS5TJiwYmmDDdV4i16ByM36tV9Hpe632DEB5&crd=fhOysE0g3Bah%252BuqXA7NPQ87MoHrnzb%252BauJLKoOEbJspajGEnTlletkbUyxS1k6L1ebHzI74Rw1EZu%252BXoQ7bTQg%253D%253D)

#### generate the qr code data at server side with login informations

You need to encode in your QR code some data in JSON string format.

We need the current user email and some data, we could call it token.

To response to an HTTP request (4DAction?) you could send this information:

```4d
... // compute $token and get user email according to its session

$qrCodeData:=New object("email"; $currentUserEmail; "token"; $token)
WEB SEND TEXT(JSON Stringify($qrCodeData))
```

This token could contains an expiration date, uuid, random data, some user data, etc...
and be preferably encrypted (using `ENCRYPT BLOB` or `Crypto` class)

You could store it in memory (`Storage`?) or database to be able to check it in next step.

### manage authentication at 4d server side

The user will scan the QR code from mobile app and the login process begin at server side in `On Mobile App Authentication`.

In this database method you need to check the data received.

```4d
$email:=$1.email
$token:=$1.userInfo.token // all json informations except email will be in userInfo
```

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
[check-shield]: https://github.com/mesopelagique/form-login-SignInWithQRCode/workflows/check/badge.svg
[check-url]: https://github.com/mesopelagique/form-login-SignInWithQRCode/actions?workflow=check
