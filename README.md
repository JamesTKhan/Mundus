<img alt="" src="logo.svg" height="80px" />

# Mundus ![Java CI workflow](https://github.com/JamesTKhan/Mundus/actions/workflows/gradle.yml/badge.svg)
Mundus is a platform independent 3D world editor, built with Java, Kotlin and LibGDX + VisUI. You can
create your scenes in Mundus and then use the Mundus runtime to render and interact with
those scenes in a libGDX project. See [runtime usage](https://github.com/JamesTKhan/Mundus/wiki/Runtime-usage)
for an example. You can see current milestones for
releases on the [milestones](https://github.com/JamesTKhan/Mundus/milestones) page.

![Screenshot](https://user-images.githubusercontent.com/28971753/194986389-aff7af15-c04e-4639-97c6-a1c5b185bf15.png)

This project is at a very early stage in development and APIs may be missing basic features. Create an issue
if you notice something important that is missing.

## Current features
Below are some of the more interesting features, that already work

- Creation of multiple terrains
- Height map loader for terrains
- Procedural terrain generation
- Texture splatting for the terrain texture (max 5 textures per terrain)
- A complete terrain editing system with texture & height brushes
- 4 different brushes (brush form can be an arbitrary image, like in Blender or Gimp)
- 3 brush modes for each brush: Raise/Lower, Flatten & texture paint
- Custom implementation of [gdx-gltf](https://github.com/mgsx-dev/gdx-gltf) PBR shader for model rendering
- Water quads with reflections, refractions, foam, and customizable ripples
- Static Skyboxes with support for multiple skyboxes
- Loading of g3db/gltf/glb files. GLTF is the recommended format.
- Loading of obj/fbx/dae files (note, that the [fbx-conv](https://github.com/libgdx/fbx-conv) binary must be set in the settings)
- Visual translation, rotation tool, and scaling tools 
- Multiple scenes in one project
- Full screen preview mode
- A component based scene graph (not fully implemented yet)
- Basic export of the project into a json format + asset (not needed for libGDX runtime)
- Undo/Redo system for most operations
- Highly accurate game object picking system, based on id color coding & offscreen framebuffer rendering.
  Basic concept: http://www.opengl-tutorial.org/miscellaneous/clicking-on-objects/picking-with-an-opengl-hack/

### Latest Release Video

[![Release video](https://img.youtube.com/vi/e7g5q4I1gdM/0.jpg)](https://www.youtube.com/watch?v=e7g5q4I1gdM)

## Discord Channel

If you need help with Mundus you can check out the Mundus discord [here](https://discord.gg/7MGT9JbnJX) 
or alternatively in the libGDX discord go to **libraries/Mundus** thread under the **SPECIFIC TOPICS** section.

## Things to consider
- Mundus is constantly changing. Especially the internal representation of save files. At this stage of the project backwards compatability
is kept between minor changes, and I keep backwards compatability in mind for major version changes, but it is not guaranteed as the APIs are still
being developed.
- Depending on your keyboard layout some key shortcuts might be twisted (especially CTRL+Z and CTRL+Y for QWERTZ and QWERTY layouts) 
because of the default GLFW keycode mapping. You can change the layout mapping in the settings dialog under Window -> Settings.

## Runtime
The only runtime being developed is for libGDX, which is included in this repository. See [runtime usage](https://github.com/JamesTKhan/Mundus/wiki/Runtime-usage)
for an example.
Runtimes for other engines/frameworks are not planned in the near futures.

## Made with Mundus

Listed from newest to oldest:

### [ShotGun Wedding](https://antzgames.itch.io/shotgun-wedding)

Created by: [AntzGames](https://github.com/antzGames)

![gif](https://user-images.githubusercontent.com/10563814/231760688-38814007-9a6d-41a7-9ed7-296de8b2d6d9.gif)

---

### [Santa is Coming](https://wjamesfl.itch.io/santa-is-coming)

Created by: [JamesTKhan](https://github.com/JamesTKhan)
![santa](https://user-images.githubusercontent.com/10563814/231762608-5427fd2b-4368-43e2-93ef-52ef484bc473.PNG)


### [Raid on Bungeling Bay](https://antzgames.itch.io/raid3d)

Created by: [AntzGames](https://github.com/antzGames)
![image](https://user-images.githubusercontent.com/28971753/194956176-6964931c-ac80-43a0-9049-6d5abfe94be8.png)

---

### [28 Years Later](https://antzgames.itch.io/28-years-later)

Created by: [AntzGames](https://github.com/antzGames)
![image](https://user-images.githubusercontent.com/28971753/194956847-91da8ad3-eb63-42c3-b965-1317963c930f.png)

---

### [The Time Cruise](https://wjamesfl.itch.io/the-time-cruise)

Created by: [JamesTKhan](https://github.com/JamesTKhan)
![image](https://user-images.githubusercontent.com/28971753/194957213-7d83a201-9f68-46f1-a01a-f6b724ac5f11.png)


## Contributing
Contributions are greatly appreciated. To make the process as easy as possible please follow the [Contribution Guide](https://github.com/JamesTKhan/Mundus/wiki/Contributing).
To get an overview over the project you might also want to check out the [Project overview & architecture](https://github.com/JamesTKhan/Mundus/wiki/Project-overview-&-architecture) article.
I label issues that are good for first time contributes as "good first issue."

## Working from source
See this [wiki article](https://github.com/JamesTKhan/Mundus/wiki/Working-from-source).

## Mundus origin
This started as a fork of [Mundus](https://github.com/mbrlabs/Mundus). Mundus is no longer developed so this standalone repository was created. Special thanks to mbrlabs
and the original contributors of Mundus for all the work they did.

Mundus was licensed under the Apache-2.0 license.

## Credits
Logo design: [Franziska Böhm / noxmoon.de](http://noxmoon.de) ([CC-BY-SA 4.0](https://creativecommons.org/licenses/by-sa/4.0/)) 
