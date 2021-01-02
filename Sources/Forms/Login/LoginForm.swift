//
//  LoginForm.swift
//  ___PACKAGENAME___
//
//  Created by ___FULLUSERNAME___ on ___DATE___
//  ___COPYRIGHT___

import UIKit

import QMobileUI
import QMobileAPI

import SwiftyJSON
import SwiftMessages

@IBDesignable
open class LoginForm: QMobileUI.LoginForm {

    /// Get qr code scanner (children defined in storyboard)
    fileprivate var scanner: BarcodeScannerLoginViewController? {
        return children.compactMap({ $0 as? BarcodeScannerLoginViewController}).first
    }

    /// Fet json from qr code if any
    var qrCodeData: JSON? {
        guard let metadata = scanner?.metadata, !metadata.isEmpty else {
            return nil
        }
        let json = JSON(parseJSON: metadata)
        if json.isEmpty {
            return nil
        }
        return json
    }

     // MARK: overrides

    /// Get email if any from qr code
    open override var email: String {
        guard let qrCodeData = self.qrCodeData else {
            return ""
        }
        return qrCodeData["email"].stringValue
    }

    /// Get custom parameters from qr code
    open override var customParameters: [String: Any]? {
        guard let qrCodeData = self.qrCodeData else {
            return [:]
        }
        var dictionary = qrCodeData.dictionaryObject
        dictionary?.removeValue(forKey: "email")
        return dictionary
    }

    /// Override display message because no more email text field
    open override func displayInputError(message: String) {
        SwiftMessages.warning(message)
    }

    fileprivate var couldNotLogin = false
    open override func couldLogin() -> Bool {
        let result = super.couldLogin()
        couldNotLogin = !result
        if couldNotLogin {
            self.scanner?.captureFrameError()
        }
        return result
        //return true
    }

    open override func onWillLogin () {
        SwiftMessages.loading()
    }

    open override func onDidLogin(result: Result<AuthToken, APIError>) {
        SwiftMessages.hide()
        switch result {
        case .success:
            self.scanner?.endSession()
        case .failure:
            self.scanner?.beginSession() // restart scanner
        }
    }

    // MARK: Event
    /// Called after the view has been loaded. Default does nothing
    open override func onLoad() {
        guard let scanner = self.scanner else {
            return
        }
        scanner.hideInterface() // no need interface , we are inside other controller
        scanner.onDismissCallback = { _ in
            if self.couldNotLogin { // reactive session if we could not login due to wrong qr code
                scanner.beginSession()
            }
        }
    }
    /// Called when the view is about to made visible. Default does nothing
    open override func onWillAppear(_ animated: Bool) {
    }
    /// Called when the view has been fully transitioned onto the screen. Default does nothing
    open override func onDidAppear(_ animated: Bool) {
    }
    /// Called when the view is dismissed, covered or otherwise hidden. Default does nothing
    open override func onWillDisappear(_ animated: Bool) {
    }
    /// Called after the view was dismissed, covered or otherwise hidden. Default does nothing
    open override func onDidDisappear(_ animated: Bool) {
    }

}

public class BarcodeScannerLoginViewController: BarcodeScannerViewController {

    override open func onMetaDataOutput(_ metadata: String) -> Bool {
        _ = super.onMetaDataOutput(metadata)

        if let loginForm = self.parent as? LoginForm {
            loginForm.login(self)
        }
        return false // not consumed, need to reactive it
    }

}
