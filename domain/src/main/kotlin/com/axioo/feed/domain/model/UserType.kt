package com.axioo.feed.domain.model

enum class UserType {
    Founder,
    Investor,
    ;

    companion object {
        fun fromStorage(value: String?): UserType =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: Investor
    }
}
