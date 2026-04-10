package com.example.alinaposledam.actionTest

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight

class ActionPage(private val rule: ComposeTestRule) {

    private fun imageButton() = rule.onNodeWithTag("image_button", useUnmergedTree = true)
    private fun placeholder() = rule.onNodeWithTag("placeholder_image", useUnmergedTree = true)
    private fun selected() = rule.onNodeWithTag("selected_image", useUnmergedTree = true)

    private fun tabLost() = rule.onNodeWithTag("tab_Потерялся")
    private fun tabFound() = rule.onNodeWithTag("tab_Нашелся")

    private fun addressComponent() = rule.onNodeWithTag("address_component")
    private fun mainComponent() = rule.onNodeWithTag("main_component")

    private fun backButton() = rule.onNodeWithTag("back_button")
    private fun methodTabRow() = rule.onNodeWithTag("method_tab_row")

    private fun pager() = rule.onNodeWithTag("pager")
    private fun addPhotoPage() = rule.onNodeWithTag("add_photo_page")
    private fun photoPage(page: Int) = rule.onNodeWithTag("photo_page_$page")

    private fun progressBar() = rule.onNodeWithTag("progress_bar")
    private fun notification() = rule.onNodeWithTag("notification")

    fun notificationIsDisplayed() = apply {
        notification().assertIsDisplayed()
    }

    fun progressBarIsDisplayed() = apply {
        progressBar().assertIsDisplayed()
    }

    fun notificationIsHidden() = apply {
        notification().assertIsNotDisplayed()
    }

    fun progressBarIsHidden() = apply {
        progressBar().assertIsNotSelected()
    }


    private val createButton = rule.onNode(
        hasTestTag("create_button")
    )

    private val petDog = rule.onNode(
        hasTestTag("pet_1")
    )
    private val petCat = rule.onNode(
        hasTestTag("pet_0")
    )
    private val petOther = rule.onNode(
        hasTestTag("pet_2")
    )

    private val maleButton = rule.onNode(
        hasTestTag("gender_0")
    )
    private val femaleButton = rule.onNode(
        hasTestTag("gender_1")
    )

    private val addPhotoButton = rule.onNode(
        hasTestTag("add_photo_button")
    )

    private val horizontalPager = rule.onNode(
        hasTestTag("pager")
    )

    fun selectMale() = apply {
        maleButton.performScrollTo()
        maleButton.performClick()
    }

    fun selectFemale() = apply {
        femaleButton.performScrollTo()
        femaleButton.performClick()
    }


    fun checkMaleSelected() = apply {
        maleButton.assertIsSelected()
        femaleButton.assertIsNotSelected()
    }

    fun checkFemaleSelected() = apply {
        maleButton.assertIsNotSelected()
        femaleButton.assertIsSelected()
    }


    fun selectDog() = apply {
        petDog.performClick()
    }

    fun selectCat() = apply {
        petCat.performClick()
    }

    fun selectOther() = apply {
        petOther.performClick()
    }

    fun checkDogSelected() = apply {
        petDog.assertIsSelected()
        petCat.assertIsNotSelected()
        petOther.assertIsNotSelected()
    }

    fun checkCatSelected() = apply {
        petCat.assertIsSelected()
        petDog.assertIsNotSelected()
        petOther.assertIsNotSelected()
    }

    fun checkOtherSelected() = apply {
        petOther.assertIsSelected()
        petDog.assertIsNotSelected()
        petCat.assertIsNotSelected()
    }


    fun addressComponentIsDisplayed() = apply {
        addressComponent().assertIsDisplayed()
    }


    fun mainComponentIsDisplayed() = apply {
        mainComponent().assertIsDisplayed()
    }

    fun clickLost() = apply {
        tabLost().performClick()
    }

    fun clickFound() = apply {
        tabFound().performClick()
    }

    fun checkLostSelected() = apply {
        tabLost().assertIsSelected()
        tabFound().assertIsNotSelected()
    }

    fun checkFoundSelected() = apply {
        tabFound().assertIsSelected()
        tabLost().assertIsNotSelected()
    }

    fun clickImageButton() = apply { imageButton().performClick() }

    fun checkPlaceholderShown() = apply {
        placeholder().assertExists().assertIsDisplayed()
        selected().assertDoesNotExist()
    }

    fun checkSelectedImageShown() = apply {
        selected().assertExists().assertIsDisplayed()
        placeholder().assertDoesNotExist()
    }


    fun nextButtonClick() = apply {
        val node = rule.onNodeWithTag("next_button")
        node.performScrollTo()
        node.performClick()
        rule.waitForIdle()
    }


    fun backButtonClick() = apply {
        backButton().performClick()
    }

    fun tabRowIsDisplayed() {
        methodTabRow().assertIsDisplayed()
    }

    fun tabRowIsNotDisplayed() {
        methodTabRow().assertDoesNotExist()
    }

    private fun scrollToCreateButton() = apply {
        createButton.performScrollTo()
        rule.waitForIdle()
    }


    fun createButtonIsDisabled() = apply {
        createButton.assertExists()
        createButton.assertIsNotEnabled()
    }

    fun createButtonIsEnable() = apply {
        createButton.assertExists()
        createButton.assertIsEnabled()
    }

    fun addPhotoButtonIsVisible() = apply {
        addPhotoPage().assertIsDisplayed()
    }

    fun photoIsVisible(page: Int) = apply {
        photoPage(page).assertIsDisplayed()
    }

    fun swipePagerLeft() {
        pager().performTouchInput {
            swipeLeft()
        }
    }

    fun swipePagerRight() {
        pager().performTouchInput {
            swipeRight()
        }
    }


}
