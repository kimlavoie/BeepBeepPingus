How to use the monitor
======================

The material is in the folder "test".

## Step 1

Create a named pipe in your temp dir by typing

> `mkfifo /tmp/beepbeep.pipe`

## Step 2

Start BeepBeep:

> `java -jar TerminalGui.jar`

Invoke it with `--help` to see the command line options. The "GUI" allows
you to type commands to create/query monitors.

`add *filename*`
: Reads *filename*, instantiates a monitor with the formula and adds it
  to the set of active monitors.
  
`reset all` or `ra`
: Resets all monitors to their initial state

`reset *i*`
: Resets the *i*-th monitor to its initial state

`show monitor status` or `sms`
: Shows the current state of all monitors

`show monitor verdict` or `smv`
: Shows the current verdict of all monitors

`show event count` or `sec`
: Shows the number of events received since the last `reset all`

`start`
: Starts listening to events on the named pipe

`stop`
: Stops listening to events on the named pipe

`notify on events` or `noe`
: Displays a message for every event received

`notify on verdict` or `nov`
: Displays a message only when when a monitor reaches a verdict

`notify on nothing` or `non`
: Disables all notifications

## Step 3

Feed events. Currently the script `feed-events.php` can be used to feed
events fetched from an XML file (`trace.xml`) one by one, writing them to
the named pipe (which must be open on the receiving end). Run it from the
command line:

> `php feed-events.php`

Otherwise you can send events manually from the command line:

> `echo -ne "event contents go here" > /tmp/beepbeep.pipe`
