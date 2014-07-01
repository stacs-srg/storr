#Data file name should be first argument
#Output image file name should be the second
args <- commandArgs(TRUE)
print(args[1])
#Reading in the data
codeStats <- read.csv(toString(args[1]))
#codeStats <- read.csv("~/downloads/codeSats5.csv")
attach(codeStats)
install.packages("RSvgDevice",repos="http://star-www.st-andrews.ac.uk/cran/")
library(RSvgDevice)
#Opening a png printing device
svg(file=toString(args[2]),width=10,height=10)
#png("testGraph.png")

#Plotting
pairs(codeStats, gap=0, diag.panel = function (x, ...) {
        par(new = TRUE) 
        hist(x, col = "light blue", probability = TRUE, axes = FALSE, main = "")
        lines(density(x), col = "red", lwd = 3)
        rug(x)})

#Closing the png printing device
dev.off()
