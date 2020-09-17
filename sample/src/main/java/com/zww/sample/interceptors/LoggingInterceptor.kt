package com.zww.sample.interceptors

import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.platform.Platform.Companion.INFO
import okio.Buffer
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


/**
 * @author ihsan on 09/02/2017.
 */
class LoggingInterceptor private constructor(private val builder: Builder) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        //将请求体中的数据进行封装并返回新的request
//        val topRequest = formartParams(chain.request())

        val request = addQueryAndHeaders(chain.request())

        if (builder.level == Level.NONE) {
            return chain.proceed(request)
        }

        printlnRequestLog(request)

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = proceedResponse(chain, request)
        } catch (e: Exception) {
            Printer.printFailed(builder.getTag(false), builder)
            throw e
        }
        val receivedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        printlnResponseLog(receivedMs, response, request)
        return response
    }

    private fun formartParams(request: Request): Request? {
        val requestBody = request.body
        val buffer = Buffer()
        try {
            requestBody?.writeTo(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
        //编码设为UTF-8
        var charset: Charset = Charset.forName("UTF-8")
        val contentType = requestBody?.contentType()
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"))!!
        }
        //拿到request
        val requestString: String = buffer.readString(charset)
        if (!requestString.contains("requestData")) {
            val formBody = FormBody.Builder(charset = charset)
            val requestOtherMap = hashMapOf<String, Any>()
//        val sb = StringBuffer()
            if (requestString.isNotEmpty()) {
                requestString.split("&").forEach {
                    //{"requestSystem":"user","requestUri":"/userapi/securityCode","requestData":{"mobile":"15317026229"}}}}
                    val key = it.split("=")[0]
                    val value = it.split("=")[1]
                    if (key == "requestSystem") {
                        formBody.add(key, value)
                    } else if (key == "requestUri") {
                        formBody.add(key, URLDecoder.decode(value, "UTF-8"))
                    } else {
                        requestOtherMap.put(key, value)
                    }
                }
            }
            if (requestOtherMap.isNotEmpty()) {
                formBody.add("requestData", URLDecoder.decode(Gson().toJson(requestOtherMap)))
            }
            val requestBuilder = request.newBuilder()

            requestBuilder.method(
                request.method,
                formBody.build()
            )
            return requestBuilder.build()
        } else return request

    }


    private fun printlnResponseLog(receivedMs: Long, response: Response, request: Request) {
        Printer.printJsonResponse(
            builder,
            receivedMs,
            response.isSuccessful,
            response.code,
            response.headers,
            response,
            request.url.encodedPathSegments,
            response.message,
            request.url.toString()
        )
    }

    private fun printlnRequestLog(request: Request) {
        Printer.printJsonRequest(
            builder,
            request.body,
            request.url.toUrl().toString(),
            request.headers,
            request.method
        )
    }

    private fun proceedResponse(chain: Interceptor.Chain, request: Request): Response {
        return if (builder.isMockEnabled && builder.listener != null) {
            TimeUnit.MILLISECONDS.sleep(builder.sleepMs)
            Response.Builder()
                .body(
                    builder.listener!!.getJsonResponse(request)
                        ?.toResponseBody("application/json".toMediaTypeOrNull())
                )
                .request(chain.request())
                .protocol(Protocol.HTTP_2)
                .message("Mock data from LoggingInterceptor")
                .code(200)
                .build()
        } else chain.proceed(request)
    }

    private fun addQueryAndHeaders(request: Request): Request {
        val requestBuilder = request.newBuilder()
        builder.headers.keys.forEach { key ->
            builder.headers[key]?.let {
                requestBuilder.addHeader(key, it)
            }
        }
        val httpUrlBuilder: HttpUrl.Builder? = request.url.newBuilder(request.url.toString())
        httpUrlBuilder?.let {
            builder.httpUrl.keys.forEach { key ->
                httpUrlBuilder.addQueryParameter(key, builder.httpUrl[key])
            }
        }
        return requestBuilder.url(httpUrlBuilder?.build()!!).build()
    }

    @Suppress("unused")
    class Builder {
        val headers: HashMap<String, String> = HashMap()
        val httpUrl: HashMap<String, String> = HashMap()
        var isLogHackEnable = false
            private set
        var isDebugAble = false
        var type: Int = INFO
            private set
        private var requestTag: String? = null
        private var responseTag: String? = null
        var level = Level.BASIC
            private set
        var logger: Logger? = null
            private set
        var isMockEnabled = false
        var sleepMs: Long = 0
        var listener: BufferListener? = null

        /**
         * @param level set log level
         * @return Builder
         * @see Level
         */
        fun setLevel(level: Level): Builder {
            this.level = level
            return this
        }

        fun getTag(isRequest: Boolean): String {
            return when (isRequest) {
                true -> if (requestTag.isNullOrEmpty()) TAG else requestTag!!
                false -> if (responseTag.isNullOrEmpty()) TAG else responseTag!!
            }
        }

        /**
         * @param name  Filed
         * @param value Value
         * @return Builder
         * Add a field with the specified value
         */
        fun addHeader(name: String, value: String): Builder {
            headers[name] = value
            return this
        }

        /**
         * @param name  Filed
         * @param value Value
         * @return Builder
         * Add a field with the specified value
         */
        fun addQueryParam(name: String, value: String): Builder {
            httpUrl[name] = value
            return this
        }

        /**
         * Set request and response each log tag
         *
         * @param tag general log tag
         * @return Builder
         */
        fun tag(tag: String): Builder {
            TAG = tag
            return this
        }

        /**
         * Set request log tag
         *
         * @param tag request log tag
         * @return Builder
         */
        fun request(tag: String?): Builder {
            requestTag = tag
            return this
        }

        /**
         * Set response log tag
         *
         * @param tag response log tag
         * @return Builder
         */
        fun response(tag: String?): Builder {
            responseTag = tag
            return this
        }

        /**
         * @param isDebug set can sending log output
         * @return Builder
         */
        @Deprecated(
            message = "Set level based on your requirement",
            replaceWith = ReplaceWith(expression = "setLevel(Level.Basic)"),
            level = DeprecationLevel.ERROR
        )
        fun loggable(isDebug: Boolean): Builder {
            this.isDebugAble = isDebug
            return this
        }

        /**
         * @param type set sending log output type
         * @return Builder
         * @see okhttp3.internal.platform.Platform
         */
        fun log(type: Int): Builder {
            this.type = type
            return this
        }

        /**
         * @param logger manuel logging interface
         * @return Builder
         * @see Logger
         */
        fun logger(logger: Logger?): Builder {
            this.logger = logger
            return this
        }

        /**
         * @param executor manual executor for printing
         * @return Builder
         * @see Logger
         */
        @Deprecated(
            message = "Create your own Logcat filter for best result",
            level = DeprecationLevel.ERROR
        )
        fun executor(executor: Executor?): Builder {
            TODO("Deprecated")
        }

        /**
         * @param useMock let you use json file from asset
         * @param sleep   let you see progress dialog when you request
         * @return Builder
         * @see LoggingInterceptor
         */
        fun enableMock(useMock: Boolean, sleep: Long, listener: BufferListener?): Builder {
            isMockEnabled = useMock
            sleepMs = sleep
            this.listener = listener
            return this
        }

        /**
         * Call this if you want to have formatted pretty output in Android Studio logCat.
         * By default this 'hack' is not applied.
         *
         * @param useHack setup builder to use hack for Android Studio v3+ in order to have nice
         * output as it was in previous A.S. versions.
         * @return Builder
         * @see Logger
         */
        @Deprecated(
            message = "Android studio has resolved problem for latest versions",
            level = DeprecationLevel.WARNING
        )
        fun enableAndroidStudioV3LogsHack(useHack: Boolean): Builder {
            isLogHackEnable = useHack
            return this
        }

        fun build(): LoggingInterceptor {
            return LoggingInterceptor(this)
        }

        companion object {
            private var TAG = "LoggingI"
        }
    }
}