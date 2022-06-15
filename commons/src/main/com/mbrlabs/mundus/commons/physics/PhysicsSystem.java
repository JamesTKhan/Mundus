package com.mbrlabs.mundus.commons.physics;

import com.badlogic.gdx.utils.Disposable;

/**
 * @author James Pooley
 * @version June 15, 2022
 */
public interface PhysicsSystem extends Disposable {
    void setGravity(float x, float y, float z);
    void setTimeStep(float timeStep);
    void update(float delta);
}
