import UIKit
import SwiftUI
import shared
import GoogleSignIn
import Firebase
import FirebaseAuth
import AuthenticationServices

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = Main_iosKt.MainViewController()
        controller.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    
    @StateObject private var viewModel = MainViewModel()
        
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.keyboard)
    }
}
