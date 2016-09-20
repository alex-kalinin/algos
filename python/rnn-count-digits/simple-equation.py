import tensorflow as tf
from time import time

# User TensorFlow to initialize and calculate a simple equation:
# (2.2 + X / 11) + (7 * cos(Y))

def main():
    # graph = tf.Graph()
    start = time()

    X = tf.placeholder(tf.float32)
    Y = tf.placeholder(tf.float32)
    left = 2.2 - X / 11.
    right = 7. * tf.cos(Y)
    op = left + right

    s = tf.Session()
    s.run(tf.initialize_all_variables())

    end = time()
    print('Build graph took {} ms'.format((end - start) * 1000))
    start = end

    print(s.run(op, { X: 1., Y: 2. }))
    print(s.run(op, { X: 10., Y: 5. }))

    print('Execute took {}'.format((time() - start) * 1000))

if __name__ == '__main__':
    main()