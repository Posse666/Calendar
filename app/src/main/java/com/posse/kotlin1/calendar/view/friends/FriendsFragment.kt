package com.posse.kotlin1.calendar.view.friends

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentFriendsBinding
import com.posse.kotlin1.calendar.utils.Account
import com.posse.kotlin1.calendar.utils.putText
import com.posse.kotlin1.calendar.utils.showToast
import com.posse.kotlin1.calendar.view.SettingsTabSwitcher
import com.posse.kotlin1.calendar.view.calendar.CalendarFragment
import com.posse.kotlin1.calendar.view.friends.list.FriendsListFragment
import com.posse.kotlin1.calendar.viewModel.FriendsViewModel

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private var settingsTabSwitcher: SettingsTabSwitcher? = null
    private var friendsListFragment: FriendsListFragment? = null
    private val viewModel: FriendsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myMail = Account.getEmail()
        if (myMail != null && myMail.contains("@")) {
            viewModel.refreshLiveData(myMail) { context?.showToast(getString(R.string.no_connection)) }
            viewModel.getLiveData().observe(viewLifecycleOwner, { data ->
                if (data.first) {
                    friendsListFragment?.let {
                        childFragmentManager
                            .beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .setReorderingAllowed(true)
                            .remove(it)
                            .commit()
                    }
                    friendsListFragment = null
                    binding.friendName.putText(getString(R.string.select_friend))
                    var friendSelected = false
                    data.second.forEach { friend ->
                        if (friend.selected) {
                            binding.friendName.putText(friend.name)
                            swapFragment(CalendarFragment.newInstance(friend.email, false))
                            friendSelected = true
                        }
                    }
                    if (!friendSelected) {
                        friendsListFragment = FriendsListFragment.newInstance()
                        swapFragment(friendsListFragment!!)
                    }
                    binding.friendsCard.setOnClickListener {
                        if (friendsListFragment == null || !friendsListFragment!!.isVisible) {
                            friendsListFragment = FriendsListFragment.newInstance()
                            swapFragment(friendsListFragment!!)
                        }
                    }
                }
            })
        } else {
            binding.friendName.putText(getString(R.string.login_to_see_friends_calendars))
            binding.friendsCard.setOnClickListener {
                settingsTabSwitcher?.switchToSettings()
            }
        }
    }

    private fun swapFragment(fragment: Fragment) {
        childFragmentManager
            .beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .setReorderingAllowed(true)
            .replace(R.id.friendsMainFragment, fragment)
            .commit()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        settingsTabSwitcher = context as SettingsTabSwitcher
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        settingsTabSwitcher = null
        friendsListFragment = null
    }

    companion object {
        fun newInstance() = FriendsFragment()
    }
}