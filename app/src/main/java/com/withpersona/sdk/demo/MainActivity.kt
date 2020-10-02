package com.withpersona.sdk.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.withpersona.sdk.demo.databinding.ActivityMainBinding
import com.withpersona.sdk.inquiry.Attributes
import com.withpersona.sdk.inquiry.Environment
import com.withpersona.sdk.inquiry.Inquiry
import com.withpersona.sdk.inquiry.Relationships
import java.text.SimpleDateFormat
import java.util.*

/**
 * [MainActivity] showcases how to start the Persona SDK inquiry flow through [Inquiry], and
 * handles the result in [onActivityResult].
 */
class MainActivity : AppCompatActivity() {
    companion object {
        /** Choose your own request code to receive inquiry results in [onActivityResult] */
        private const val INQUIRY_REQUEST_CODE = 2020

        /** Replace this ID with your own template from withpersona.com */
        private val TEMPLATE_ID: String = TODO("tmpl_PUT_YOUR_TEMPLATE_ID_HERE")
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var resultPresenter: PawsonaResultsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        resultPresenter = PawsonaResultsPresenter(binding)
        onBackPressedDispatcher.addCallback(resultPresenter.backHandler)
        setContentView(binding.root)

        /**
         * Starts an inquiry when one of the buttons is pressed:
         *
         * "Human":     starts an inquiry with Persona's default theme, this is how your flow will
         *              look like if no custom theme is required.
         *
         * "Pawsona":   starts an inquiry with a custom theme [R.style.PersonaThemeCustom], you can
         *              find the example in res/styles and tweak the styles for your own app.
         *
         * Both inquiries are run in [Environment.SANDBOX] for testing out your flow.
         */
        binding.apply {
            startInquiryView.btnHuman.setOnClickListener {
                Inquiry.fromTemplate(TEMPLATE_ID)
                    .environment(Environment.SANDBOX)
                    .build()
                    .start(this@MainActivity, INQUIRY_REQUEST_CODE)
            }
            startInquiryView.btnPawsona.setOnClickListener {
                Inquiry.fromTemplate(TEMPLATE_ID)
                    .environment(Environment.SANDBOX)
                    .theme(R.style.PersonaThemeCustom)
                    .build()
                    .start(this@MainActivity, INQUIRY_REQUEST_CODE)
            }
        }
    }

    /**
     * [Inquiry] response will be returned through the [data] intent when our inquiry [Activity]
     * finishes with result code [INQUIRY_REQUEST_CODE] specified on [Inquiry.start].
     *
     * Use the [Inquiry.onActivityResult] method to retrieve the [Inquiry.Response] object and
     * handle the response.
     *
     * In this demo app, we'll show show a result page through [resultPresenter] and display the
     * relevant Inquiry info.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == INQUIRY_REQUEST_CODE) {
            when (val result = Inquiry.onActivityResult(data)) {
                is Inquiry.Response.Success -> {
                    resultPresenter.showResults(
                        title = "Inquiry Succeeded",
                        values = ResultValues()
                            .inquiryId(result.inquiryId)
                            .attributes(result.attributes)
                            .relationships(result.relationships)
                            .values
                    )
                }
                is Inquiry.Response.Failure -> {
                    resultPresenter.showResults(
                        title = "Inquiry Failed",
                        values = ResultValues()
                            .inquiryId(result.inquiryId)
                            .attributes(result.attributes)
                            .relationships(result.relationships)
                            .values
                    )
                }
                is Inquiry.Response.Cancel -> {
                    resultPresenter.showResults(
                        title = "Inquiry Canceled",
                        content = "User has canceled the inquiry."
                    )
                }
                is Inquiry.Response.Error -> {
                    resultPresenter.showResults(
                        title = "Inquiry Error",
                        content = result.debugMessage
                    )

                }
            }
        }
    }

    /**
     * Present relevant [Inquiry] information as a list of labeled values.
     *
     * Only used for this demo app.
     */
    private class ResultValues(val values: MutableList<Pair<String, String>> = mutableListOf()) {

        companion object {
            private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        }

        fun inquiryId(id: String): ResultValues = apply {
            values.add("Inquiry Id" to id)
        }

        fun attributes(attributes: Attributes): ResultValues = apply {
            attributes.apply {
                name?.let { name ->
                    name.first?.let { values.add("First Name" to it) }
                    name.middle?.let { values.add("Middle Name" to it) }
                    name.last?.let { values.add("Last Name" to it) }
                }
                address?.let { address ->
                    address.countryCode?.let { values.add("Country Code" to it) }
                    address.street1?.let { values.add("Street" to it) }
                    address.street2?.let { values.add("APT/Unit" to it) }
                    address.city?.let { values.add("City" to it) }
                    address.subdivision?.let { values.add("Subdivision" to it) }
                    address.postalCode?.let { values.add("Postal Code" to it) }
                }
                birthdate?.let {
                    values.add("Birthdate" to dateFormat.format(it))
                }
            }
        }

        fun relationships(relationships: Relationships): ResultValues = apply {
            relationships.verifications.withIndex().forEach {
                val verification = it.value
                val content = "id: ${verification.id}\nstatus: ${verification.status}"
                values.add("Verification ${it.index + 1}" to content)
            }
        }
    }
}