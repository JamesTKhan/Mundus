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
    GameObject gameObject;

    public GameObjectMotionState(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    @Override
    public void getWorldTransform (Matrix4 worldTrans) {
        worldTrans.set(gameObject.getTransform());
    }

    @Override
    public void setWorldTransform (Matrix4 worldTrans) {
        // GameObjects rely on vectors, so we update their vectors and not a matrix.
        worldTrans.getTranslation(tmp);
        worldTrans.getRotation(tmpQuat);
        gameObject.setLocalPosition(tmp.x, tmp.y, tmp.z);
        gameObject.setLocalRotation(tmpQuat.x, tmpQuat.y, tmpQuat.z, tmpQuat.w);
    }
}

