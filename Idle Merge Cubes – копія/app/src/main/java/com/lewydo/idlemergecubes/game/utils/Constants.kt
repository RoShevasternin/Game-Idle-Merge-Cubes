package com.lewydo.idlemergecubes.game.utils

const val WIDTH_UI  = 2160f
const val HEIGHT_UI = 3840f

const val TIME_ANIM_SCREEN = 0.333f

const val IDLE_CYCLE_SECONDS = 3f

// ------------------------------------------------------------------------
// OFFLINE Idle Income
// ------------------------------------------------------------------------
const val OFFLINE_MIN_SEC   = 10f
const val OFFLINE_MAX_HOURS = 8f
const val OFFLINE_MAX_SEC   = OFFLINE_MAX_HOURS * 3600f

// Ефективність офлайн-доходу — чим більше часу тим менший % від онлайну
// 0.35 = 35% від онлайн-швидкості, щоб 8h не давало овер багато
const val OFFLINE_EFFICIENCY = 0.35f

// ------------------------------------------------------------------------
// REMOVE_ADS_PRICE
// ------------------------------------------------------------------------
const val REMOVE_ADS_PRICE = 1.99f
