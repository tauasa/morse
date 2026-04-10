
# **tui-morse (- ..- .. -- --- .-. ... .)**

Simple Morse Code encoder/decoder

To build: `mvn clean install`
<br/>
Usage: `java Morse <-e|-d> [audioFlag] "Hello World"`

**From command line:**
<br/>
Encode an alphanumeric string: `java Morse -e -a "plain text"` -> `.--. .-.. .- .. -. - . -..- -`
<br/>
Decode a string of dits and dahs: `java Morse -d -a "... --- ..."` -> `SOS`
<br/><br/>
**Execute using Maven:**
<br/>
Encode a string with audio: `mvn exec:java -Dexec.args="-e -a \"once upon a time\""`
<br/>
Decode a string without audio: `mvn exec:java -Dexec.args="-d \"... --- ...\""`
<br/><br/>
