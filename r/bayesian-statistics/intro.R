rm(list=ls())
#p <- seq(from=0.1, to=0.9, by=0.1)
#prior <- c(rep(0.06, 4), 0.52, rep(0.06, 4))
#likelihood <- dbinom(40, 200, p)
#
#numerator <- prior * likelihood
#denom <- sum(numerator)
#posterior <- numerator / denom
#
#pvalue <- sum(dbinom(0:58, 10000, 0.007))


#N_lottery_outcomes <- seq(from=100e6, to=900e6, by=100e6)
#p <- 1 / N_lottery_outcomes
#prior <- rep(1/length(p), length(p))
#likelyhood <- dbinom(3, 413271201, p)
#numerator <- prior * likelyhood
#denom <- sum(numerator)
#posterior <- numerator / denom
#last = length(p)-4
#result = sum(posterior[1 : last])

#x <-  seq(0, 1, length=21)
#plot(x, pbeta(x, 90, 10))
#plot(x, pbeta(x, 9, 1))

#library(statsr)
#data(brfss)
#
#n <- length(brfss$sex)
#x <- sum(brfss$sex == "Female")
# plot.new()
#x <- seq(from=0.01, to=.99, by=0.01)
#
#prior <- dbeta(x, 1, 1)
#likelihood <- pbinom(x, prior, 90)
#
#
#plot(likelihood, type='l', lty='solid', col='blue')
#lines(prior, type='l', lty='dotted')
#legend_y = range(0, posterior)[2]
#legend(x = 1, y = legend_y, legend=c('prior', 'male'), 
#       cex = 0.9,
#       col = c('grey', 'blue'),
#       lty = c('dotted', 'solid'))
#
#
#post <- dbeta(x, 5030, 4972)
#plot(post, type='l', lty='solid', col='blue')
#Pp <- pbeta(0.5, 5030, 4972) 

# Week 3 Lab
library(statsr)
library(dplyr)
library(ggplot2)

data(nc)
str(nc)
plot(table(nc$weight))
t = table(nc$weight)

#plot(nc$weight)

# Credible interval for mu
bayes_inference(y = weight, data = nc, statistic = "mean", type = "ci", cred_level = 0.99)

# Hypothesis testing for H1 and H2
bayes_inference(y = weight, data = nc, statistic = "mean", type = "ht", null = 7, alternative = "twosided")


bayes_inference(y = premie, data = nc, statistic = "mean", type = "ht", null = 5.5, alternative = "twosided")


#habit = as.numeric(nc$habit)
#weight = nc$weight
boxplot(weight~habit, data=nc)


bayes_inference(y = lowbirthweight, x = habit, data=nc, success="low", statistic = "proportion", type = "ht", 
                null = 0, alternative = "twosided")

