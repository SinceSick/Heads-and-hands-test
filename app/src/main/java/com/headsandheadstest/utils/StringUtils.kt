package com.headsandheadstest.utils

import android.util.Patterns

const val MIN_PASSWORD_LENGTH = 6

fun String.validateEmail(): Boolean {
    return this.isNotEmpty()
            && Patterns.EMAIL_ADDRESS.matcher(this.subSequence(0, this.length - 1)).matches()
}

fun String.validatePassword(): Boolean {
    return this.length >= MIN_PASSWORD_LENGTH
            && this.replace(Regex("[^0-9]"), "").isNotEmpty()
            && this.any { it.isUpperCase() }
            && this.any { it.isLowerCase() }
}