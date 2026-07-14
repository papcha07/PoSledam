package com.example.alinaposledam.notification

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import db.notification.NotificationDao
import db.notification.NotificationDatabase
import db.notification.NotificationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)
@SmallTest
class NotificationDatabaseTest {

    private lateinit var notifcationDataBase: NotificationDatabase
    private lateinit var dao: NotificationDao

    @Before
    fun setUp() {
        notifcationDataBase = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = NotificationDatabase::class.java
        ).build()
        dao = notifcationDataBase.notificationDao()
    }

    @After
    fun tearDown() {
        notifcationDataBase.close()
    }


    @Test
    fun insertNotificationItem() = runTest {
        val item = NotificationEntity(
            id = 9,
            title = "Добро пожаловать",
            body = "asdas",
            timestamp = 199L,
            isRead = false,
            type = 0,
            announcementId = "adsdasd"
        )

        dao.insert(item)
        val result = dao.getAllNotificationEntity().first()
        assertThat(result).contains(item)

    }

    @Test
    fun deleteNotificationItem() = runTest {


        val item = NotificationEntity(
            id = 9,
            title = "Добро пожаловать",
            body = "asdas",
            timestamp = 199L,
            isRead = false,
            type = 0,
            announcementId = "adsdasd"
        )

        val item2 = NotificationEntity(
            id = 91,
            title = "Добро пожаловать",
            body = "asdas",
            timestamp = 199L,
            isRead = false,
            type = 0,
            announcementId = "adsdasd"
        )

        dao.insert(item)
        dao.insert(item2)

        val result = dao.getAllNotificationEntity().first()
        assertThat(result).contains(item2)
        assertThat(result).contains(item)


        dao.deleteById(91)

        val newResult = dao.getAllNotificationEntity().first()
        assertThat(newResult).doesNotContain(item2)

    }


    @Test
    fun checkIsReadMarker() = runTest {
        val item = NotificationEntity(
            id = 91,
            title = "Добро пожаловать",
            body = "asdas",
            timestamp = 199L,
            isRead = false,
            type = 0,
            announcementId = "adsdasd"
        )

        dao.insert(item)
        dao.markIsRead(91)
        val isReadValue = dao.getAllNotificationEntity().first().first()
        assertThat(isReadValue.isRead).isTrue()
    }
}
