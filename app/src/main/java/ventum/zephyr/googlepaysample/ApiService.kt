package ventum.zephyr.googlepaysample

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


const val BASE_URL = "https://api.stripe.com/v1/"

interface ApiService {
    @POST("charges")
    fun createCharge(@Header("Authorization") auth: String,
                     @Query("amount") amount: Int,
                     @Query("currency") currency: String,
                     @Query("description") description: String,
                     @Query("source") source: String): Call<ResponseBody>
}