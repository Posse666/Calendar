package com.posse.kotlin1.calendar.view.friends.list

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentRecyclerListBinding
import com.posse.kotlin1.calendar.common.domain.model.Friend
import com.posse.kotlin1.calendar.common.presentation.utils.Account
import com.posse.kotlin1.calendar.utils.*
import com.posse.kotlin1.calendar.view.deleteConfirmation.DeleteFragmentDialog
import com.posse.kotlin1.calendar.view.update.UpdateDialog
import com.posse.kotlin1.calendar.viewModel.FriendsViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class FriendsListFragment : Fragment(), FriendAdapterListener {

    @Inject
    lateinit var account: Account

    private var _binding: FragmentRecyclerListBinding? = null
    private val binding get() = _binding!!
    private var viewModel: FriendsViewModel? = null
    private lateinit var adapter: FriendListRecyclerAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var hiddenBtn = false
    private val keyboard = Keyboard()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        arguments?.let {
            hiddenBtn = it.getBoolean(ARG_HIDDEN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentRecyclerListBinding.inflate(inflater, container, false)
        .also { _binding = it }
        .root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboard.setGlobalListener(activity?.window?.decorView?.rootView)
        val myMail = account.getEmail()
        if (myMail != null && myMail.contains("@")) {
            setupCloseBtn(myMail)
            setupRecyclerAdapter()
            viewModel?.getLiveData()?.observe(viewLifecycleOwner) { showFriends(it) }
        }
    }

    private fun setupCloseBtn(myMail: String) {
        if (hiddenBtn) binding.listClose.disappear()
        else binding.listClose.setOnClickListener {
            viewModel?.refreshLiveData(myMail) {
                if (it == null) UpdateDialog.newInstance().show(childFragmentManager, null)
            }
        }
    }

    private fun showFriends(friends: Pair<Boolean, Set<Friend>>) {
        if (friends.first) {
            if (friends.second.isEmpty()) {
                binding.noData.show()
                binding.noDataText.putText(getString(R.string.nobody_shared))
                binding.listRecyclerView.disappear()
            } else {
                val friendsList = friends.second.toMutableList()
                friendsList.sortBy { it.position }
                adapter.setData(friendsList)
            }
        }
    }

    private fun setupRecyclerAdapter() {
        adapter = FriendListRecyclerAdapter(
            mutableListOf(),
            this,
            keyboard
        ) { itemTouchHelper.startDrag(it) }
        binding.listRecyclerView.adapter = adapter
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.listRecyclerView)
    }

    override fun friendClicked(friend: Friend) = viewModel?.friendSelected(friend)

    override fun friendMoved(fromPosition: Int, toPosition: Int) =
        viewModel?.itemMoved(fromPosition, toPosition)

    override fun friendDeleted(friend: Friend) {
        val dialogText = "${getString(R.string.delete_text)} ${friend.name}?"
        DeleteFragmentDialog
            .newInstance(dialogText, getString(R.string.delete), Color.RED, true) { blocked ->
                friend.blocked = blocked
                viewModel?.deleteFriend(friend) {
                    if (it == null) UpdateDialog.newInstance().show(childFragmentManager, null)
                }
            }.show(childFragmentManager, null)
    }

    override fun friendNameChanged(friend: Friend) = viewModel?.changeName(friend)

    fun setViewModel(model: FriendsViewModel) {
        viewModel = model
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboard.hide(binding.root)
        _binding = null
        keyboard.setListener(null)
        keyboard.removeGlobalListener(activity?.window?.decorView?.rootView)
        viewModel = null
    }

    companion object {
        private const val ARG_HIDDEN = "Hidden"

        @JvmStatic
        fun newInstance(hiddenBtn: Boolean) = FriendsListFragment()
            .apply { arguments = bundleOf(ARG_HIDDEN to hiddenBtn) }
    }
}