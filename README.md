## MixDrinks app

The app is available on **Google Play** and **App Store**:

- [Android](https://play.google.com/store/apps/details?id=org.mixdrinks.app)
- [App Store](https://apps.apple.com/app/id6447103081)
- [Web](https://mixdrinks.org/)

## Before start any works on project

> **Warning**
> Writing and running iOS-specific code for a simulated or real device requires macOS. This is an
> Apple limitation.


Check your environment has all requirements for Kotlin Multiplatform Mobile Development:

- The computer must be running latest macOS.
- [Xcode](https://developer.apple.com/xcode/)
- [Android Studio](https://developer.android.com/studio)
- [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
- [CocoaPods](https://kotlinlang.org/docs/native-cocoapods.html)

### The environment can be checked by `kdoctor`

**Before opening the project in Android Studio**, use [`kdoctor`](https://github.com/Kotlin/kdoctor)
to ensure your development environment is configured correctly. Install `kdoctor`
via [`brew`](https://brew.sh/):

```
brew install kdoctor
```

The kdoctor tool will check your environment and provide a list of errors if something goes wrong:

```
Environment diagnose (to see all details, use -v option):
[✓] Operation System
[✓] Java
[✓] Android Studio
[✓] Xcode
[✓] Cocoapods

Conclusion:
  ✓ Your system is ready for Kotlin Multiplatform Mobile Development!
```

#### Codding environment preparation

Check you have
installed [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)

## Project structure

To view the project structure use **Project view**.

The project has three modules:

### `shared`

The module contains code that will be shared across all platforms.

App `@Composable` фукція знаходиться в `shared/src/commonMain/kotlin/App.kt`.

### `androidApp`

The module contains code that will be used only on Android.

### `iosApp`

The module contains code that will be used only on iOS.
The module `iosApp` depends on the `shared` module as a CocoaPods dependency.

## Run the app/project

## Android

Chose the configuration `androidApp` -> `Run`

Or run by gradle command
`./gradlew installDebug`

## iOS

Before run the mixdrinks project for ios, we highly recommend to run the `Hello, World` Xcode
project. Just to be sure that your environment is ready for ios development.

### Run ios mixdrinks app on simulator

Chose the configuration `iosApp` -> `Run`

### Run ios mixdrinks app on real ios device

Before run the app on real ios device you need to prepare your environment:

- Create an [Apple ID](https://support.apple.com/en-us/HT204316)
- Register your iphone in Xcode

Change the `TEAM_ID` to your team id (can be found in apple.developer),
in `iosApp/Configuration/Config.xcconfig`

Alternatively way to get team id, use kdoctor command `kdoctor --team-ids`. The command will produce
the list of team ids from your system, choose the one you prefer.

### Contributing

We are happy to accept small and large contributions, you can just make changes and create a pull,
or you can check our [issue tab](https://github.com/MixDrinks/Mobile/issues) and choose the one you
like.

### Troubleshooting

If you have any problems with the project, please check the following list:

#### Most popular problems, you can run ios app.

Usually happens after you make change is some ios specific files,
like `iosApp/Configuration/Config.xcconfig` or `iosApp/Info.plist`

**Close Android Studio or you idea. Than run the `./cleanup.sh`.** Now you can open the project
again.

#### The resource files are not available in ios project

After you maker changes into resources into `shared` module, you need to run `pod install` in
the `iosApp` folder.
