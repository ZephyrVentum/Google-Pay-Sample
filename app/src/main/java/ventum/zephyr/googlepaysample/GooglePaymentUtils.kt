package ventum.zephyr.googlepaysample

import android.content.Context
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import java.util.*

object GooglePaymentUtils {
    const val LOAD_PAYMENT_DATA_REQUEST_CODE = 1001

    fun createPaymentsClient(context: Context): PaymentsClient =
            Wallet.getPaymentsClient(
                    context,
                    Wallet.WalletOptions.Builder()
                            .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                            .build())

    fun isReadyToPay(paymentsClient: PaymentsClient, callback: (res: Boolean) -> Unit) {
        val request = IsReadyToPayRequest.newBuilder()
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .build()
        val task = paymentsClient.isReadyToPay(request)
        task.addOnCompleteListener {
            try {
                callback.invoke(task.result)
            } catch (exception: ApiException) {
                exception.printStackTrace()
                callback.invoke(false)
            }
        }
    }

    private fun createTokenizationParameters(): PaymentMethodTokenizationParameters {
        return PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .addParameter("gateway", "stripe")
                .addParameter("stripe:publishableKey", "pk_test_zq17QZc7rWHCnvRqwX0FeDBj")
                .addParameter("stripe:version", "8.0.0")
                .build()
    }

    fun createPaymentDataRequest(): PaymentDataRequest {
        val request = PaymentDataRequest.newBuilder()
                .setTransactionInfo(
                        TransactionInfo.newBuilder()
                                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                                .setTotalPrice("10.00")
                                .setCurrencyCode("USD")
                                .build())
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_CARD)
                .addAllowedPaymentMethod(WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD)
                .setCardRequirements(
                        CardRequirements.newBuilder()
                                .addAllowedCardNetworks(Arrays.asList(
                                        WalletConstants.CARD_NETWORK_AMEX,
                                        WalletConstants.CARD_NETWORK_DISCOVER,
                                        WalletConstants.CARD_NETWORK_VISA,
                                        WalletConstants.CARD_NETWORK_MASTERCARD))
                                .build())
        request.setPaymentMethodTokenizationParameters(createTokenizationParameters())
        return request.build()
    }
}