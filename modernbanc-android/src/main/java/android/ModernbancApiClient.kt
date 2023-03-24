package android

import com.google.gson.Gson
import okhttp3.*
import okhttp3.Headers.Companion.toHeaders
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class ModernbancApiClient(private val apiKey: String) {
    private val baseUrl: String = "https://api.modernbanc.com/v1"
    private var client = OkHttpClient()
    private val gson = Gson()

    init {
        val interceptor = object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val original = chain.request()
                val requestBuilder = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("authorization", "ApiKey $apiKey")
                    .method(original.method, original.body)
                val request = requestBuilder.build()
                return chain.proceed(request)
            }
        }

        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    fun <T> apiCall(
        method: String,
        endpoint: String,
        queryParams: Map<String, String> = emptyMap(),
        headers: Map<String, String> = emptyMap(),
        requestBody: Any? = null,
        responseClass: Class<T>,
        onResponse: (T?) -> Unit,
        onFailure: (MdbApiError?) -> Unit
    ) {
        val urlBuilder = "$baseUrl$endpoint".toHttpUrlOrNull()!!.newBuilder()
        queryParams.forEach { (key, value) -> urlBuilder.addQueryParameter(key, value) }

        val requestBuilder = Request.Builder()
            .url(urlBuilder.build())
            .headers(headers.toMutableMap().apply { put("Authorization", "Bearer $apiKey") }.toHeaders())

        val body = requestBody?.let {
            gson.toJson(requestBody).toRequestBody("application/json".toMediaTypeOrNull())
        }

        when (method) {
            "GET" -> requestBuilder.get()
            "POST" -> {
                if (body == null) throw IllegalArgumentException("Request body must be provided for POST method")
                requestBuilder.post(body)
            }
            "PUT" -> {
                if (body == null) throw IllegalArgumentException("Request body must be provided for PUT method")
                requestBuilder.put(body)
            }
            "PATCH" -> {
                if (body == null) throw IllegalArgumentException("Request body must be provided for PATCH method")
                requestBuilder.patch(body)
            }
            "DELETE" -> requestBuilder.delete(body)
            else -> throw IllegalArgumentException("Unsupported HTTP method: $method")
        }

        val request = requestBuilder.build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onFailure(MdbApiError(code = "NETWORK_ERROR", message = e.message ?: "Network error"))
            }

            override fun onResponse(call: Call, response: Response) {
                response.use { res ->
                    val body = res.body?.string()
                    if (res.isSuccessful) {
                        val responseObject = gson.fromJson(body, responseClass)
                        onResponse(responseObject)
                    } else {
                        val errorResponse = body?.let { gson.fromJson(it, MdbApiError::class.java) }
                            ?: MdbApiError(code = "UNKNOWN_ERROR", message = "Unknown error")
                        onFailure(errorResponse)
                    }
                }
            }
        })
    }
}
