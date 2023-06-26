import SwiftUI
import shared

struct LoginView: View {
    
    init() {
        Main_iosKt.Login(token: "some new token")
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
