#  Fit a straight line, of the form y=m*x+b

import tensorflow as tf

xs = [0.00, 1.00, 2.00, 3.00, 4.00, 5.00, 6.00, 7.00]  # Features
ys = [-0.82, -0.94, -0.12, 0.26, 0.39, 0.64, 1.02, 1.00]  # Labels

'''
with enough iterations, initial weights don't matter since our cost function is convex.
'''
m_initial = -0.5  # Initial guesses
b_initial = 1.0

'''
define free variables to be solved. we'll be taking partial derivatives of m and b with respect to j (cost).
'''
m = tf.Variable(m_initial)  # Parameters
b = tf.Variable(b_initial)

loss = 0.0
for i in range(len(xs)):
    # noinspection PyTypeChecker
    y_model = m * xs[i] + b  # Output of the model aka yhat
    loss += (ys[i] - y_model) ** 2  # Difference squared - this is the "cost" to be minimized

'''
once cost function is defined, use gradient descent to find global minimum.
'''

train_op = tf.train\
    .GradientDescentOptimizer(learning_rate=0.001)\
    .minimize(loss)  # Does one step

with tf.Session() as session:
    session.run(tf.initialize_all_variables())  # Initialize session

    MAX_ITERS = 20000  # number of "sweeps" across data

    prev_loss_value = None

    for iteration in range(MAX_ITERS):
        _, loss_value = session.run([train_op, loss])
        print('{}'.format(loss_value))
        if prev_loss_value is not None and abs(loss_value - prev_loss_value) < 1e-9:
            print("Max iteration: {}".format(iteration))
            break
        prev_loss_value = loss_value

    print('Slope: {}, Intercept: {}, Loss: {}'.format(m.eval(), b.eval(), prev_loss_value))


