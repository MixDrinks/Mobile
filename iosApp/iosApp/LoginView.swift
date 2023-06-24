import SwiftUI
import shared

struct LoginView: View {
    
    init() {
        Main_iosKt.doNewTokenProvide()
    }

  @EnvironmentObject var viewModel: AuthenticationViewModel

  var body: some View {
    VStack {
      Spacer()

      Text("Автризуйся в MixDrinks")
        .fontWeight(.black)
        .foregroundColor(Color(.systemIndigo))
        .font(.largeTitle)
        .multilineTextAlignment(.center)

      Text("Ще більше фіч з MixDrinks, облюблені коктейлі, твій бар, автоматична синхронізація між всіма пристроями")
        .fontWeight(.light)
        .multilineTextAlignment(.center)
        .padding()

      Spacer()

      GoogleSignInButton()
        .padding()
        .onTapGesture {
          viewModel.signIn()
        }
    }
  }
}


/*
            HStack {
                Spacer()
                Button(action: {
                    
                    Auth.auth().signIn(withEmail: "test@plusmobileapps.com", password: "Password123!") { (authResult, error) in
                            if let error = error {
                                // Handle the sign-in error
                                print("Sign-in failed: \(error.localizedDescription)")
                            } else {
                                // User successfully signed in
                                print("User signed in successfully.")
                                if let user = authResult?.user {
                                                // Print user details
                                                print("User ID: \(user.uid)")
                                                print("Display Name: \(user.displayName ?? "")")
                                                print("Email: \(user.email ?? "")")
                                                // You can access other user properties as well
                                                
                                                // Perform any additional actions or navigate to another view
                                            }
                                // Perform any additional actions or navigate to another view
                            }
                        }
                }){
                    Text("Create")
                    .bold()
                    .font(Font.custom("Helvetica Neue", size: 24.0))
                    .padding(20)
                    .foregroundColor(Color.white)
                    .background(Color.purple)
                    .cornerRadius(12)
                }
                Spacer()
            }
        }.padding(20) */
