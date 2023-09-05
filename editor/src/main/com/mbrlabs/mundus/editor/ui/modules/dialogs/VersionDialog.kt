package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.Gdx
import com.kotcrab.vis.ui.widget.LinkLabel
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.VERSION

/**
 * @author JamesTKhan
 * @version July 15, 2022
 */
class VersionDialog : BaseDialog("Version Info") {

    private lateinit var root: VisTable

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
        label.setText("v0.5.0 introduces PBR Terrain rendering along with materials that can be assigned to Terrains. Lighting was" +
                " also overhauled to more closely align with libGDX and gdx-gltf conventions. Many other new features and" +
                " UI improvements have also been made.")
        label.wrap = true

        root.add(label).expandX().prefWidth(width).padBottom(10f).row()

        label = VisLabel()
        label.setText("If you are using a project from a previous version of Mundus, lighting values will be incorrect." +
                " You will need to adjust lighting intensity values within your project.")
        label.wrap = true

        root.add(label).expandX().prefWidth(width).padBottom(10f).row()

        label = VisLabel()
        label.setText("This window can be reopened anytime by navigating to Window in the top toolbar and selecting Version Info")
        label.wrap = true

        root.add(label).expandX().prefWidth(width).padBottom(10f).row()

        root.add(LinkLabel("See list of changes","https://github.com/JamesTKhan/Mundus/releases/tag/$VERSION")).row()

        add(root)
    }
}