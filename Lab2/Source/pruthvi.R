library(MASS)
library(ISLR)
Cars93
str(Cars93)
attach(Cars93)
a=Cars93$RPM
Random=rnorm(93,60,89)
data=data.frame(a,b)
cluster= kmeans(data ,8,trace = TRUE)
cluster$cluster
plot(RPM,Random,col=cluster$cluster)
points(cluster$centers,pch=16)


