## MixDrinks app

### Build:

#### Android

- installDebug

#### IOS

##### Simulator ios iPhone

`./gradlew iosDeployIPhone13ProDebug`

##### Simulator ios IPad

`./gradlew iosDeployIPadDebug`

##### Ios real device:

- Add team id to local.properties

```properties
compose.ios.teamId=*****
```

- Attach device to your mac computer
  Run: `./gradlew iosDeployDeviceRelease`
