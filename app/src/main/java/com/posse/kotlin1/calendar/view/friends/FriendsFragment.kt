package com.posse.kotlin1.calendar.view.friends

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentFriendsBinding
import com.posse.kotlin1.calendar.utils.Account
import com.posse.kotlin1.calendar.utils.putText
import com.posse.kotlin1.calendar.utils.showToast
import com.posse.kotlin1.calendar.common.presentation.SettingsTabSwitcher
import com.posse.kotlin1.calendar.view.calendar.CalendarFragment
import com.posse.kotlin1.calendar.view.friends.list.FriendsListFragment
import com.posse.kotlin1.calendar.view.update.UpdateDialog
import com.posse.kotlin1.calendar.viewModel.FriendsViewModel
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FriendsFragment : Fragment() {
    @Inject
    lateinit var account: Account

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private var settingsTabSwitcher: SettingsTabSwitcher? = null
    private var friendsListFragment: FriendsListFragment? = null
    private val viewModel: FriendsViewModel by lazy {
        viewModelFactory.get().create(FriendsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFriendsBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myMail = account.getEmail()
        if (myMail != null && myMail.contains("@")) {
            viewModel.refreshLiveData(myMail) { offline ->
                if (offline == true) context?.showToast(getString(R.string.no_connection))
                if (offline == null) UpdateDialog.newInstance().show(childFragmentManager, null)
            }
            viewModel.getLiveData().observe(viewLifecycleOwner) { data ->
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
                    binding.friendsCard.setOnClickListener {
                        if (friendsListFragment == null || !friendsListFragment!!.isVisible) {
                            friendsListFragment = getFriendsListFragment(false)
                            swapFragment(friendsListFragment!!)
                        }
                    }
                    if (!friendSelected) {
                        friendsListFragment = getFriendsListFragment(true)
                        swapFragment(friendsListFragment!!)
                        if (data.second.isEmpty()) binding.friendsCard.setOnClickListener(null)
                    }
                }
            }
        } else {
            binding.friendName.putText(getString(R.string.login_to_see_friends_calendars))
            binding.friendsCard.setOnClickListener {
                settingsTabSwitcher?.switchToSettings()
            }
        }
    }

    private fun getFriendsListFragment(hiddenBtn: Boolean) =
        FriendsListFragment
            .newInstance(hiddenBtn)
            .apply { setViewModel(viewModel) }

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