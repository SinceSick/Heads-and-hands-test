package com.headsandheadstest

import android.content.Context
import android.os.Bundle
import android.view.View.OnClickListener
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.headsandheadstest.databinding.ActivityMainBinding
import com.headsandheadstest.entities.WeatherMain
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()
    private var binding: ActivityMainBinding? = null

    private val onFieldFocusChangeListener = OnFocusChangeListener { _, _ ->
        showToolbarButton()
    }

    private val onLoginButtonClickListener = OnClickListener {
        binding?.apply {
            val email = mailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (!viewModel.validateLoginData(email, password)) {
                setErrorStates()
                return@apply
            }

            viewModel.login()
            setLoaderVisibility(true)
            hideKeyboard()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        initEmailField()
        initPasswordField()
        initButtons()
        initToolbar()
    }

    override fun onResume() {
        super.onResume()
        initSubscribers()
        viewModel.initScope()
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelScope()
    }

    private fun initToolbar() {
        binding?.toolbar?.setNavigationOnClickListener {
            showCloseAppDialog()
        }
        onBackPressedDispatcher.addCallback {
            showCloseAppDialog()
        }
    }

    private fun showCloseAppDialog() {
        MaterialAlertDialogBuilder(this)
            .setBackground(ContextCompat.getDrawable(this, R.color.white))
            .setTitle(getString(R.string.close_dialog_title))
            .setPositiveButton(getString(R.string.close_dialog_positive)) { _, _ ->
                this.finish()
            }
            .setNegativeButton(getString(R.string.close_dialog_negative)) { _, _ -> }
            .create()
            .show()
    }

    private fun initSubscribers() {
        viewModel.subscribeOnWeather()
            .onEach {
                setLoaderVisibility(false)
                showWeatherSnackBar(it)
            }
            .launchIn(lifecycleScope)

        viewModel.subscribeOnWeatherError()
            .onEach {
                setLoaderVisibility(false)
                showErrorSnackBar()
            }
            .launchIn(lifecycleScope)

    }

    private fun showWeatherSnackBar(weatherMain: WeatherMain) {
        val temperature = weatherMain.temperature.roundToInt().toString()
        val feelsLike = weatherMain.feelsLike.roundToInt().toString()
        val result = getString(R.string.result_weather, temperature, feelsLike)
        binding?.root?.let { view ->
            Snackbar.make(view, result, Snackbar.LENGTH_INDEFINITE)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.main_purple))
                .setActionTextColor(ContextCompat.getColor(this, R.color.white))
                .setTextColor(ContextCompat.getColor(this, R.color.white))
                .setAction(R.string.snack_bar_close) {}
                .show()
        }
    }

    private fun showErrorSnackBar() {
        binding?.root?.let { view ->
            Snackbar.make(view, getString(R.string.error_snack_text), Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(this, R.color.main_red))
                .setActionTextColor(ContextCompat.getColor(this, R.color.white))
                .setTextColor(ContextCompat.getColor(this, R.color.white))
                .show()
        }
    }

    private fun initEmailField() {
        binding?.apply {
            mailEditText.doAfterTextChanged {
                if (it.isNullOrEmpty()) return@doAfterTextChanged
                mailContainer.isErrorEnabled = false
                viewModel.emailErrorStatus = false
            }
            mailEditText.onFocusChangeListener = onFieldFocusChangeListener
            mailContainer.onFocusChangeListener = onFieldFocusChangeListener
        }
    }

    private fun showToolbarButton() {
        binding?.apply {
            toolbarButton.isVisible = mailEditText.isFocused
                    || mailContainer.isFocused
                    || passwordEditText.isFocused
                    || passwordContainer.isFocused
        }
    }

    private fun initPasswordField() {
        binding?.apply {
            passwordContainer.setEndIconOnClickListener {
                showPasswordDialog()
            }
            passwordContainer.setErrorIconOnClickListener {
                showPasswordDialog()
            }
            passwordEditText.doAfterTextChanged {
                if (it.isNullOrEmpty()) return@doAfterTextChanged
                passwordContainer.isErrorEnabled = false
                viewModel.passwordErrorStatus = false
            }

            passwordEditText.onFocusChangeListener = onFieldFocusChangeListener
            passwordContainer.onFocusChangeListener = onFieldFocusChangeListener
        }
    }

    private fun initButtons() {
        binding?.apply {
            loginButton.setOnClickListener(onLoginButtonClickListener)
            toolbarButton.setOnClickListener(onLoginButtonClickListener)
        }
    }

    private fun setLoaderVisibility(value: Boolean) {
        binding?.apply {
            loginButton.isEnabled = !value
            toolbarButton.isEnabled = !value
            loader.isInvisible = !value
        }
    }

    private fun hideKeyboard() {
        this.currentFocus?.let { view ->
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputManager?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun showPasswordDialog() {
        MaterialAlertDialogBuilder(this)
            .setBackground(ContextCompat.getDrawable(this, R.color.white))
            .setTitle(getString(R.string.password_dialog_title))
            .setMessage(getString(R.string.password_dialog_content))
            .setNegativeButton(getString(R.string.password_dialog_button_text)) { _, _ -> }
            .create()
            .show()
    }

    private fun setErrorStates() {
        binding?.apply {
            if (viewModel.emailErrorStatus)
                mailContainer.error = getString(R.string.error_email)
            if (viewModel.passwordErrorStatus)
                passwordContainer.error = getString(R.string.error_password)
        }
    }
}