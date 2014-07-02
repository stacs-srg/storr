#Data file name should be first argument
#Output image file name should be the second
args <- commandArgs(TRUE)

#Reading in the data
codeStats <- read.csv(toString(args[1]))

#Opening a png printing device
png(file=toString(args[2]),width=800,height=800)

#Plotting
pairs(codeStats, gap=0, diag.panel = function (x, ...) {
        par(new = TRUE) 
        hist(x, col = "light blue", probability = TRUE, axes = FALSE, main = "")
        lines(density(x), col = "red", lwd = 3)
        rug(x)})

#Closing the png printing device
#dev.off()
