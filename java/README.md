
package structure follows

* no.ntnu.osnap.social				'social' related packages
|---no.ntnu.osnap.social.sender		Sender demo application package
|---no.ntnu.osnap.social.receiver	Receiver demo application package

BUILDING INSTRUCTIONS:

The no.ntnu.osnap.social package contains one Java file (Util.java)
Import it in your IDE of choice as an Android library project.
It must reference both json-simple and opensocial jar libraries (found in /java/libs)

social.sender and social.receiver packages have one Jave file each,
import them in your IDE of choice as an Android project and make
it import the library project.

The Sender app will send a 'Person' object to the Receiver app
which will display the person's name.
