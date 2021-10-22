# Sensor Based Player
## Requirements
1. Using Exoplayer (https://developer.android.com/guide/topics/media/exoplayer), load and play a video file 4 seconds after launch. Video file (http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4). Video file should be loaded over http.
2. Using the user’s location, a change of 10 meters of the current and previous location will reset the video and replay from the start.
3. A shake of the device should pause the video.
4. Using gyroscope events, rotation along the z-axis should be able to control the current time where the video is playing. While rotation along the x-axis should control the volume of the sound.
## Implementation
### Video playback using ExoPlayer
This is quite trivial. I just imported `ExoPlayer` dependency and used its standard components.
One part worth mentioning was that I needed to load the file over `http` (as opposed to `https`). I used `networkSecurityConfig` for that, but ideally, we shouldn't allow clear text loading since it impacts security.
### Reset playback on 10 meters location change
This isn't quite simple as it sounds. There are the following challenges:
 - Often, location position can be inaccurate. There can be sharp jumps when a device is still.
 - Device needs some time to gain the signal. There can be latency because of that.

I implemented several options:

 - Basic location provider which uses standard Android components
 - Improved location provider which uses the library [mad-location-manager](https://github.com/maddevsio/mad-location-manager). This library internally uses standard Android `LocationManager` with sensors along with Kalman filter to increase position accuracy. It had a crash (I made [a pull request](https://github.com/maddevsio/mad-location-manager/pull/112) to fix it), therefore I had to import the library as a local project.
 - Step counter based detector. It's an experimental approach that uses step detector. If we assume that a device is being carried along a straight line and if each step is about 1.39 meters, then it might be correct.

### Shake pausing the video

Implementing shake detection also isn't quite simple. The problem is that the acceleration sensor gives events which represent a short period of time.
A naive approach would be to check if one of the events values exceed some threshold. But this would lead to false-positives.
Instead, we should take into account all the events for some appropriate period of time.
For that I used a library called [Seismic](https://github.com/square/seismic). Internally, it has a counter of acceleration events that occurred during some window of time. If the counter exceeds a threshold, then we have a shake event.

### Control player using gyroscope events

Initially, I thought of using current device's angles in space. For that I created 2 implementations:

 - `AcceleratorMagnetometerRotationDetector`
 - `AcceleratorRotationDetector`

But later I realized that instead of current position we rather need angular velocity along each axis.

For that I implemented `GyroscopeRotationDetector`. It uses the same approach as `Seismic`:

 - There's a window of time for which events are taken into account
 - It calculates mean velocity along each axis periodically

The main question is how to use this data exactly to control the player.

A naive approach would be to just check if the velocity exceeded some threshold and to change the value. This is implemented in `NaiveRotationHandler`. But this would lead to bad user experience, as there's often a backward motion. For instance, a user wanted to increase the volume by inclining the device forward. When the motion is done, the user will move the device to initial position, which will lead to decreasing the volume to its initial value.

A slightly better approach would be to allow some time for backward motion. This is implemented in `DefaultRotationHandler`. But this isn't ideal, since the time might not match the user gesture.

Other possible options:

 - Calculate if an event corresponds to backward motion. For that we might introduce some state which might take into account previous events.
 - Use ML to detect gestures

## TODO

 - Move detector needs to be improved as neither of the implementations gives reliable results.
 - Gyroscope events control needs to be improved too, as it might have false-positives on both backward and forward motion.
 - Dependencies should be managed using some DI tool, such as Dagger, Toothpick or Koin
 - Permission requests should check the result, including "Never ask again"
 - Sensors shouldn't be assumed to be always present. Need to fallback in case if some is missing
 - Landscape orientation might be handled. For now, the app is locked in portrait.
 - Handle no network connection state