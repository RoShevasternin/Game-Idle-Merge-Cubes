package com.lewydo.rozval.game.utils.camera

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.input.GestureDetector
import com.lewydo.rozval.game.utils.scaledToWorld

class CameraGestureListener(
    private val cameraWorld: OrthographicCamera,
    private val cameraDebug: OrthographicCamera,
) : GestureDetector.GestureAdapter() {

    private var initialZoom = 1f

    // 🔥 Дефолтні стани
    private val defaultWorldState = CameraState(
        cameraWorld.position.x,
        cameraWorld.position.y,
        cameraWorld.zoom
    )

    override fun pan(x: Float, y: Float, deltaX: Float, deltaY: Float): Boolean {
        // Розраховуємо зміщення для World камери (UI одиниці)
        val moveX = -deltaX * cameraWorld.zoom
        val moveY =  deltaY * cameraWorld.zoom

        // Рухаємо основну камеру
        cameraWorld.translate(moveX, moveY)

        // Рухаємо дебаг камеру, масштабуючи UI значення у фізичні метри
        cameraDebug.translate(moveX.scaledToWorld, moveY.scaledToWorld)

        return false
    }

    override fun zoom(initialDistance: Float, distance: Float): Boolean {
        // Обчислюємо новий коефіцієнт зуму
        val ratio = initialDistance / distance
        val targetZoom = initialZoom * ratio

        // Встановлюємо однаковий зум для обох камер
        cameraWorld.zoom = targetZoom
        cameraDebug.zoom = targetZoom

        return false
    }

    override fun pinchStop() {
        // Фіксуємо поточний зум як початковий для наступного жесту зумування
        initialZoom = cameraWorld.zoom
    }

    fun reset() {
        cameraWorld.position.set(
            defaultWorldState.x,
            defaultWorldState.y,
            0f
        )
        cameraWorld.zoom = defaultWorldState.zoom

        cameraDebug.position.set(
            defaultWorldState.x.scaledToWorld,
            defaultWorldState.y.scaledToWorld,
            0f
        )
        cameraDebug.zoom = defaultWorldState.zoom

        initialZoom = defaultWorldState.zoom
    }

    data class CameraState(
        val x: Float,
        val y: Float,
        val zoom: Float
    )
}