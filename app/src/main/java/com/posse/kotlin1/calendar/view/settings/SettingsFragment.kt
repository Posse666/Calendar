package com.posse.kotlin1.calendar.view.settings

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.databinding.FragmentSettingsBinding
import com.posse.kotlin1.calendar.utils.*
import com.posse.kotlin1.calendar.view.settings.share.ShareFragment
import com.posse.kotlin1.calendar.viewModel.AccountState
import com.posse.kotlin1.calendar.viewModel.SettingsViewModel
import com.squareup.picasso.Picasso

const val NIGHT_THEME_SDK = 29

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val account = Account
    private var isInitCompleted = false
    private var isLoginPressed = false
    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this).get(SettingsViewModel::class.java)
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
        account.getLiveData().observe(viewLifecycleOwner, { renderSettings(it) })
        viewModel.getLastTheme()
            .observe(viewLifecycleOwner, {
                if (isInitCompleted) requireActivity().recreate()
                else isInitCompleted = true
            })
        account.getAccountState()
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
            account.login(this)
        }
    }

    private fun setupLogoutButton() {
        binding.logoutButton.setOnClickListener {
            isLoginPressed = true
            account.logout(this)
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
        } else binding.switchTheme.disappear()

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

    private fun renderSettings(accountState: AccountState) {
        val defaultPicture = getDrawable(
            requireContext(),
            R.drawable.common_google_signin_btn_icon_light_normal
        )
        when (accountState) {
            is AccountState.LoggedIn -> {
                binding.loginButton.disappear()
                binding.logoutButton.show()
                accountState.userEmail?.let { binding.userEmail.putText(it) }
                Picasso.get()
                    .load(accountState.userPicture)
                    .resize(
                        defaultPicture?.intrinsicWidth ?: 0,
                        defaultPicture?.intrinsicHeight ?: 0
                    )
                    .into(binding.userLogo)
                if (isLoginPressed) binding.motionSettings.transitionToEnd()
                else binding.motionSettings.progress = 1f
            }
            is AccountState.LoggedOut -> {
                binding.loginButton.show()
                binding.logoutButton.disappear()
                binding.userEmail.putText(getString(R.string.login_to_sync))
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