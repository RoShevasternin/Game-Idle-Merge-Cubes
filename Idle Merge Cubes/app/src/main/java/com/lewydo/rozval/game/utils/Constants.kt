package com.lewydo.rozval.game.utils

import com.badlogic.gdx.math.Vector2
import kotlin.math.PI

const val WIDTH_UI     = 1920f
const val HEIGHT_UI    = 1080f
const val WIDTH_WORLD  = 20f
const val HEIGHT_WORLD = 11.25f

const val METER_UI = WIDTH_UI / WIDTH_WORLD

val Vector2.scaledToWorld get() = this.divOr(METER_UI, 0f) // convert UI to Box2d
val Vector2.scaledToUI    get() = this.scl(METER_UI)       // convert Box2d to UI
val Float.scaledToWorld   get() = this.divOr(METER_UI, 0f) // convert UI to Box2d
val Float.scaledToUI      get() = this * METER_UI          // convert Box2d to UI

const val DEGTORAD = (PI / 180f).toFloat()
const val RADTODEG = (180f / PI).toFloat()

const val JOINT_WIDTH = 1f

const val TIME_ANIM_SCREEN = 0.333f
