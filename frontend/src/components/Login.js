import React, { useState } from 'react';
import { Form, Button, Card, Alert, Container, Row, Col } from 'react-bootstrap';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

function Login() {
  const [formData, setFormData] = useState({
    email: '',
    password: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await axios.post('/api/users/signin', {
        email: formData.email,
        password: formData.password
      }, {
        timeout: 10000,
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (response.data && response.data.success) {
        localStorage.setItem('user', JSON.stringify(response.data));

        // Redirect based on user role
        if (response.data.role === 'ADMIN') {
          navigate('/admin-dashboard');
        } else {
          navigate('/customer-dashboard');
        }
      }
    } catch (err) {
      console.error('Login error:', err);
      if (err.code === 'ECONNABORTED') {
        setError('Request timeout. Please check if the backend server is running.');
      } else if (err.response && err.response.data && err.response.data.error) {
        setError(err.response.data.error);
      } else if (err.response) {
        setError('Invalid email or password. Please try again.');
      } else if (err.request) {
        setError('Unable to connect to server. Please ensure the backend is running.');
      } else {
        setError('An unexpected error occurred. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ 
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      display: 'flex',
      alignItems: 'center',
      paddingTop: '80px',
      paddingBottom: '2rem'
    }}>
      <Container>
        <Row className="justify-content-center">
          <Col md={6} lg={5} xl={4}>
            <Card 
              className="modern-card shadow-lg border-0 animate-fade-in"
              style={{ 
                borderRadius: '25px',
                background: 'rgba(255, 255, 255, 0.95)',
                backdropFilter: 'blur(20px)',
                boxShadow: '0 20px 60px rgba(0, 0, 0, 0.2)'
              }}
            >
              <Card.Header 
                className="text-center py-4 border-0"
                style={{
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  borderRadius: '25px 25px 0 0',
                  color: 'white'
                }}
              >
                <div className="mb-3">
                  <span 
                    style={{ 
                      fontSize: '3rem',
                      display: 'block',
                      animation: 'float 3s ease-in-out infinite'
                    }}
                  >
                    🔐
                  </span>
                </div>
                <h3 className="mb-0 fw-bold">Welcome Back!</h3>
                <p className="mb-0 mt-2 opacity-75">Sign in to your account</p>
              </Card.Header>
              
              <Card.Body className="p-4">
                {error && (
                  <Alert 
                    variant="danger" 
                    className="mb-3 alert-modern border-0"
                    style={{
                      background: 'linear-gradient(135deg, #ff6b6b, #ff8e8e)',
                      color: 'white',
                      borderRadius: '15px'
                    }}
                  >
                    <div className="d-flex align-items-center">
                      <span className="me-2">❌</span>
                      <div>
                        <strong>Login Failed</strong>
                        <div className="mt-1 small">{error}</div>
                      </div>
                    </div>
                  </Alert>
                )}

                <Form onSubmit={handleSubmit}>
                  <Form.Group className="mb-3">
                    <Form.Label className="fw-bold text-dark">
                      <span className="me-2">📧</span>Email Address
                    </Form.Label>
                    <Form.Control
                      type="email"
                      name="email"
                      value={formData.email}
                      onChange={handleChange}
                      required
                      placeholder="Enter your email address"
                      className="form-control-modern"
                      style={{
                        border: '2px solid rgba(102, 126, 234, 0.1)',
                        borderRadius: '12px',
                        padding: '0.75rem 1rem',
                        background: 'rgba(255, 255, 255, 0.8)',
                        transition: 'all 0.3s ease'
                      }}
                    />
                    <Form.Text className="text-muted small">
                      Use your registered email address
                    </Form.Text>
                  </Form.Group>

                  <Form.Group className="mb-4">
                    <Form.Label className="fw-bold text-dark">
                      <span className="me-2">🔒</span>Password
                    </Form.Label>
                    <Form.Control
                      type="password"
                      name="password"
                      value={formData.password}
                      onChange={handleChange}
                      required
                      placeholder="Enter your password"
                      className="form-control-modern"
                      style={{
                        border: '2px solid rgba(102, 126, 234, 0.1)',
                        borderRadius: '12px',
                        padding: '0.75rem 1rem',
                        background: 'rgba(255, 255, 255, 0.8)',
                        transition: 'all 0.3s ease'
                      }}
                    />
                  </Form.Group>

                  <Button
                    type="submit"
                    disabled={loading}
                    className="btn-modern w-100 mb-3"
                    style={{
                      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                      border: 'none',
                      borderRadius: '12px',
                      padding: '0.75rem 1.5rem',
                      fontWeight: '600',
                      fontSize: '1rem',
                      color: 'white',
                      transition: 'all 0.3s ease'
                    }}
                    onMouseEnter={(e) => {
                      e.target.style.transform = 'translateY(-2px)';
                      e.target.style.boxShadow = '0 8px 25px rgba(102, 126, 234, 0.3)';
                    }}
                    onMouseLeave={(e) => {
                      e.target.style.transform = 'translateY(0)';
                      e.target.style.boxShadow = 'none';
                    }}
                  >
                    {loading ? (
                      <>
                        <span 
                          className="spinner-border spinner-border-sm me-2" 
                          style={{ width: '1rem', height: '1rem' }}
                        ></span>
                        Signing In...
                      </>
                    ) : (
                      <>
                        <span className="me-2">🚀</span>
                        Sign In
                      </>
                    )}
                  </Button>
                </Form>

                <div className="text-center">
                  <hr className="my-4" />
                  <p className="text-muted mb-0">
                    Don't have an account?{' '}
                    <Link 
                      to="/register" 
                      className="text-decoration-none fw-bold"
                      style={{ 
                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        WebkitBackgroundClip: 'text',
                        WebkitTextFillColor: 'transparent'
                      }}
                    >
                      Create Account
                    </Link>
                  </p>
                </div>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default Login;
