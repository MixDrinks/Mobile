var trackAnalyticsCallback: (action: String, data: Map<String, String>) -> Unit = { _, _ -> }
actual fun trackEvent(action: String, data: Map<String, String>) {
    trackAnalyticsCallback(action, data)
}
