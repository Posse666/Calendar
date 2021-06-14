package com.posse.kotlin1.calendar.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentSettingsBinding
import com.posse.kotlin1.calendar.viewModel.SettingsState
import com.posse.kotlin1.calendar.viewModel.SettingsViewModel
import com.squareup.picasso.Picasso

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this).get(SettingsViewModel::class.java)
    }
    private var googleAccount: GoogleSignInAccount? = null
    private val gso = GoogleSignInOptions
        .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()
    private val googleSignInClient by lazy { GoogleSignIn.getClient(requireActivity(), gso) }
    private val startLogin =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            setAuthResult(result)
        }

    private fun setAuthResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                googleAccount = task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                Log.w("login", "signInResult:failed code=" + e.statusCode)
            }
            viewModel.getSettingsState(requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getLiveData().observe(viewLifecycleOwner, { renderSettings(it) })
        viewModel.getSettingsState(requireActivity())
        binding.loginButton.setOnClickListener {
            startLogin.launch(googleSignInClient.signInIntent)
        }
        binding.logoutButton.setOnClickListener {
            googleSignInClient.signOut()
            viewModel.getSettingsState(requireActivity())
        }
        binding.shareButton.setOnClickListener {
            requireActivity().supportFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.shareFragment, ContactsFragment.newInstance())
                    .addToBackStack("")
                    .commit()
            }
            binding.shareFragment.visibility = View.VISIBLE
        }
    }

    private fun renderSettings(settingsState: SettingsState) {
        val defaultPicture = getDrawable(
            requireContext(),
            R.drawable.common_google_signin_btn_icon_light_normal
        )
        when (settingsState) {
            is SettingsState.LoggedIn -> {
                binding.loginButton.hide()
                binding.logoutButton.show()
                binding.userEmail.text = settingsState.userEmail
                Picasso.get()
                    .load(settingsState.userPicture)
                    .resize(
                        defaultPicture?.intrinsicWidth ?: 0,
                        defaultPicture?.intrinsicHeight ?: 0
                    )
                    .into(binding.userLogo)
            }
            SettingsState.LoggedOut -> {
                binding.loginButton.show()
                binding.logoutButton.hide()
                binding.userEmail.text = getString(R.string.login_to_sync)
                binding.userLogo.setImageDrawable(defaultPicture)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}

private fun View.show() {
    this.visibility = View.VISIBLE
}

private fun View.hide() {
    this.visibility = View.GONE
}