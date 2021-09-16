package com.posse.kotlin1.calendar.view.settings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.app.App
import com.posse.kotlin1.calendar.databinding.FragmentSettingsBinding
import com.posse.kotlin1.calendar.utils.*
import com.posse.kotlin1.calendar.view.ActivityRefresher
import com.posse.kotlin1.calendar.view.settings.blackList.BlackListFragment
import com.posse.kotlin1.calendar.view.settings.share.ShareFragment
import com.posse.kotlin1.calendar.view.update.UpdateDialog
import com.posse.kotlin1.calendar.viewModel.NICKNAME
import com.posse.kotlin1.calendar.viewModel.SettingsViewModel
import com.squareup.picasso.Picasso

const val NIGHT_THEME_SDK = 29

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val account = Account
    private val keyboard = Keyboard()
    private val animator = Animator()
    private var isInitCompleted = false
    private var isLoginPressed = false
    private var isEditMode = false
    private var activityRefresher: ActivityRefresher? = null
    private val viewModel: SettingsViewModel by lazy {
        ViewModelProvider(this).get(SettingsViewModel::class.java)
    }
    private val startLogin: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        Account.setAuthResult(it) {
            UpdateDialog.newInstance().show(childFragmentManager, null)
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
        account.getLiveData().observe(viewLifecycleOwner, { renderSettings(it) })
        viewModel.getLastTheme()
            .observe(viewLifecycleOwner, {
                if (isInitCompleted) requireActivity().recreate()
                else isInitCompleted = true
            })
        account.getAccountState()
        setupLoginButton()
        setupLogoutButton()
        setupNicknameField()
        setupBlackList()
        setupThemeSwitch()
        setupEditNicknameButton()
        setupLocaleSwitch()
        keyboard.setListener { binding.nickName.editText?.clearFocus() }
    }

    private fun setupLocaleSwitch() {
        when (getStringLocale()) {
            LOCALE_RU -> binding.languageChips.check(R.id.chipRu)
            LOCALE_EN -> binding.languageChips.check(R.id.chipEn)
        }
        binding.languageChips.setOnCheckedChangeListener { _, checkedId ->
            val stringLocale = when (checkedId) {
                R.id.chipEn -> LOCALE_EN
                R.id.chipRu -> LOCALE_RU
                else -> LOCALE_EN
            }
            setAppLocale(stringLocale)
            activityRefresher?.refreshNavBar()
        }
    }

    private fun setupEditNicknameButton() {
        binding.btnEditNickname.setOnClickListener { view ->
            if (isEditMode) {
                val nickname = binding.nickName.editText?.text.toString()
                if (!nickname.contains(" ") && nickname.isNotEmpty())
                    animator.animate(view) {
                        viewModel.saveNickname(Account.getEmail()!!, nickname) { saved ->
                            when (saved) {
                                NICKNAME.EMPTY -> {
                                    binding.nickName.error = getString(R.string.no_internet)
                                }
                                NICKNAME.BUSY -> {
                                    binding.nickName.error = getString(R.string.nickname_is_busy)
                                }
                                NICKNAME.ERROR -> {
                                    UpdateDialog.newInstance().show(childFragmentManager, null)
                                }
                                NICKNAME.SAVED -> {
                                    (view as AppCompatImageButton).setImageDrawable(
                                        getDrawable(requireContext(), R.drawable.shotglass_empty)
                                    )
                                    view.drawable.setTint(
                                        getColor(requireContext(), R.color.strokeColor)
                                    )
                                    keyboard.hide(view)
                                    binding.nickName.disable()
                                    binding.nickName.error = null
                                    isEditMode = false
                                }
                            }
                        }
                    }
            } else {
                animator.animate(view) {
                    (view as AppCompatImageButton).setImageDrawable(
                        getDrawable(
                            requireContext(),
                            R.drawable.shotglass_full
                        )
                    )
                    view.drawable.setTint(getColor(requireContext(), R.color.fillColor))
                    binding.nickName.enable()
                    binding.nickName.editText?.let {
                        it.setSelection(it.length())
                    }
                    keyboard.show()
                    binding.nickName.error = null
                    isEditMode = true
                }
            }
        }
    }

    private fun setupNicknameField() {
        binding.nickName.editText?.doOnTextChanged { text, _, _, _ ->
            if (text?.contains(" ") == true) {
                binding.nickName.error = getString(R.string.remove_space)
            } else binding.nickName.error = null
        }
        binding.nickName.editText?.let {
            it.setOnEditorActionListener { textView, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    textView.clearFocus()
                }
                false
            }
        }
    }

    private fun setupShareFragment() {
        childFragmentManager.apply {
            this.beginTransaction()
                .replace(R.id.shareFragmentContainer, ShareFragment.newInstance())
                .commit()
        }
    }

    private fun setupBlackList() {
        binding.settingsBlackList.setOnClickListener {
            BlackListFragment.newInstance(account.getEmail()!!).show(childFragmentManager, null)
        }
    }

    private fun setupLoginButton() {
        binding.loginButton.setOnClickListener {
            isLoginPressed = true
            account.login(this, startLogin)
        }
    }

    private fun setupLogoutButton() {
        binding.logoutButton.setOnClickListener {
            isLoginPressed = true
            account.logout(this)
        }
    }

    private fun setupThemeSwitch() {
        val themeSwitch = App.sharedPreferences.themeSwitch
        if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK) {
            binding.chipDay.isEnabled = !themeSwitch
            binding.chipNight.isEnabled = !themeSwitch
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
        } else {
            binding.switchTheme.disappear()
            binding.chipDay.isEnabled = true
            binding.chipNight.isEnabled = true
        }

        if (App.sharedPreferences.lightTheme) binding.themeChips.check(THEME.DAY.resID)
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
                binding.btnEditNickname.show()
                binding.loginButton.disappear()
                binding.logoutButton.show()
                binding.nickName.show()
                binding.userEmail.putText(accountState.userEmail)
                binding.nickName.editText?.setText(accountState.nickname)
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
                binding.btnEditNickname.disappear()
                binding.loginButton.show()
                binding.logoutButton.disappear()
                binding.nickName.disappear()
                binding.userEmail.putText(getString(R.string.login_to_sync))
                binding.userLogo.setImageDrawable(defaultPicture)
                if (isLoginPressed) binding.motionSettings.transitionToStart()
                else binding.motionSettings.progress = 0f
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityRefresher = context as ActivityRefresher
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        keyboard.setListener(null)
        keyboard.removeGlobalListener()
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}