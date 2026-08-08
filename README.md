# AssertJ Swing

This project provides a simple and intuitive API for functional testing of Swing user interfaces, resulting in tests that are compact, easy to write, and read like a specification. Tests written using AssertJ Swing are also robust.

AssertJ Swing simulates actual user gestures at the operating system level, ensuring that the application will behave correctly in front of the user. It also provides a reliable mechanism for GUI component lookup that ensures that changes in the GUI's layout or look-and-feel will not break your tests.

# About this fork

This repository is a personal fork of [assertj/assertj-swing](https://github.com/assertj/assertj-swing), maintained by [ingokegel](https://github.com/ingokegel). It is not published to Maven Central, but you can get the artifacts via [JitPack](https://jitpack.io).

## Changes since upstream was abandoned

- Added a new `assertj-swing-junit-jupiter` module for JUnit 5+
- Track new AssertJ core, TestNG and JUnit versions
- Replaced fest-mock with Mockito
- Replaced jarjar-maven-plugin with maven-shade-plugin, added the Maven wrapper, and modernized the Maven setup
- Added xvfb support for headless test runs
- Added the ability to choose the monitor for testing via the `TEST_DISPLAY` environment variable
- Removed flaky and obsolete tests, reruns of unstable tests, and cleaned up dependencies
- Keep Java 8 compatibility and added support for modern JDKs
- Fixed `waitForIdle` hangs caused by orphaned EventQueues after heavyweight popup interactions on modern JDKs
- Fixed recognition of disposed windows that were made displayable again

## Getting the dependency via JitPack

Add the JitPack repository:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then add the module you need, for example:

```xml
<dependency>
    <groupId>com.github.ingokegel.assertj-swing</groupId>
    <artifactId>assertj-swing-junit</artifactId>
    <version>TAG</version>
</dependency>
```

Available modules: `assertj-swing`, `assertj-swing-junit`, `assertj-swing-junit-jupiter`, `assertj-swing-testng`.

For Gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.ingokegel.assertj-swing:assertj-swing-junit:TAG'
}
```

Replace `TAG` with any tag from this repository.

# Building

Use the Maven wrapper:

```
./mvnw clean install
```

To skip the tests:

```
./mvnw clean install -DskipTests
```

On Linux, tests run headless via xvfb. You can set the `TEST_DISPLAY` environment variable to choose the monitor for interactive test runs.
