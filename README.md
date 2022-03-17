# Weaver

Extension of [fabric-gametestapi](https://github.com/FabricMC/fabric/tree/1.17/fabric-gametest-api-v1)

# Use this api
GroovyDSL:
```groovy
repositories {
    maven 'https://maven.turtton.net'
}
dependencies {
    implementation 'net.turtton:weaver:$VERSION'
}
```
KotlinDSL
```kotlin
repositories {
    maven("https://maven.turtton.net")
}
dependencies {
    implementation(group = "net.turtton", name = "weaver", version = "$VERSION")
}
```

# Features

## Enables connection to TestServer

You can test with (custom) client.

### Params

- port
default: 25565

- network-compression-threshold
default: -1(no compression)

You can change params like this.
```java
import net.turtton.weaver.TestServerVariables;

public class Example implements ModInitializer {
    @Override
    public void onInitialize() {
        TestServerVariables.setPort(25577);
    }
}
```

## Disables TestCommand

See [this](https://github.com/turtton/Weaver/blob/f229191d00a0fc89ae187a314cd43e5c9c86e7c6/src/main/java/net/turtton/weaver/mixin/MixinTestCommand.java#L15)

## Provides some useful context extensions

### Examples:
Java:
```java
import net.turtton.weaver.TestContextExtensions;
import net.minecraft.test.GameTest;
import net.fabricmc.fabric.api.gametest.v1.FabricGameTest;

public class ExampleTest {
    @GameTest
    public void example(TestContext context) {
        var serverPlayer = TestContextExtensions.createMockServerPlayer(context, new BlockPos(0, 1, 0));
    }
}
```
Kotlin:
```kotlin
import net.turtton.weaver.createMockServerPlayer

@GameTest
fun example(context: TestContext) {
    val serverPlayer = context.createMockServerPlayer()
}
```
Scala:
```scala
import net.turtton.waver.TestContextImplicits._

@GameTest
def example(context: TestContext): Unit = {
 val serverPlayer = context.createMockServerPlayer() 
}
```
> Full function list is [here](https://github.com/turtton/Weaver/blob/main/src/main/java/net/turtton/weaver/TestServerVariables.java)