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
import com.posse.kotlin1.calendar.databinding.FragmentFriendsListBinding
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.disappear
import com.posse.kotlin1.calendar.utils.show
import com.posse.kotlin1.calendar.view.deleteConfirmation.DeleteFragmentDialog
import com.posse.kotlin1.calendar.viewModel.FriendsViewModel

class FriendsListFragment : Fragment(), FriendAdapterListener {

    private var _binding: FragmentFriendsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsViewModel by activityViewModels()
    private lateinit var adapter: FriendListRecyclerAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.friendsListClose.setOnClickListener { viewModel.refreshLiveData() }
        viewModel.getLiveData().observe(viewLifecycleOwner, {
            if (it.first) {
                if (it.second.isEmpty()) {
                    binding.noFriends.show()
                    binding.friendsRecyclerView.disappear()
                } else {
                    val friends = it.second.toMutableList()
                    friends.sortBy { friend -> friend.position }
                    adapter = FriendListRecyclerAdapter(
                        friends,
                        { viewHolder -> itemTouchHelper.startDrag(viewHolder) },
                        this
                    )
                    binding.friendsRecyclerView.adapter = adapter
                    itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
                    itemTouchHelper.attachToRecyclerView(binding.friendsRecyclerView)
                }
            }
        })
    }

    override fun friendClicked(friend: Friend) = viewModel.friendSelected(friend)

    override fun friendMoved(fromPosition: Int, toPosition: Int) =
        viewModel.itemMoved(fromPosition, toPosition)

    override fun friendDeleted(friend: Friend) {
        val dialogText = "${getString(R.string.delete_text)} ${friend.name}?"
        val dialog = DeleteFragmentDialog
            .newInstance(dialogText, getString(R.string.delete), Color.RED, true)
        dialog.setListener {
            friend.isBlocked = it
            viewModel.deleteFriend(friend)
        }
        dialog.show(childFragmentManager, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FriendsListFragment()
    }
}