# modernbanc-android

Modernbanc Android is an official Android library you can use to securely collect sensitive data from your users and store it in Modernbanc's encrypted vault.

## Installation

You can install this library via Jitpack.

### Requirements

- Android 7.0+ (API level 24+)
- AndroidX

### Gradle

To install `modernbanc-android` add the following to your build.gradle;

```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Modernbanc:modernbanc-android:<tag>'
}
```

## Usage

This library contains a ModernbancInput component that you can embed into your app. It is a native component that aims to mimic EditText so you can style and customize it to your liking.

To use it initialize a Modernbanc API client and then pass it to the ModernbancInput.

```kotlin
val apiClient = ModernbancApiClient(apiKey)
val modernbancInput = ModernbancInput(context = requireContext(), client = apiClient)
```

Once the user has entered the details you can create a secret from the value in the input.

```kotlin
modernbancInput?.createSecret(
  onResponse = { response: CreateSecretResponse? ->
      // Handle the secret response here
      val secret = response?.result?.firstOrNull()
      Log.d("CreateSecret", "Secret created: ${secret?.id}")
      activity?.runOnUiThread {
          secretLabel.text = "Created secret with id ${secret?.id}"
      }
  },
  onFailure = { error: MdbApiError? ->
      // Handle the error here
      Log.e("CreateSecret", "Error: ${error?.code} - ${error?.message}")
      activity?.runOnUiThread {
          secretLabel.text = "Oops there was an error ${error.toString()}"
      }
  }
)
```

### Validation

We prevent you from accessing input's raw text but if you want to validate the input you set your validation function in the following way:

```kotlin
val isLongerThan5Characters: (String) -> Boolean = { it.length > 5 }
input.validationFn = isLongerThan5Characters

input.setText("Hello")

Log.d("Value is currently valid: ", input.isValid.toString()) // Should print `false`
```

### Demo app

The project also contains a demo-app, to run it ensure that you create an substitute an API key with **write** permission for **secret** functionlaity.
