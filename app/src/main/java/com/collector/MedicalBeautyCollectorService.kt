package com.collector

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class MedicalBeautyCollectorService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
