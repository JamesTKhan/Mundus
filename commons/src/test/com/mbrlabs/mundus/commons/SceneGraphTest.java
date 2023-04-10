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

package com.mbrlabs.mundus.commons;

import com.badlogic.gdx.utils.Array;
import com.mbrlabs.mundus.commons.env.MundusEnvironment;
import com.mbrlabs.mundus.commons.scene3d.GameObject;
import com.mbrlabs.mundus.commons.scene3d.InvalidComponentException;
import com.mbrlabs.mundus.commons.scene3d.Node;
import com.mbrlabs.mundus.commons.scene3d.SceneGraph;
import com.mbrlabs.mundus.commons.scene3d.SimpleNode;

import com.mbrlabs.mundus.commons.scene3d.components.Component;
import com.mbrlabs.mundus.commons.scene3d.components.ModelComponent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Marcus Brummer
 * @version 21-06-2016
 */
public class SceneGraphTest {

    @Test
    public void basicParenting() {
        Node<SimpleNode> root = new SimpleNode<>(0);
        Assert.assertNull(root.getChildren());

        SimpleNode<SimpleNode> c0 = new SimpleNode<>(1);
        root.addChild(c0);
        Assert.assertEquals(1, root.getChildren().size);
        Assert.assertSame(root.getChildren().first(), c0);
        Assert.assertSame(root, c0.getParent());

        SimpleNode<SimpleNode> c1 = new SimpleNode<>(2);
        root.addChild(c1);
        Assert.assertEquals(2, root.getChildren().size);
        Assert.assertSame(root, c0.getParent());

        c0.remove();
        Assert.assertEquals(1, root.getChildren().size);
        Assert.assertNull(c0.getParent());
    }

    @Test
    public void gameObjectFindByName() {
        SceneGraph sceneGraph = new SceneGraph(null);

        // The GO to search for
        String searchName = "SearchName";
        GameObject searchObject = new GameObject(sceneGraph, searchName, -1);

        // Parent GO
        GameObject parent = new GameObject(sceneGraph, "Parent", -1);
        parent.addChild(searchObject);

        sceneGraph.addGameObject(parent);

        GameObject result = sceneGraph.findByName(searchName);
        Assert.assertNotNull(result);
        Assert.assertEquals(searchName, result.name);
    }

    @Test
    public void gameObjectsFindByName() {
        SceneGraph sceneGraph = new SceneGraph(null);

        // The GO's to search for
        String searchName = "SearchName";
        GameObject searchObject = new GameObject(sceneGraph, searchName, -1);
        GameObject searchObjectTwo = new GameObject(sceneGraph, searchName, -1);

        // Parent GO
        GameObject parent = new GameObject(sceneGraph, "Parent", -1);
        parent.addChild(searchObject);
        parent.addChild(searchObjectTwo);

        sceneGraph.addGameObject(parent);

        Array<GameObject> result = sceneGraph.findAllByName(searchName);
        Assert.assertEquals(2, result.size);
        Assert.assertEquals(searchObject, result.get(0));
        Assert.assertEquals(searchObjectTwo, result.get(1));
    }

    @Test
    public void gameObjectsHierarchyFindByName() {
        SceneGraph sceneGraph = new SceneGraph(null);

        // The GO's to search for
        String searchName = "SearchName";

        int expectedResult = 0;

        // Parent GO
        GameObject parent = new GameObject(sceneGraph, "Parent", -1);
        GameObject currentParent = parent;

        // Create a hierarchy of Parent->Child GO's to search through
        for (int i = 0; i < 4; i++) {
            GameObject searchObject = new GameObject(sceneGraph, searchName, -1);
            GameObject nonSearchObject = new GameObject(sceneGraph, "OtherObject", -1);
            currentParent.addChild(searchObject);
            currentParent.addChild(nonSearchObject);

            // each iteration, current child becomes next parent
            // variate which one is parent to complicate scenegraph more
            currentParent = i % 2 == 0 ? searchObject : nonSearchObject;
            expectedResult++;
        }

        int parentCount = 0;
        // Add the top level parent multiple times just add complexity to scenegraph
        for (int i = 0; i < 4; i++) {
            sceneGraph.addGameObject(parent);
            parentCount++;
        }

        Array<GameObject> result = sceneGraph.findAllByName(searchName);
        Assert.assertEquals(expectedResult * parentCount, result.size);
    }

    @Test
    public void gameObjectsFindByComponent() throws InvalidComponentException {
        Scene mock = Mockito.mock(Scene.class);
        mock.environment = new MundusEnvironment();

        SceneGraph sceneGraph = new SceneGraph(null);
        sceneGraph.scene = mock;

        // The GO's to search for
        String searchName = "SearchName";
        GameObject searchObject = new GameObject(sceneGraph, searchName, -1);
        GameObject searchObjectTwo = new GameObject(sceneGraph, searchName, -1);

        searchObject.addComponent(new ModelComponent(searchObject));
        searchObjectTwo.addComponent(new ModelComponent(searchObjectTwo));

        // Parent GO
        GameObject parent = new GameObject(sceneGraph, "Parent", -1);
        parent.addChild(searchObject);
        parent.addChild(searchObjectTwo);

        sceneGraph.addGameObject(parent);

        Array<GameObject> result = sceneGraph.findAllByComponent(Component.Type.MODEL);
        Assert.assertEquals(2, result.size);
        Assert.assertEquals(searchObject, result.get(0));
        Assert.assertEquals(searchObjectTwo, result.get(1));
    }

    @Test
    public void gameObjectFindByTag() {
        SceneGraph sceneGraph = new SceneGraph(null);

        // The GO to search for
        String searchTag = "SearchTag";
        GameObject searchObject = new GameObject(sceneGraph, "search", -1);
        searchObject.addTag(searchTag);

        // Parent GO
        GameObject parent = new GameObject(sceneGraph, "Parent", -1);
        parent.addChild(searchObject);

        sceneGraph.addGameObject(parent);

        Array<GameObject> result = sceneGraph.findAllByTag(searchTag);
        Assert.assertEquals(1, result.size);
        Assert.assertEquals(searchTag, result.first().getTags().first());
    }

}
