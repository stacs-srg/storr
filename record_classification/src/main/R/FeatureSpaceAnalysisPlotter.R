#Data file name should be first argument
#Output image file name should be the second
args <- commandArgs(TRUE)

#Reading in the data
dataSet <- read.csv(toString(args[1]))
attach(dataSet)

#Opening a png printing device
png(file=toString(args[2]),width=800,height=800)

#Plotting
# Grouped Bar Plot
counts <- tapply(ff.icf,list(as.factor(filename), as.factor(feature)), "+")
barplot(counts, main="Feature Frequency - Inverse Code Frequency",
        xlab="Feature", ylab="ff-icf",col=c("darkblue","red"),
        legend = rownames(counts), beside=TRUE)

#Closing the png printing device
#dev.off()
