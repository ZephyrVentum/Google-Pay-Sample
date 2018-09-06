package ventum.zephyr.googlepaysample

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import com.stripe.android.model.Token
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ventum.zephyr.googlepaysample.GooglePaymentUtils.LOAD_PAYMENT_DATA_REQUEST_CODE
import ventum.zephyr.googlepaysample.databinding.ActivityMainBinding


const val STRIPE_SECRET_KEY: String = "Bearer sk_test_Y0LouVGgqgBqTdZvkLCeXUZq"

class MainActivity : AppCompatActivity() {

    private lateinit var mPaymentsClient: PaymentsClient
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mPaymentsClient = GooglePaymentUtils.createPaymentsClient(this)
        GooglePaymentUtils.isReadyToPay(mPaymentsClient) {
            mBinding.isReadyToPay = it
        }
    }

    fun onPayButtonClicked(v: View) = AutoResolveHelper.resolveTask(
            mPaymentsClient.loadPaymentData(GooglePaymentUtils.createPaymentDataRequest()),
            this,
            LOAD_PAYMENT_DATA_REQUEST_CODE)

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> when (resultCode) {
                Activity.RESULT_OK -> {
                    val rawToken = data?.let {
                        val paymentData = PaymentData.getFromIntent(data)
                        paymentData?.paymentMethodToken?.token
                    }
                    val stripeToken = Token.fromString(rawToken)
                    stripeToken?.let(this::createCharge)
                }
            }
        }
    }

    private fun createCharge(token: Token) = GooglePaySampleApp
            .getApiService()
            .createCharge(STRIPE_SECRET_KEY, 1000, "usd", "Test charge", token.id)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                    response?.isSuccessful?.let {
                        Toast.makeText(this@MainActivity, if (it) "Successful" else "Error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                    Toast.makeText(this@MainActivity, "Fail", Toast.LENGTH_SHORT).show()
                }
            })
}
