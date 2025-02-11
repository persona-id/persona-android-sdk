package com.withpersona.demo.inlineInquirySample.inquiry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.withpersona.demo.inlineInquirySample.databinding.FragmentInquirySuccessBinding

class InquirySuccessFragment : Fragment() {

    private lateinit var binding: FragmentInquirySuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentInquirySuccessBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            continueButton.setOnClickListener {
                findNavController().popBackStack()
            }
        }
    }
}