/*
 * Copyright (c) 2024. See AUTHORS file.
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

package com.mbrlabs.mundus.editorcommons.events;

import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.editorcommons.EventListener;
import com.mbrlabs.mundus.editorcommons.Subscribe;

public class GameObjectModifiedEvent {

    private GameObject gameObject;

    public GameObjectModifiedEvent(final GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public interface GameObjectModifiedListener extends EventListener {

        @Subscribe
        void onGameObjectModified(GameObjectModifiedEvent event);
    }
}
