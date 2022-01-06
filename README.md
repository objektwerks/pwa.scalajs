PWA Scala.js App
----------------
>Progressive web app ( PWA ) using Scala.js and Http4s.

Install
-------
>jsdom **must** be installed locally - **not** globally!
1. brew install node
2. npm install jsdom

Dev
---
1. sbt
2. project jvm
3. ~reStart
4. open browser to http://127.0.0.1:7878

Test
----
1. sbt clean test

Run
---
1. sbt jvm/run
2. open browser to http://127.0.0.1:7878
