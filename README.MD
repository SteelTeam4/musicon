# MusiCon

### What it does
We have developed an app which recognizes the physical activity of the user and plays music on the phone according to the speed with which the person moves. The songs are played from the user's Spotify account, and the activity of the user decides the tempo of the song.

### How we built it
We used a phone's accelerometer to determine the physical movement of the user and calculate his/her speed.There are different phases of activity, namely: Still, Walking slow, Brisk walk, Jogging and Sprint. Using these activities, the app plays the songs from user's Spotify playlists. To get the song information, we use Spotify's API which provides us details about the song's tempo, and we use this value to predict the song that will be played next.

### Challenges we ran into
Initially we planned to get the user activity information using a Fitbit device we had, but after much research found out that we cannot get real time information about the user's physical activity thorough their web APIs. Fitbit exposes the user's information through Web APIs at an interval of 15-20 minutes, which was not helpful for our project. We thought of moving to Pebble, which was provided to us by the organizers, but then decided to move to a phone's accelerometer to get the data about the user's running activity. Also, while working on Spotify API, we came across a few issues of our own as we were not familiar with the API.

### Accomplishments that we're proud of
We were able to come up with a working prototype of our project in a short time, and plan to improve it further.

### What we learned
We spent some time working understanding the APIs and also the Android SDK as only the 2 of us were familiar with Android app development. But as we worked together, we learnt a lot from each other. We got a quite impatient sometimes during the past 24 hours when our code did not work or ran into issues, but overall it was a great learning experience.

### What's next for Musicon
We would definitely try to improve our project and extend it to work with both Android and iOS devices. We plan to improve the activity recognition and the recommendation list of the songs, and also improve the user interface. Watch out for our app on the play store soon!
