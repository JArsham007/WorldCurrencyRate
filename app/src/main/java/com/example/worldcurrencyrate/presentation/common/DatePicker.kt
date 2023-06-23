package com.example.worldcurrencyrate.presentation.common

import androidx.compose.runtime.*
import com.example.worldcurrencyrate.ui.theme.MainItemBackgroundColor
import com.example.worldcurrencyrate.ui.theme.TextColor
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.MaterialDialogState
import com.vanpra.composematerialdialogs.datetime.date.DatePickerDefaults
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun datePicker(
    initialDate: LocalDate,
    dateDialogState: MaterialDialogState,
    onOkClicked: () -> Unit
): LocalDate {

    var pickedDate by remember {
        mutableStateOf(initialDate)
    }

    MaterialDialog(
        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                onOkClicked()
            }
            negativeButton(text = "Cancel")
        }
    ) {
        datepicker(
            initialDate = initialDate,
            title = "Pick date",
            yearRange = IntRange(2010, LocalDate.now().year),
            colors = DatePickerDefaults.colors(
                headerBackgroundColor = MainItemBackgroundColor,
                headerTextColor = TextColor,
                calendarHeaderTextColor = TextColor,
                dateActiveBackgroundColor = MainItemBackgroundColor,
                dateActiveTextColor = TextColor
            ),
            allowedDateValidator = {
                !it.isAfter(LocalDate.now())
            }
        ) {
            pickedDate = it
        }
    }

    return pickedDate

}