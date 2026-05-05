package com.example.alinaposledam.actionTest

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.getKoin
import ui.screen.ActionScreen
import ui.viewModel.ActionViewModel
import java.time.LocalDate
import java.time.LocalTime

@RunWith(AndroidJUnit4::class)
class ActionScenarioUiTest {

    @get:Rule
    val rule = createComposeRule()

    private lateinit var vm: ActionViewModel
    private lateinit var page: ActionPage

    @Before
    fun setUp() {
        vm = getKoin().get()

        rule.setContent {
            ActionScreen(
                viewModel = vm,
                onProfilePage = {}
            )
        }

        page = ActionPage(rule)
    }

    @Test
    fun selectLostPetButton() {
        page.clickLost()
        page.checkLostSelected()
    }


    @Test
    fun selectFoundPetButton() {
        page.clickFound()
        page.checkFoundSelected()
    }

    @Test
    fun mainComponentShow() {
        page.mainComponentIsDisplayed()
    }






    @Test
    fun tabRowIsDisplayedWhenMainComponent() {
        page.mainComponentIsDisplayed()
        page.tabRowIsDisplayed()
    }





    @Test
    fun selectDogButton() {
        page.selectDog()
        page.checkDogSelected()
    }

    @Test
    fun selectDogAndSelectOther() {
        page.selectDog()
        page.selectOther()
        page.checkOtherSelected()
    }

    @Test
    fun selectCatAndSelectDog() {
        page.selectCat()
        page.checkCatSelected()
        page.selectDog()
        page.checkDogSelected()
    }

    @Test
    fun selectCatChangePageAndRemove() {
        page.selectCat()
        page.checkCatSelected()
        page.nextButtonClick()
        page.backButtonClick()
        page.checkCatSelected()
    }

    @Test
    fun selectMaleButton() {
        page.selectMale()
        page.checkMaleSelected()
    }

    @Test
    fun selectFemaleButton() {
        page.selectFemale()
        page.checkFemaleSelected()
    }

    @Test
    fun selectMaleAfterFemale() {
        page.selectMale()
        page.checkMaleSelected()
        page.selectFemale()
        page.checkFemaleSelected()
    }

    @Test
    fun selectMaleChangePageAndBack() {
        page.selectMale()
        page.checkMaleSelected()
        page.nextButtonClick()
        page.backButtonClick()
        page.checkMaleSelected()
    }



    private fun fillValidFormDirectly(vm: ActionViewModel) = apply {
        rule.runOnIdle {
            vm.updateName("Бобик")
            vm.updateBreed("Дворняга")
            vm.updateColor("Рыжий")
            vm.updateDescription("Есть ошейник")
            vm.updateTypeOfPet(1)
            vm.updateGender(0)
            vm.addImage("content://test".toUri())
            vm.updateSelectedDate(LocalDate.now())
            vm.updateSelectedTime(LocalTime.of(12, 0))
            vm.updateLatitude(
                latitude = 37.2323
            )
            vm.updateLongitude(
                long = 98.2323
            )
        }
        rule.waitForIdle()
    }


    @Test
    fun firstInitAddPhotoVisible() {
        page.addPhotoButtonIsVisible()
    }

    @Test
    fun addPhoto() {
        page.addPhotoButtonIsVisible()
        vm.addImage("content://com.android.providers.media.documents/document/image%3A1000000030".toUri())
        page.photoIsVisible(0)
    }

    @Test
    fun addPhotoAndSwipeToButtonPage() {
        page.addPhotoButtonIsVisible()
        rule.runOnIdle {
            vm.addImage("content://com.android.providers.media.documents/document/image%3A1000000030".toUri())
        }
        page.photoIsVisible(0)
        page.swipePagerLeft()
        page.addPhotoButtonIsVisible()
    }

    @Test
    fun addTwoPhotoAndSwipeToButtonPageAndBack() {
        page.addPhotoButtonIsVisible()
        rule.runOnIdle {
            vm.addImage("content://com.android.providers.media.documents/document/image%3A1000000030".toUri())
        }
        page.photoIsVisible(0)
        page.swipePagerLeft()
        page.addPhotoButtonIsVisible()
        rule.runOnIdle {
            vm.addImage("content://com.android.providers.media.documents/document/image%3A1000000030".toUri())
        }
        page.photoIsVisible(1)
        page.swipePagerLeft()
        page.addPhotoButtonIsVisible()
        page.swipePagerRight()
        page.swipePagerRight()
        page.photoIsVisible(0)

    }

}



