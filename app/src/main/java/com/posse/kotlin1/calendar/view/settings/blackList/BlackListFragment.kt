package com.posse.kotlin1.calendar.view.settings.blackList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentBlackListBinding
import com.posse.kotlin1.calendar.model.Friend
import com.posse.kotlin1.calendar.utils.*
import com.posse.kotlin1.calendar.view.update.UpdateDialog
import com.posse.kotlin1.calendar.viewModel.BlackListViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class BlackListFragment : DialogFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private var _binding: FragmentBlackListBinding? = null
    private val binding get() = _binding!!
    private lateinit var email: String
    private val viewModel: BlackListViewModel by lazy {
        viewModelFactory.create(BlackListViewModel::class.java)
    }
    private lateinit var adapter: BlackListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
        arguments?.let {
            email = it.getString(ARG_EMAIL)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlackListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWindowSize(this, WindowManager.LayoutParams.MATCH_PARENT)
        binding.blackListCard.listClose.setOnClickListener { dismiss() }
        adapter = BlackListRecyclerAdapter(
            mutableListOf()
        ) { viewModel.personSelected(it) }
        binding.blackListCard.listRecyclerView.adapter = adapter
        viewModel.getLiveData().observe(viewLifecycleOwner, { showFriends(it) })
        viewModel.refreshLiveData(
            email,
            { UpdateDialog.newInstance().show(childFragmentManager, null) }) {
            context?.showToast(getString(R.string.no_connection))
        }
        isCancelable = true
    }

    private fun showFriends(friends: Pair<Boolean, Set<Friend>>) {
        if (friends.first) {
            if (friends.second.isEmpty()) {
                binding.blackListCard.noData.show()
                binding.blackListCard.noDataText.putText(getString(R.string.empty_black_list))
                binding.blackListCard.listRecyclerView.disappear()
            } else {
                val friendsList = friends.second.toMutableList()
                friendsList.sortBy { it.name }
                adapter.setData(friendsList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ARG_EMAIL = "Email"

        @JvmStatic
        fun newInstance(email: String) =
            BlackListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EMAIL, email)
                }
            }
    }
}