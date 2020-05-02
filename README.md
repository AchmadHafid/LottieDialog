LottieDialog
============

[![Release](https://jitpack.io/v/AchmadHafid/LottieDialog.svg)](https://jitpack.io/#AchmadHafid/LottieDialog)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)

**Assalamu'alaikum brothers and sisters, peace be upon you!**

Display a beautiful confirmation dialog with Lottie animation inside easily using new material design components.

![image](https://drive.google.com/uc?export=download&id=1S3JFiB7ubROiWDSOY0ofz-BVjIEtU19a)
![image](https://drive.google.com/uc?export=download&id=1_S4Au5aF6DbOFQdX0L-nWHr2xK3wcqj_)
<br />
![image](https://drive.google.com/uc?export=download&id=14W02bKMa1FQRuF_sSjqln4d81t8NTTdr)
![image](https://drive.google.com/uc?export=download&id=1l8SGbwPEl1y31L84i-L_qD_2Hi_ZVr7e)
<br />
![image](https://drive.google.com/uc?export=download&id=1walULd_3oAcTFsnsVSrPoZWTe3_59cKp)
![image](https://drive.google.com/uc?export=download&id=1T93iGEfESTVg1SsmMYLDzQBkw3iItIrs)
<br />
<br />
[**Download Demo App Here**](https://github.com/AchmadHafid/LottieDialog/releases/download/v4.1.0/LottieDialog.4.1.0.apk)

Main Features
--------
* Create dialogs easily by using __*extension functions*__ available for Activity & Fragment
* Two Flavors: (conventional) __*alert dialog*__ and __*bottom sheet based dialog*__ (default)
* Three Types: __*confirmation*__ (yes-no dialog), __*input*__ & __*loading progress*__
* Full support of __*Material Components*__ theme (e.g. light theme & dark theme)
* __*Flexible*__ customization options from corner radius to callback delay
* __*Lifecycle aware, self contained reference*__. No need to declare a variable just to dismiss it later.
* __*Priority option*__ available to prevent multiple overlapping dialogs show up at the same time (e.g. when multiple errors occured at the same time)
* __*Builder*__ / base dialog template available for further code simplification


Compatibility
-------------

This library is compatible from API 21 (Android 5.0 Lollipop) & AndroidX.


Download
--------

Add jitpack repository into your root build.gradle

```groovy
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
    ...
  }
}
```

Add the dependency

```groovy
dependencies {
  ...
  implementation "com.github.AchmadHafid:LottieDialog:4.1.0"
  ...
}
```


Quick Usage
-------------

```kotlin
class MainActivity : AppCompatActivity(R.layout.activity_main) {
//class MainFragment : Fragment(R.layout.fragment_main) {

  fun showSomeDialog() {
    lottieConfirmationDialog(priority, baseBuilderIfAny) {
    //lottieInputDialog(priority, baseBuilderIfAny) {
    //lottieLoadingDialog(priority, baseBuilderIfAny) {

      // smaller priority (absolute) value -> take precedence
      // set it to 0 for the most important dialog (no other dialogs can be shown when this dialog is still displayed)

      // customize your dialog here using DSL
      // please use Android Studio auto complete feature to see all available API
      // just type "this." then Android Studio will show you the complete list of it

    }
  }

  fun dismissCurrentDialog() {
    dismissLottieDialog()
  }

}
```

<br />


__That's it! May this library ease your Android development task__


License
-------

    Copyright 2019 Achmad Hafid

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

