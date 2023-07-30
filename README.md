# 🪡 jNeedle (or Needle)

Yet another jar malware detection tool

---

## Quickstart / How to use

If you came from [MMPA](https://blog.mmpa.info) or already know what this program does, look here to see how to use jNeedle.

### GUI (Graphical) mode

The easiest way to use jNeedle is to use the GUI version.
Just download the `jneedle-gui-xxx.jar` from the [release page](https://github.com/KosmX/jneedle/releases/latest) and double click on it.

_Help, it won't start!_

There currently is a bug with Java 1.8, which won't let the program start. This is known and will be fixed in the future.

### CLI mode

If you want to use the CLI version, you have to download the `jneedle-xxx-fat.jar` from the [release page](https://github.com/KosmX/jneedle/releases/latest) and run it with the following command:

```text
java -jar jneedle-xxx-fat.jar -f <path to jar or directory>
```

JNeedle will then check the given file or directory for malicious code. It gives a summary of the results at the end.

_Help, where do I need to put that command?_  
On Windows, open the file explorer, navigate to the folder where you downloaded the jar file. Hold `Shift` and right click on empty space. Select `Open PowerShell window here` and type the command above.

_Help, it won't start!_  
See the [GUI section](#gui-graphical-mode) for more information.

For more CLI arguments, type the command without `-f`:

```text
> java -jar .\jneedle-1.0.0.jar
Value for option --file should be always provided in command line.
Usage: jNeedle options_list
Options:
    --file, -f -> file or directory (always required) { String }
    --url, -u [https://maven.kosmx.dev/dev/kosmx/needles] { String }
    --dblocation [C:\Users\kosmx\.jneedle] { String }
    --threads [16] { Int }
    --help, -h -> Usage info
```

## How to find the needle in a haystack? - Use a strong enough magnet

The detection tool is parsing jar `.class` files looking for signature instruction sequences.
It is actually similar to string search:

Is the following sequence: `"jump into the well"`
in the program:

```text
exit house and lock door,
get the bus and to the shop to buy milk
jump into the well then get the bus
come home
```

## See the **light** at the end of the tunnel

**Attention! These are advanced options, which you probably won't need!**  
For an easy setup, look at the [Quickstart](#quickstart--how-to-use) section.

### Add to PrismLauncher

### Extra jar // recommended but harder to set-up

1. Check current launch classpath in the Version menu:  
   Select the Fabric/Forge/Quilt/Minecraft (most bottom of these) and on the right menu, click Customize then Edit
2. This will open a text-editor, look for the `mainClass` entry in the class  
   Fabrc for example: `"mainClass": "net.fabricmc.loader.impl.launch.knot.KnotClient",`
3. Save (copy) the entry value: `net.fabricmc.loader.impl.launch.knot.KnotClient`
4. close the editor and optionally click Revert in Prismlauncher
5. click `Add to Minecraft.jar` button and select jneedle.jar
6. Click edit while jneedle.jar is selected
7. Add the following to the json:  
   `"+jvmArgs": ["-Ddev.kosmx.jneedle.launchClass={launchClass}"],` where you replace `{launchClass}` with the earlier saved class.
8. Add the following to the json:  
   `"mainClass": "dev.kosmx.needle.launchWrapper.ParameterizedWrapper",`
9. Save the file and have fun!

---

**The lines for specific launchers:**  
Quilt: `"+jvmArgs": ["-Ddev.kosmx.jneedle.launchClass=org.quiltmc.loader.impl.launch.knot.KnotClient"],`  
Fabric: `"+jvmArgs": ["-Ddev.kosmx.jneedle.launchClass=net.fabricmc.loader.impl.launch.knot.KnotClient"],`  
Forge up to 1.12.2: `"+jvmArgs": ["-Ddev.kosmx.jneedle.launchClass=net.minecraft.launchwrapper.Launch"],`  
Forge from 1.13.2: `"+jvmArgs": ["-Ddev.kosmx.jneedle.launchClass=io.github.zekerzhayard.forgewrapper.installer.Main"],`

### Easy path // slow but easy-to-setup

1. In the game version menu, click `Add agents`:
2. Select jneedle.jar
3. Done. (It will be slow in large modpacks)

## JVM properties

Some parameter can be set as a JVM property:  
`-Ddev.kosmx.jneedle.remoteDatabase` to set online database location.  
`-Ddev.kosmx.jneedle.remoteDatabase=https://maven.kosmx.dev/dev/kosmx/needles` is default.

`-Ddev.kosmx.jneedle.databasePath` to set local database cache.  
`-Ddev.kosmx.jneedle.databasePath=${user.home}/.jneedle` by default.

If using `-cp jneedle.jar dev.kosmx.needle.launchWrapper.ParameterizedWrapper`  
`-Ddev.kosmx.jneedle.launchClass` to set the start class after checking is completed. The application has to be in classpath.  
(This is for Minecraft wrapper use)

## API usage:

<not yet in maven, I'll change that soon>  
dev.kosmx.needle.CheckWrapper object (static class) has API functions:  
These can be used from any JVM interop language (ideally Kotlin)  
First call `CheckWrapper.init()` to initialize database. Optionally database location and file location can be specified

Then the program state is effectively immutable, calling check function is safe from any thread anytime.

`CheckWrapper.checkJar()` to check a single jar file (extension doesn't have to be jar)  
`CheckWrapper.checkPathBlocking()` to check jar file or walk directory. Function will return with results once completed.

`CheckWrapper.checkPath()` with parameters to check path as a coroutine function. It can have feedback while running.  
This function is kotlin only.
