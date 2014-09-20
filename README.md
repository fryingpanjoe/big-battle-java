big-battle-java
===============

A simple multiplayer game hack written in Java.

Requirements
------------
* Java 7
* LWJGL supported platform

Project setup: Eclipse
----------------------
1. Create a Java project for `big-battle-client`, `big-battle-server`, and
`big-battle-common`.
2. Add `big-battle-client/res` as a source folder to `big-battle-client`.
2. Add `big-battle-server/res` as a source folder to `big-battle-server`.
3. Add `big-battle-common` as a project dependency to `big-battle-client` and `big-battle-server`.
4. Add these JAR dependencies for `big-battle-client`, `big-battle-server`, and
`big-battle-common` on:
  * `thirdparty/commons-io-2.4.jar`
  * `thirdparty/config-1.2.1.jar`
  * `thirdparty/guava-18.0.jar`
  * `thirdparty/hamcrest-core-1.3.jar`
  * `thirdparty/json-20140107.jar`
  * `thirdparty/junit-4.11.jar`
  * `thirdparty/mockito-all-1.9.5.jar`
  * `thirdparty/slick-util.jar`
  * `thirdparty/lwjgl-2.9.1/lwjgl_util.jar`
  * `thirdparty/lwjgl-2.9.1/lwjgl.jar` (set native library location, e.g. `thirdparty/lwjgl-2.9.1/native/windows`)
5. Run `big-battle-server/src/org/fryingpanjoe/bigbattle/server/Main.java`
6. Run `big-battle-client/src/org/fryingpanjoe/bigbattle/client/Main.java`
