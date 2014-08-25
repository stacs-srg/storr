args <- commandArgs(TRUE)
mysurface <- read.csv(toString(args[1]), header=F)
mysurface <- as.matrix(mysurface)
mysurface <- t(mysurface)
dimnames(mysurface) <- list(0:(ncol(mysurface)-1),0:(nrow(mysurface)-1))
library("lattice")
png(file=toString(args[2]),width=800,height=800)
levelplot(mysurface, grid, col.regions = topo.colors(100),xlab = "Number of Gold Standard Codes",
               ylab = "Number of Codes Predicted",
               panel=function(...) {
                 arg <- list(...)
                 panel.levelplot(...)
                 panel.text(arg$x, arg$y, round(arg$z,1))})

dev.off()
