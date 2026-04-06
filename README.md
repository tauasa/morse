
# **tui-morse**

Simple Morse Code encoder/decoder

To build: `mvn clean install`

# **Usage:**
Encode a string: `java Morse -e "plain text"`
<br/>
Decode a string dits and dahs: `java Morse -d \"... --- ...\"`
<br/><br/>
**Alternatively you can execute using Maven:**
<br/>
Encode a string: `mvn exec:java -Dexec.args="-e \"once upon a time\""`
<br/>
Decode a string: `mvn exec:java -Dexec.args="-d \"... --- ...\""`
