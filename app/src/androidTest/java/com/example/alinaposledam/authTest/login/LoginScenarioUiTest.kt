package com.example.alinaposledam.authTest.login

import androidx.compose.ui.test.junit4.createComposeRule
import domain.model.LoginInfo
import org.junit.Rule
import org.junit.Test
import ui.login.LoginScreen
import ui.model.state.AuthScreenState

class LoginScenarioUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    private fun launchLoginScreen(
        state: AuthScreenState = AuthScreenState.Idle,
        onLogin: (LoginInfo) -> Unit = {}
    ) {
        composeRule.setContent {
            LoginScreen(
                state = state,
                onLogin = onLogin
            )
        }
    }

    @Test
    fun allComponents_isDisplayed() {
        launchLoginScreen()
        LoginPage(composeRule)
            .emailFieldIsDisplayed()
            .passwordFieldIsDisplayed()
            .loginButtonIsDisplayed()
    }


    @Test
    fun loginButton_isDisabled_whenFieldsAreEmpty() {
        launchLoginScreen()
        LoginPage(composeRule)
            .loginButtonIsDisabled()

    }

    @Test
    fun loginButton_isEnabled_whenFieldsFilled() {
        launchLoginScreen()
        LoginPage(composeRule)
            .setEmailText()
            .setPasswordText()
            .loginButtonIsEnabled()
    }

    @Test
    fun progressBar_isDisplayed_whenStateIsLoading() {
        launchLoginScreen(
            state = AuthScreenState.Loading
        )
        LoginPage(composeRule)
            .progressBarIsDisplayed()
    }


    @Test
    fun errorToast_isDisplayed() {
        launchLoginScreen(state = AuthScreenState.Error("Что-то пошло не так"))
        LoginPage(composeRule)
            .errorToastIsDisplayed()

    }


}