package com.withpersona.sdk2.demo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Typeface
import android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.withpersona.sdk2.demo.databinding.ActivityMainBinding
import com.withpersona.sdk2.demo.databinding.ContentInquiryBinding
import com.withpersona.sdk2.demo.databinding.ContentResultBinding
import com.withpersona.sdk2.inquiry.InquiryField

/**
 * Presenter to help display the results page for this demo app.
 *
 * Shows a result page displaying provided information and handle back button press to return to
 * the start page.
 *
 * Only used for this demo app.
 */
internal class PawsonaResultsPresenter(
    private val binding: ActivityMainBinding
) {
    companion object {
        private const val ANIMATION_DURATION: Long = 200
    }

    init {
        binding.inquiryResultView.hide(animated = false)
    }

    val backHandler: OnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            binding.apply {
                inquiryResultView.hide()
                startInquiryView.show()
            }
            isEnabled = false
        }
    }

    /**
     * Show a message [title] and [content] in the content box.
     */
    fun showResults(title: CharSequence, content: CharSequence) {
        binding.apply {
            startInquiryView.hide(animated = false)
            inquiryResultView.apply {
                inquiryStatus.text = title
                inquiryContent.text = content
            }.show()
        }
        backHandler.isEnabled = true
    }

    /**
     * Show a message [title] and format [values] as a list of entries in the content box.
     */
    fun showResults(title: String, values: List<Pair<String, InquiryField>>) {
        val builder = SpannableStringBuilder()
        for ((label, value) in values) {
            val length = builder.length
            builder.appendLine(label)
            builder.setSpan(
                StyleSpan(Typeface.BOLD),
                length,
                builder.length,
                SPAN_EXCLUSIVE_EXCLUSIVE
            )
            val stringValue: String = when (value) {
                is InquiryField.IntegerField -> value.value.toString()
                is InquiryField.BooleanField -> value.value.toString()
                is InquiryField.StringField -> value.value.toString()
                is InquiryField.FloatField -> value.value.toString()
                is InquiryField.DateField -> value.value.toString()
                is InquiryField.DatetimeField -> value.value.toString()
                is InquiryField.UnknownField -> "Unknown"
            }
            builder.appendLine(stringValue)
            builder.appendLine()
        }
        showResults(title, builder)
    }

    private fun ContentResultBinding.show() {
        root.slideIn(1000f)
    }

    private fun ContentResultBinding.hide(animated: Boolean = true) {
        root.slideOut(1000f, if (animated) ANIMATION_DURATION else 0)
    }

    private fun ContentInquiryBinding.show() {
        root.slideIn(-1000f)
    }

    private fun ContentInquiryBinding.hide(animated: Boolean = true) {
        root.slideOut(-1000f, if (animated) ANIMATION_DURATION else 0)
    }

    private fun View.slideIn(translateX: Float, duration: Long = ANIMATION_DURATION) {
        animate()
            .alpha(1f)
            .translationX(0f)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    alpha = 0f
                    translationX = -translateX
                    visibility = View.VISIBLE
                }
            })
    }

    private fun View.slideOut(translateX: Float, duration: Long = ANIMATION_DURATION) {
        animate()
            .alpha(0f)
            .translationX(translateX)
            .setDuration(duration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    alpha = 1f
                    translationX = 0f
                }

                override fun onAnimationEnd(animation: Animator) {
                    visibility = View.INVISIBLE
                }
            })
    }

}