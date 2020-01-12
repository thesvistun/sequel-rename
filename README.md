# PhotoRename
I started to use Plex to watch movies stored on my PC from another devices in my home network. I encountered a problem of unreadable movies by Plex due to their file names with ...sXXeXX... structure.
  
For the purpose this program was created.
It can rename movies according to regex you specify.
It also can move files out of their parent directories when every series placed in their own one.
Just point to path to the movies and regex with single group.
<pre><code>usage: java -jar SequelRename.jar [OPTION]... &lt;PATH>
Options:
-dr,--dry-run      Just output how rename will occur
-h,--help          Print help message
-op,--out-parent   Move sequel out of its parent dir.
-r,--regex &lt;arg>   Java patterned regex according with https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html. Define only one group. File will be renamed with content of the group</code></pre>
