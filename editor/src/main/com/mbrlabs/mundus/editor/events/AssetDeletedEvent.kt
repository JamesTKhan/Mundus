package com.mbrlabs.mundus.editor.events

import com.mbrlabs.mundus.editorcommons.Subscribe

/**
 * @author JamesTKhan
 * @version July 28, 2022
 */
class AssetDeletedEvent  {

    interface AssetDeletedListener {
        @Subscribe
        fun onAssetDeleted(event: AssetDeletedEvent)
    }

}