package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.kotcrab.vis.ui.widget.LinkLabel
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.kotcrab.vis.ui.widget.VisTextButton
import com.mbrlabs.mundus.commons.utils.ModelUtils
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.VERSION
import com.mbrlabs.mundus.editor.core.project.ProjectManager

/**
 * @author JamesTKhan
 * @version July 15, 2022
 */
class VersionDialog : BaseDialog("Version Info") {

    private val projectManager: ProjectManager = Mundus.inject()

    private lateinit var root: VisTable
    private var resetOpacityBtn = VisTextButton("Reset Opacity")

    init {
        setupUI()
    }

    private fun setupUI() {
        root = VisTable()
        root.defaults().pad(6f)

        val width = Gdx.graphics.width * 0.3f

        root.add("Welcome to Mundus $VERSION!").row()

        var label = VisLabel()
        label.setText("Mundus is in early development and may be lacking important features and contain bugs. You can submit a bug report " +
                "on Github Issues.")
        label.wrap = true

        root.add(label).expandX().prefWidth(width).padBottom(10f).row()
        root.add(LinkLabel("Issues","https://github.com/JamesTKhan/Mundus/issues")).row()

        label = VisLabel()
        label.setText("v0.4.0 introduced a modified implementation of gdx-gltf's PBR shader for model rendering as well as changes to the " +
                "lighting and fog code. You may need to adjust lighting and fog if you are using a project from " +
                "a prior version.")
        label.wrap = true

        root.add(label).expandX().prefWidth(width).padBottom(10f).row()

        label = VisLabel()
        label.setText("If you are using a project from a previous version of Mundus, models may not be visible. This is due to opacity " +
                "being defaulted to zero in previous versions. To resolve this, set the opacity on materials to 1.0 or press the button below " +
                "to set all materials to 1.0 opacity.")
        label.wrap = true

        root.add(label).expandX().prefWidth(width).padBottom(10f).row()

        root.add(resetOpacityBtn).row()

        label = VisLabel()
        label.setText("This window can be reopened anytime by navigating to Window in the top toolbar and selecting Version Info")
        label.wrap = true

        root.add(label).expandX().prefWidth(width).padBottom(10f).row()

        root.add(LinkLabel("See list of changes","https://github.com/JamesTKhan/Mundus/releases/tag/$VERSION")).row()

        add(root)

        resetOpacityBtn.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                for (asset in projectManager.current().assetManager.materialAssets) {
                    asset.opacity = 1.0f
                    projectManager.current().assetManager.addModifiedAsset(asset!!)
                }

                for (asset in projectManager.current().assetManager.modelAssets) {
                    asset.applyDependencies()
                }

                ModelUtils.applyGameObjectMaterials(projectManager.current().currScene.sceneGraph.root)

                resetOpacityBtn.setText("Done")
                resetOpacityBtn.focusLost()
                resetOpacityBtn.isDisabled = true
            }
        })
    }
}