package com.example.alinaposledam.profileTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick

class ProfilePage(
    private val rule: ComposeTestRule
) {
    private fun missButton() = rule.onNodeWithTag("tab_Пропажи")
    private fun findButton() = rule.onNodeWithTag("tab_Найденные")
    private fun methodTabRow() = rule.onNodeWithTag("tab_row")

    private fun animalList() = rule.onNodeWithTag("animal_list")
    private fun emptyList() = rule.onNodeWithTag("empty_list")
    private fun progressBar() = rule.onNodeWithTag("progress_bar")

    fun clickMissButton() = apply {
        missButton().performClick()
    }

    fun clickFindButton() = apply {
        findButton().performClick()
    }

    fun missButtonIsSelected() = apply {
        findButton().assertIsNotSelected()
        missButton().assertIsSelected()
    }

    fun findButtonIsSelected() = apply {
        missButton().assertIsNotSelected()
        findButton().assertIsSelected()
    }

    fun animalListIsDisplayed() = apply{
        animalList().assertIsDisplayed()
    }

    fun emptyListIsDisplayed() = apply{
        emptyList().assertIsDisplayed()
    }

    fun progressBarIsDisplayed() = apply {
        progressBar().assertIsDisplayed()
    }



}
