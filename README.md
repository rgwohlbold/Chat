# Chat
Java End-to-End chat client

This application allows to connect to another client. It can both take the role of a server or a client, depending on whether it connects or is conneted to.

The classes are:   Main, the main class with the EventListeners
                   Connecter implementing Runnable, used to start a new Thread to connect to another client
                   Receiver implementing Runnable, used to listen for incoming connections+
                   Window extending JFrame, a simple class to create a standard, non-visible, flowLayout, non-resizable window
