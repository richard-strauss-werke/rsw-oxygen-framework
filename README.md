Richard Strauss: Werke. Kritische Ausgabe - oXygen TEI / MEI framework and extensions
=======

The RSW oXygen framework makes extensive use of form controls in oXygen's Author Mode. In order to simplify the management of the framework's user interface (for which oXygen uses an extended version of CSS), the RSW framework's CSS code is generated from SCSS files. SCSS acts as an abstraction layer, allowing to reduce the amount of code-repetition and make the code more semantically meaningful. Parts of the SCSS code (most of the value-label pairs used in the combo box form controls) are generated by an XSLT from the project's RNG schemata. This way, manual duplication of attribute content lists from the RNG / ODD files in CSS can be avoided. 

Status
------

In development / migration. The UI has only been tested in oXygen 15.0 on Windows 7 - obtaining a clean look with other setups most probably requires changes to the SCSS constants in `/frameworks/RSW/scss`.

Directory structure
-------------------

The Java author extensions are organized as a [Maven project](http://maven.apache.org/). The extensions' Java artifacts are stored to `/frameworks/RSW/lib` when you perform a new Java build. The configuration files of the framework are not part of the Maven project structure, but are stored in `/frameworks/RSW`. The can be both read and written from oXygen.   

Setup for development
---------------------

- install Ant
- install Maven
- install the [oXygen 16 SDK](http://www.oxygenxml.com/oxygen_sdk.html) in your local Maven repo
- install the dbTagger plugin in your local Maven repo
- run `ant build` from the project's root directory 
- in oXygen, open the `Options` dialog, select `Document type associations` and add `/path/to/projectdir/frameworks/` to the list of additional framework directories (note: be sure to add the parent folder of the actual framework directory, not the framework directory `/path/to/projectdir/frameworks/RSW/` itself). Restart oXygen to let the changes take effect. 

Commands from the project root directory
--------

- `ant xslt` converts RNG attribute values / descriptions from the schemata in `frameworks/RSW/schema` to SCSS code in `frameworks/RSW/scss/_combo-lists-schema.scss`
- `ant watch` starts the Compass file watcher which converts the SCSS files in `frameworks/RSW/scss` to CSS files in `frameworks/RSW/css` each time a SCSS file changes. When editing the SCSS files, oXygen may be running, allowing you to check the effects of your modifications instantly.
- `ant build`, updates SCSS content read from RNG, converts all SCSS to CSS, updates the Jar files in `frameworks/RWS/lib`. When performing a new Java build, oXygen should be closed.
- `ant dist`, like `ant build`, additionally stores the generated files in an eXist database (for configuration, see the ant property files in the project root folder)

Configuration
-------------

TODO

Java extensions
---------------

TODO

Adding the Java extensions to another oXygen framework
---

TODO

