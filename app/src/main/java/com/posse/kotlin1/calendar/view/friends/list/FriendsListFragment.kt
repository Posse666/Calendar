package com.posse.kotlin1.calendar.view.friends.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.posse.kotlin1.calendar.databinding.FragmentFriendsListBinding
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.viewModel.FriendsListViewModel

class FriendsListFragment : Fragment() {

    private var _binding: FragmentFriendsListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsListViewModel by viewModels()
    private lateinit var adapter: FriendListRecyclerAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private var friends: MutableList<Friend> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveData()
            .observe(viewLifecycleOwner, { refreshAdapterData(it.toMutableList()) })

        adapter = FriendListRecyclerAdapter(
            friends,
            object : OnStartDragListener {
                override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
                    itemTouchHelper.startDrag(viewHolder)
                }
            },
            requireActivity()
        )

        binding.friendsRecyclerView.adapter = adapter
        itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback(adapter))
        itemTouchHelper.attachToRecyclerView(binding.friendsRecyclerView)

        viewModel.getFriendsList()
    }

    private fun refreshAdapterData(friends: MutableList<Friend>) {
        adapter.setData(friends)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = FriendsListFragment()
    }
}