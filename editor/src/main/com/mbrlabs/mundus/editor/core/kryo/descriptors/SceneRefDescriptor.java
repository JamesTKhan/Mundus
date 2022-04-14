package com.mbrlabs.mundus.editor.core.kryo.descriptors;

import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer.Tag;

/**
 * This class holds reference to Scene names for saving. Originally, scenes were being saved as List<String> in
 * ProjectDescriptor but there seems to be a strange issue occurring with Kryo when serializing and deserializing the
 * list the strings come back as empty. Using List<SceneRefDescriptor> is a workaround for that problem.
 */
public class SceneRefDescriptor {

    @Tag(0)
    private String name;

    public SceneRefDescriptor() {
    }

    public SceneRefDescriptor(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}