import numpy as np
from random import shuffle
import tensorflow as tf

train_input = ['{0:020b}'.format(i) for i in range(2**20)]
shuffle(train_input)
print(len(train_input))

temp_train_input = []
# split digits
for num in train_input:
    number_as_list = []
    for digit in num:
        number_as_list.append([int(digit)])
    temp_train_input.append(number_as_list)
train_input = temp_train_input

# print(train_input[:10])

# We "count" ones (1), and assign the number to a class
# If the number has zero 1's then it belongs to a class zero,
# if one 1 then class one, etc.
# The representation is the list of of length 21 with all zeroes
# except a one at the index of the class to which the sequence belongs.

train_output = []
for number_as_list in train_input:
    count = sum([i[0] for i in number_as_list])
    class_list = [0] * 21
    class_list[count] = 1
    train_output.append(class_list)

print(train_output[:10])

# So no we have inputs and the classification of each input using
# the so-called one-hot encoded representation (https://en.wikipedia.org/wiki/One-hot)
TRAIN_SIZE_RATIO = 0.01
TRAIN_SIZE = int(len(train_input) * TRAIN_SIZE_RATIO)

test_input = train_input[TRAIN_SIZE : ]
test_output = train_output[TRAIN_SIZE : ]

train_input = train_input[ : TRAIN_SIZE]
train_output = train_output[ : TRAIN_SIZE]

print(len(test_input), len(test_output), len(train_input), len(train_output))

# Phase 1. Define a graph
#
# Build out TensorFlow model
# [Batch size, Sequence Length, Input Dimension]; we'll set Batch Size later.
# Placeholders will hold data later
#
data = tf.placeholder(tf.float32, [None, 20, 1])
target = tf.placeholder(tf.float32, [None, 21])

num_hidden = 24
cell = tf.nn.rnn_cell.LSTMCell(num_hidden, state_is_tuple=True)

val, state = tf.nn.dynamic_rnn(cell, data, dtype=tf.float32)

val = tf.transpose(val, [1, 0, 2])
last = tf.gather(val, int(val.get_shape()[0]) - 1)

weight = tf.Variable(tf.truncated_normal([num_hidden, int(target.get_shape()[1])]))
bias = tf.Variable(tf.constant(0.1, shape=[target.get_shape()[1]]))

prediction = tf.nn.softmax(tf.matmul(last, weight) + bias)

cross_entropy = -tf.reduce_sum(target * tf.log(prediction))

optimizer = tf.train.AdamOptimizer()
minimize = optimizer.minimize(cross_entropy)

mistakes = tf.not_equal(tf.argmax(target, 1), tf.argmax(prediction, 1))
error = tf.reduce_mean(tf.cast(mistakes, tf.float32))

# Execute tha graph

init_op = tf.initialize_all_variables()
session = tf.Session()
session.run(init_op)

batch_size = 1000
no_of_batches = int(len(train_input) / batch_size)
epoch = 5000

for cur_epoch in range(epoch):
    ptr = 0
    for cur_batch in range(no_of_batches):
        start = ptr
        end = ptr + batch_size
        inp, out = train_input[start : end], train_output[start : end]
        ptr += batch_size
        session.run(minimize,{data: inp, target: out})

    print("Epoch ", str(cur_epoch + 1))
    incorrect = session.run(error, {data: test_input, target: test_output})
    print('Epoch {:2d} error {:3.1f}%'.format(cur_epoch + 1, 100 * incorrect))
    print(session.run(prediction,
                   # 12 1'ds
                   {data: [[[1],[0],[0],[1],[1],[0],[1],[1],[1],[0],[1],[0],[0],[1],[1],[0],[1],[1],[1],[0]]]}))
session.close()