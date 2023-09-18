package com.posse.kotlin1.calendar.feature_calendar.utils

import dev.icerock.moko.resources.StringResource
import resources.MR

enum class Months(val nameResource: StringResource) {
    January(nameResource = MR.strings.january),
    February(nameResource = MR.strings.february),
    March(nameResource = MR.strings.march),
    April(nameResource = MR.strings.april),
    May(nameResource = MR.strings.may),
    June(nameResource = MR.strings.june),
    July(nameResource = MR.strings.july),
    August(nameResource = MR.strings.august),
    September(nameResource = MR.strings.september),
    October(nameResource = MR.strings.october),
    November(nameResource = MR.strings.november),
    December(nameResource = MR.strings.december)
}

enum class Days(val nameResource: StringResource) {
    Monday(nameResource = MR.strings.monday),
    Tuesday(nameResource = MR.strings.tuesday),
    Wednesday(nameResource = MR.strings.wednesday),
    Thursday(nameResource = MR.strings.thursday),
    Friday(nameResource = MR.strings.friday),
    Saturday(nameResource = MR.strings.saturday),
    Sunday(nameResource = MR.strings.sunday)
}