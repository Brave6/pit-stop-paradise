package com.seth.pitstopparadise.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.seth.pitstopparadise.LoginActivity
import com.seth.pitstopparadise.databinding.FragmentGetStartedBinding

class GetStartedFragment : Fragment() {

    private var _binding: FragmentGetStartedBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGetStartedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Animate the "Get Started" button
        binding.btnGetStarted.apply {
            translationY = 100f
            alpha = 0f
            animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(500)
                .withEndAction {
                    // Start the Lottie animation after the button animation
                    binding.lottieWelcome.apply {
                        alpha = 0f
                        visibility = View.VISIBLE
                        animate()
                            .alpha(1f)
                            .setDuration(500)
                            .start()
                        playAnimation()
                    }
                }
                .start()
        }
        // Initially hide Lottie
        binding.lottieWelcome.alpha = 0f
        binding.lottieWelcome.visibility = View.INVISIBLE

        // Navigate to LoginActivity on click
        binding.btnGetStarted.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        // Handle back pressed specifically in this fragment
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Exit the app instead of going back
                    requireActivity().finish()
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
