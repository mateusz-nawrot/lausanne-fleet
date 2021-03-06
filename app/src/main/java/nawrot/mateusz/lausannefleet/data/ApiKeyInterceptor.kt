package nawrot.mateusz.lausannefleet.data

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Named

class ApiKeyInterceptor @Inject constructor(@Named("api_key") private val apiKey: String) : Interceptor {

    companion object {
        const val API_KEY = "key"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url().newBuilder().addQueryParameter(API_KEY, apiKey).build()
        return chain.proceed(request.newBuilder().url(newUrl).build())
    }

}