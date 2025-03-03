package com.mbrlabs.mundus.editor.ui.modules.dialogs

import com.badlogic.gdx.Input
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.core.keymap.KeymapKey
import com.mbrlabs.mundus.editor.core.keymap.KeyboardShortcutManager
import com.mbrlabs.mundus.editor.utils.ButtonUtils

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

        val shortcutTableOne = VisTable()
        shortcutTableOne.defaults().pad(4f)

        addShortcut(keyboardShortcutManager.getKey(KeymapKey.MOVE_FORWARD), "Move Forward", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKey(KeymapKey.MOVE_BACK), "Move Back", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKey(KeymapKey.STRAFE_LEFT), "Strafe Left", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKey(KeymapKey.STRAFE_RIGHT), "Strafe Right", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKey(KeymapKey.MOVE_UP), "Move Up", shortcutTableOne)
        addShortcut(keyboardShortcutManager.getKey(KeymapKey.MOVE_DOWN), "Move Down", shortcutTableOne)
        addShortcut("Hold " + ButtonUtils.buttonToString(keyboardShortcutManager.getKey(KeymapKey.LOOK_AROUND)) + " Click",
            "Look Around", shortcutTableOne)
        addShortcut("Scroll", "Zoom forward/backward", shortcutTableOne)
        addShortcut("Hold Shift", "Camera Panning", shortcutTableOne)
        addShortcut("F8", "Toggle Fullscreen 3d", shortcutTableOne)

        root.add(shortcutTableOne).top()
        root.addSeparator(true)

        val shortcutTableTwo = VisTable()
        shortcutTableTwo.defaults().pad(4f)

        addShortcut("CTRL+Z", "Undo", shortcutTableTwo)
        addShortcut("CTRL+Y", "Redo", shortcutTableTwo)
        addShortcut("CTRL+S", "Save Project", shortcutTableTwo)
        addShortcut("CTRL+T", "Translate Tool", shortcutTableTwo)
        addShortcut("CTRL+R", "Rotate Tool", shortcutTableTwo)
        addShortcut("CTRL+G", "Scale Tool", shortcutTableTwo)
        addShortcut("CTRL+F", "Select Tool", shortcutTableTwo)
        addShortcut("CTRL+F2", "Debug Render Mode", shortcutTableTwo)
        addShortcut("CTRL+F3", "Wireframe Mode", shortcutTableTwo)

        root.add(shortcutTableTwo).top()
        add(root)
    }

    private fun addShortcut(key: String, desc: String, table: VisTable) {
        table.add(key).left()
        table.addSeparator(true)
        table.add(desc).left().row()
        table.addSeparator().colspan(3)
    }

    private fun addShortcut(keycode: Int, desc: String, table: VisTable) {
        table.add(Input.Keys.toString(keycode)).left()
        table.addSeparator(true)
        table.add(desc).left().row()
        table.addSeparator().colspan(3)
    }
}