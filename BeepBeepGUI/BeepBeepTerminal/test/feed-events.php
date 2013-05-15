#! /usr/bin/php
<?php
/*
Reads an XML file and writes the events one by one to the named pipe.
*/
$trace_filename = "trace.xml";
$pipe_name = "/tmp/beepbeep.pipe"; // Create this by doing mkfifo <pipename>
$message_name = "message";

$trace = file_get_contents($trace_filename);
preg_match_all("/<$message_name>.*?<\\/$message_name>/ms", $trace, $matches);
foreach ($matches[0] as $event)
{
  echo "Press a key to send the next event...\n";
  $c = fread(STDIN, 1);
  file_put_contents($pipe_name, $event);
  echo "Sent:\n";
  echo $event;
  echo "\n";
}
?>

