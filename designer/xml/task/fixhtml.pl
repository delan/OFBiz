
   $sourcefile = "DEMO2level.html";
   $destinationfile = "$sourcefile.tmp";
   open (SOURCE, "< $sourcefile") or die "could not open $sourcefile to read";
   open (DESTINATION, "> $destinationfile") or die "could not open $destinationfile to write";

   print DESTINATION "<HTML>\n";
   while (<SOURCE>) {
      s/</&lt/g;
      s/>/&gt/g;
      s/^(.*)$/\1<BR> /;

      if (/\s*id="/) {
         print;
      }

      print DESTINATION;
   }

   print DESTINATION "</HTML>\n";

   close DESTINATION;
   close SOURCE;
   system ("copy /y $destinationfile $sourcefile");
   system ("del $destinationfile");

