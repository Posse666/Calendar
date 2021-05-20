package com.posse.kotlin1.calendar.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.posse.kotlin1.calendar.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val mBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    companion object {
        fun newInstance() = FriendsFragment()
    }
}