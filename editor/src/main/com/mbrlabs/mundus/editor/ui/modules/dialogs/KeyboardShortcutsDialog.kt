package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.scenes.scene2d.Stage
import com.kotcrab.vis.ui.widget.VisDialog
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.keymap.KeymapKey
import com.mbrlabs.mundus.editor.core.keymap.KeyboardShortcutManager

/**
 * @author JamesTKhan
 * @version July 26, 2022
 */
class KeyboardShortcutsDialog : BaseDialog("Keyboard Shortcuts") {

    private val keyboardShortcutManager = Mundus.inject<KeyboardShortcutManager>()
    private lateinit var root: VisTable

    init {
        setupUI()
    }

    private fun setupUI() {
        root = VisTable()
        root.defaults().pad(6f)
        add(root)
    }

    override fun show(stage: Stage?): VisDialog {
        root.clear()

        val shortcutTableOne = VisTable()
        shortcutTableOne.defaults().pad(4f)

        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.MOVE_FORWARD), "Move Forward", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.MOVE_BACK), "Move Back", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.STRAFE_LEFT), "Strafe Left", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.STRAFE_RIGHT), "Strafe Right", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.MOVE_UP), "Move Up", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.MOVE_DOWN), "Move Down", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.OBJECT_SELECTION), "Object Selection", shortcutTableOne)
        addShortcut("Hold " + keyboardShortcutManager.getKeyText(KeymapKey.LOOK_AROUND) + " Click", "Look Around", shortcutTableOne)
        addShortcut("Scroll", "Zoom forward/backward", shortcutTableOne)
        addShortcut("Hold Shift", "Camera Panning", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.FULLSCREEN), "Toggle Fullscreen 3d", shortcutTableOne)

        root.add(shortcutTableOne).top()
        root.addSeparator(true)

        val shortcutTableTwo = VisTable()
        shortcutTableTwo.defaults().pad(4f)

        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.UNDO), "Undo", shortcutTableTwo)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.REDO), "Redo", shortcutTableTwo)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.SAVE_PROJECT), "Save Project", shortcutTableTwo)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.TRANSLATE_TOOL), "Translate Tool", shortcutTableTwo)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.ROTATE_TOOL), "Rotate Tool", shortcutTableTwo)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.SCALE_TOOL), "Scale Tool", shortcutTableTwo)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.SELECT_TOOL), "Select Tool", shortcutTableTwo)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.DEBUG_RENDER_MODE), "Debug Render Mode", shortcutTableTwo)
        addShortcut(keyboardShortcutManager.getKeyText(KeymapKey.WIREFRAME_RENDER_MODE), "Wireframe Mode", shortcutTableTwo)

        root.add(shortcutTableTwo).top()

        return super.show(stage)
    }

    private fun addShortcut(key: String, desc: String, table: VisTable) {
        table.add(key).left()
        table.addSeparator(true)
        table.add(desc).left().row()
        table.addSeparator().colspan(3)
    }
}