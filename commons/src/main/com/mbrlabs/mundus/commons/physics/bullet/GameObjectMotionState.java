/*
 * Copyright (c) 2022. See AUTHORS file.
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

package com.mbrlabs.mundus.commons.physics.bullet;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
/**
 * @author James Pooley
 * @version June 15, 2022
 */
public class GameObjectMotionState extends btMotionState {
    private static final Vector3 tmp = new Vector3();
    private static final Quaternion tmpQuat = new Quaternion();
    private static final Matrix4 tmpMat = new Matrix4();

    GameObject gameObject;

    public GameObjectMotionState(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(gameObject.getPosition(tmp), gameObject.getRotation(tmpQuat));
    }

    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
        // GameObjects rely on vectors, so we update their vectors and not a matrix.
        Matrix4 worldToLocal = tmpMat.set(worldTrans).mulLeft(gameObject.getParent().getTransform().inv());
        worldToLocal.getTranslation(tmp);
        worldToLocal.getRotation(tmpQuat, true);

        gameObject.setLocalPosition(tmp.x, tmp.y, tmp.z);
        gameObject.setLocalRotation(tmpQuat.x, tmpQuat.y, tmpQuat.z, tmpQuat.w);
    }
}

