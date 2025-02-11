package com.withpersona.demo.inlineInquirySample.inquiry

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.CONSUMED
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.withpersona.demo.inlineInquirySample.R
import com.withpersona.demo.inlineInquirySample.databinding.FragmentInquiryBinding
import com.withpersona.sdk2.inquiry.Environment
import com.withpersona.sdk2.inquiry.ExperimentalInlineApi
import com.withpersona.sdk2.inquiry.Inquiry
import com.withpersona.sdk2.inquiry.InquiryResponse
import com.withpersona.sdk2.inquiry.createFragment
import com.withpersona.sdk2.inquiry.inline_inquiry.InlineInquiryController
import com.withpersona.sdk2.inquiry.inline_inquiry.InlineInquiryScreen
import com.withpersona.sdk2.inquiry.toInlineInquiryBuilder
import com.withpersona.sdk2.inquiry.types.DEFAULT_REQUEST_KEY
import kotlinx.coroutines.launch

/**
 * Screen that hosts the Persona inquiry flow.
 */
@OptIn(ExperimentalInlineApi::class)
class InquiryFragment : Fragment(), InlineInquiryController {

    companion object {
        /**
         * Replace this ID with your own template from your Persona Dashboard
         * https://withpersona.com/dashboard/integration
         */
        private const val TEMPLATE_ID: String = "itmpl_PUT_YOUR_TEMPLATE_ID_HERE"
    }

    private lateinit var binding: FragmentInquiryBinding

    /**
     * You can use [InlineInquiryScreen] to control it's navigation flow or listen for events.
     */
    private var inlineInquiryScreen: InlineInquiryScreen? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentInquiryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()

        with(binding) {
            ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
                val systemBarsInsets = insets.getInsetsIgnoringVisibility(
                    WindowInsetsCompat.Type.systemBars(),
                )

                root.setPadding(
                    systemBarsInsets.left,
                    systemBarsInsets.top,
                    systemBarsInsets.right,
                    systemBarsInsets.bottom,
                )

                CONSUMED
            }

            // Listen for results from the Persona inquiry. By default the inquiry fragment uses
            // com.withpersona.sdk2.inquiry.types.DEFAULT_REQUEST_KEY as the request key to return
            // results to. This can be changed in the InlineInquiryBuilder.
            childFragmentManager
                .setFragmentResultListener(DEFAULT_REQUEST_KEY, viewLifecycleOwner) { _, bundle ->
                    val result = Inquiry.extractInquiryResponseFromBundle(bundle, context)

                    when (result) {
                        is InquiryResponse.Cancel -> {
                            // User cancelled the flow.

                            findNavController().popBackStack()
                        }
                        is InquiryResponse.Complete -> {
                            // User completed the flow. Note that this doesn't mean the user
                            // verified successfully. It just means the user got to the end.

                            childFragmentManager.commit {
                                setCustomAnimations(
                                    androidx.navigation.ui.R.animator.nav_default_enter_anim,
                                    androidx.navigation.ui.R.animator.nav_default_exit_anim,
                                    androidx.navigation.ui.R.animator.nav_default_pop_enter_anim,
                                    androidx.navigation.ui.R.animator.nav_default_pop_exit_anim
                                )
                                replace(
                                    inquiryContainer.id,
                                    InquirySuccessFragment(),
                                )
                            }
                        }
                        is InquiryResponse.Error -> {
                            // An error prevented the user from completing the flow.

                            MaterialAlertDialogBuilder(context)
                                .setMessage(R.string.error_verify)
                                .setPositiveButton(R.string.button_ok) { _, _ ->
                                    findNavController().popBackStack()
                                }
                                .show()
                        }
                    }
                }

            if (savedInstanceState == null) {
                if (TEMPLATE_ID.contains("PUT_YOUR_TEMPLATE_ID_HERE")) {
                    MaterialAlertDialogBuilder(context)
                        .setMessage("Please set the TEMPLATE_ID variable in InquiryFragment.kt.")
                        .show()
                } else {
                    val inlineInquiryFragment = Inquiry
                        .fromTemplate(TEMPLATE_ID)
                        .environment(Environment.SANDBOX)
                        .toInlineInquiryBuilder()

                        // This example hosts the inquiry flow within a larger screen with it's own
                        // navigation bar so we do not want the inquiry flow to display it's own
                        // navigation bar.
                        //
                        // Note that the inquiry flow will still handle back presses. If you want to
                        // prevent the user from being able to navigate back, you will need to configure
                        // it the inquiry template editor.
                        .isNavBarEnabled(false)
                        .createFragment()

                    childFragmentManager.commit {
                        setCustomAnimations(
                            androidx.navigation.ui.R.animator.nav_default_enter_anim,
                            androidx.navigation.ui.R.animator.nav_default_exit_anim,
                            androidx.navigation.ui.R.animator.nav_default_pop_enter_anim,
                            androidx.navigation.ui.R.animator.nav_default_pop_exit_anim
                        )
                        replace(
                            inquiryContainer.id,
                            requireNotNull(inlineInquiryFragment),
                        )
                    }
                }
            }

            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    @ExperimentalInlineApi
    override fun onAttached(inlineInquiryScreen: InlineInquiryScreen) {
        this.inlineInquiryScreen = inlineInquiryScreen

        viewLifecycleOwner.lifecycleScope.launch {
            inlineInquiryScreen.screenStateFlow.collect {
                // Ideally you would want to do this in the view model but this is a simple example.

                if (!it.isNavigationEnabled) {
                    // Disable navigation buttons if showing custom navigation buttons here.
                } else {
                    // Enable navigation buttons if showing custom navigation buttons here.
                }

                if (it.shouldShowBackButton) {
                    // Hide back button if showing custom navigation buttons here.
                } else {
                    // Show back button if showing custom navigation buttons here.
                }

                if (it.shouldShowCancelButton) {
                    // Hide cancel button if showing custom navigation buttons here.
                } else {
                    // Show cancel button if showing custom navigation buttons here.
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            inlineInquiryScreen.eventFlow.collect {
                // Ideally you would want to do this in the view model but this is a simple example.

                Log.d("Inquiry", "Event: $it")
            }
        }
    }

    @ExperimentalInlineApi
    override fun onDetached() {
        this.inlineInquiryScreen = null
    }
}