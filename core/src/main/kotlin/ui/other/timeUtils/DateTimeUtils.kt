package ui.other.timeUtils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)

object DateTimeUtils {

    fun getUtcFromDevice(): String {
        return Instant.now().toString()
    }

    fun getCurrentDeviceUiDate(): String {
        val formatter = SimpleDateFormat(
            "dd/MM '•' HH:mm",
            Locale.getDefault()
        )
        return formatter.format(Date())
    }

}