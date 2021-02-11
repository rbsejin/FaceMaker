package com.example.facemaker

import java.io.Serializable

data class RepeatCycle(val dayOfWeeks: BooleanArray, var repeatWeek: Int) : Serializable
