package com.fajarproject.travels.ui.previewPictureWisata


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.bumptech.glide.Glide
import com.fajarproject.travels.FlavorConfig
import com.fajarproject.travels.R
import com.fajarproject.travels.databinding.FragmentPreviewPictureBinding
import com.fajarproject.travels.util.Util

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */

class PreviewPictureFragment : Fragment() {

    var name: String? = ""
    var url : String? = ""
    var pos = 0
    private val argImageSection = "section_number"
    private val argImageTitle   = "image_title"
    private val argImageUrl     = "image_url"
    private lateinit var binding : FragmentPreviewPictureBinding

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        this.pos    = args!!.getInt(argImageSection)
        this.name   = args.getString(argImageTitle)
        this.url    = args.getString(argImageUrl)
    }

    fun newInstance(
        sectionNumber: Int,
        name: String?,
        url: String?
    ): Fragment {
        val fragment = PreviewPictureFragment()
        val args = Bundle()
        args.putInt(argImageSection, sectionNumber)
        args.putString(argImageTitle, name)
        args.putString(argImageUrl, url)
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPreviewPictureBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireActivity()).load(FlavorConfig.baseImage() + url).error(R.drawable.image_dieng).placeholder(
            Util.circleLoading(requireActivity())).thumbnail(0.1f).into(binding.detailImage)

        binding.detailImage.setOnTouchListener(ImageMatrixTouchHandler(activity))
        postponeEnterTransition()
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementReturnTransition = null
    }

}
