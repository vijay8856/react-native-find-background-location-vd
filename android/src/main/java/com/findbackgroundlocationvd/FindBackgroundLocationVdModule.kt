package com.findbackgroundlocationvd

import com.facebook.react.bridge.ReactApplicationContext

class FindBackgroundLocationVdModule(reactContext: ReactApplicationContext) :
  NativeFindBackgroundLocationVdSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeFindBackgroundLocationVdSpec.NAME
  }
}
