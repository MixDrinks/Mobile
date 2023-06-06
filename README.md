## MixDrinks app

Даний додаток ще не є готовим до продакшну, але доступний для відкритого бета-тестування в Google
Play.

В додатка є відритий beta тест в google play. (В нього попадаються нічні зробки)

- [Android](https://play.google.com/store/apps/details?id=org.mixdrinks.app)
- [App Store](https://apps.apple.com/app/id6447103081)
- [Web](https://play.google.com/apps/testing/org.mixdrinks.app)

## Перед роботою з проектом

> **Warning**
> Writing and running iOS-specific code for a simulated or real device requires macOS. This is an
> Apple limitation.

Підготовка до роботи з проектом
Для роботи з проектом вам знадобляться:

- Комютер з актуальною версією macOs
- [Xcode](https://developer.apple.com/xcode/)
- [Android Studio](https://developer.android.com/studio)
- [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)
- [CocoaPods](https://kotlinlang.org/docs/native-cocoapods.html)

### Це все можна перевірити через `kdoctor`

**Before opening the project in Android Studio**, use [`kdoctor`](https://github.com/Kotlin/kdoctor)
to ensure your development environment is configured correctly. Install `kdoctor`
via [`brew`](https://brew.sh/):

```
brew install kdoctor
```

Якщо щось пішло не так, kdoctor надасть вам список помилок:

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

Переконайтеся, що встановлений
плагін [Kotlin Multiplatform Mobile](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile)

## Струкутура проекту

Для перегляду структури проекту використовуйте Project view.

В проекті є три модулі:

### `shared`

Цей модуль містить код, який буде спільним для всіх платформ.

App `@Composable` фукція знаходиться в `shared/src/commonMain/kotlin/App.kt`.

### `androidApp`

Цей модуль містить код, який буде використовуватись тільки в Android.

### `iosApp`

Цей модуль містить код, який буде використовуватись тільки в iOS.
Модуль `iosApp` залежить від `shared` модуля як від CocoaPods залежності.

## Запуск проекту

## Android

Виберіть конфігурацію `androidApp` -> `Run`

Gradle
`./gradlew installDebug`

## iOS

### Запуск на iOS simulator

Виберіть конфігурацію `iosApp` -> `Run`

### Запуск на реальному iOS пристрої

Підготовка до запуску на фізичному пристрої

- [Apple ID](https://support.apple.com/en-us/HT204316)
- Зареєструй пристрій в Xcode

Перед тим як запускати цей проект переконайтеся що можете запустити "Hello, World" додаток що
створений Xcode на вашому фізичному пристрої.

Далі додайте значення для поля `TEAM_ID` в `iosApp/Configuration/Config.xcconfig`

Список team id що є в системі можна отримати за допомогою команди `kdoctor --team-ids`

### Contributing

Автор проекту буде радий будь-якій допомозі.

Нотатки про для розробників що будуть робити зміни в проекті:

#### Config.xcconfig

- Якщо ваші зміни в код відбуваються в файлі `iosApp/Configuration/Config.xcconfig` то закрийте
  Android studio та зробіть зміни в іншому редакторі, далі запустіть `./cleanup.sh` після чого
  проект
  знову можна відкривати в Android Studio.

```shell
./cleanup.sh
```

#### Робота з ресурсами

Після змін в ресурсах необхідно в `iosApp` запустити `pod install` щоб згенерувати нові файли.

```shell
pod install
```
