#!/bin/tcsh -f

echo $SHELL
ps -p $$
# How to do stuff in tcsh shell -- compare with bash_syntax for bash equivalents
# 1: Using Variables
# 2: Command Line Arguments aka Positional Parameters
# 3: Command Error Codes
# 4: Quoting
# 5: Conditional Statements - If, case
# 6: String Comparisons
# 7: Number Comparisons
# 8: Logical Operators 
# 9: File Comparisons 
# 10: Looping
# 11: Functions

echo " "
echo tcsh Syntax Guide
echo 1: Using Variables ---------------

# Spaces allowed around the = sign
set var1 = 0
set var2 = text
set var3 = 'text with spaces'
set var4 = "text with spaces"
set var5 = text\ with\ spaces

echo set var1 = $var1
echo set var2 = $var2
echo set var3 = "'$var3'"
echo set var4 = \"$var4\"
echo set var5 = text\\ with\\ spaces -- $var5

echo " "
echo 2: Command Line Arguments aka Positional Parameters ---------------

set numargs = $#
set progname = $0
set arg1 = $1
set arg2 = $2
set allargs = $*
# $@ is not same in tcsh as in bash
set argv1 = $argv[1]
set argv2 = $argv[2]
set allargs1 = $argv

# use shift to discard $1 and shift all parameters down one
# thus $1 will now be $2 etc.  Specify a number with shift
# to shift by that many parameters
shift
set newarg1=$1

echo numargs=$numargs
echo progname=$progname
echo arg1=$arg1
echo arg2=$arg2
echo allargs=$allargs
echo argv1=$argv1
echo argv2=$argv2
echo allargs1=$allargs1
echo newarg1=$newarg1

echo " "
echo 3: Command Error Codes ---------------

set cmd_error = $?

echo cmd_error=$cmd_error

echo " "
echo 4: Quoting ---------------
# double quotes interpolate variables, single quotes do not
# backslash takes the next character literally 
# backticks ` execute a shell command and evaluate to standard output
set var2 = "this is $var1"
set var3 = 'this is $var1'
set var4 = "this is \$var1"
set var5 = `wc -l $0`

echo var2=$var2
echo var3=$var3
echo var4=$var4
echo var5=$var5

echo " "
echo 5: Conditional Statements - If, case ---------------

if ( $var1 == "Yes" ) then
   echo If Yes
else if ( $var1 == No ) then
   echo Elif No
else
   echo Else $var1
endif

if ($var1 != "Yes") echo If not Yes

# Omitting the breaksw will fall through to the next case.
# you can use wildcards ?*[] in the case selections for matching strings
switch ($var1)
   case yes | YES:
      echo Case: Yes; breaksw
   case [nN][oO]:
      echo Case: No; breaksw
   default:
      echo Case: default; breaksw
endsw

echo " "
echo 6: String Comparisons ---------------
# $s == $s  equal?
# $s != $s not equal?
set string1 = "abc"
set string2 = "abd"
set string3 = ""
# note string4 is undefined
set string5 = 0
set string6 = 1
set string7 =

# tcsh terminates your script when you try to use a variable that's not defined
echo string1=$string1
echo string2=$string2
echo string3=$string3
# Can't echo undefind values in tcsh the script will die!
# Always define all your variables at the top in tcsh!
#echo string4="$string4"
echo string4=
echo string5=$string5
echo string6=$string6
echo string7=$string7

# spaces must be around the == or != but don't have to be around the ()
if ($string1 == $string2) then
   echo test eq1: string1 equal to string2
else
   echo test eq1: string1 not equal to string2
endif

# This causes script to die string4 not defined!
#if ($string3 == $string4)  then
#   echo test eq2: string3 equal to string4
#else
#   echo test eq2: string3 not equal to string4
#endif

# This causes script to die - string 4 not defined - be careful
#if ($string5 == $string4) then
#   echo test eq3: string5 equal to string4
#else
#   echo test eq3: string5 not equal to string4
#endif

# You can't do this on a single line, must use two if statements
#if ( $?string4 && $string3 == $string4 ) then
#   echo test eq2: string3 equal to string4
#else
#   echo test eq2: string3 not equal to string4
#endif

if ( $?string4 ) then
   if ( $string3 == $string4 ) then
      echo test eq2: string3 equal to string4
   endif
else
   echo test eq2: string3 not equal to string4
endif

if ($string3 == $string3)  then
   echo test eq3: string3 equal to string3
else
   echo test eq3: string3 not equal to string3
endif

if ($string1 == $string3)  then
   echo test eq4: string1 equal to string3
else
   echo test eq4: string1 not equal to string3
endif

# Not even quotes will make tcsh happy,  
# always make sure a variable is defined before using it
#if ("$string5" == "$string4") then
#   echo test eq4: string5 equal to string4
#else
#   echo test eq4: string5 not equal to string4
#endif

#if ("-$string5" == "-$string4") then
#   echo test eq5: string5 equal to string4
#else
#   echo test eq5: string5 not equal to string4
#endif

# test for equality similar to bash
[ $string1 = $string2 ]
if ( $? == 0 ) then
   echo test eq5: string1 equal to string2
else
   echo test eq5: string1 not equal to string2
endif

[ $string1 = $string1 ]
if ( $? == 0 ) then
   echo test eq6: string1 equal to string1
else
   echo test eq6: string1 not equal to string1
endif

# test for inequality
[ $string2 != $string1 ]
if ( $? == 0 ) then
   echo test ne1: string2 not equal to string1
else
   echo test ne1: string2 equal to string1
endif

if ( $string2 != $string1 ) then
   echo test ne2: string2 not equal to string1
else
   echo test ne2: string2 equal to string1
endif

# test for emptiness
if ( $string1 != "" ) then
   echo test empty1: string1 is not empty
else
   echo test empty1: string1 is empty
endif

if ( $string3 != "" ) then
   echo test empty2: string3 is not empty
else
   echo test empty2: string3 is empty
endif

# works if variable is not defined
if ( $?string4 ) then
   echo test def1: string4 is defined
else
   echo test def1: string4 is not defined
endif

if ( $?string3 ) then
   echo test def2: string3 is defined
else
   echo test def2: string3 is not defined
endif

if ( $%string1 == 0 ) then
   echo test zlen1: string1 has length equal to zero
else
   echo test zlen1: string1 has a length greater than zero
endif

if ( $%string3 == 0) then
   echo test zlen2: string3 has length equal to zero
else
   echo test zlen2: string3 has a length greater than zero
endif

if ( $%string1 != 0 ) then
   echo test nzlen1: string1 has a length greater than zero
else
   echo test nzlen1: string1 has length equal to zero
endif

if ( $%string3 != 0) then
   echo test nzlen2: string3 has a length greater than zero
else
   echo test nzlen2: string3 has length equal to zero
endif

if ( $%string1 == 3 ) then
   echo test len1: string1 has a length of three
else
   echo test len1: string1 has length of not three
endif

if ( $%string3 == 3 ) then
   echo test len2: string3 has a length of three
else
   echo test len2: string3 has length of not three
endif

echo " "
echo 7: Number Comparisons ---------------
# $n -eq $n
# $n -ge $n
# $n -le $n
# $n -ne $n
# $n -gt $n
# $n -lt $n

set number1=5
set number2=10
set number3=05
set number4=""
# number5 not defined

echo number1=$number1
echo number2=$number2
echo number3=$number3
echo number4=$number4
# Can't echo undefind values in tcsh the script will die!
# Always define all your variables at the top in tcsh!
#echo number5=$number5
echo number5=

# This gives the wrong answer if you're thinking numerically
# Note that == is a string compare, not a numeric compare
if ( $number1 == $number3 ) then
   echo x test eq1: number1 is equal to number3
else
   echo x test eq1: number1 is not equal to number3
endif

if ( $number1 == $number2 ) then
   echo test eq2: number1 is equal to number2
else
   echo test eq2: number1 is not equal to number2
endif

# The right way to compare numbers with leading zeros
if ( $number1 >= $number3 && $number1 <= $number3 ) then
   echo test eq3: number1 is equal to number3
else
   echo test eq3: number1 is not equal to number3
endif

# This is also an error can't compare a number to a string
#if ( $number1 >= $string1 ) then
#   echo test eq5: number1 is equal to string1
#else
#   echo test eq5: number1 is not equal to string1
#endif

# This trick won't work either
#if ( "0$number1" -eq "0$string1" ) then
#   echo test eq6: number1 is equal to string1
#else
#   echo test eq6: number1 is not equal to string1
#endif

# The proper way to check if a number is not another number
if ( $number1 < $number3 || $number1 > $number3) then
   echo test ne1: number1 is not equal to number3
else
   echo test ne1: number1 is equal to number3
endif

if ( $number1 < $number2 || $number1 > $number2) then
   echo test ne2: number1 is not equal to number2
else
   echo test ne2: number1 is equal to number2
endif

if ( $number1 > $number3 ) then
   echo test gt1: number1 is greater than number3
else
   echo test gt1: number1 is not greater than number3
endif

if ( $number2 > $number1 ) then
   echo test gt2: number2 is greater than number1
else
   echo test gt2: number2 is not greater than number1
endif

if ( $number1 >= $number3 ) then
   echo test ge1: number1 is greater than or equal to number3
else
   echo test ge1: number1 is not greater than or equal to number3
endif

if ( $number2 >= $number1 ) then
   echo test ge2: number2 is greater than or equal to number1
else
   echo test ge2: number2 is not greater than or equal to number1
endif

if ( $number1 >= $number2 ) then
   echo test ge3: number1 is greater than or equal to number2
else
   echo test ge3: number1 is not greater than or equal to number2
endif

if ( $number1 < $number3 ) then
   echo test lt1: number1 is less than number3
else
   echo test lt1: number1 is not less than number3
endif

if ( $number1 < $number2 ) then
   echo test lt2: number1 is less than number2
else
   echo test lt2: number1 is not less than number2
endif

if ( $number1 <= $number3 ) then
   echo test le1: number1 is less than or equal to number3
else
   echo test le1: number1 is not less than or equal to number3
endif

if ( $number2 <= $number1 ) then
   echo test le2: number2 is less than or equal to number1
else
   echo test le2: number2 is not less than or equal to number1
endif

if ( $number1 <= $number2 ) then
   echo test le3: number1 is less than or equal to number2
else
   echo test le3: number1 is not less than or equal to number2
endif

echo " " 
echo 8: Logical Operators ---------------
# ! ex      negation
# ex && ex  logical AND
# ex || ex  logical OR
# ( )       specify evaluation order

# Note spaces are not necessary around the ( )
if ( !($number1 >= 5 && $number1 <= 5) ) then
   echo test not1: number1 is not \(equal to 5\)
else
   echo test not1: number1 is equal to 5
endif

if ( ! ($number1 < 5 || $number1 > 5) ) then
   echo test not2: number1 is not \(not equal to 5\)
else
   echo test not2: number1 is not equal to 5
endif

# NOTE the ! negates just the next logical expression, not the whole expression
if ( ! 0 == 1 && 0 == 1  ) then
   echo test not3: \! 0 == 1 \&\& 0 == 1 is true
else
   echo test not3: \! 0 == 1 \&\& 0 == 1 is false
endif

if ( ! 0 == 1 && ! 0 == 1  ) then
   echo test not4: \! 0 == 1 \&\& \! 0 == 1 is true
else
   echo test not4: \! 0 == 1 \&\& \! 0 == 1 is false
endif

if ( $number1 && $number2 ) then
   echo test and1: number1 and number2 is true
else
   echo test and1: number1 and number2 is false
endif

if ( $number1 && "$number4" ) then
   echo test and2: number1 and number4 is true
else
   echo test and2: number1 and number4 is false
endif

if ( $number1 || "$number4" ) then
   echo test or1: number1 or number4 is true
else
   echo test or1: number1 or number4 is false
endif

if ( "$number4" || "$number4" ) then
   echo test or2: number4 or number4 is true
else
   echo test or2: number4 or number4 is false
endif

echo " "
echo 9: File Comparisons ---------------
# see man tcsh for even more!
# -d dir    is directory?
# -e file   exists?
# -f file   is a regular file?
# -l file   is a symbolic link??
# -o file   am I the owner?
# -r file   can I read it?
# -w file   can I write to it?
# -x file   can I execute it?
# -z file   is it zero size?
# -s file   is it non-zero size?

# first set up a subdirectory and file for the test
if (-d _tmp) then
   echo _tmp already exists, I dare not muck with it!
   echo skipping the file and directory testing
else
   mkdir _tmp
   touch _tmp/file1
   echo hello > _tmp/file2
   touch _tmp/file3
   chmod 500 _tmp/file1
   chmod 200 _tmp/file3
   mkdir _tmp/dir1 
   chmod 700 _tmp/dir
   ln -s file1 _tmp/link1
   ln -s dir1  _tmp/linkdir
   ls -al _tmp

   # Does it exist?
   if ( -e _tmp/null ) then
      echo test -e1: null exists
   else
      echo test -e1: null does not exist
   endif

   if ( -e _tmp/dir1 ) then
      echo test -e1: dir1 exists
   else
      echo test -e1: dir1 does not exist
   endif

   if ( -e _tmp/file1 ) then
      echo test -e2: file1 exists
   else
      echo test -e2: file1 does not exist
   endif

   if ( -e _tmp/link1 ) then
      echo test -e3: link1 exists
   else
      echo test -e3: link1 does not exist
   endif

   if ( -e _tmp/linkdir ) then
      echo test -e4: linkdir exists
   else
      echo test -e4: linkdir does not exist
   endif

   # is it a directory?
   if ( -d _tmp/null ) then
      echo test -d1: null is a directory
   else
      echo test -d1: null is not a directory
   endif

   if ( -d _tmp/dir1 ) then
      echo test -d1: dir1 is a directory
   else
      echo test -d1: dir1 is not a directory
   endif

   if ( -d _tmp/file1 ) then
      echo test -d2: file1 is a directory
   else
      echo test -d2: file1 is not a directory
   endif

   if ( -d _tmp/link1 ) then
      echo test -d3: link1 is a directory
   else
      echo test -d3: link1 is not a directory
   endif

   if ( -d _tmp/linkdir ) then
      echo test -d4: linkdir is a directory
   else
      echo test -d4: linkdir is not a directory
   endif

   # is it a file?
   if ( -f _tmp/null ) then
      echo test -f1: null is a file
   else
      echo test -f1: null is not a file
   endif

   if ( -f _tmp/dir1 ) then
      echo test -f1: dir1 is a file
   else
      echo test -f1: dir1 is not a file
   endif

   if ( -f _tmp/file1 ) then
      echo test -f2: file1 is a file
   else
      echo test -f2: file1 is not a file
   endif

   if ( -f _tmp/link1 ) then
      echo test -f3: link1 is a file
   else
      echo test -f3: link1 is not a file
   endif

   if ( -f _tmp/linkdir ) then
      echo test -f4: linkdir is a file
   else
      echo test -f4: linkdir is not a file
   endif

   # is it a symbolic link?
   if ( -l _tmp/null ) then
      echo test -l1: null is a link
   else
      echo test -l1: null is not a link
   endif

   if ( -l _tmp/dir1 ) then
      echo test -l2: dir1 is a link
   else
      echo test -l2: dir1 is not a link
   endif

   if ( -l _tmp/file1 ) then
      echo test -l3: file1 is a link
   else
      echo test -l3: file1 is not a link
   endif

   if ( -l _tmp/link1 ) then
      echo test -l4: link1 is a link
   else
      echo test -l4: link1 is not a link
   endif

   if ( -l _tmp/linkdir ) then
      echo test -l5: linkdir is a link
   else
      echo test -l5: linkdir is not a link
   endif

   # do I own it?
   if ( -o _tmp/file1 ) then
      echo test -o1: file1 is owned by me
   else
      echo test -o1: file2 is not owned by me
   endif

   if ( -o /etc/fstab ) then
      echo test -o2: /etc/fstab is owned by me
   else
      echo test -o2: /etc/fstab is not owned by me
   endif

   # can I read it?
   if ( -r _tmp/file1 ) then
      echo test -r1: file1 is readable
   else
      echo test -r1: file1 is not readable
   endif

   if ( -r _tmp/file2 ) then
      echo test -r2: file2 is readable
   else
      echo test -r2: file2 is not readable
   endif

   if ( -r _tmp/file3 ) then
      echo test -r3: file3 is readable
   else
      echo test -r3: file3 is not readable
   endif

   # can I write to it?
   if ( -w _tmp/file1 ) then
      echo test -w1: file1 is writable
   else
      echo test -w1: file1 is not writable
   endif

   if ( -w _tmp/file2 ) then
      echo test -w2: file2 is writable
   else
      echo test -w2: file2 is not writable
   endif

   if ( -w _tmp/file3 ) then
      echo test -w3: file3 is writable
   else
      echo test -w3: file3 is not writable
   endif

   # can I execute it?
   if ( -x _tmp/file1 ) then
      echo test -x1: file1 is executable
   else
      echo test -x1: file1 is not executable
   endif

   if ( -x _tmp/file2 ) then
      echo test -x2: file2 is executable
   else
      echo test -x2: file2 is not executable
   endif

   if ( -x _tmp/file3 ) then
      echo test -x3: file3 is executable
   else
      echo test -x3: file3 is not executable
   endif

   # Is it zero size?
   if ( -z _tmp/file1 ) then
      echo test -z1: file1 is zero size
   else
      echo test -z1: file1 is not zero size
   endif

   if ( -z _tmp/file2 ) then
      echo test -z2: file2 is zero size
   else
      echo test -z2: file2 is not zero size
   endif

   if ( -z _tmp/file3 ) then
      echo test -z3: file3 is zero size
   else
      echo test -z3: file3 is not zero size
   endif

   if ( -z _tmp/dir1 ) then
      echo test -z3: dir1 is zero size
   else
      echo test -z3: dir1 is not zero size
   endif

   # Is it non-zero size?
   if ( -s _tmp/file1 ) then
      echo test -s1: file1 is not zero size
   else
      echo test -s1: file1 is zero size
   endif

   if ( -s _tmp/file2 ) then
      echo test -s2: file2 is not zero size
   else
      echo test -s2: file2 is zero size
   endif

   if ( -s _tmp/file3 ) then
      echo test -s3: file3 is not zero size
   else
      echo test -s3: file3 is zero size
   endif

   if ( -s _tmp/dir1 ) then
      echo test -s3: dir1 is not zero size
   else
      echo test -s3: dir1 is zero size
   endif

   # clean up the test subdirectory
   rm -rf _tmp
endif

echo " "
echo 10: Looping ---------------
# use 'break' to break out of a loop prematurely
# use 'exit' with an optional number to terminate the script with the number
# as the command return code in $?

# Loop on matching file names
foreach var (*.txt)
   echo for1: $var
end

# Loop on a list of items
foreach var (a b c)
   echo for2: $var
end

# Loop on all command line arguments
# same as for var in "$@"
foreach var ($*)
   echo for3: $var
end

set idx = 0
while ($idx < 5)
   echo while1: $idx
   set idx=`expr $idx + 1`
end

# there is no 'until' loop in tcsh - just negate the condition

set idx = 0
while (! ($idx >= 5) )
   echo until1: $idx
   set idx=`expr $idx + 1`
end

# Repeat a single command a fixed number of times
echo repeat1: 
repeat 76 echo -n '-'
echo " "

# tcsh has no 'select' menu statement but we can emulate it
set items=(File Edit Tools Syntax Buffers Window Help Exit)
set chosen=
# Loop until they select the Exit item from menu
while ($chosen != "Exit")
   # Show the Menu.
   # bash actually shows menu in four columns numbered downward
   # once theres more than 8 entries -- this could be done with a little more work
   set idx=1
   foreach item ($items)
      echo $idx\) $item
      set idx=`expr $idx + 1`
   end
   # prompt for a response
   echo -n "#? "
   set choice=$<

   # figure out which was selected
   set idx=1
   foreach item ($items)
      if ($idx == $choice) then
         set chosen=$item
         break
      endif
      set idx=`expr $idx + 1`
   end
 
   # Act on the selected item
   switch ($item)
      case File:
         echo File menu selected; breaksw
      case Edit:
         echo Edit menu selected; breaksw
      case Tools:
         echo Tools menu selected; breaksw
      case Syntax:
         echo Syntax menu selected; breaksw
      case Buffers:
         echo Buffers menu selected; breaksw
      case Window:
         echo Window menu selected; breaksw
      case Help:
         echo Help menu selected; breaksw
      case Exit:
         breaksw
      default:
         echo Incorrect Menu Selection; breaksw
   endsw
end

echo 
echo 11: Functions ---------------
# Cannot define functions in tcsh
