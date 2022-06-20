package com.posse.kotlin1.calendar.view.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getDrawable
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.color.MaterialColors
import com.posse.kotlin1.calendar.R
import com.posse.kotlin1.calendar.databinding.FragmentSettingsBinding
import com.posse.kotlin1.calendar.utils.*
import com.posse.kotlin1.calendar.utils.LocaleUtils.Companion.LOCALE_EN
import com.posse.kotlin1.calendar.utils.LocaleUtils.Companion.LOCALE_RU
import com.posse.kotlin1.calendar.common.presentation.ActivityRefresher
import com.posse.kotlin1.calendar.common.presentation.utils.Account
import com.posse.kotlin1.calendar.common.presentation.utils.AccountState
import com.posse.kotlin1.calendar.view.settings.blackList.BlackListFragment
import com.posse.kotlin1.calendar.view.settings.share.ShareFragment
import com.posse.kotlin1.calendar.view.update.UpdateDialog
import com.posse.kotlin1.calendar.viewModel.SettingsViewModel
import com.squareup.picasso.Picasso
import dagger.Lazy
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class SettingsFragment : Fragment() {
    @Inject
    lateinit var localeUtils: LocaleUtils

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var account: Account

    @Inject
    lateinit var viewModelFactory: Lazy<ViewModelProvider.Factory>

    @Inject
    lateinit var picasso: Picasso
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val animator = Animator()
    private var isInitCompleted = false
    private var isLoginPressed = false
    private var isEditMode = false
    private val keyboard = Keyboard()
    private var activityRefresher: ActivityRefresher? = null
    private val viewModel: SettingsViewModel by lazy {
        viewModelFactory.get().create(SettingsViewModel::class.java)
    }
    private val startLogin: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        account.setAuthResult(it) {
            UpdateDialog.newInstance().show(childFragmentManager, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidSupportInjection.inject(this)
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
        account.getLiveData().observe(viewLifecycleOwner) { renderSettings(it) }
        viewModel.getLastTheme().observe(viewLifecycleOwner) {
            if (isInitCompleted) requireActivity().recreate()
            else isInitCompleted = true
        }
        account.getAccountState()
        setupLoginButton()
        setupLogoutButton()
        setupNicknameField()
        setupBlackList()
        setupThemeSwitch()
        setupEditNicknameButton()
        setupLocaleSwitch()
        keyboard.setGlobalListener(activity?.window?.decorView?.rootView)
        keyboard.setListener { binding.nickName.editText?.clearFocus() }
    }

    private fun setupLocaleSwitch() {
        when (localeUtils.getStringLocale()) {
            LOCALE_RU -> binding.languageChips.check(R.id.chipRu)
            LOCALE_EN -> binding.languageChips.check(R.id.chipEn)
        }
        binding.languageChips.setOnCheckedChangeListener { _, checkedId ->
            val stringLocale = when (checkedId) {
                R.id.chipEn -> LOCALE_EN
                R.id.chipRu -> LOCALE_RU
                else -> LOCALE_EN
            }
            localeUtils.setAppLocale(stringLocale)
            activityRefresher?.refreshNavBar()
        }
    }

    private fun setupEditNicknameButton() = with(binding) {
        btnEditNickname.setOnClickListener { view ->
            if (isEditMode) {
                val nickname = nickName.editText?.text.toString()
                if (!nickname.contains(" ") && nickname.isNotEmpty())
                    animator.animate(view) {
                        viewModel.saveNickname(account.getEmail()!!, nickname) { saved ->
                            when (saved) {
                                SettingsViewModel.Nickname.Empty -> {
                                    nickName.error = getString(R.string.no_internet)
                                }
                                SettingsViewModel.Nickname.Busy -> {
                                    nickName.error = getString(R.string.nickname_is_busy)
                                }
                                SettingsViewModel.Nickname.Error -> {
                                    UpdateDialog.newInstance().show(childFragmentManager, null)
                                }
                                SettingsViewModel.Nickname.Saved -> {
                                    (view as AppCompatImageButton).setImageDrawable(
                                        getDrawable(requireContext(), R.drawable.shotglass_empty)
                                    )
                                    view.drawable.setTint(
                                        MaterialColors.getColor(
                                            requireContext(),
                                            R.attr.strokeColor,
                                            "Should set color attribute first"
                                        )
                                    )
                                    keyboard.hide(view)
                                    nickName.disable()
                                    nickName.error = null
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
                    nickName.enable()
                    nickName.editText?.let {
                        it.setSelection(it.length())
                    }
                    keyboard.show(activity)
                    nickName.error = null
                    isEditMode = true
                }
            }
        }
    }

    private fun setupNicknameField() = with(binding) {
        nickName.editText?.doOnTextChanged { text, _, _, _ ->
            if (text?.contains(" ") == true) nickName.error = getString(R.string.remove_space)
            else nickName.error = null
        }
        nickName.editText?.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                btnEditNickname.performClick()
                textView.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun setupShareFragment() {
        childFragmentManager
            .beginTransaction()
            .replace(R.id.shareFragmentContainer, ShareFragment.newInstance())
            .commit()
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

    private fun setupThemeSwitch() = with(binding) {
        val themeSwitch = sharedPreferences.themeSwitch
        if (Build.VERSION.SDK_INT >= NIGHT_THEME_SDK) {
            if (themeSwitch) sharedPreferences.lightTheme =
                context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO
            chipDay.isEnabled = !themeSwitch
            chipNight.isEnabled = !themeSwitch
            switchTheme.isChecked = themeSwitch
            switchTheme.setOnCheckedChangeListener { _, isChecked ->
                chipDay.isEnabled = !isChecked
                chipNight.isEnabled = !isChecked
                viewModel.switchState = isChecked
                if (isChecked) {
                    when (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_NO -> binding.themeChips.check(ThemeUtils.THEME.DAY.resID)
                        Configuration.UI_MODE_NIGHT_YES -> binding.themeChips.check(ThemeUtils.THEME.NIGHT.resID)
                    }
                }
            }
        } else {
            switchTheme.disappear()
            chipDay.isEnabled = true
            chipNight.isEnabled = true
        }

        if (sharedPreferences.lightTheme) themeChips.check(ThemeUtils.THEME.DAY.resID)
        else themeChips.check(ThemeUtils.THEME.NIGHT.resID)

        themeChips.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                ThemeUtils.THEME.DAY.resID -> viewModel.lightTheme = true
                ThemeUtils.THEME.NIGHT.resID -> viewModel.lightTheme = false
            }
        }
    }

    private fun renderSettings(accountState: AccountState) = with(binding) {
        @DrawableRes val defaultDrawableResource =
            R.drawable.common_google_signin_btn_icon_light_normal
        val defaultPicture = getDrawable(
            requireContext(),
            defaultDrawableResource
        )
        when (accountState) {
            is AccountState.LoggedIn -> {
                btnEditNickname.show()
                loginButton.disappear()
                logoutButton.show()
                nickName.show()
                userEmail.putText(accountState.userEmail)
                nickName.editText?.setText(accountState.nickname)
                picasso
                    .load(accountState.userPicture)
                    .placeholder(defaultDrawableResource)
                    .resize(
                        defaultPicture?.intrinsicWidth ?: 0,
                        defaultPicture?.intrinsicHeight ?: 0
                    )
                    .error(defaultDrawableResource)
                    .into(userLogo)
                if (isLoginPressed) motionSettings.transitionToEnd()
                else motionSettings.progress = 1f
            }
            is AccountState.LoggedOut -> {
                btnEditNickname.disappear()
                loginButton.show()
                logoutButton.disappear()
                nickName.disappear()
                userEmail.putText(getString(R.string.login_to_sync))
                userLogo.setImageDrawable(defaultPicture)
                if (isLoginPressed) motionSettings.transitionToStart()
                else motionSettings.progress = 0f
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityRefresher = context as ActivityRefresher
    }

    override fun onDetach() {
        activityRefresher = null
        super.onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboard.hide(binding.root)
        _binding = null
        keyboard.setListener(null)
        keyboard.removeGlobalListener(activity?.window?.decorView?.rootView)
    }

    companion object {
        const val NIGHT_THEME_SDK = 29

        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}