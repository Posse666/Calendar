package com.posse.kotlin1.calendar.view.friends.list

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentRecyclerListBinding
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.Account
import com.posse.kotlin1.calendar.utils.disappear
import com.posse.kotlin1.calendar.utils.putText
import com.posse.kotlin1.calendar.utils.show
import com.posse.kotlin1.calendar.view.deleteConfirmation.DeleteFragmentDialog
import com.posse.kotlin1.calendar.view.update.UpdateDialog
import com.posse.kotlin1.calendar.viewModel.FriendsViewModel

class FriendsListFragment : Fragment(), FriendAdapterListener {

    private var _binding: FragmentRecyclerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsViewModel by activityViewModels()
    private lateinit var adapter: FriendListRecyclerAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val myMail = Account.getEmail()
        if (myMail != null && myMail.contains("@")) {
            binding.listClose.setOnClickListener {
                viewModel.refreshLiveData(myMail) {
                    if (it == null) UpdateDialog.newInstance().show(childFragmentManager, null)
                }
            }
            setupRecyclerAdapter()
            viewModel.getLiveData().observe(viewLifecycleOwner, { showFriends(it) })
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
            this
        ) { itemTouchHelper.startDrag(it) }
        binding.listRecyclerView.adapter = adapter
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.listRecyclerView)
    }

    override fun friendClicked(friend: Friend) = viewModel.friendSelected(friend)

    override fun friendMoved(fromPosition: Int, toPosition: Int) =
        viewModel.itemMoved(fromPosition, toPosition)

    override fun friendDeleted(friend: Friend) {
        val dialogText = "${getString(R.string.delete_text)} ${friend.name}?"
        val dialog = DeleteFragmentDialog
            .newInstance(dialogText, getString(R.string.delete), Color.RED, true)
        dialog.setListener { blocked ->
            friend.blocked = blocked
            viewModel.deleteFriend(friend) {
                if (it == null) UpdateDialog.newInstance().show(childFragmentManager, null)
            }
        }
        dialog.show(childFragmentManager, null)
    }

    override fun friendNameChanged(friend: Friend) = viewModel.changeName(friend)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = FriendsListFragment()
    }
}