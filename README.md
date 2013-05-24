sequitur
========

Implementation of the Sequitur compression scheme (extended by runlength
encoding).

See the wikipedia entry for a description of sequitur:
* english: http://en.wikipedia.org/wiki/Sequitur_algorithm
* german: http://de.wikipedia.org/wiki/Sequitur

This implementation is fully in Java, and very flexible, e.g. in the data types
that are to be compressed. It can be single bytes or integers, but also more
complex objects, as long as they implement the hashCode() and equals() methods.

Because of this flexibility the implementation is not very fast, because the
intrinsic data types have to be wrapped into an object (Byte, Integer, ...).

The interface is kept quite simple: For producing a sequitur grammar, just
instantiate an OutputSequence, and append new objects to it.  
For reading it back, open the InputSequence, and use the iterator to process
its content.

For simple file compression, this library also includes a byte-based
SequiturInputStream and SequiturOutputStream, which extend the standard
In/OutputStreams.

License
-------

Sequitur is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Sequitur is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

A copy of the GNU General Public License is distributed along with
Sequitur. You can also see http://www.gnu.org/licenses/.

