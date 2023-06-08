/*
 * Copyright (c) 2016. See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mbrlabs.mundus.editor.ui.modules.inspector

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.utils.Align
import com.kotcrab.vis.ui.widget.VisLabel
import com.kotcrab.vis.ui.widget.VisTable
import com.mbrlabs.mundus.editor.Mundus
import com.mbrlabs.mundus.editor.events.AssetSelectedEvent
import com.mbrlabs.mundus.editor.events.ComponentAddedEvent
import com.mbrlabs.mundus.editor.events.GameObjectModifiedEvent
import com.mbrlabs.mundus.editor.events.GameObjectSelectedEvent
import com.mbrlabs.mundus.editor.ui.widgets.AutoFocusScrollPane
import com.mbrlabs.mundus.editor.utils.Log

/**
 * @author Marcus Brummer
 * @version 19-01-2016
 */
class Inspector : VisTable(),
        GameObjectSelectedEvent.GameObjectSelectedListener,
        GameObjectModifiedEvent.GameObjectModifiedListener,
        ComponentAddedEvent.ComponentAddedListener,
        AssetSelectedEvent.AssetSelectedListener {

    companion object {
        private val TAG = Inspector::class.java.simpleName
    }

    enum class InspectorMode {
        GAME_OBJECT, ASSET, EMPTY
    }

    private var mode = InspectorMode.EMPTY
    private val root = VisTable()
    private val scrollPane = AutoFocusScrollPane(root)

    private val goInspector: GameObjectInspector
    private val assetInspector: AssetInspector

    init {
        Mundus.registerEventListener(this)

        goInspector = GameObjectInspector()
        assetInspector = AssetInspector()

        init()
    }

    fun init() {
        setBackground("window-bg")
        add(VisLabel("Inspector")).expandX().fillX().pad(3f).row()
        addSeparator().row()
        root.align(Align.top)
        scrollPane.setScrollingDisabled(true, false)
        scrollPane.setFlickScroll(false)
        scrollPane.setFadeScrollBars(false)

        add<ScrollPane>(scrollPane).expand().fill().top()
    }

    fun clearWidgets() {
        root.clear()
        mode = InspectorMode.EMPTY
    }

    override fun onGameObjectSelected(event: GameObjectSelectedEvent) {
        if (mode != InspectorMode.GAME_OBJECT) {
            mode = InspectorMode.GAME_OBJECT
            root.clear()
            root.add(goInspector).grow().row()
        }
        goInspector.setGameObject(event.gameObject!!)
    }

    override fun onGameObjectModified(event: GameObjectModifiedEvent) {
        goInspector.updateGameObject()
    }

    override fun onAssetSelected(event: AssetSelectedEvent) {
        Log.debug(TAG, event.asset.toString())
        if (mode != InspectorMode.ASSET) {
            mode = InspectorMode.ASSET
            root.clear()
            root.add(assetInspector).grow().row()
        }
        assetInspector.asset = event.asset
    }

    override fun onComponentAdded(event: ComponentAddedEvent) {
        goInspector.addComponent(event.component)
    }

}
