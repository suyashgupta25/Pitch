package com.axioo.feed.data.local

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

internal object UserPrefsKeys {
    val UserType: Preferences.Key<String> = stringPreferencesKey("user_type")
    val LikedIds: Preferences.Key<Set<String>> = stringSetPreferencesKey("liked_ids")
    val SavedIds: Preferences.Key<Set<String>> = stringSetPreferencesKey("saved_ids")
}
