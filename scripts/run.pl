#!/usr/bin/perl
use File::Copy qw/ move /;
@size = qw/ 10 20 50 100 200 /;
$a = "0.1";
for(@size){
  print $_, "\n";
  $k = "${_}k";
  $f = "Syn$k";
  `./GraphGen -ngraphs $_ -size 40 -nnodel 5 -nedgel 1 -density 0.05 -nedges 8 -edger 0.1 -fname $f`;
  mkdir $k;
  move "$f.data", "$k/$f";
  `./gSpan-64 -f "$k/$f" -s $a -o -i`;
  mkdir "$k/$a";
  move "$k/$f.fp", "$k/$a/$a$f";
}

