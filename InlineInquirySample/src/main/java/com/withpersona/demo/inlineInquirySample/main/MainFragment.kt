package com.withpersona.demo.inlineInquirySample.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.CONSUMED
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.withpersona.demo.inlineInquirySample.databinding.FragmentMainBinding

/**
 * Start screen.
 */
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

            verify.setOnClickListener {
                val direction = MainFragmentDirections.actionMainFragmentToInquiryFragment()
                findNavController().navigate(direction)
            }
        }
    }
}