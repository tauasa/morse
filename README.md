
# **- ..- .. -- --- .-. ... .**

Simple Morse Code encoder/decoder

To build: `mvn clean install`

**From command line:**
<br/>
Encode an alphanumeric string: `java Morse -e "plain text"` -> `.--. .-.. .- .. -. - . -..- -`
<br/>
Decode a string of dits and dahs: `java Morse -d "... --- ..."` -> `SOS`
<br/><br/>
**Execute using Maven:**
<br/>
Encode a string: `mvn exec:java -Dexec.args="-e \"once upon a time\""`
<br/>
Decode a string: `mvn exec:java -Dexec.args="-d \"... --- ...\""`
<br/><br/>
**Note:** does not support special characters, only alphanumerics
