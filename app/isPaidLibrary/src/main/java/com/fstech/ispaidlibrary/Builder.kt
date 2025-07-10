package com.fstech.ispaidlibrary

/**
 * Builder interface defines all possible ways to configure a product.
 */
interface Builder {
    fun setMessage(message: String)
    fun setUrl(url: String)
}