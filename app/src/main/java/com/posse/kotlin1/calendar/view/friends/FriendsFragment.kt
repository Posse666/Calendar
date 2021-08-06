package com.posse.kotlin1.calendar.view.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentFriendsBinding
import com.posse.kotlin1.calendar.view.calendar.CalendarFragment
import com.posse.kotlin1.calendar.view.friends.list.FriendsListFragment

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swapFragment(CalendarFragment.newInstance("friend", false))

        binding.friendName.setOnClickListener {
            swapFragment(FriendsListFragment.newInstance())
        }
    }

    private fun swapFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .setReorderingAllowed(true)
            .replace(R.id.friendsMainFragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FriendsFragment()
    }
}