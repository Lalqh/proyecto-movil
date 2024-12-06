package com.example.proyecto.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.proyecto.R
import com.example.proyecto.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())
    private var currentPageDestacados = 0
    private var currentPageNormales = 0

    private val imagesDestacados = listOf(
        R.drawable.img1, R.drawable.img2, R.drawable.img3,
        R.drawable.img4, R.drawable.img5
    )

    private val imagesNormales = listOf(
        R.drawable.img6, R.drawable.img7, R.drawable.img8,
        R.drawable.img9, R.drawable.img10, R.drawable.img11,
        R.drawable.img12, R.drawable.img13
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        startAutoScroll(binding.scrollViewDestacados, imagesDestacados, true)
        startAutoScroll(binding.scrollViewNormales, imagesNormales, false)

        return root
    }

    private fun startAutoScroll(scrollView: HorizontalScrollView, images: List<Int>, isDestacados: Boolean) {
        val runnable = object : Runnable {
            override fun run() {
                val currentPage = if (isDestacados) currentPageDestacados else currentPageNormales
                val imageView = scrollView.getChildAt(0) as LinearLayout
                val image = imageView.getChildAt(currentPage) as ImageView

                val scaleUp = ScaleAnimation(
                    1.0f, 1.1f,
                    1.0f, 1.1f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                )
                scaleUp.duration = 2500
                scaleUp.fillAfter = true

                val scaleDown = ScaleAnimation(
                    1.1f, 1.0f,
                    1.1f, 1.0f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f,
                    ScaleAnimation.RELATIVE_TO_SELF, 0.5f
                )
                scaleDown.startOffset = 2500
                scaleDown.duration = 2500
                scaleDown.fillAfter = true

                val animationSet = AnimationSet(true)
                animationSet.addAnimation(scaleUp)
                animationSet.addAnimation(scaleDown)
                animationSet.repeatCount = 1

                image.startAnimation(animationSet)

                scrollView.smoothScrollTo(image.left, 0)

                if (isDestacados) {
                    currentPageDestacados = (currentPageDestacados + 1) % images.size
                } else {
                    currentPageNormales = (currentPageNormales + 1) % images.size
                }

                handler.postDelayed(this, 5000)
            }
        }
        handler.post(runnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }
}