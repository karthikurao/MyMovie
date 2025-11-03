import React, { useState } from 'react';
import { Form, Button, Card, Alert, Row, Col, Container } from 'react-bootstrap';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

function Register() {
  const [formData, setFormData] = useState({
    customerName: '',
    email: '',
    password: '',
    confirmPassword: '',
    mobileNumber: '',
    address: ''
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

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      setLoading(false);
      return;
    }

    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      setLoading(false);
      return;
    }

    try {
      // Create customer account directly
      const response = await axios.post('/api/customers', {
        customerName: formData.customerName,
        email: formData.email,
        password: formData.password,
        mobileNumber: formData.mobileNumber,
        address: formData.address
      }, {
        timeout: 10000,
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (response.data) {
        alert('🎉 Registration successful! You can now login with your email and password.');
        navigate('/login');
      }
    } catch (err) {
      console.error('Registration error:', err);
      if (err.response && err.response.status === 400) {
        setError('Email already exists. Please use a different email address.');
      } else if (err.response && err.response.status === 401) {
        setError('Registration failed: authentication is required. Please try again or contact support.');
      } else if (err.code === 'ECONNABORTED') {
        setError('Request timeout. Please check if the backend server is running.');
      } else if (err.request) {
        setError('Unable to connect to server. Please ensure the backend is running.');
      } else {
        setError('Registration failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      paddingTop: '90px',
      paddingBottom: '2rem'
    }}>
      <Container>
        <Row className="justify-content-center">
          <Col lg={6} md={8}>
            <Card
              className="border-0"
              style={{
                background: 'rgba(255, 255, 255, 0.95)',
                backdropFilter: 'blur(20px)',
                borderRadius: '25px',
                boxShadow: '0 20px 60px rgba(0, 0, 0, 0.1)'
              }}
            >
              <Card.Body className="p-5">
                {/* Header */}
                <div className="text-center mb-5">
                  <div className="mb-3">
                    <span style={{ fontSize: '4rem', display: 'block' }}>✨</span>
                  </div>
                  <h2 className="mb-3" style={{
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    WebkitBackgroundClip: 'text',
                    WebkitTextFillColor: 'transparent',
                    fontWeight: 'bold'
                  }}>
                    Join CinemaHub
                  </h2>
                  <p className="text-muted">
                    Create your account and start booking amazing movie experiences
                  </p>
                </div>

                {error && (
                  <Alert
                    variant="danger"
                    className="mb-4"
                    style={{
                      borderRadius: '15px',
                      border: 'none',
                      background: 'linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%)',
                      color: 'white'
                    }}
                  >
                    <div className="d-flex align-items-center">
                      <span className="me-2">⚠️</span>
                      {error}
                    </div>
                  </Alert>
                )}

                <Form onSubmit={handleSubmit}>
                  <Row>
                    <Col md={6}>
                      <Form.Group className="mb-4">
                        <Form.Label className="fw-semibold text-muted">
                          <span className="me-2">👤</span>
                          Full Name
                        </Form.Label>
                        <Form.Control
                          type="text"
                          name="customerName"
                          value={formData.customerName}
                          onChange={handleChange}
                          required
                          placeholder="Enter your full name"
                          style={{
                            borderRadius: '15px',
                            border: '2px solid #e9ecef',
                            padding: '0.75rem 1rem',
                            fontSize: '1rem',
                            transition: 'all 0.3s ease'
                          }}
                          onFocus={(e) => {
                            e.target.style.borderColor = '#667eea';
                            e.target.style.boxShadow = '0 0 20px rgba(102, 126, 234, 0.1)';
                          }}
                          onBlur={(e) => {
                            e.target.style.borderColor = '#e9ecef';
                            e.target.style.boxShadow = 'none';
                          }}
                        />
                      </Form.Group>
                    </Col>
                    <Col md={6}>
                      <Form.Group className="mb-4">
                        <Form.Label className="fw-semibold text-muted">
                          <span className="me-2">📧</span>
                          Email Address
                        </Form.Label>
                        <Form.Control
                          type="email"
                          name="email"
                          value={formData.email}
                          onChange={handleChange}
                          required
                          placeholder="Enter your email"
                          style={{
                            borderRadius: '15px',
                            border: '2px solid #e9ecef',
                            padding: '0.75rem 1rem',
                            fontSize: '1rem',
                            transition: 'all 0.3s ease'
                          }}
                          onFocus={(e) => {
                            e.target.style.borderColor = '#667eea';
                            e.target.style.boxShadow = '0 0 20px rgba(102, 126, 234, 0.1)';
                          }}
                          onBlur={(e) => {
                            e.target.style.borderColor = '#e9ecef';
                            e.target.style.boxShadow = 'none';
                          }}
                        />
                        <Form.Text className="text-muted small">
                          This will be your login email
                        </Form.Text>
                      </Form.Group>
                    </Col>
                  </Row>

                  <Row>
                    <Col md={6}>
                      <Form.Group className="mb-4">
                        <Form.Label className="fw-semibold text-muted">
                          <span className="me-2">🔒</span>
                          Password
                        </Form.Label>
                        <Form.Control
                          type="password"
                          name="password"
                          value={formData.password}
                          onChange={handleChange}
                          required
                          placeholder="Create a password"
                          style={{
                            borderRadius: '15px',
                            border: '2px solid #e9ecef',
                            padding: '0.75rem 1rem',
                            fontSize: '1rem',
                            transition: 'all 0.3s ease'
                          }}
                          onFocus={(e) => {
                            e.target.style.borderColor = '#667eea';
                            e.target.style.boxShadow = '0 0 20px rgba(102, 126, 234, 0.1)';
                          }}
                          onBlur={(e) => {
                            e.target.style.borderColor = '#e9ecef';
                            e.target.style.boxShadow = 'none';
                          }}
                        />
                        <Form.Text className="text-muted small">
                          Must be at least 6 characters long
                        </Form.Text>
                      </Form.Group>
                    </Col>
                    <Col md={6}>
                      <Form.Group className="mb-4">
                        <Form.Label className="fw-semibold text-muted">
                          <span className="me-2">�</span>
                          Confirm Password
                        </Form.Label>
                        <Form.Control
                          type="password"
                          name="confirmPassword"
                          value={formData.confirmPassword}
                          onChange={handleChange}
                          required
                          placeholder="Confirm your password"
                          style={{
                            borderRadius: '15px',
                            border: '2px solid #e9ecef',
                            padding: '0.75rem 1rem',
                            fontSize: '1rem',
                            transition: 'all 0.3s ease'
                          }}
                          onFocus={(e) => {
                            e.target.style.borderColor = '#667eea';
                            e.target.style.boxShadow = '0 0 20px rgba(102, 126, 234, 0.1)';
                          }}
                          onBlur={(e) => {
                            e.target.style.borderColor = '#e9ecef';
                            e.target.style.boxShadow = 'none';
                          }}
                        />
                      </Form.Group>
                    </Col>
                  </Row>

                  <Row>
                    <Col md={6}>
                      <Form.Group className="mb-4">
                        <Form.Label className="fw-semibold text-muted">
                          <span className="me-2">📱</span>
                          Mobile Number
                        </Form.Label>
                        <Form.Control
                          type="tel"
                          name="mobileNumber"
                          value={formData.mobileNumber}
                          onChange={handleChange}
                          required
                          placeholder="Enter your mobile number"
                          style={{
                            borderRadius: '15px',
                            border: '2px solid #e9ecef',
                            padding: '0.75rem 1rem',
                            fontSize: '1rem',
                            transition: 'all 0.3s ease'
                          }}
                          onFocus={(e) => {
                            e.target.style.borderColor = '#667eea';
                            e.target.style.boxShadow = '0 0 20px rgba(102, 126, 234, 0.1)';
                          }}
                          onBlur={(e) => {
                            e.target.style.borderColor = '#e9ecef';
                            e.target.style.boxShadow = 'none';
                          }}
                        />
                      </Form.Group>
                    </Col>
                    <Col md={6}>
                      <Form.Group className="mb-4">
                        <Form.Label className="fw-semibold text-muted">
                          <span className="me-2">🏠</span>
                          Address
                        </Form.Label>
                        <Form.Control
                          as="textarea"
                          rows={3}
                          name="address"
                          value={formData.address}
                          onChange={handleChange}
                          required
                          placeholder="Enter your address"
                          style={{
                            borderRadius: '15px',
                            border: '2px solid #e9ecef',
                            padding: '0.75rem 1rem',
                            fontSize: '1rem',
                            transition: 'all 0.3s ease',
                            resize: 'none'
                          }}
                          onFocus={(e) => {
                            e.target.style.borderColor = '#667eea';
                            e.target.style.boxShadow = '0 0 20px rgba(102, 126, 234, 0.1)';
                          }}
                          onBlur={(e) => {
                            e.target.style.borderColor = '#e9ecef';
                            e.target.style.boxShadow = 'none';
                          }}
                        />
                      </Form.Group>
                    </Col>
                  </Row>

                  <div className="d-grid gap-2 mb-4">
                    <Button
                      variant="primary"
                      type="submit"
                      disabled={loading}
                      size="lg"
                      style={{
                        background: loading
                          ? 'linear-gradient(135deg, #95a5a6 0%, #7f8c8d 100%)'
                          : 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                        border: 'none',
                        borderRadius: '15px',
                        padding: '0.75rem',
                        fontWeight: '600',
                        fontSize: '1.1rem',
                        transition: 'all 0.3s ease'
                      }}
                      onMouseEnter={(e) => {
                        if (!loading) {
                          e.target.style.transform = 'translateY(-2px)';
                          e.target.style.boxShadow = '0 8px 25px rgba(102, 126, 234, 0.3)';
                        }
                      }}
                      onMouseLeave={(e) => {
                        if (!loading) {
                          e.target.style.transform = 'translateY(0)';
                          e.target.style.boxShadow = 'none';
                        }
                      }}
                    >
                      {loading ? (
                        <>
                          <span className="me-2">⏳</span>
                          Creating Account...
                        </>
                      ) : (
                        <>
                          <span className="me-2">✨</span>
                          Create Account
                        </>
                      )}
                    </Button>
                  </div>

                  <div className="text-center">
                    <p className="text-muted mb-2">Already have an account?</p>
                    <Link
                      to="/login"
                      style={{
                        color: '#667eea',
                        textDecoration: 'none',
                        fontWeight: '600',
                        padding: '0.5rem 1.5rem',
                        border: '2px solid #667eea',
                        borderRadius: '25px',
                        transition: 'all 0.3s ease'
                      }}
                      onMouseEnter={(e) => {
                        e.target.style.background = '#667eea';
                        e.target.style.color = 'white';
                        e.target.style.transform = 'translateY(-2px)';
                      }}
                      onMouseLeave={(e) => {
                        e.target.style.background = 'transparent';
                        e.target.style.color = '#667eea';
                        e.target.style.transform = 'translateY(0)';
                      }}
                    >
                      <span className="me-1">🔐</span>
                      Sign In
                    </Link>
                  </div>
                </Form>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>
    </div>
  );
}

export default Register;
