package com.fstech.utils

/**
 * Builder interface defines all possible ways to configure isPaidClass.
 */
interface Builder {
    fun setMessage(message: String)
    fun setUrl(url: String)
}