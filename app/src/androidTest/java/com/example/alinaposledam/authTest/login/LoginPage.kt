package com.example.alinaposledam.authTest.login

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput

class LoginPage(private val testRule: ComposeTestRule) {


    private fun loginButton() = testRule.onNodeWithTag(LOGIN_BUTTON)
    private fun emailField() = testRule.onNodeWithTag(EMAIL_FIELD)
    private fun passwordField() = testRule.onNodeWithTag(PASSWORD_FIELD)
    private fun errorToast() = testRule.onNodeWithTag(TOAST)
    private fun progressBar() = testRule.onNodeWithTag(PROGRESS_BAR)


    fun emailFieldIsDisplayed() = apply {
        emailField().assertIsDisplayed()
    }

    fun passwordFieldIsDisplayed() = apply {
        passwordField().assertIsDisplayed()
    }

    fun loginButtonIsDisplayed() = apply {
        loginButton().assertIsDisplayed()
    }


    fun setEmailText() = apply {
        emailField().performTextInput("test@gmail.com")
    }

    fun setPasswordText() = apply {
        passwordField().performTextInput("test")
    }

    fun loginButtonIsDisabled() = apply {
        loginButton().assertIsNotEnabled()
    }

    fun loginButtonIsEnabled() = apply {
        loginButton()
            .assertHasClickAction()
            .assertIsEnabled()
    }

    fun clickLoginButton() = apply {
        loginButton().performClick()
    }

    fun progressBarIsDisplayed() = apply {
        progressBar().assertIsDisplayed()
    }

    fun errorToastIsDisplayed() = apply {
        errorToast().assertIsDisplayed()
    }


    companion object {
        const val PROGRESS_BAR = "login_loading"
        const val LOGIN_BUTTON = "login_button"
        const val EMAIL_FIELD = "0_field"
        const val PASSWORD_FIELD = "1_field"
        const val TOAST = "toast_message"
    }
}