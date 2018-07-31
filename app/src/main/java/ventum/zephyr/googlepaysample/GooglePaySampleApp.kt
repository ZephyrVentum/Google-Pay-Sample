package ventum.zephyr.googlepaysample

import android.app.Application
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GooglePaySampleApp : Application() {
    private lateinit var retrofit: Retrofit

    companion object {
        private lateinit var apiService: ApiService

        fun getApiService(): ApiService {
            return apiService
        }
    }

    override fun onCreate() {
        super.onCreate()

        val interceptor = HttpLoggingInterceptor()

        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        apiService = retrofit.create(ApiService::class.java)
    }


}