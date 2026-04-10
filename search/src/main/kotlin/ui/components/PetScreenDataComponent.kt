package ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import domain.models.Creator
import domain.models.FoundPetInfo
import domain.models.PetInfo

@Composable
fun PetScreenDataComponent(
    modifier: Modifier = Modifier,
    petInfo: FoundPetInfo
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row {

            }
        }
    }
}



@Preview
@Composable
private fun PetScreenDataComponentPreview() {
//    PetScreenDataComponent(
//        petInfo = FoundPetInfo(
//            street = "Бограда",
//            house = "45",
//            district = "Октябрьский",
//            imagePath = "asdasdasd",
//            creator = Creator(
//                id = "dasd23e24asdawd23e",
//                firstName = "asd",
//                secondName = "asd",
//                patronymic = "asd",
//                avatarPath = "dasd"
//            ),
//            petInfo = PetInfo(
//                petType = 0,
//                gender = 0,
//                color = "Розовый",
//                breed = "Лабрадор",
//                description = "Нашел собаку, видимо кабель"
//            ),
//            lon = 45.23,
//            lat = 23.23,
//        )
//    )
}