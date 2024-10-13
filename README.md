# Project description
    This project requires us to build an audio streaming application similar
    to Spotify. All the actions are done from the admin perspective.

# Implementation
       All the data used by this application is contained in a database that utilizes the
    Singleton Design Patern. The databse also contains methods to find a specific song,
    user, podcast or playlist.
        All the commands are read by a menu that invokes the right method from the rest
    of the classes.
#### Design Paterns Used:
    * Singleton => for the database
    * Visitor => for page printing and correct wrapped call
    * Observer => for updating notifications of each user
    * Factory => for new users creation
### Search
        Each user has its own search bar that has access to all public audio files and
    artist/host pages. The search bar retains a list of names of the first 5 found 
    resources. It also retains the type of search conducted in a separate variable.
### Select
        The select command populates the selected field with one of the searches and
    copies the search type var into the selected type var.
### Load
        This command looks in all the database for the reasource with the name contained
    by the selected field in the search bar. The loading is done in the music player of 
    each user. Sets the source's owner as being used by someone.
        Artist/host pages are loaded directly by the select command.
### Play/Pause
        This command modifies a field in the music player that is responsible for the
    players ability to update the remaining time of the playing content.
### Status
        It prints the current player status, after updating the music player.
### Create Playlist
        This creates a new empty playlist and adds it to the user. This creation is not possible
    if the user has another playlist with the same name.
### Add/Remove in Playlist
        Returns true if a song was added to a playlist, and false if it was deleted from the
    playlist.
### Like
        This command increments or decremets the number of likes of a song, depending if
    the song is in the user's liked songs.
### Show Playlist
        This command prints a specified playlist.
### Show Preferred Songs
        This command prints all the song names from a user's liked songs list.
### Repeat
        This command changes the field responsible that tells the player how to proceed
    if the remaining time reaches 0.
### Follow
        This command adds a playlist in the user's followed playlist list.
### Get Top 5 Songs
        Prints the names of the 5 songs with the most ammount of likes from the database.
### Get Top 5 Playlists
        Prints the names of the 5 playlists with the most ammount of users in its followed by list.
### Forward
        This command checks if the loaded source of the user's player is a podcast, and
    if it is, decrements the remaining time by 90.
### Backward
        This command checks if the loaded source of the user's player is a podcast, and
    if it is, increments the remaining time by 90. If the remaining time is larger than the
    source's duration, the remaining time is set the the source's duration.
### Next
        This command sets the remianig time to a negative ammount, so the next player update
    passes to the next source.
### Prev
        This command sets the current remianing time to the soruce's duration, or loads the
    previous source if the remaining time is equal to the current source's duration.
### Shuffle 
        This command shuffles the index list that decides which song should be played next.
    Using it again will reorder the previouly mentioned list.
### Switch Connection status
        This command switches the user's current status. The status being set to offline
    blocks some user functionalities, like searching, selecting, loading, liking.
### Get Online Users
        Prints all the users with the status set as online.
### Add User
        This command creates a new user, artist or host, using the Factory Design Dattern for their
    creation; and adds them to the databse if a user with the same name doesn't already exist.
### Add Album
        Creates a new album and adds it to the user's album list only if the user is an artist.
    Each album is created as a playlist extension and the music player uses it like a
    normal playlist.
### Show Albums
        Prints all the albums of the user, if the user is an artist.
### Delete Album
        This command removes an album from a user's album list only if the user is an artist.
### Print Current Page
        This command prints the user's current page. This is by default its home page
    , but it can be modified by changing it or selecting an artist's/host's page.
        The printing for each kind if page is different and a Visitor Design Pattern is
    used to do the correct print method for the current page.
### Add Merch/ Add Event
        This commands creates and adds a new merch or event to an artist's page, if they
    don't exist already.
        Addition event also notifies all subscribed users.
### Remove Merch/ Remove Event
        This commands removes a merch or event from an artist's page, if they
    exist already
### Delete User
        This command removes a user from the database only if possible. This is determined
    by evaluating if the user has any of it's content in use by other users.
### Add Podcast
        This command creates a new podcast and adds it to the user's podcast list only
    if the user is a host.
        It also notifies all subscribed users.
### Show Podcasts
        Prints all the podcasts of the user, if the user is an host.
### Delete Podcast
        This command removes a pocast from a user's pocast list only if the user is a host.
### Add/Remove Announcement
        Adds or removes a new announcement from a host's page, only if user calling it is the
    host that owns the host page.
        Addition also notifies all subscribed users. 
### Change Page
        Changes the user's current page to the specified one, adding it to a page list.
    The current page is kept separetly from the list.
### Get Top 5 Albums
        Returns a list with the 5 albums with the most amount of likes from its containing
    songs.
### Get Top 5 Artists
        Returns a list with the 5 artists with the most likes, that are a sum of all
    likes from all the songs from all its albums.
### Wrapped
        Artists have maps for their played sources with the names of the sources and their
    number of plays. Similarly, users have maps for their statistics. These maps are updated
    during runtime and are printed at the end.
        For the final wrapped printing, a Visitor Design Patern is used to display
    the correct information.
### EndProgram
        Artists generated revenue is added in a separate temporary structure. Each artist that has 
    something played or that has merch bought has its own such structure.
        This structure contains the revenue from songs, merch, a map with songs and their
    profit. Based on this map, the most profitable song is chosen.
### Buy/Cancel Premium
        The buy premium command switches the user's status and starts a song history.
    The profits for each artist that was played since the user's subscription are
    spread either at the end of the program, or when the cancel premium appears.
### Buy/See Merch
        The buy merch adds to the users's bought merch the merch with the specified name
    from the selected artist page, if possible. It increases the artists merch revenue.
        The see merch prints the user's owned merch.
### Subscribe
        It adds the current user to the subscriber list of the artist that owes the
    selected artist page.
### Get Notifications
        Each time something happens that users should be notified of, the user that caused
    the event notifies all correct users to add to their notification list. This is done
    using an observer design pattern.
        When notifications are printed, the list is emptied.
### Next/Previous Page
        The previous page command switches the current page to the previous one from
    the back queue, if possible.
        The next page command switches the current page to the page that was last added
    in the front queue by the back command.
### Update Recommendations
        This command updates current user's player and creates a fan playlist based
    on the given conditions and adds it to the user's recommended playlists.
