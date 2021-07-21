package com.posse.kotlin1.calendar.view.settings

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
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
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.databinding.FragmentSettingsBinding
import com.posse.kotlin1.calendar.utils.THEME
import com.posse.kotlin1.calendar.utils.lightTheme
import com.posse.kotlin1.calendar.utils.themeSwitch
import com.posse.kotlin1.calendar.view.settings.share.ShareFragment
import com.posse.kotlin1.calendar.viewModel.SettingsState
import com.posse.kotlin1.calendar.viewModel.SettingsViewModel
import com.squareup.picasso.Picasso

const val NIGHT_THEME_SDK = 29

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var isInitCompleted = false
    private var isLoginPressed = false
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
            viewModel.getSettingsState()
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
        setupShareFragment()
        viewModel.getLiveData().observe(viewLifecycleOwner, { renderSettings(it) })
        viewModel.getLastTheme()
            .observe(viewLifecycleOwner, {
                if (isInitCompleted) requireActivity().recreate()
                else isInitCompleted = true
            })
        viewModel.getSettingsState()
        setupLoginButton()
        setupLogoutButton()
        setupThemeSwitch()
    }

    private fun setupShareFragment() {
        childFragmentManager.apply {
            this.beginTransaction()
                .replace(R.id.shareFragmentContainer, ShareFragment.newInstance())
                .commit()
        }
    }

    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            isLoginPressed = true
            startLogin.launch(googleSignInClient.signInIntent)
        }
    }

    private fun setupLogoutButton() {
        binding.logoutButton.setOnClickListener {
            isLoginPressed = true
            googleSignInClient.signOut()
            viewModel.getSettingsState()
        }
    }

    private fun setupThemeSwitch() {
        val themeSwitch = App.sharedPreferences?.themeSwitch ?: true
        if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK) {
            binding.switchTheme.isChecked = themeSwitch
            binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
                binding.chipDay.isEnabled = !isChecked
                binding.chipNight.isEnabled = !isChecked
                viewModel.switchState = isChecked
                if (isChecked) {
                    when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_NO -> binding.themeChips.check(THEME.DAY.resID)
                        Configuration.UI_MODE_NIGHT_YES -> binding.themeChips.check(THEME.NIGHT.resID)
                    }
                }
            }
        } else binding.switchTheme.visibility = View.GONE

        binding.chipDay.isEnabled = !themeSwitch
        binding.chipNight.isEnabled = !themeSwitch

        if (App.sharedPreferences?.lightTheme == true) binding.themeChips.check(THEME.DAY.resID)
        else binding.themeChips.check(THEME.NIGHT.resID)

        binding.themeChips.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                THEME.DAY.resID -> viewModel.lightTheme = true
                THEME.NIGHT.resID -> viewModel.lightTheme = false
            }
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
                if (isLoginPressed) binding.motionSettings.transitionToEnd()
                else binding.motionSettings.progress = 1f
            }
            SettingsState.LoggedOut -> {
                binding.loginButton.show()
                binding.logoutButton.hide()
                binding.userEmail.text = getString(R.string.login_to_sync)
                binding.userLogo.setImageDrawable(defaultPicture)
                if (isLoginPressed) binding.motionSettings.transitionToStart()
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