# Gnuplot script file for plotting data in file "memory_usage.dat"
set terminal win
set   autoscale                        # scale axes automatically
unset log                              # remove any log-scaling
unset label                            # remove any previous labels
set xtic auto                          # set xtics automatically
set ytic auto                          # set ytics automatically
set title "Memory Usage and Population over time"
set xlabel "Days"
plot    "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/memory_usage.dat" using 1:2 title 'Memory (MB)' with line , \
      "E:/wrkspc/digitising_scotland/population_model/src/main/resources/output/memory_usage.dat" using 1:3 title 'Population' with line