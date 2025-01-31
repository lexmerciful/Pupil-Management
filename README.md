<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>

<h1>Pupil Management App</h1>

<h2>Overview</h2>
<p>The Pupil Management App is designed to allow users to efficiently manage a list of pupils with full CRUD (Create, Read, Update, Delete) capabilities. The app ensures seamless operation both online and offline, synchronizing data when network connectivity is restored.</p>

<h2>Features</h2>
<ul>
    <li><strong>View All Pupils</strong>
        <ul>
            <li>Fetches and displays a list of pupils from the server.</li>
        </ul>
    </li>
    <li><strong>Pupil Details</strong>
        <ul>
            <li>Allows users to view detailed information about each pupil.</li>
        </ul>
    </li>
    <li><strong>Add New Pupil</strong>
        <ul>
            <li>Users can add a new pupil by filling out a form.</li>
            <li>If offline, the pupil is added once connectivity is restored.</li>
        </ul>
    </li>
    <li><strong>Edit Pupil</strong>
        <ul>
            <li>Users can edit existing pupil information.</li>
        </ul>
    </li>
    <li><strong>Delete Pupil</strong>
        <ul>
            <li>Provides the ability to delete a pupil from the list.</li>
        </ul>
    </li>
    <li><strong>Offline Access</strong>
        <ul>
            <li>Data is cached locally using Room database.</li>
            <li>All functionalities are available offline.</li>
            <li>Changes made offline are synchronized when back online.</li>
        </ul>
    </li>
    <li><strong>Data Synchronization</strong>
        <ul>
            <li>Utilizes WorkManager to handle synchronization tasks.</li>
            <li>Displays notifications upon successful synchronization.</li>
        </ul>
    </li>
</ul>

<h2>Architecture and Design</h2>
<ul>
    <li><strong>Architecture Pattern</strong>: Model-View-ViewModel (MVVM)
        <ul>
            <li>Separates concerns, making the app more maintainable and testable.</li>
        </ul>
    </li>
    <li><strong>Data Layer</strong>
        <ul>
            <li><strong>Room Database</strong>: For local data caching and offline access.</li>
            <li><strong>Repository Pattern</strong>: Abstracts data sources and provides a clean API.</li>
        </ul>
    </li>
    <li><strong>Network Layer</strong>
        <ul>
            <li><strong>Retrofit</strong>: For handling network requests to the Swagger API.</li>
            <li><strong>OkHttp</strong>: Used with Retrofit for HTTP logging and network interceptors.</li>
            <li><strong>Swagger API</strong>: Provides endpoints for GET, POST, PUT, and DELETE operations.</li>
        </ul>
    </li>
    <li><strong>Dependency Injection</strong>
        <ul>
            <li><strong>Hilt</strong>: Simplifies dependency injection throughout the app.</li>
        </ul>
    </li>
    <li><strong>Background Processing</strong>
        <ul>
            <li><strong>WorkManager</strong>: Manages background tasks, especially for data synchronization.</li>
        </ul>
    </li>
</ul>

<h2>Technologies and Libraries Used</h2>
<ul>
    <li><strong>Kotlin</strong>: Primary programming language.</li>
    <li><strong>Android Jetpack Components</strong>:
        <ul>
            <li><strong>ViewModel</strong>: Manages UI-related data in a lifecycle-conscious way.</li>
            <li><strong>LiveData</strong>: Updates UI with data changes.</li>
            <li><strong>Room</strong>: Database layer for local data persistence.</li>
            <li><strong>WorkManager</strong>: Handles deferrable, guaranteed background work.</li>
        </ul>
    </li>
    <li><strong>Networking</strong>:
        <ul>
            <li><strong>Retrofit</strong>: For making network calls.</li>
            <li><strong>OkHttp Logging Interceptor</strong>: For logging network requests and responses.</li>
        </ul>
    </li>
    <li><strong>Dependency Injection</strong>:
        <ul>
            <li><strong>Hilt</strong>: For managing dependencies.</li>
        </ul>
    </li>
    <li><strong>Material Components</strong>: For implementing Material Design UI components.</li>
</ul>

<h2>Assumptions Made</h2>
<ul>
    <li><strong>Network Reliability</strong>: Users may have intermittent network connectivity and Swagger API may randomly return error to simulate real lide scenarios; therefore, offline support is implemented for fetching and creating new puipil</li>

</ul>

<h2>Implemented Requirements</h2>
<ol>
    <li><strong>View a List of All Pupils</strong>
        <ul>
            <li>Fetches pupils using the <strong>GET</strong> endpoint from the Swagger API.</li>
            <li>Displays pupils in a RecyclerView with support for pagination.</li>
        </ul>
    </li>
    <li><strong>View Details of a Single Pupil</strong>
        <ul>
            <li>Implements a detailed view that shows comprehensive information about a selected pupil.</li>
        </ul>
    </li>
    <li><strong>Add a New Pupil and Submit</strong>
        <ul>
            <li>Users can add new pupils via a form.</li>
            <li>Image selection is enabled through the camera or gallery.</li>
            <li>If offline, the app schedules the addition using WorkManager, syncing when online.</li>
        </ul>
    </li>
    <li><strong>Delete a Pupil</strong>
        <ul>
            <li>Users can delete pupils which are then removed from both local storage and the server upon synchronization.</li>
        </ul>
    </li>
    <li><strong>Offline Continuation</strong>
        <ul>
            <li>All functionalities are available offline.</li>
            <li>Data modifications are stored locally and synchronized using WorkManager when network connectivity is restored.</li>
        </ul>
    </li>
</ol>

<h2>How Offline Access Works</h2>
<ul>
    <li><strong>Local Data Storage</strong>: Implemented using Room to cache data.</li>
    <li><strong>Data Synchronization</strong>:
        <ul>
            <li><strong>WorkManager</strong> handles scheduling and execution of tasks that require network connectivity.</li>
            <li>When online, WorkManager synchronizes the local changes with the server.</li>
        </ul>
    </li>
</ul>

<h2>Notifications</h2>
<p>Upon successful synchronization of data (e.g., adding a new pupil), the app sends a <strong>post notification</strong> to inform the user.</p>

<h2>Design Decisions</h2>
<ul>
    <li><strong>MVVM Architecture</strong>: Chosen for its separation of concerns, facilitating testing and scalability.</li>
    <li><strong>Hilt for Dependency Injection</strong>: Simplifies the codebase by providing automatic dependency management.</li>
    <li><strong>WorkManager for Background Tasks</strong>: Ensures tasks are completed even if the app is closed or the device restarts.</li>
</ul>
