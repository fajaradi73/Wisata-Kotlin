package com.fajarproject.travels.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.fajarproject.travels.feature.previewPictureWisata.PreviewPictureFragment
import com.fajarproject.travels.models.PictureItem

/**
 * Create by Fajar Adi Prasetyo on 13/01/2020.
 */
class SectionsPagerAdapter(fragmentManager: FragmentManager, val data : List<PictureItem>) : FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return PreviewPictureFragment().newInstance(position,data[position].namaWisata,data[position].picture)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return data[position].namaWisata
    }
}