# Gnuplot script file for plotting data in file "memory_usage.dat"
reset
set style line 11 lc rgb '#808080' lt 1
set border 3 back ls 11
set tics nomirror
set style line 12 lc rgb '#808080' lt 0 lw 1
set grid back ls 12
set style line 1 lc rgb '#8b1a0e' pt 1 ps 1 lt 1 lw 20 # --- red
set style line 2 lc rgb '#5e9c36' pt 6 ps 1 lt 1 lw 20 # --- green

set terminal win
set   autoscale                        # scale axes automatically
unset log                              # remove any log-scaling
unset label                            # remove any previous labels
set xtic auto                          # set xtics automatically
set ytic auto                          # set ytics automatically
set title "Memory Usage and Population over time"
set xlabel "Year"
plot    "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/memory_usage.dat" using 1:2 title 'Population' with line , \
      "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/memory_usage.dat" using 1:3 title 'Total Peopl Generated' with line , \
      "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/memory_usage.dat" using 1:4 title 'Memory (KB)' with line
