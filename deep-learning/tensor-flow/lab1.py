
# coding: utf-8

# ## Test basic Python code

# In[ ]:

print "hello, there"


# In[ ]:

a=2
c=a+2
print c


# ## Test Python package import

# In[ ]:

import numpy as np


# In[ ]:

import tensorflow as tf


# ## Run basic TensorFlow code

# In[ ]:

a1=tf.ones([10])
sess = tf.Session()
print(sess.run(a1))


# this is different from print a0 itself

# In[ ]:

print(a1)


# In[ ]:

hello = tf.constant('Hello, Joe!')
print(sess.run(hello))
print(hello)


# Note that sess.run() gives us the actual output value, it takes *tf* variable
# The following tf.constant is required, otherwise it will generate error

# In[ ]:

a = tf.constant(10)
b = tf.constant(20)
c=a+b
print(sess.run(c))


# Put everything in a session, no need to call session, just use .eval()

# In[ ]:

with tf.Session():
    a = tf.constant(10)
    b = tf.constant(20)
    c=a+b
    print c
    print(c.eval())


# the following code will generate error, that because c is not tf variable, thus cannot be run by sess.run

# In[ ]:




# ## See different tensor dimensions

# In[ ]:

a2=tf.ones([10,3])
print(sess.run(a2))


# In[ ]:

a3=tf.ones([10,3,2])
print(sess.run(a3))


# b1 = tf.ones([4])
# b2 = tf.constant([2.0, 2.0, 2.0, 2.0])
# c = tf.add(b1, b2)
# print c

# In[ ]:

print(sess.run(c))


# In[ ]:

with tf.Session():
  b1 = tf.ones([4])
  b2 = tf.constant([2.0, 2.0, 2.0, 2.0])
  c = tf.add(b1, b2)
  print c.eval()


# ## Get random number and data type

# In[ ]:

shape=[2,3]
x=tf.random_normal(shape, stddev=0.7) 
with tf.Session():
    print x.eval()


# In[ ]:

shape=[2,2]
x=tf.truncated_normal(shape, stddev=0.1) 
with tf.Session():
    print x.eval()


# ## Exercise: Print out the sigmoid value of a 2x2 marix of 1's, using TensorFlow
