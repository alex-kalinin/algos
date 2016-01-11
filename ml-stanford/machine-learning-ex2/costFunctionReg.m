function [J, grad] = costFunctionReg(theta, X, y, lambda)
%COSTFUNCTIONREG Compute cost and gradient for logistic regression with regularization
%   J = COSTFUNCTIONREG(theta, X, y, lambda) computes the cost of using
%   theta as the parameter for regularized logistic regression and the
%   gradient of the cost w.r.t. to the parameters. 

% Initialize some useful values
m = length(y); % number of training examples

% You need to return the following variables correctly 
J = 0;
grad = zeros(size(theta));

% ====================== YOUR CODE HERE ======================
% Instructions: Compute the cost of a particular choice of theta.
%               You should set J to the cost.
%               Compute the partial derivatives and set grad to the partial
%               derivatives of the cost w.r.t. each parameter in theta

z = theta' * X';
h = sigmoid(z);
h = h';  % convert to a proper vector

j_part = -y .* log(h) - (1 - y) .* log(1 .- h);
J = sum(j_part) / m;

% Skip theta(1) as we don't regularize the bias paramter theta_0
J += lambda / (2 * m) * sumsq(theta(2:end,:));

grad = X' * (h - y) / m;

% Zero out reg_elem(1) as we don't regularize theta_0.
reg_elem = lambda / m * theta;
reg_elem(1) = 0;
grad += reg_elem;

% =============================================================

end
