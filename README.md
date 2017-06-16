# Chat
Java End-to-End chat client

This application allows to connect to another client. It can both take the role of a server or a client, depending on whether it connects or is conneted to.

<ul>
The classes are
<li>Main, the main class with the EventListeners</li>             
<li>Connecter implementing Runnable, used to start a new Thread to connect to another client</li>
<li>Receiver implementing Runnable, used to listen for incoming connections</li>
<li>Window extending JFrame, a simple class to create a standard, non-visible, flowLayout, non-resizable window</li>
</ul>
