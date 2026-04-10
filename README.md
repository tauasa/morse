
# **morse (-- --- .-. ... .)**

Simple Morse Code encoder/decoder in java. This doesn't produce CW but can be used over GMRS

To build: `mvn clean install`
<br/><br/>
Usage: `java Morse <-e|-d> [-a] "Hello World"`
<br/><br/>
Supports characters `A-Z`, `0-9` and special characters `. , ? \ ! / & : ; - _ " $ @ = +`

**From command line:**
<br/>
Encode an alphanumeric string: `java Morse -e -a "plain text"` -> `.--. .-.. .- .. -. - . -..- -`
<br/>
Decode a string of dits and dahs: `java Morse -d -a "... --- ..."` -> `SOS`

**Execute using Maven:**
<br/>
Encode a string with audio: `mvn exec:java -Dexec.args="-e -a \"once upon a time\""`
<br/>
Decode a string without audio: `mvn exec:java -Dexec.args="-d \"... --- ...\""`
<br/><br/>
