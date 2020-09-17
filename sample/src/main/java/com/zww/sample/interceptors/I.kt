package com.zww.sample.interceptors

import okhttp3.internal.platform.Platform.Companion.INFO
import java.util.logging.Level
import java.util.logging.Logger

/**
 * @author ihsan on 10/02/2017.
 */
internal open class I protected constructor() {
    companion object {
        private val prefix = arrayOf(". ", " .")
        private var index = 0
        fun log(type: Int, tag: String, msg: String?, isLogHackEnable: Boolean) {
            val finalTag = getFinalTag(tag, isLogHackEnable)
            val logger = Logger.getLogger(if (isLogHackEnable) finalTag else tag)
            when (type) {
                INFO -> logger.log(Level.INFO, msg)
                else -> logger.log(Level.WARNING, msg)
            }
        }

        private fun getFinalTag(tag: String, isLogHackEnable: Boolean): String {
            return if (isLogHackEnable) {
                index = index xor 1
                prefix[index] + tag
            } else {
                tag
            }
        }
    }

    init {
        throw UnsupportedOperationException()
    }
}