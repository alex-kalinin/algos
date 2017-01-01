
# coding: utf-8

# ## Load data

# In[1]:

directory='mnist'
train_data_filename = directory+'/train-images-idx3-ubyte.gz'
train_labels_filename = directory+'/train-labels-idx1-ubyte.gz'
test_data_filename = directory+'/t10k-images-idx3-ubyte.gz'
test_labels_filename =directory+'/t10k-labels-idx1-ubyte.gz'
print (test_data_filename)


# ## Inspect data, show the first data point f

# In[10]:

get_ipython().magic('matplotlib inline')
import matplotlib.pyplot as plt
import gzip, binascii, struct, numpy

with gzip.open(test_data_filename) as f:
  for field in ['magic number', 'image count', 'rows', 'columns']:
    line=f.read(4)  #read the first 4 elements in current row
    item0=struct.unpack('>i', line)[0]  #unpack binary data and interpret as integer
    print (field, item0)
    
  buf = f.read(28 * 28)  #read 28x28=784 rows
  #image is a one-dimensional array of 28x28=784 rows elements
  image = numpy.frombuffer(buf, dtype=numpy.uint8) #data type is integer

  print ("image is:")
  print (image)
  print ('The first 10 pixels of image', image[:10])


# ## Plot out the data

# In[11]:

# We'll show the image and its pixel value histogram side-by-side.
plot1= plt.subplot(1, 3, 1) #the plot has 1 row, 2 columns
plot2= plt.subplot(1, 3, 2)
plot3= plt.subplot(1, 3, 3)

plot1.imshow(image.reshape(28, 28));  #reshape the image to 28x28 pixels
plot2.imshow(image.reshape(28, 28), cmap=plt.cm.Greys);  #with grey scale
plot3.hist(image, bins=20, range=[0,255]);


# ## Show the data label

# In[12]:

with gzip.open(test_labels_filename) as f:
  for field in ['magic number', 'label count']:
    line=f.read(4)  #read the first 4 elements in current row
    item0=struct.unpack('>i', line)[0]
    print (field, item0)

  line= f.read(1) #red the next line
  print ('First label:', struct.unpack('B',line)[0])  #unpacked binary data, and interpret as 'B'=unsigned char


# ## Extract data, as 4-d tensor

# In[13]:

IMAGE_SIZE = 28
PIXEL_DEPTH = 255

"""
  Extract the images into a 4D tensor [image index, y, x, channels].
  For greyscale MNIST, the number of channels is always 1.
  Values are rescaled from [0, 255] down to [-0.5, 0.5].
==================================================================
"""
def extract_data(filename, num_images):
  print ('Extracting', filename)
  with gzip.open(filename) as bytestream:
    # Skip the magic number and dimensions; we know these values.
    bytestream.read(16)
    
    buf = bytestream.read(IMAGE_SIZE * IMAGE_SIZE * num_images)
    data = numpy.frombuffer(buf, dtype=numpy.uint8).astype(numpy.float32)
    data = (data - (PIXEL_DEPTH / 2.0)) / PIXEL_DEPTH
    data = data.reshape(num_images, IMAGE_SIZE, IMAGE_SIZE, 1)
    return data

train_data = extract_data(train_data_filename, 60000)
test_data = extract_data(test_data_filename, 10000)


# ## Visualize the data

# In[14]:

print ('Training data shape', train_data.shape)
plot1= plt.subplot(1, 2, 1) #the plot has 1 row, 2 columns
plot2= plt.subplot(1, 2, 2)

image1=train_data[0].reshape(28, 28)
image2=train_data[1].reshape(28, 28)
plot1.imshow(image1, cmap=plt.cm.Greys);
plot2.imshow(image2, cmap=plt.cm.Greys);


# In[15]:

NUM_LABELS = 10

def extract_labels(filename, num_images):
  """Extract the labels into a 1-hot matrix [image index, label index]."""
  print ('Extracting', filename)
  with gzip.open(filename) as bytestream:
    # Skip the magic number and count; we know these values.
    bytestream.read(8)
    
    buf = bytestream.read(1 * num_images)
    labels = numpy.frombuffer(buf, dtype=numpy.uint8)
  # Convert to dense 1-hot representation.
  return (numpy.arange(NUM_LABELS) == labels[:, None]).astype(numpy.float32)

train_labels = extract_labels(train_labels_filename, 60000)
test_labels = extract_labels(test_labels_filename, 10000)
print ('Training labels shape', train_labels.shape)
print ('First label vector', train_labels[0])
print ('Second label vector', train_labels[1])


# ## Split the training data into 2 sets: training , validation

# In[16]:

VALIDATION_SIZE = 5000

validation_data = train_data[:VALIDATION_SIZE, :, :, :]
validation_labels = train_labels[:VALIDATION_SIZE]
train_data = train_data[VALIDATION_SIZE:, :, :, :]
train_labels = train_labels[VALIDATION_SIZE:]

train_size = train_labels.shape[0]

print ('Validation shape', validation_data.shape)
print ('Train size', train_size)


# In[17]:

import tensorflow as tf

BATCH_SIZE = 60 #Each batch is the number of data we feed into NN each time
# We have only one channel in our grayscale images.
NUM_CHANNELS = 1
# The random seed that defines initialization.
SEED = 42

# This is where training samples and labels are fed to the graph.
# These placeholder nodes will be fed a batch of training data at each
# training step, which we'll write once we define the graph structure.
train_data_node = tf.placeholder(tf.float32, shape=(BATCH_SIZE, IMAGE_SIZE, IMAGE_SIZE, NUM_CHANNELS))
train_labels_node = tf.placeholder(tf.float32,shape=(BATCH_SIZE, NUM_LABELS))

# For the validation and test data, we'll just hold the entire dataset in one constant node.
validation_data_node = tf.constant(validation_data)
test_data_node = tf.constant(test_data)


# ## Initialize weights

# In[18]:

# The variables below hold all the trainable weights. For each, the
# parameter defines how the variables will be initialized.
conv1_weights = tf.Variable(tf.truncated_normal([5, 5, NUM_CHANNELS, 32],  # 5x5 filter, depth 32.
                      stddev=0.1,
                      seed=SEED))
conv1_biases = tf.Variable(tf.zeros([32]))

conv2_weights = tf.Variable(tf.truncated_normal([5, 5, 32, 64],stddev=0.1,seed=SEED))
conv2_biases = tf.Variable(tf.constant(0.1, shape=[64]))

# fully connected, depth 512.
fc1_weights = tf.Variable(tf.truncated_normal([IMAGE_SIZE / 4 * IMAGE_SIZE / 4 * 64, 512],stddev=0.1,seed=SEED))
fc1_biases = tf.Variable(tf.constant(0.1, shape=[512]))

fc2_weights = tf.Variable(tf.truncated_normal([512, NUM_LABELS],stddev=0.1, seed=SEED))
fc2_biases = tf.Variable(tf.constant(0.1, shape=[NUM_LABELS]))

print ('Done')


# In[ ]:

def model(data, train=False):
  """The Model definition."""
  # 2D convolution, with 'SAME' padding (i.e. the output feature map has
  # the same size as the input). Note that {strides} is a 4D array whose
  # shape matches the data layout: [image index, y, x, depth].
  conv = tf.nn.conv2d(data,
                      conv1_weights,
                      strides=[1, 1, 1, 1],
                      padding='SAME')

  # Bias and rectified linear non-linearity.
  relu = tf.nn.relu(tf.nn.bias_add(conv, conv1_biases))
  
  # Max pooling. The kernel size spec ksize also follows the layout of
  # the data. Here we have a pooling window of 2, and a stride of 2.
  pool = tf.nn.max_pool(relu,
                        ksize=[1, 2, 2, 1],
                        strides=[1, 2, 2, 1],
                        padding='SAME')
  conv = tf.nn.conv2d(pool,
                      conv2_weights,
                      strides=[1, 1, 1, 1],
                      padding='SAME')
  relu = tf.nn.relu(tf.nn.bias_add(conv, conv2_biases))
  pool = tf.nn.max_pool(relu,
                        ksize=[1, 2, 2, 1],
                        strides=[1, 2, 2, 1],
                        padding='SAME')
  
  # Reshape the feature map cuboid into a 2D matrix to feed it to the
  # fully connected layers.
  pool_shape = pool.get_shape().as_list()
  reshape = tf.reshape(
      pool,
      [pool_shape[0], pool_shape[1] * pool_shape[2] * pool_shape[3]])
  
  # Fully connected layer. Note that the '+' operation automatically
  # broadcasts the biases.
  hidden = tf.nn.relu(tf.matmul(reshape, fc1_weights) + fc1_biases)
  
  # Add a 50% dropout during training only. Dropout also scales
  # activations such that no rescaling is needed at evaluation time.
  if train:
    hidden = tf.nn.dropout(hidden, 0.5, seed=SEED)
  return tf.matmul(hidden, fc2_weights) + fc2_biases

print('Done')


# In[ ]:

# Training computation: logits + cross-entropy loss.
logits = model(train_data_node, True)
loss = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(
  logits, train_labels_node))

# L2 regularization for the fully connected parameters.
regularizers = (tf.nn.l2_loss(fc1_weights) + tf.nn.l2_loss(fc1_biases) +
                tf.nn.l2_loss(fc2_weights) + tf.nn.l2_loss(fc2_biases))
# Add the regularization term to the loss.
loss += 5e-4 * regularizers

# Optimizer: set up a variable that's incremented once per batch and
# controls the learning rate decay.
batch = tf.Variable(0)
# Decay once per epoch, using an exponential schedule starting at 0.01.
learning_rate = tf.train.exponential_decay(
  0.01,                # Base learning rate.
  batch * BATCH_SIZE,  # Current index into the dataset.
  train_size,          # Decay step.
  0.95,                # Decay rate.
  staircase=True)
# Use simple momentum for the optimization.
optimizer = tf.train.MomentumOptimizer(learning_rate,
                                       0.9).minimize(loss,
                                                     global_step=batch)

# Predictions for the minibatch, validation set and test set.
train_prediction = tf.nn.softmax(logits)
# We'll compute them only once in a while by calling their {eval()} method.
validation_prediction = tf.nn.softmax(model(validation_data_node))
test_prediction = tf.nn.softmax(model(test_data_node))

print ('Done')


# In[ ]:

# Create a new interactive session that we'll use in
# subsequent code cells.
s = tf.InteractiveSession()

# Use our newly created session as the default for 
# subsequent operations.
s.as_default()

# Initialize all the variables we defined above.
tf.initialize_all_variables().run()


# In[ ]:

BATCH_SIZE = 60

# Grab the first BATCH_SIZE examples and labels.
batch_data = train_data[:BATCH_SIZE, :, :, :]
batch_labels = train_labels[:BATCH_SIZE]

# This dictionary maps the batch data (as a numpy array) to the
# node in the graph it should be fed to.
feed_dict = {train_data_node: batch_data,
             train_labels_node: batch_labels}

# Run the graph and fetch some of the nodes.
_, l, lr, predictions = s.run(
  [optimizer, loss, learning_rate, train_prediction],
  feed_dict=feed_dict)

print('Done')


# In[ ]:

print(predictions[0])


# In[ ]:

# The highest probability in the first entry.
print('First prediction', numpy.argmax(predictions[0]))
print(predictions.shape)
print('All predictions', numpy.argmax(predictions, 1))


# In[ ]:

print('Batch labels', numpy.argmax(batch_labels, 1))


# In[ ]:

correct = numpy.sum(numpy.argmax(predictions, 1) == numpy.argmax(batch_labels, 1))
total = predictions.shape[0]

print(float(correct) / float(total))

confusions = numpy.zeros([10, 10], numpy.float32)
bundled = zip(numpy.argmax(predictions, 1), numpy.argmax(batch_labels, 1))
for predicted, actual in bundled:
  confusions[predicted, actual] += 1

plt.grid(False)
plt.xticks(numpy.arange(NUM_LABELS))
plt.yticks(numpy.arange(NUM_LABELS))
plt.imshow(confusions, cmap=plt.cm.jet, interpolation='nearest');


# In[ ]:

def error_rate(predictions, labels):
  """Return the error rate and confusions."""
  correct = numpy.sum(numpy.argmax(predictions, 1) == numpy.argmax(labels, 1))
  total = predictions.shape[0]

  error = 100.0 - (100 * float(correct) / float(total))

  confusions = numpy.zeros([10, 10], numpy.float32)
  bundled = zip(numpy.argmax(predictions, 1), numpy.argmax(labels, 1))
  for predicted, actual in bundled:
    confusions[predicted, actual] += 1
    
  return error, confusions

print('Done')


# In[ ]:

# Train over the first 1/4th of our training set.
steps = int(train_size / BATCH_SIZE)
for step in xrange(steps):
  # Compute the offset of the current minibatch in the data.
  # Note that we could use better randomization across epochs.
  offset = (step * BATCH_SIZE) % (train_size - BATCH_SIZE)
  batch_data = train_data[offset:(offset + BATCH_SIZE), :, :, :]
  batch_labels = train_labels[offset:(offset + BATCH_SIZE)]
  # This dictionary maps the batch data (as a numpy array) to the
  # node in the graph it should be fed to.
  feed_dict = {train_data_node: batch_data,
               train_labels_node: batch_labels}
  # Run the graph and fetch some of the nodes.
  _, l, lr, predictions = s.run(
    [optimizer, loss, learning_rate, train_prediction],
    feed_dict=feed_dict)
  
  # Print out the loss periodically.
  if step % 100 == 0:
    error, _ = error_rate(predictions, batch_labels)
    print('Step %d of %d' % (step, steps))
    print('Mini-batch loss: %.5f Error: %.5f Learning rate: %.5f' % (l, error, lr))
    print('Validation error: %.1f%%' % error_rate(
        validation_prediction.eval(), validation_labels)[0])


# In[ ]:

test_error, confusions = error_rate(test_prediction.eval(), test_labels)
print('Test error: %.1f%%' % test_error)

plt.xlabel('Actual')
plt.ylabel('Predicted')
plt.grid(False)
plt.xticks(numpy.arange(NUM_LABELS))
plt.yticks(numpy.arange(NUM_LABELS))
plt.imshow(confusions, cmap=plt.cm.jet, interpolation='nearest');

for i, cas in enumerate(confusions):
  for j, count in enumerate(cas):
    if count > 0:
      xoff = .07 * len(str(count))
      plt.text(j-xoff, i+.2, int(count), fontsize=9, color='white')


# In[ ]:

plt.xticks(numpy.arange(NUM_LABELS))
plt.hist(numpy.argmax(test_labels, 1));


# In[ ]:



