import UIKit
import SwiftUI
import shared
import GoogleSignIn
import Firebase
import FirebaseAuth

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        let controller = Main_iosKt.MainViewController()
        controller.navigationController?.interactivePopGestureRecognizer?.isEnabled = false
        return controller
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    
    @State private var showingLogin = true
    
    init() {
        
    }
    
    var body: some View {
        if (showingLogin) {
            LoginView()
        } else {
            ComposeView()
              .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }
    }
}
