## MixDrinks app

Зараз додаток не є prod ready, але є відкритий бета тест в google play.

В додатка є відритий beta тест в google play. (В нього попадаються нічні зробки)

- [Android](https://play.google.com/store/apps/details?id=org.mixdrinks.app)
- [Web](https://play.google.com/apps/testing/org.mixdrinks.app)

### Build:

#### Android

`./gradlew android:installDebug`

Додаток для debug має applicationIdSuffix `.debug`,
тому він може бути встановлений на телефон разом з релізним додатком.

Для зміни version name та version code використовуйте наступні environment variables:

- `MIXDRINKS_MOBILE_APP_VERSION_NAME` - version name, default `0.0.1`
- `MIXDRINKS_MOBILE_APP_VERSION_CODE` - version code, default `1`

#### IOS

##### Simulator ios iPhone

`./gradlew iosDeployIPhone13ProDebug`

##### Simulator ios IPad

`./gradlew iosDeployIPadDebug`

Якщо вам треба додати інші симуляти ви можете просто додати їх в `iosCompose/build.gradle.kts`

```kotlin
compose.experimental {
  web.application {}
  uikit.application {
    bundleIdPrefix = "org.mixdrinks"
    projectName = "MixDrinks"
    deployConfigurations {
      // <--- Add new your simulator here
      simulator("IPhone13Pro") {
        //Usage: ./gradlew iosDeployIPhone13ProDebug
        device = IOSDevices.IPHONE_13_PRO
      }
      simulator("IPad") {
        //Usage: ./gradlew iosDeployIPadDebug
        device = IOSDevices.IPAD_MINI_6th_Gen
      }
      connectedDevice("Device") {
        //Usage: ./gradlew iosDeployDeviceRelease
      }
    }
  }
}
```
Ми зрадістю приймемо подібні PR

##### Запуск на реальному ios пристрої:

- Додайте team id в `local.properties`

```properties
compose.ios.teamId=*****
```

Далі підєнтай iphone до комп'ютера і запустіть

`./gradlew iosDeployDeviceRelease`
