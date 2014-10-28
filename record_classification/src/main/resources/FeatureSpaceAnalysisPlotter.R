#Data file name should be first argument
#Output image file name should be the second
args <- commandArgs(TRUE)

#Reading in the data
data <-read.table(args[1],header=TRUE,as.is=TRUE,sep=",",dec=".")
dataNum <- sapply(data,as.numeric)


#Opening a png printing device
png(file=toString(args[2]),width=800,height=800)

string <- gsub("_"," ",args[3])
title<-"Feature Frequency - Inverse Code Frequency"

barplot(dataNum, main = c(string,title), ylab="ff-icf",col=c("darkblue","red"),
        legend = c("dataSet1","dataSet2"), beside=TRUE,las=2)