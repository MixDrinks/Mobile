//
//  MainViewModel.swift
//  iosApp
//
//  Created by Vova Stelmashchuk on 25.06.2023.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared
import GoogleSignIn
import Firebase
import CryptoKit
import AuthenticationServices


// Unhashed nonce.
fileprivate var currentNonce: String?

class MainViewModel: NSObject, ObservableObject, ASAuthorizationControllerDelegate, ASAuthorizationControllerPresentationContextProviding {

    override init() {
        super.init()
        Main_iosKt.setGoogleAuthStart {
            self.googleSignIn()
        }
        Main_iosKt.setAppleAuthStart {
            self.startSignInWithAppleFlow()
        }

        Main_iosKt.setLogout {
            let firebaseAuth = Auth.auth()
            do {
              try firebaseAuth.signOut()
            } catch let signOutError as NSError {
              print("Error signing out: %@", signOutError)
            }
        }
    }

    func presentationAnchor(for controller: ASAuthorizationController) -> ASPresentationAnchor {
        // Find the window associated with the currently active scene
        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene,
            let window = windowScene.windows.first else {
                fatalError("Unable to find a valid window to display the authorization UI.")
        }

        return window
    }

    @available(iOS 13, *)
    func startSignInWithAppleFlow() {
      let nonce = randomNonceString()
      currentNonce = nonce
      let appleIDProvider = ASAuthorizationAppleIDProvider()
      let request = appleIDProvider.createRequest()
      request.requestedScopes = [.fullName, .email]
      request.nonce = sha256(nonce)

      let authorizationController = ASAuthorizationController(authorizationRequests: [request])
      authorizationController.delegate = self
      authorizationController.presentationContextProvider = self
      authorizationController.performRequests()
    }


    private func randomNonceString(length: Int = 32) -> String {
      precondition(length > 0)
      var randomBytes = [UInt8](repeating: 0, count: length)
      let errorCode = SecRandomCopyBytes(kSecRandomDefault, randomBytes.count, &randomBytes)
      if errorCode != errSecSuccess {
        fatalError(
          "Unable to generate nonce. SecRandomCopyBytes failed with OSStatus \(errorCode)"
        )
      }

      let charset: [Character] =
        Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")

      let nonce = randomBytes.map { byte in
        // Pick a random character from the set, wrapping around if needed.
        charset[Int(byte) % charset.count]
      }

      return String(nonce)
    }

    @available(iOS 13, *)
    private func sha256(_ input: String) -> String {
      let inputData = Data(input.utf8)
      let hashedData = SHA256.hash(data: inputData)
      let hashString = hashedData.compactMap {
        String(format: "%02x", $0)
      }.joined()

      return hashString
    }

    func googleSignIn() {
      if GIDSignIn.sharedInstance.hasPreviousSignIn() {
        GIDSignIn.sharedInstance.restorePreviousSignIn { [unowned self] user, error in
            authenticateUser(for: user, with: error)
        }
      } else {
        guard let clientID = FirebaseApp.app()?.options.clientID else { return }

        let configuration = GIDConfiguration(clientID: clientID)

        guard let windowScene = UIApplication.shared.connectedScenes.first as? UIWindowScene else { return }
        guard let rootViewController = windowScene.windows.first?.rootViewController else { return }

        GIDSignIn.sharedInstance.signIn(with: configuration, presenting: rootViewController) { [unowned self] user, error in
          authenticateUser(for: user, with: error)
        }
      }
    }

    private func authenticateUser(for user: GIDGoogleUser?, with error: Error?) {
      if let error = error {
        print(error.localizedDescription)
        return
      }

      guard let authentication = user?.authentication, let idToken = authentication.idToken else { return }

      let credential = GoogleAuthProvider.credential(withIDToken: idToken, accessToken: authentication.accessToken)

      Auth.auth().signIn(with: credential) { (authResult, error) in
        if let error = error {
          print(error.localizedDescription)
        } else {
            if let user = authResult?.user {
                    user.getIDToken(completion: { (token, error) in
                                    if let error = error {
                                        // Handle the token retrieval error
                                        print("Error retrieving token: \(error.localizedDescription)")
                                    } else {
                                        print("token \(token ?? "")")

                                        if let unwrappedToken = token {
                                            Main_iosKt.NewToken(token: unwrappedToken)
                                        }
                                    }
                                })
                }
        }
      }
    }

  func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
      print("Sign in with Apple start success")
    if let appleIDCredential = authorization.credential as? ASAuthorizationAppleIDCredential {
      guard let nonce = currentNonce else {
        fatalError("Invalid state: A login callback was received, but no login request was sent.")
      }
      guard let appleIDToken = appleIDCredential.identityToken else {
        print("Unable to fetch identity token")
        return
      }
      guard let idTokenString = String(data: appleIDToken, encoding: .utf8) else {
        print("Unable to serialize token string from data: \(appleIDToken.debugDescription)")
        return
      }
      // Initialize a Firebase credential, including the user's full name.
      let credential = OAuthProvider.appleCredential(withIDToken: idTokenString,
                                                        rawNonce: nonce,
                                                        fullName: appleIDCredential.fullName)
      // Sign in with Firebase.
      Auth.auth().signIn(with: credential) { (authResult, error) in
        if error != nil {
          // Error. If error.code == .MissingOrInvalidNonce, make sure
          // you're sending the SHA256-hashed nonce as a hex string with
          // your request to Apple.

            if let safeError = error {
                // The error is not nil, it has been successfully unwrapped
                let description = safeError.localizedDescription
                // Use the unwrapped value here
                print("111111_here the erorr with descrpition")
                print(description)
            } else {
                print("error is nill")
            }


          return
        }
          print("success")
          if let user = authResult?.user {
                  user.getIDToken(completion: { (token, error) in
                                  if let error = error {
                                      // Handle the token retrieval error
                                      print("Error retrieving token: \(error.localizedDescription)")
                                  } else {
                                      print("token \(token ?? "")")

                                      if let unwrappedToken = token {
                                          Main_iosKt.NewToken(token: unwrappedToken)
                                      }
                                  }
                              })
              }
        // User is signed in to Firebase with Apple.
        // ...
      }
    }
  }

  func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
    // Handle error.
    print("Sign in with Apple errored: \(error)")
  }

}
