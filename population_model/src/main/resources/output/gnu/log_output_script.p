# This file is called log_output_script.p
set terminal pdf
set output 'output.pdf'
set title "Number of Children Distribution - Affairs - 1600 - end"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfAffairs_1600.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfAffairs_1600.dat" using 1:3 title 'Dist' with line
set title "Number of Children Distribution - Cohabitation - 1678 - 1778"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohab_1678.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohab_1678.dat" using 1:3 title 'Dist' with line
set title "Number of Children Distribution - Cohabitation - 1778 - 1800"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohab_1778.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohab_1778.dat" using 1:3 title 'Dist' with line
set title "Number of Children Distribution - Cohabitation - 1800 - end"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohab_1800.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohab_1800.dat" using 1:3 title 'Dist' with line
set title "Number of Children Distribution - Cohabitation Then Marriage - 1678 - 1778"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohabTheMarriage_1678.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohabTheMarriage_1678.dat" using 1:3 title 'Dist' with line
set title "Number of Children Distribution - Cohabitation Then Marriage - 1778 - 1800"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohabTheMarriage_1778.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohabTheMarriage_1778.dat" using 1:3 title 'Dist' with line
set title "Number of Children Distribution - Cohabitation Then Marriage - 1800 - end"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohabTheMarriage_1800.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfCohabTheMarriage_1800.dat" using 1:3 title 'Dist' with line
set title "Number of Children Distribution - Marriage - 1678 - 1778"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfMarriage_1678.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfMarriage_1678.dat" using 1:3 title 'Dist' with line
set title "Number of Children Distribution - Marriage - 1778 - 1800"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfMarriage_1778.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfMarriage_1778.dat" using 1:3 title 'Dist' with line
set title "Number of Children Distribution - Marriage - 1800 - end"
set ylabel "Frequency"
set xlabel "Number of Children"
plot "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfMarriage_1800.dat" using 1:2 title 'Actual' with line, "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/gnu/ChildrenNumberOfMarriage_1800.dat" using 1:3 title 'Dist' with line
set terminal png
