package com.withpersona.sdk2.demo

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.withpersona.sdk2.demo.databinding.ActivityMainBinding
import com.withpersona.sdk2.inquiry.*

/**
 * [MainActivity] showcases how to start the Persona SDK inquiry flow with the [Inquiry.Contract]
 * and [Inquiry.fromTemplate] builder.
 */
class MainActivity : AppCompatActivity() {
    companion object {
        /**
         * Replace this ID with your own template from your Persona Dashboard
         * https://withpersona.com/dashboard/integration
         */
        private const val TEMPLATE_ID: String = "itmpl_PUT_YOUR_TEMPLATE_ID_HERE"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var resultPresenter: PawsonaResultsPresenter

    private val getInquiry = registerForActivityResult(Inquiry.Contract(this)) { inquiry ->
        when (inquiry) {
            is InquiryResponse.Complete -> {
                resultPresenter.showResults(
                    title = "Inquiry Complete (${inquiry.status})",
                    values = inquiry.fields.toList()
                )
            }
            is InquiryResponse.Cancel -> {
                resultPresenter.showResults(
                    title = "Inquiry Canceled",
                    content = "User has canceled the inquiry."
                )
            }
            is InquiryResponse.Error -> {
                resultPresenter.showResults(
                    title = "Inquiry Error",
                    content = inquiry.debugMessage
                )
            }
        }
    }

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
         * Both inquiries are run in [Environment.SANDBOX] for testing out your flow.
         */
        binding.apply {
            startInquiryView.btnHuman.setOnClickListener {
                if (TEMPLATE_ID.contains("PUT_YOUR_TEMPLATE_ID_HERE")) {
                    remindToUseTemplateId()
                } else {
                    getInquiry.launch(
                        Inquiry.fromTemplate(TEMPLATE_ID)
                            .environment(Environment.SANDBOX)
                            .build()
                    )
                }
            }
        }
    }

    private fun remindToUseTemplateId() = AlertDialog.Builder(this)
        .setTitle("Look up your template ID")
        .setMessage("See the README.md for instructions.")
        .create()
        .show()
}
