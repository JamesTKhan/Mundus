package com.mbrlabs.mundus.commons.scene3d.components;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.mbrlabs.mundus.commons.scene3d.GameObject;

public class AnimationComponent extends AbstractComponent {

    private AnimationController animationController;

    public AnimationComponent(GameObject go) {
        super(go);
        type = Type.ANIMATION;

        ModelComponent modelComponent = (ModelComponent) gameObject.findComponentByType(Type.MODEL);
        if (modelComponent == null) {
            throw new IllegalArgumentException("GameObject must have a model component to attach an animation component.");
        }

        if (modelComponent.modelInstance.animations.isEmpty()) {
            throw new IllegalArgumentException("GameObject must have at least one animation to add animation component.");
        }
        animationController = new AnimationController(modelComponent.modelInstance);
        animationController.setAnimation(modelComponent.modelInstance.animations.get(0).id);
    }

    @Override
    public void render(float delta) {

    }

    @Override
    public void update(float delta) {
        animationController.update(delta);
    }

    @Override
    public Component clone(GameObject go) {
        return null;
    }
}
