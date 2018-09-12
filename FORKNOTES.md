# Fork Notes

This is a fork of Timber, primarily to support a native build. This should be api compatible with the 5.0.0-SNAPSHOT build
from the fork, but obviously may drift in the future (because it's a SNAPSHOT).

## Changes

There are some minor internal changes to the common build.

### No List

The original config had a list that could be mutated and an array that consuming methods would access. The modification 
methods would synchronize on the list, then produce and update the array reference. Java reference writes are atomic, so
this is safe for the JDK. However, for Kotlin/Native, even if you could force Timber to be non-frozen, you'd run the 
risk of crashes due to reference counting mechanics.

The alternative is to keep a reference in "NativeBox" which on the JDK does nothing special, but on native uses an 
AtomicReference on the array, and allows modifications.

'synchronized2' maps to 'synchronized' in the jdk, but on native explicitly uses a mutex.

## Native

The current native implementation is iOS only, but should be split up to support other native implementations. To do that
we'd need to use a posix implementation of the mutex, but it should otherwise be fine.

The iOS logging implementation is using NSLog. I didn't see anything in the interop for the newer unified logging, so 
we'll need more work for that to be enabled. TBH, I didn't look very long. I was mainly focused on the Timber architecture 
side of things.

From a performance perspective, I'm a little concerned that the actual log calls need to access the array in the AtomicReference,
as well as the shared list of trees. Due to the nature of how that works, they're all doing some thread synchronization. Should do a
deeper dive into native and performance to see if that's really worth worrying about. I played with an alternative init structure
whereby you provide a lambda to Timber which returns the array of trees. That is kept thread local, and avoids all atomic references 
between threads, but can't be changed once inited. Again, not sure if that's worth pursuing (probably not).

## JS

Had a JS build issue. Commented it out.

## Dependencies

Published to https://dl.bintray.com/touchlabpublic/kotlin

Fork's version is 5.0.0-kn0.9-a2. Kotlin native is changing a lot, so easier to reflect that in the string.

```
android "com.jakewharton.timber:timber-android:5.0.0-kn0.9-a2"
common "com.jakewharton.timber:timber-common:5.0.0-kn0.9-a2"
jdk "com.jakewharton.timber:timber-jdk:5.0.0-kn0.9-a2"
native "com.jakewharton.timber:timberNative:5.0.0-kn0.9-a2"
```
