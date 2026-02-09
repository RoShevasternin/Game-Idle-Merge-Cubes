package com.lewydo.rozval.game.actors

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.lewydo.rozval.game.screens.LoaderScreen
import com.lewydo.rozval.game.utils.GameColor
import com.lewydo.rozval.game.utils.Layout
import com.lewydo.rozval.game.utils.actor.addActors
import com.lewydo.rozval.game.utils.actor.setBounds
import com.lewydo.rozval.game.utils.advanced.AdvancedGroup
import com.lewydo.rozval.game.utils.font.FontParameter
import com.lewydo.rozval.game.utils.gdxGame

class AMainLoader(override val screen: LoaderScreen): AdvancedGroup() {

    private val parameter = FontParameter().setCharacters(FontParameter.CharType.ALL).setSize(66)
    private val font      = screen.fontGenerator_LondrinaSolid_Regular.generateFont(parameter)

    val loaderImg        = Image(gdxGame.assetsLoader.loader)
    val loadingLbl       = Label("Loading", Label.LabelStyle(font, GameColor.white))
    val loadingPointsLbl = Label("", Label.LabelStyle(font, GameColor.white))
    val progressLbl      = Label("", Label.LabelStyle(font, GameColor.white))
    val builderImg       = Image(gdxGame.assetsLoader.builderList.random())


    override fun addActorsOnGroup() {
        addLoader()
        addLoading()
        addProgress()
        addBuilder()
    }

    // Actors ------------------------------------------------------------------------

    private fun addLoader() {
        addActor(loaderImg)
        loaderImg.apply {
            setBounds(Layout.Loader.loader)
            setOrigin(Align.center)
            addAction(Actions.forever(Actions.rotateBy(-360f, 5f, Interpolation.linear)))
        }
    }

    private fun addLoading() {
        addActors(loadingLbl, loadingPointsLbl)
        loadingLbl.setBounds(Layout.Loader.loading)

        loadingPointsLbl.apply {
            setBounds(Layout.Loader.loadingPoints)

            val time = 0.23f
            addAction(
                Actions.forever(
                    Actions.sequence(
                Actions.run { setText("") },
                Actions.delay(time),
                Actions.run { setText(".") },
                Actions.delay(time),
                Actions.run { setText("..") },
                Actions.delay(time),
                Actions.run { setText("...") },
                Actions.delay(time),
                Actions.run { setText("..") },
                Actions.delay(time),
                Actions.run { setText(".") },
                Actions.delay(time),
            )))
        }
    }

    private fun addProgress() {
        addActor(progressLbl)
        progressLbl.apply {
            setBounds(Layout.Loader.progress)
            setAlignment(Align.center)
        }
    }

    private fun addBuilder() {
        addActor(builderImg)
        builderImg.setBounds(Layout.Loader.builder)
    }

}